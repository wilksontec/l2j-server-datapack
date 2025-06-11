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
package com.l2jserver.datapack.ai.npc.Custodian.Ron;

import com.l2jserver.datapack.ai.npc.Custodian.Custodian;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Ron extends Custodian {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010024, new Location(19025, 145245, -3107), 0),
		new TelPosList(1010026, new Location(21511, 145866, -3153), 0),
		new TelPosList(1010025, new Location(18891, 142365, -3051), 0),
		new TelPosList(1010028, new Location(17394, 147593, -3129), 0),
		new TelPosList(1010027, new Location(16582, 144130, -2960), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010024, new Location(19025, 145245, -3107), 0),
		new TelPosList(1010026, new Location(21511, 145866, -3153), 0),
		new TelPosList(1010025, new Location(18891, 142365, -3051), 0),
		new TelPosList(1010028, new Location(17394, 147593, -3129), 0),
		new TelPosList(1010027, new Location(16582, 144130, -2960), 0),
		new TelPosList(1010614, new Location(5106, 126916, -3664), 500),
		new TelPosList(1010113, new Location(47382, 111278, -2104), 500),
		new TelPosList(1010111, new Location(630, 179184, -3720), 500),
		new TelPosList(1010115, new Location(60374, 164301, -2856), 500),
		new TelPosList(1010036, new Location(17430, 170103, -3506), 500)
	};
	
	private static final int npcId = 35403;
	
	public Ron() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
	}
}