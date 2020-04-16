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
package com.abonforti.utils;

import org.pircbotx.hooks.events.InviteEvent;
import org.pircbotx.hooks.types.GenericUserEvent;

import java.util.Arrays;

public class EventUtils {

    public static boolean isAdmin(final GenericUserEvent event) {
        return event.getUser().getNick().equalsIgnoreCase(Config.getInstance().getAdmin());
    }

    public static boolean isKnownChannel(final InviteEvent event) {
        return Arrays.asList(Config.getInstance().getChannels()).contains(event.getChannel());
    }
}
