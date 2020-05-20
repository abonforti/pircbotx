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

import com.abonforti.service.ChannelService;
import com.abonforti.utils.Config;
import com.abonforti.utils.db.JDBCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DefaultQuoteChannelService implements ChannelService {

    private static final String ADDQUOTE = "addquote";
    private static final String DELQUOTE = "delquote";
    private static final String QUOTE = "quote";
    private static final String FINDQUOTE = "findquote";
    private static final String LASTQUOTE = "lastquote";
    private static final String MOSCONI = "mosconi";


    @Override
    public String process(final MessageEvent event) {
        String reply = "Quote module not enabled for the current channel!";
        final String channel = StringUtils.removeStart(event.getChannel().getName(), "#");
        final String message = event.getMessage();
        final String command = StringUtils.lowerCase(StringUtils.removeStart(StringUtils.split(message, " ")[0], "!"));
        if(isQuoteModuleEnabled(channel)) {
            final String text = StringUtils.removeStart(message, "!" + command);

            switch (command) {
                case ADDQUOTE:
                    reply = addQuote(text, event.getUser().getNick(), channel);
                    break;
                case DELQUOTE:
                    reply = delQuote(text, channel);
                    break;
                case QUOTE:
                    reply = quote(text, channel);
                    break;
                case FINDQUOTE:
                    reply = findQuote(text, channel);
                    break;
                case LASTQUOTE:
                    reply = lastquote(channel);
                    break;
                case MOSCONI:
                    reply = mosconi();
                    break;
            }
        }
        return reply;
    }

    private String addQuote(final String message, final String author, final String channel) {
        final Connection c = JDBCUtils.getConnection();
        final PreparedStatement ps;
        String result = StringUtils.EMPTY;
        try {
            final String query = "INSERT INTO `" + channel + "` (text,user) VALUES (?,?);";
            ps = c.prepareStatement(query);
            ps.setString(1, message);
            ps.setString(2, author);

            result = !ps.execute() ? "Quote added to the database" : StringUtils.EMPTY;
            ps.close();
        } catch (final SQLException ex) {
            log.error("There were an error by adding quote to the db: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String delQuote(final String id, final String channel) {
        final Connection c = JDBCUtils.getConnection();
        final PreparedStatement ps;
        String result = StringUtils.EMPTY;
        try {
            final String query = "DELETE FROM `" + channel + "` WHERE id = " + id + " ;";
            ps = c.prepareStatement(query);
            final int deleted = ps.executeUpdate();
            result = deleted == 1 ? "Quote deleted from the database" : StringUtils.EMPTY;
            ps.close();
        } catch (final SQLException ex) {
            log.error("There were an error by deleting quote to the db: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String mosconi() {
        final Connection c = JDBCUtils.getConnection();
        final ResultSet rs;
        final PreparedStatement ps;
        String result = StringUtils.EMPTY;

        try {
            final String query = "SELECT quote FROM `mosconi` ORDER BY RAND() LIMIT 1;";
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                result = returnMosconi(rs);
            }

            rs.close();
            ps.close();
            return result;
        } catch (final SQLException ex) {
            log.error("There were an error by picking a random mosconi quote: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String lastquote(final String channel) {
        final Connection c = JDBCUtils.getConnection();
        final ResultSet rs;
        final PreparedStatement ps;
        String result = StringUtils.EMPTY;

        try {
            final String query = "SELECT * FROM `" + channel + "` ORDER BY id DESC LIMIT 1;";
            ps = c.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()) {
                result = returnQuote(rs);
            }

            rs.close();
            ps.close();
        } catch (final SQLException ex) {
            log.error("There were an error by picking a random quote: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String quote(final String message, final String channel) {
        final Connection c = JDBCUtils.getConnection();
        final ResultSet rs;
        final PreparedStatement ps;
        String result = StringUtils.EMPTY;
        try {
            String query = "SELECT * FROM `" + channel + "` ORDER BY RAND() LIMIT 1;";
            if(StringUtils.isEmpty(message)) {
                ps = c.prepareStatement(query);
                rs = ps.executeQuery();

                while(rs.next()) {
                    result = returnQuote(rs);
                }

                rs.close();
                ps.close();
                return result;
            }
            query = "SELECT * FROM `" + channel + "` WHERE id = ?;";
            ps = c.prepareStatement(query);
            ps.setString(1, message);
            rs = ps.executeQuery();

            while(rs.next()) {
                result = returnQuote(rs);
            }
            rs.close();
            ps.close();
        } catch (final SQLException ex) {
            log.error("There were an error by picking a random quote: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String findQuote(final String pattern, final String channel) {
        final Connection c = JDBCUtils.getConnection();
        final PreparedStatement ps;
        final ResultSet rs;
        final StringBuilder foundIds = new StringBuilder(StringUtils.EMPTY);
        String result = StringUtils.EMPTY;
        try {
            final String query = "SELECT id FROM `" + channel + "` WHERE text LIKE ?;";
            ps = c.prepareStatement(query);
            ps.setString(1, "%" + pattern + "%");
            rs = ps.executeQuery();

            while(rs.next()) {
                foundIds.append(rs.getString("id")).append(" ");
            }
            rs.close();
            ps.close();

            result = "Found the following query IDs: " + foundIds;
        } catch (final SQLException ex) {
            log.error("There were an error by searching for a quote: {}", ex.getMessage(), ex);
        } finally {
            JDBCUtils.closeConnection();
        }
        return result;
    }

    private String returnQuote(final ResultSet rs) throws SQLException {
        return Colors.BOLD + "ID: " + Colors.NORMAL + rs.getString("id") +
                " | " + Colors.BOLD + "Quote: " + Colors.NORMAL + rs.getString("text") +
                " | " + Colors.BOLD + "User: " + Colors.NORMAL + rs.getString("user") +
                " | " + Colors.BOLD + "Date: " + rs.getString("date");
    }

    private String returnMosconi(final ResultSet rs) throws SQLException {
        return Colors.BOLD + "Germano Mosconi disse: " + Colors.NORMAL + rs.getString("quote");
    }

    private boolean isQuoteModuleEnabled(final String chan) {
        return Config.getInstance().getConfigurationChannelList().get(chan).contains("quote");
    }
}