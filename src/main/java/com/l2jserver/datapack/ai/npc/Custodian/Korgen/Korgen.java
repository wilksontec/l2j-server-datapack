/*
 * Copyright © 2004-2025 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.ai.npc.Custodian.Korgen;

import com.l2jserver.datapack.ai.npc.Custodian.Custodian;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Korgen extends Custodian {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010024, new Location(147450, 28081, -2294), 0),
		new TelPosList(1010026, new Location(151950, 25094, -2172), 0),
		new TelPosList(1010027, new Location(142593, 26344, -2425), 0),
		new TelPosList(1010028, new Location(147503, 32299, -2501), 0),
		new TelPosList(1010025, new Location(147465, 20737, -2130), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010024, new Location(147450, 28081, -2294), 0),
		new TelPosList(1010026, new Location(151950, 25094, -2172), 0),
		new TelPosList(1010027, new Location(142593, 26344, -2425), 0),
		new TelPosList(1010028, new Location(147503, 32299, -2501), 0),
		new TelPosList(1010025, new Location(147465, 20737, -2130), 0),
		new TelPosList(1010127, new Location(106517, -2871, -3454), 500),
		new TelPosList(1010618, new Location(124904, 61992, -3973), 500),
		new TelPosList(1010619, new Location(104426, 33746, -3825), 500),
		new TelPosList(1010060, new Location(155310, -16339, -3320), 500),
		new TelPosList(1010585, new Location(142065, 81300, -3000), 500),
		new TelPosList(1010607, new Location(166182, 91560, -3168), 500),
		new TelPosList(1010137, new Location(181726, -7524, -3464), 500),
		new TelPosList(1010192, new Location(168779, -18790, -3184), 500),
		new TelPosList(1010604, new Location(184742, 19745, -3168), 500),
		new TelPosList(1010143, new Location(168217, 37990, -4072), 500),
		new TelPosList(1010144, new Location(114649, 11115, -5120), 500),
		new TelPosList(1010702, new Location(183985, 61424, -3992), 500),
		new TelPosList(1010703, new Location(191754, 56760, -7624), 500)
	};
	
	private static final int npcId = 35447;
	
	public Korgen() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
	}
}