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
package com.abonforti.command.impl;

import com.abonforti.command.ChannelCommand;
import com.abonforti.processor.ChannelProcessor;
import com.abonforti.processor.impl.QuoteChannelProcessor;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.regex.Pattern;

public class DefaultChannelCommand implements ChannelCommand {

    private static final char COMMAND_PREFIX = '\u0021';
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Pattern QUOTE_PATTERN = Pattern.compile("^(?:addquote|quote|delquote|findquote)$");


    @Override
    public void process(final MessageEvent event) {
        final String message = event.getMessage();
        if(message.charAt(0) >= COMMAND_PREFIX) {
            final String command = StringUtils.split(StringUtils.remove(message, COMMAND_PREFIX))[0];
            final String channel = event.getChannel().getName();
            if(StringUtils.equals(command, "help")) {
                printHelpMessage(event);
            } else if(QUOTE_PATTERN.matcher(command).find()) {
                final ChannelProcessor processor = new QuoteChannelProcessor();
                final String nick = event.getUser().getNick();
                final String result = processor.process(event);
                event.getBot().sendIRC().message(channel, result);
            }
        }
    }

    private void printHelpMessage(final GenericMessageEvent event) {

        final String text = LINE_SEPARATOR + "This is the list of the commands available:" +
                LINE_SEPARATOR + Colors.BOLD + "!help " + Colors.NORMAL + "- shows this help." +
                LINE_SEPARATOR + Colors.BOLD + "!addquote text " + Colors.NORMAL + "- Adds a quote to the db." +
                LINE_SEPARATOR + Colors.BOLD + "!delquote number " + Colors.NORMAL + "- Removes a quote from the db." +
                LINE_SEPARATOR + Colors.BOLD + "!findquote text " + Colors.NORMAL + "- Searches for a quote id." +
                LINE_SEPARATOR + Colors.BOLD + "!quote " + Colors.NORMAL + "- Prints a random quote" +
                LINE_SEPARATOR + Colors.BOLD + "!quote number " + Colors.NORMAL + "- Prints the corresponding quote.";

        event.respondWith(text);
    }
}
