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
package com.l2jserver.datapack.ai.npc.Teleporter.Jasmine;

import com.l2jserver.datapack.ai.npc.Teleporter.Teleporter;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Jasmine extends Teleporter {
	
	private static final TelPosList[] _position = new TelPosList[] {
		new TelPosList(1010005, new Location(-12787, 122779, -3114), 10000),
		new TelPosList(1010004, new Location(-80684, 149770, -3043), 18000),
		new TelPosList(1010158, new Location(115120, -178224, -917), 22000),
		new TelPosList(1010001, new Location(-84141, 244623, -3729), 24000),
		new TelPosList(1010157, new Location(-45158, -112583, -236), 13000),
		new TelPosList(1010648, new Location(-117251, 46771, 380), 13000),
		new TelPosList(1010589, new Location(-22224, 14168, -3232), 890),
		new TelPosList(1010164, new Location(-21966, 40544, -3192), 1100),
		new TelPosList(1010166, new Location(-61095, 75104, -3383), 3600),
		new TelPosList(1010612, new Location(-10612, 75881, -3592), 1700)
	};
	
	private static final TelPosList[] _positionNoblessNeedItemField = new TelPosList[] {
		new TelPosList(1010506, new Location(-87328, 142266, -3640), 1),
		new TelPosList(1010507, new Location(73579, 142709, -3768), 1),
		new TelPosList(1010613, new Location(-18415, 85624, -3680), 1),
		new TelPosList(1010590, new Location(-14129, 27094, -3680), 1),
		new TelPosList(1010489, new Location(-49185, 49441, -5912), 1),
		new TelPosList(1010053, new Location(146440, 46723, -3400), 1)
	};
	
	private static final TelPosList[] _positionNoblessNoItemField = new TelPosList[] {
		new TelPosList(1010506, new Location(-87328, 142266, -3640), 1000),
		new TelPosList(1010507, new Location(73579, 142709, -3768), 1000),
		new TelPosList(1010613, new Location(-18415, 85624, -3680), 1000),
		new TelPosList(1010590, new Location(-14129, 27094, -3680), 1000),
		new TelPosList(1010489, new Location(-49185, 49441, -5912), 1000),
		new TelPosList(1010053, new Location(146440, 46723, -3400), 1000)
	};
	
	private static final TelPosList[] _positionNewbie = new TelPosList[] {
		new TelPosList(1010158, new Location(115120, -178224, -917), 1),
		new TelPosList(1010001, new Location(-84141, 244623, -3729), 1),
		new TelPosList(1010155, new Location( 46951, 51550, -2976), 1),
		new TelPosList(1010157, new Location(-45158, -112583, -236), 1)
	};
	
	private static final int npcId = 30134;
	
	public Jasmine() {
		super(npcId);
		
		super.position = _position;
		super.positionNoblessNeedItemField = _positionNoblessNeedItemField;
		super.positionNoblessNoItemField = _positionNoblessNoItemField;
		super.positionNewbie = _positionNewbie;
	}
}