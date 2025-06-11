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
package com.l2jserver.datapack.ai.npc.Steward.ChamberlainBandello;

import com.l2jserver.datapack.ai.npc.Steward.Steward;
import com.l2jserver.gameserver.model.Location;

/**
* @author Charus
* @version 2.6.3.0
*/
public class ChamberlainBandello extends Steward {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010592, new Location(52107, -54328, -3158), 500),
		new TelPosList(1010537, new Location(43805, -88010, -2780), 500),
		new TelPosList(1010539, new Location(62084, -40935, -2802), 500),
		new TelPosList(1010200, new Location(43835, -47749, -792), 500)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010592, new Location(52107, -54328, -3158), 500),
		new TelPosList(1010537, new Location(43805, -88010, -2780), 500),
		new TelPosList(1010539, new Location(62084, -40935, -2802), 500),
		new TelPosList(1010600, new Location(65307, -71445, -3696), 500),
		new TelPosList(1010565, new Location(69340, -50203, -3314), 500),
		new TelPosList(1010575, new Location(106414, -87799, -2949), 500),
		new TelPosList(1010199, new Location(148024, -55281, -2728), 500),
		new TelPosList(1010200, new Location(43835, -47749, -792), 500),
		new TelPosList(1010023, new Location(144635, 26664, -2220), 500),
		new TelPosList(1010706, new Location(76911, -55295, -5824), 500)
	};
	
	private static final int npcId = 35640;
	
	public ChamberlainBandello() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnHi = "data/html/clanHallManager/chamberlain_bandello001.htm";
		super.fnNotMyLord = "data/html/clanHallManager/chamberlain_bandello002.htm";
		super.fnBanish = "data/html/clanHallManager/chamberlain_bandello004.htm";
		super.fnAfterOpenGate = "data/html/clanHallManager/chamberlain_bandello006.htm";
		super.fnAfterCloseGate = "data/html/clanHallManager/chamberlain_bandello007.htm";
		super.fnAfterBanish = "data/html/clanHallManager/chamberlain_bandello008.htm";
		super.fnNotEnoughAdena = "data/html/clanHallManager/chamberlain_bandello010.htm";
		super.fnNoAuthority = "data/html/clanHallManager/chamberlain_bandello017.htm";
		super.fnIsUnderSiege = "data/html/clanHallManager/chamberlain_bandello018.htm";
		
		super.fnManageRegen = "data/html/clanHallManager/ol_mahum_AgitDeco_ar01.htm";
		super.fnManageEtc = "data/html/clanHallManager/ol_mahum_AgitDeco_ae01.htm";
	}
}