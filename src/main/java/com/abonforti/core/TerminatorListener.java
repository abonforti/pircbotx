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

package com.abonforti.core;

import com.abonforti.facade.ChannelFacade;
import com.abonforti.facade.impl.DefaultChannelFacade;
import com.abonforti.utils.Config;
import com.abonforti.utils.EventUtils;
import org.apache.commons.lang.StringUtils;
import org.pircbotx.Configuration.Builder;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TerminatorListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TerminatorListener.class);

    @Override
    public void onMessage(final MessageEvent event) {
        final ChannelFacade channelFacade = new DefaultChannelFacade();
        channelFacade.process(event);
    }

    @Override
    public void onPrivateMessage(final PrivateMessageEvent event) {
        String message = event.getMessage();
        if(StringUtils.startsWith(message, "say") && EventUtils.isAdmin(event)) {
            message = StringUtils.removeStart(message, "say ");
            final String[] raw = StringUtils.split(message, " ");
            final StringBuilder replyText = new StringBuilder(StringUtils.EMPTY);
            for (int i = 1; i < raw.length; i++) {
                replyText.append(raw[i]).append(" ");
            }
            event.getBot().sendIRC().message(StringUtils.trim(raw[0]), StringUtils.trim(replyText.toString()));
        }
    }

    @Override
    public void onInvite(final InviteEvent event) {
        final String channel = event.getChannel();
        final User user = event.getUser();
        if(EventUtils.isAdmin(event) || EventUtils.isKnownChannel(event)) {
           LOG.info("Got invite to join {} from {}, joining.", channel, user.getNick());
           event.getBot().sendIRC().joinChannel(channel);
       }
    }

    @Override
    public void onNotice(final NoticeEvent event) {
        final ChannelFacade channelFacade = new DefaultChannelFacade();
        channelFacade.process(event);
    }

    @Override
    public void onVersion(final VersionEvent event) {
        event.respond(String.format("VERSION LamerBot Version %s (Powered by PircBotX) - %s", Config.VERSION, Config.SOURCE));
    }

    @Override
    public void onKick(final KickEvent event) {
        if(EventUtils.isAdmin(event)) {
            event.getBot().sendIRC().joinChannel(event.getChannel().getName());
        }
    }

    public static void main(String[] args) throws Exception {
        final Config config = Config.getInstance();
        final Iterable<String> channels = Arrays.asList(config.getChannels());
        final Builder builder = new Builder();

        builder.setName(config.getNick());
        builder.setLogin(config.getLogin());
        builder.setRealName(config.getRealName());
        builder.addServer(config.getIrcServer(), config.getIrcPort());
        builder.setServerPassword(config.getServerPassword());
        builder.setSocketFactory(new UtilSSLSocketFactory().trustAllCertificates());
        // builder.setSocketFactory(new UtilSSLSocketFactory().disableDiffieHellman());
        builder.addAutoJoinChannels(channels);
        builder.addListener(new TerminatorListener());
        builder.setAutoReconnect(config.isAutoReconnect());
        builder.setNickservPassword(config.getNickservPassword());

        final PircBotX bot = new PircBotX(builder.buildConfiguration());

        bot.startBot();

    }

}
