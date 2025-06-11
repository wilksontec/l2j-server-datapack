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
package com.l2jserver.datapack.ai.npc.Chamberlain.Alfred;

import com.l2jserver.datapack.ai.npc.Chamberlain.Chamberlain;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Alfred extends Chamberlain {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010695, new Location(153996, -50182, -2992), 0),
		new TelPosList(1010661, new Location(147728, -56331, -2776), 0),
		new TelPosList(1010685, new Location(153460, -70055, -3312), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010695, new Location(153996, -50182, -2992), 0),
		new TelPosList(1010661, new Location(147728, -56331, -2776), 0),
		new TelPosList(1010685, new Location(153460, -70055, -3312), 0),
		new TelPosList(1010609, new Location(144880, -113468, -2560), 500),
		new TelPosList(1010491, new Location(125740, -40864, -3736), 500),
		new TelPosList(1010492, new Location(146990, -67128, -3640), 500),
		new TelPosList(1010530, new Location(169018, -116303, -2432), 500),
		new TelPosList(1010568, new Location(165054, -47861, -3560), 500)
	};
	
	private static final int npcId = 35363;
	
	public Alfred() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnSetGate = "data/html/chamberlain/chamberlain_alfred005.htm";
		super.fnMyFortressStatus = "data/html/chamberlain/chamberlain_alfred070.htm";
		super.fnDoorStrengthen = "data/html/chamberlain/chamberlain_alfred053.htm";
		super.fnSetSlowZone = "data/html/chamberlain/chamberlain_alfred058.htm";
	}
}