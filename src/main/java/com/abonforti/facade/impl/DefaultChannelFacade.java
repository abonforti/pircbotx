/**
 * Copyright (C) 2010-2014 Leon Blakey <lord.quackstar at gmail.com>
 *
 * This file is part of PircBotX.
 *
 * PircBotX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * PircBotX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * PircBotX. If not, see <http://www.gnu.org/licenses/>.
 *
 * This is a custom version developed by Alessio Bonnforti for Azzurra IRC Network
 * Please do not contact directly Leon Blakey in case of issue using this repository
 * as the customization might be not done by him
 */
package com.abonforti.facade.impl;

import com.abonforti.facade.ChannelFacade;
import com.abonforti.service.ChannelService;
import com.abonforti.service.NoticeService;
import com.abonforti.service.impl.DefaultNoticeChannelService;
import com.abonforti.service.impl.DefaultQuoteChannelService;
import com.abonforti.utils.Config;
import com.abonforti.utils.EventUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.regex.Pattern;

public class DefaultChannelFacade implements ChannelFacade {

    private static final String COMMAND_PREFIX = "!";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String KICK_COMMAND = "kick";
    private static final String BAN_COMMAND = "ban";

    private static final Pattern QUOTE_PATTERN = Pattern.compile("^(?:addquote|quote|delquote|findquote)$");
    private static final Pattern FANCY_COMMAND_PATTERN = Pattern.compile("^(?:op|deop|hop|dehop|voice|devoice)$");


    @Override
    public void process(final MessageEvent event) {
        final String message = event.getMessage();
        if(StringUtils.startsWith(message, COMMAND_PREFIX)) {
            final String command = StringUtils.split(StringUtils.remove(message, COMMAND_PREFIX))[0];
            final String channel = event.getChannel().getName();
            if(StringUtils.equals(command, "help")) {
                printHelpMessage(event);
            } else if(QUOTE_PATTERN.matcher(command).find() || StringUtils.equals(command, "mosconi")) {
                final ChannelService channelService = new DefaultQuoteChannelService();
                final String nick = event.getUser().getNick();
                final String result = channelService.process(event);
                event.getBot().sendIRC().message(channel, result);
            } else if (StringUtils.equals(command, "reload") && EventUtils.isAdmin(event)) {
                Config.reloadConfig();
            } else if (FANCY_COMMAND_PATTERN.matcher(command).find()) {
                final ChannelService channelService = new DefaultChannelService();
                channelService.process(event);
            }
        }
    }

    @Override
    public void process(final NoticeEvent event) {
        final String message = event.getNotice();
        final String command = StringUtils.split(message, " ")[0];
        final String strippedMessage = StringUtils.trim(StringUtils.removeStart(message, command));
        final NoticeService noticeService = new DefaultNoticeChannelService();

        switch(command) {
            case KICK_COMMAND:
                kick(noticeService, event, strippedMessage);
                break;
            case BAN_COMMAND:
                final String user = StringUtils.split(strippedMessage, " ")[0];
                String userHostMask = StringUtils.EMPTY;
                for (final User u : event.getChannel().getUsers()) {
                    if (u.getNick().equals(user)) {
                        userHostMask = u.getHostmask();
                    }
                }

                event.getBot().sendIRC().mode(event.getChannel().getName(), "+b *!*@" + StringUtils.split(userHostMask,"@")[1]);
                kick(noticeService, event, strippedMessage);
        }
    }

    private void kick(final NoticeService noticeService, NoticeEvent event, String strippedMessage) {
        noticeService.process(event, strippedMessage);
    }

    private void printHelpMessage(final GenericMessageEvent event) {

        final String text = LINE_SEPARATOR + "This is the list of the commands available:" +
                LINE_SEPARATOR + Colors.BOLD + "!help " + Colors.NORMAL + "- shows this help." +
                LINE_SEPARATOR + Colors.BOLD + "!addquote text " + Colors.NORMAL + "- Adds a quote to the db." +
                LINE_SEPARATOR + Colors.BOLD + "!delquote number " + Colors.NORMAL + "- Removes a quote from the db." +
                LINE_SEPARATOR + Colors.BOLD + "!findquote text " + Colors.NORMAL + "- Searches for a quote id." +
                LINE_SEPARATOR + Colors.BOLD + "!quote " + Colors.NORMAL + "- Prints a random quote" +
                LINE_SEPARATOR + Colors.BOLD + "!quote number " + Colors.NORMAL + "- Prints the corresponding quote." +
                LINE_SEPARATOR + Colors.BOLD + "!op user, !deop user, !hop user, !dehop user, !voice user, !devoice user " + Colors.NORMAL + "- pretty much self explaining";

        event.getBot().sendIRC().notice(event.getUser().getNick(), text);
    }
}
