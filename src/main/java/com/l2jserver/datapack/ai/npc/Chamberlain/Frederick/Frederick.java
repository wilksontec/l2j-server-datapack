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
package com.l2jserver.datapack.ai.npc.Chamberlain.Frederick;

import com.l2jserver.datapack.ai.npc.Chamberlain.Chamberlain;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Frederick extends Chamberlain {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010696, new Location(27400, -49180, -1320), 0),
		new TelPosList(1010671, new Location(43889, -49101, -792), 0),
		new TelPosList(1010668, new Location(71814, -57054, -3088), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010696, new Location(27400, -49180, -1320), 0),
		new TelPosList(1010671, new Location(43889, -49101, -792), 0),
		new TelPosList(1010668, new Location(71814, -57054, -3088), 0),
		new TelPosList(1010592, new Location(52107, -54328, -3158), 500),
		new TelPosList(1010537, new Location(43805, -88010, -2780), 500),
		new TelPosList(1010565, new Location(69340, -50203, -3314), 500),
		new TelPosList(1010600, new Location(65307, -71445, -3696), 500),
		new TelPosList(1010575, new Location(106414, -87799, -2949), 500),
		new TelPosList(1010706, new Location(76911, -55295, -5824), 500)
	};
	
	private static final int npcId = 35509;
	
	public Frederick() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnSetGate = "data/html/chamberlain/chamberlain_frederick005.htm";
		super.fnMyFortressStatus = "data/html/chamberlain/chamberlain_alfred070.htm";
		super.fnDoorStrengthen = "data/html/chamberlain/chamberlain_frederick053.htm";
		super.fnSetSlowZone = "data/html/chamberlain/chamberlain_alfred058.htm";
	}
}