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
package org.pircbotx;

import org.pircbotx.exception.IrcException;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 *
 * @author Leon
 */
public class NickservTest {
	@Test
	@SuppressWarnings("resource")
	public void nickservTest() throws IOException, IrcException {
		PircBotX bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.setNickservPassword("testpw")
				.buildConfiguration());
		InputParser inputParser = bot.getInputParser();

		assertFalse(bot.isNickservIdentified(), "Nickserv hasn't even started yet!");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :jibberish");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :You are now identified for PircBotX");
		assertTrue(bot.isNickservIdentified(), "Bot isn't identified even when nickserv");
	}

	@Test
	@SuppressWarnings("resource")
	public void nickservCustomMessageTest() throws IOException, IrcException {
		PircBotX bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.setNickservPassword("testpw")
				.setNickservOnSuccess("hello der")
				.buildConfiguration());
		InputParser inputParser = bot.getInputParser();

		assertFalse(bot.isNickservIdentified(), "Nickserv hasn't even started yet!");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :jibberish");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :hello der");
		assertTrue(bot.isNickservIdentified(), "Bot isn't identified even when nickserv");
	}

	@Test
	@SuppressWarnings("resource")
	public void nickservOtherNickTest() throws IOException, IrcException {
		PircBotX bot = new PircBotX(TestUtils.generateConfigurationBuilder()
				.setNickservPassword("testpw")
				.setNickservNick("somenick")
				.buildConfiguration());
		InputParser inputParser = bot.getInputParser();

		assertFalse(bot.isNickservIdentified(), "Nickserv hasn't even started yet!");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :jibberish");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":NickServ!services@swiftirc.net NOTICE PircBotXUser :");
		assertFalse(bot.isNickservIdentified(), "Nickserv identified too early");
		inputParser.handleLine(":somenick!services@swiftirc.net NOTICE PircBotXUser :You are now identified for PircBotX");
		assertTrue(bot.isNickservIdentified(), "Bot isn't identified even when nickserv");
	}
}
