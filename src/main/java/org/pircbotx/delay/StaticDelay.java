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
package org.pircbotx.delay;

import static com.google.common.base.Preconditions.checkArgument;

public class StaticDelay implements Delay {
	
	private long delay;
	
	public StaticDelay( long delay) {
		setDelay(delay);
	}
	

	@Override
	public long getDelay() {
		return delay;
	}
	
	public void setDelay(long delay) {
		checkArgument(delay >= 0, "Delay may not be negative");
		this.delay = delay;
	}

}
