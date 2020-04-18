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
package com.abonforti.utils.db;

import com.abonforti.utils.Config;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class JDBCUtils {
    private static Connection connection;
    private static final String DB_URL_PREFIX = "jdbc:mariadb://";
    private static final String DB_USERNAME = Config.getInstance().getMysqlUser();
    private static final String DB_PASSWORD = Config.getInstance().getMysqlPassword();
    private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                loadJDBCDriver();
                loadConnection();
            }
        } catch (final SQLException ex) {
            log.error("An error occurred: {}", ex.getMessage(), ex);
        }
        return connection;
    }

    private static synchronized void loadConnection() {
        try {
            connection = DriverManager.getConnection(getFormattedUrl());
        } catch (final SQLException ex) {
            log.error("There were an error loading the connection. {}", ex.getMessage(), ex);
        }
    }

    private static String getFormattedUrl() {
        return DB_URL_PREFIX + Config.getInstance().getMysqlHost() + "/" + Config.getInstance().getMysqlDbName() +
                "?user=" + DB_USERNAME + "&password=" + DB_PASSWORD + "&useSSL=false";
    }

    private static void loadJDBCDriver() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (final ClassNotFoundException ex) {
            log.error("There were an error by loading the JDBC driver: {}", ex.getMessage(), ex);
        }
    }

    public static void closeConnection() {
        if (connection == null) {
            log.error("No connection found.");
        } else {
            try {
                connection.close();
                connection = null;
            } catch (final SQLException ex) {
                log.error("There were an error by closing the connection: {}", ex.getMessage(), ex);
            }
        }
    }
}
