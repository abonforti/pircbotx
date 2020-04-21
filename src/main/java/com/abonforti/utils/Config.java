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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Config {

    public static final String VERSION = "1.0-alpha1";
    public static final String SOURCE = "https://github.com/abonforti/pircbotx";

    private static final String BOT_PROPERTIES_FILE = "bot.properties";
    private static final char COMMA_DELIMITER = '\u002C';

    private static Config instance = null;

    @Getter
    private String nick;

    @Getter
    private String login;

    @Getter
    private String realName;

    @Getter
    private String nickservPassword;

    @Getter
    private String ircServer;

    @Getter
    private String serverPassword;

    @Getter
    private String[] channels;

    @Getter
    private int ircPort;

    @Getter
    private boolean rejoinOnKick;

    @Getter
    private boolean useSSL;

    @Getter
    private boolean autoReconnect;

    @Getter
    private String admin;

    @Getter
    private String mysqlHost;

    @Getter
    private String mysqlUser;

    @Getter
    private String mysqlPassword;

    @Getter
    private String mysqlDbName;

    @Getter
    @Setter
    private Map<String, List<String>> configurationChannelList;

    public static Config getInstance() {
        if(instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public static void reloadConfig() {
        log.info("Reloading configuration...");
        instance = new Config();
    }

    private Config() {
        PropertiesConfiguration properties = null;

        try {
            properties = new PropertiesConfiguration(BOT_PROPERTIES_FILE);
        } catch (ConfigurationException ex) {
           log.error("Unable to configure the bot! {}", ex.getMessage(), ex);
        }
        if(properties != null) {
            log.info("Loading configurations...");
            properties.setListDelimiter(COMMA_DELIMITER);
            properties.reload();

            nick = properties.getString("bot.nickname");
            login = properties.getString("bot.ident");
            realName = properties.getString("bot.realname");
            nickservPassword = properties.getString("bot.nickserv.password");
            ircServer = properties.getString("irc.server.host");
            ircPort = properties.getInt("irc.server.port");
            serverPassword = properties.getString("irc.server.password", StringUtils.EMPTY);
            useSSL = Boolean.parseBoolean(properties.getString("irc.ssl.enabled"));
            autoReconnect = Boolean.parseBoolean(properties.getString("bot.enable.auto.reconnect"));
            rejoinOnKick = Boolean.parseBoolean(properties.getString("irc.rejoin.on.kick"));
            channels = properties.getStringArray("irc.channels.auto.join");
            admin = properties.getString("irc.admin.nick");
            configurationChannelList = buildConfigurationChannelList(properties);

            mysqlHost = properties.getString("mysql.host.name");
            mysqlDbName = properties.getString("mysql.db.name");
            mysqlUser = properties.getString("mysql.user.name");
            mysqlPassword = properties.getString("mysql.user.password");
        }
    }

    private Map<String, List<String>> buildConfigurationChannelList(final PropertiesConfiguration properties) {
        final Map<String,List<String>> chansConfig = new HashMap<>();
        final String[] rawModules = properties.getStringArray("irc.channels.modules");

        for(final String group : rawModules) {
            final String[] args = StringUtils.split(group, ":");
            final String channel = StringUtils.removeStart(args[0],"#");
            final String[] modules = StringUtils.split(args[1], "+");
            chansConfig.put(channel, Arrays.asList(modules));
        }
        return chansConfig;
    }

}
