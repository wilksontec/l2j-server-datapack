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
package com.l2jserver.datapack.ai.npc.Steward.Biggerstaff;

import com.l2jserver.datapack.ai.npc.Steward.Steward;
import com.l2jserver.gameserver.model.Location;

/**
* @author Charus
* @version 2.6.3.0
*/
public class Biggerstaff extends Steward {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010060, new Location(138879, -1860, -4050), 500),
		new TelPosList(1010553, new Location(181737, 46469, -4276), 500),
		new TelPosList(1010585, new Location(142065, 81300, -3000), 500),
		new TelPosList(1010023, new Location(144635, 26664, -2220), 500)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010060, new Location(138879, -1860, -4050), 500),
		new TelPosList(1010553, new Location(181737, 46469, -4276), 500),
		new TelPosList(1010585, new Location(142065, 81300, -3000), 500),
		new TelPosList(1010023, new Location(144635, 26664, -2220), 500),
		new TelPosList(1010604, new Location(184742, 19745, -3168), 500),
		new TelPosList(1010144, new Location(114649, 11115, -5120), 500),
		new TelPosList(1010020, new Location(117304, 76318, -2670), 500),
		new TelPosList(1010702, new Location(183985, 61424, -3992), 500),
		new TelPosList(1010703, new Location(191754, 56760, -7624), 500)
	};
	
	private static final int npcId = 35421;
	
	public Biggerstaff() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnHi = "data/html/clanHallManager/steward_biggerstaff001.htm";
		super.fnNotMyLord = "data/html/clanHallManager/steward_biggerstaff002.htm";
		super.fnBanish = "data/html/clanHallManager/steward_biggerstaff004.htm";
		super.fnAfterOpenGate = "data/html/clanHallManager/steward_biggerstaff006.htm";
		super.fnAfterCloseGate = "data/html/clanHallManager/steward_biggerstaff007.htm";
		super.fnAfterBanish = "data/html/clanHallManager/steward_biggerstaff008.htm";
		super.fnNotEnoughAdena = "data/html/clanHallManager/steward_biggerstaff010.htm";
		super.fnNoAuthority = "data/html/clanHallManager/steward_biggerstaff017.htm";
		super.fnIsUnderSiege = "data/html/clanHallManager/steward_biggerstaff018.htm";
		
		super.fnManageRegen = "data/html/clanHallManager/ol_mahum_AgitDeco_ar01.htm";
		super.fnManageEtc = "data/html/clanHallManager/ol_mahum_AgitDeco_ae01.htm";
	}
}