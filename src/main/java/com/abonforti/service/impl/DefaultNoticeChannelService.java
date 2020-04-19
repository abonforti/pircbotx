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
package com.abonforti.service.impl;

import com.abonforti.service.NoticeService;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.hooks.events.NoticeEvent;

public class DefaultNoticeChannelService implements NoticeService {

    @Override
    public void process(final NoticeEvent event, final String text) {
        final String user = StringUtils.split(text, " ")[0];
        final String kickMessage = StringUtils.removeStart(text, user);
        event.getBot().sendRaw().rawLine(buildKickCommand(event.getChannel().getName(), user, kickMessage));
    }

    private String buildKickCommand(final String channel, final String user, final String message) {
        return "KICK " + channel + " " + user + " :" + StringUtils.trim(message);
    }
}
