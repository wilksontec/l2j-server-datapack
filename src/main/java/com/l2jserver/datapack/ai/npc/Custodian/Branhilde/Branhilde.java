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
package com.l2jserver.datapack.ai.npc.Custodian.Branhilde;

import com.l2jserver.datapack.ai.npc.Custodian.Custodian;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Branhilde extends Custodian {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010024, new Location(147728, -56331, -2776), 0),
		new TelPosList(1010025, new Location(147731, -58930, -2976), 0),
		new TelPosList(1010026, new Location(150561, -57489, -2976), 0),
		new TelPosList(1010027, new Location(144866, -57464, -2976), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010024, new Location(147728, -56331, -2776), 0),
		new TelPosList(1010025, new Location(147731, -58930, -2976), 0),
		new TelPosList(1010026, new Location(150561, -57489, -2976), 0),
		new TelPosList(1010027, new Location(144866, -57464, -2976), 0),
		new TelPosList(1010491, new Location(125740, -40864, -3736), 500),
		new TelPosList(1010492, new Location(146990, -67128, -3640), 500),
		new TelPosList(1010530, new Location(169018, -116303, -2432), 500),
		new TelPosList(1010568, new Location(165054, -47861, -3560), 500),
		new TelPosList(1010609, new Location(144880, -113468, -2560), 500)
	};
	
	private static final int npcId = 35467;
	
	public Branhilde() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
	}
}