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
package com.l2jserver.datapack.ai.npc.Steward.OlMahumTamutak;

import com.l2jserver.datapack.ai.npc.Steward.Steward;
import com.l2jserver.gameserver.model.Location;

/**
* @author Charus
* @version 2.6.3.0
*/
public class OlMahumTamutak extends Steward {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010035, new Location(46467, 126885, -3720), 500),
		new TelPosList(1010066, new Location(43534, 164260, -2984), 500),
		new TelPosList(1010033, new Location(5941, 125455, -3400), 500),
		new TelPosList(1010034, new Location(37566, 148224, -3699), 500),
		new TelPosList(1010006, new Location(15472, 142880, -2699), 500),
		new TelPosList(1010036, new Location(17430, 170103, -3506), 500)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010035, new Location(46467, 126885, -3720), 500),
		new TelPosList(1010066, new Location(43534, 164260, -2984), 500),
		new TelPosList(1010033, new Location(5941, 125455, -3400), 500),
		new TelPosList(1010034, new Location(37566, 148224, -3699), 500),
		new TelPosList(1010006, new Location(15472, 142880, -2699), 500),
		new TelPosList(1010036, new Location(17430, 170103, -3506), 500),
		new TelPosList(1010041, new Location(16050, 114176, -3576), 500),
		new TelPosList(1010042, new Location(69770, 126371, -3800), 500),
		new TelPosList(1010005, new Location(-12787, 122779, -3114), 500),
		new TelPosList(1010007, new Location(83336, 147972, -3404), 500)
	};
	
	private static final int npcId = 35383;
	
	public OlMahumTamutak() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
		
		super.fnHi = "data/html/clanHallManager/ol_mahum_steward_tamutak001.htm";
		super.fnNotMyLord = "data/html/clanHallManager/ol_mahum_steward_tamutak002.htm";
		super.fnBanish = "data/html/clanHallManager/ol_mahum_steward_tamutak004.htm";
		super.fnAfterOpenGate = "data/html/clanHallManager/ol_mahum_steward_tamutak006.htm";
		super.fnAfterCloseGate = "data/html/clanHallManager/ol_mahum_steward_tamutak007.htm";
		super.fnAfterBanish = "data/html/clanHallManager/ol_mahum_steward_tamutak008.htm";
		super.fnNotEnoughAdena = "data/html/clanHallManager/ol_mahum_steward_tamutak010.htm";
		super.fnNoAuthority = "data/html/clanHallManager/ol_mahum_steward_tamutak017.htm";
		super.fnIsUnderSiege = "data/html/clanHallManager/ol_mahum_steward_tamutak018.htm";
		
		super.fnManageRegen = "data/html/clanHallManager/ol_mahum_AgitDeco_br01.htm";
		super.fnManageEtc = "data/html/clanHallManager/ol_mahum_AgitDeco_be01.htm";
	}
}