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
package com.l2jserver.datapack.ai.npc.Steward.AzitChamberlain;

import com.l2jserver.datapack.ai.npc.Steward.Steward;
import com.l2jserver.gameserver.model.Location;

/**
* @author Charus
* @version 2.6.3.0
*/
public class AzitChamberlain extends Steward {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010055, new Location(109721, -7394, -2800), 500),
		new TelPosList(1010121, new Location(64328, 26803, -3768), 500),
		new TelPosList(1010127, new Location(118509, -4779, -4000), 500),
		new TelPosList(1010618, new Location(124904, 61992, -3973), 500),
		new TelPosList(1010619, new Location(104426, 33746, -3825), 500),
		new TelPosList(1010014, new Location(85391, 16228, -3640), 500),
		new TelPosList(1010013, new Location(82323, 55466, -1480), 500)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010055, new Location(109721, -7394, -2800), 500),
		new TelPosList(1010121, new Location(64328, 26803, -3768), 500),
		new TelPosList(1010127, new Location(118509, -4779, -4000), 500),
		new TelPosList(1010618, new Location(124904, 61992, -3973), 500),
		new TelPosList(1010619, new Location(104426, 33746, -3825), 500),
		new TelPosList(1010014, new Location(85391, 16228, -3640), 500),
		new TelPosList(1010013, new Location(82323, 55466, -1480), 500),
		new TelPosList(1010144, new Location(114649, 11115, -5120), 500),
		new TelPosList(1010007, new Location(83336, 147972, -3404), 500),
		new TelPosList(1010023, new Location(146038, 30519, -2420), 500)
	};
	
	private static final int npcId = 35438;
	
	public AzitChamberlain() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnHi = "data/html/clanHallManager/azit_chamberlain001.htm";
		super.fnNotMyLord = "data/html/clanHallManager/azit_chamberlain002.htm";
		super.fnBanish = "data/html/clanHallManager/azit_chamberlain004.htm";
		super.fnAfterOpenGate = "data/html/clanHallManager/azit_chamberlain006.htm";
		super.fnAfterCloseGate = "data/html/clanHallManager/azit_chamberlain007.htm";
		super.fnAfterBanish = "data/html/clanHallManager/azit_chamberlain008.htm";
		super.fnNotEnoughAdena = "data/html/clanHallManager/azit_chamberlain010.htm";
		super.fnNoAuthority = "data/html/clanHallManager/azit_chamberlain017.htm";
		super.fnIsUnderSiege = "data/html/clanHallManager/azit_chamberlain018.htm";
		
		super.fnManageRegen = "data/html/clanHallManager/ol_mahum_AgitDeco_ar01.htm";
		super.fnManageEtc = "data/html/clanHallManager/ol_mahum_AgitDeco_ae01.htm";
	}
}