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

import com.abonforti.service.ChannelService;
import com.abonforti.utils.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class DefaultChannelService implements ChannelService {

    private static final String OP_COMMAND = "op";
    private static final String DEOP_COMMAND = "deop";
    private static final String HOP_COMMAND = "hop";
    private static final String DEHOP_COMMAND = "dehop";
    private static final String VOICE_COMMAND = "voice";
    private static final String DEVOICE_COMMAND = "devoice";

    @Override
    public String process(final MessageEvent event) {
        String reply = "Fancy module not enabled for the current channel!";
        final String channel = StringUtils.removeStart(event.getChannel().getName(), "#");
        final String message = event.getMessage();
        final String command = StringUtils.lowerCase(StringUtils.removeStart(StringUtils.split(message, " ")[0], "!"));
        final String targetUser = StringUtils.removeStart(message, "!" + command + " ");
        if(isFancyModuleEnabled(channel)) {
            reply = StringUtils.EMPTY;
            switch (command) {
                case OP_COMMAND:
                    handleOpOrHalfop(event, targetUser, "+o");
                    break;
                case DEOP_COMMAND:
                    handleOpOrHalfop(event, targetUser, "-o");
                    break;
                case HOP_COMMAND:
                    handleOpOrHalfop(event, targetUser, "+h");
                    break;
                case DEHOP_COMMAND:
                    handleOpOrHalfop(event, targetUser, "-h");
                    break;
                case VOICE_COMMAND:
                    handleVoice(event, targetUser, "+v");
                    break;
                case DEVOICE_COMMAND:
                    handleVoice(event, targetUser, "-v");
                    break;
            }
        }
        return reply;
    }

    protected void handleOpOrHalfop(final MessageEvent event, final String targetUser, final String mode) {
        if(can(event, mode)) {
            event.getBot().sendIRC().mode(event.getChannel().getName(), mode + " " + targetUser);
        }
    }

    protected void handleVoice(final MessageEvent event, final String targetUser, final String mode) {
        if(can(event, mode)) {
            event.getBot().sendIRC().mode(event.getChannel().getName(), mode + " " + targetUser);
        }
    }

    private boolean can(final MessageEvent event, final String targetMode) {
        boolean can = false;
        final Set<User> opList = event.getChannel().getOps();
        final Set<User> hopList = event.getChannel().getHalfOps();

        if(targetMode.equals("+o") || targetMode.equals("-o") || targetMode.contains("+h") || targetMode.contains("-h")) {
            final List<User> botAndSender = new ArrayList<>();
            botAndSender.add(event.getUser());
            botAndSender.add(event.getBot().getUserBot());
            can = opList.containsAll(botAndSender);
        } else if(targetMode.equals("+v") || targetMode.equals("-v")) {
            boolean senderCan = hopList.contains(event.getUser());
            if(!senderCan) {
                senderCan = opList.contains(event.getUser());
            }
            boolean botCan = hopList.contains(event.getBot().getUserBot());
            if(!botCan) {
                botCan = opList.contains(event.getBot().getUserBot());
            }
            can = botCan && senderCan;
        }
        return can;
    }


    private boolean isFancyModuleEnabled(final String chan) {
        return Config.getInstance().getConfigurationChannelList().get(chan).contains("fancycommands");
    }
}
