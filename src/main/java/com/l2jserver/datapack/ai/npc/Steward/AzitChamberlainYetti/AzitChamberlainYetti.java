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
package com.l2jserver.datapack.ai.npc.Steward.AzitChamberlainYetti;

import com.l2jserver.datapack.ai.npc.Steward.Steward;
import com.l2jserver.gameserver.model.Location;

/**
* @author Charus
* @version 2.6.3.0
*/
public class AzitChamberlainYetti extends Steward {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010568, new Location(165054, -47861, -3560), 500),
		new TelPosList(1010609, new Location(144880, -113468, -2560), 500),
		new TelPosList(1010529, new Location(191257, -59388, -2898), 500),
		new TelPosList(1010199, new Location(148024, -55281, -2728), 500),
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010568, new Location(165054, -47861, -3560), 500),
		new TelPosList(1010609, new Location(144880, -113468, -2560), 500),
		new TelPosList(1010529, new Location(191257, -59388, -2898), 500),
		new TelPosList(1010549, new Location(132997, -60608, -2960), 500),
		new TelPosList(1010550, new Location(106349, -61870, -2904), 500),
		new TelPosList(1010575, new Location(106414, -87799, -2949), 500),
		new TelPosList(1010199, new Location(148024, -55281, -2728), 500),
		new TelPosList(1010200, new Location(43835, -47749, -792), 500)
	};
	
	private static final int npcId = 35605;
	
	public AzitChamberlainYetti() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnHi = "data/html/clanHallManager/azit_chamberlain_yetti001.htm";
		super.fnNotMyLord = "data/html/clanHallManager/azit_chamberlain_yetti002.htm";
		super.fnBanish = "data/html/clanHallManager/azit_chamberlain_yetti004.htm";
		super.fnAfterOpenGate = "data/html/clanHallManager/azit_chamberlain_yetti006.htm";
		super.fnAfterCloseGate = "data/html/clanHallManager/azit_chamberlain_yetti007.htm";
		super.fnAfterBanish = "data/html/clanHallManager/azit_chamberlain_yetti008.htm";
		super.fnNotEnoughAdena = "data/html/clanHallManager/azit_chamberlain_yetti010.htm";
		super.fnNoAuthority = "data/html/clanHallManager/azit_chamberlain_yetti017.htm";
		super.fnIsUnderSiege = "data/html/clanHallManager/azit_chamberlain_yetti018.htm";
		
		super.fnManageRegen = "data/html/clanHallManager/ol_mahum_AgitDeco_ar01.htm";
		super.fnManageEtc = "data/html/clanHallManager/ol_mahum_AgitDeco_ae01.htm";
	}
}