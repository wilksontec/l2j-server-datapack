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
package com.l2jserver.datapack.ai.npc.Chamberlain.August;

import com.l2jserver.datapack.ai.npc.Chamberlain.Chamberlain;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class August extends Chamberlain {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010697, new Location(76358, -145548, -1176), 0),
		new TelPosList(1010676, new Location(87379, -142322, -1336), 0),
		new TelPosList(1010683, new Location(105007, -140874, -3360), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010697, new Location(76358, -145548, -1176), 0),
		new TelPosList(1010676, new Location(87379, -142322, -1336), 0),
		new TelPosList(1010683, new Location(105007, -140874, -3360), 0),
		new TelPosList(1010597, new Location(144706, -173223, -1520), 500),
		new TelPosList(1010572, new Location(111965, -154172, -1528), 500),
		new TelPosList(1010569, new Location(68693, -110438, -1946), 500),
		new TelPosList(1010570, new Location(113903, -108752, -860), 500),
		new TelPosList(1010571, new Location(47692, -115745, -3744), 500)
	};
	
	private static final int npcId = 35555;
	
	public August() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnSetGate = "data/html/chamberlain/chamberlain_alfred005.htm";
		super.fnMyFortressStatus = "data/html/chamberlain/chamberlain_neurath070.htm";
		super.fnDoorStrengthen = "data/html/chamberlain/chamberlain_alfred053.htm";
		super.fnSetSlowZone = "data/html/chamberlain/chamberlain_alfred058.htm";
	}
}