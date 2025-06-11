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
package com.l2jserver.datapack.ai.npc.Custodian.Bremmer;

import com.l2jserver.datapack.ai.npc.Custodian.Custodian;
import com.l2jserver.gameserver.model.Location;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class Bremmer extends Custodian {
	
	private static final TelPosList[] _position1 = new TelPosList[] {
		new TelPosList(1010024, new Location(-82445, 150788, -3129), 0),
		new TelPosList(1010025, new Location(-83331, 148563, -3148), 0),
		new TelPosList(1010026, new Location(-78405, 152723, -3181), 0),
		new TelPosList(1010028, new Location(-77460, 155995, -3194), 0),
		new TelPosList(1010027, new Location(-85138, 152749, -3160), 0)
	};
	
	private static final TelPosList[] _position2 = new TelPosList[] {
		new TelPosList(1010024, new Location(-82445, 150788, -3129), 0),
		new TelPosList(1010025, new Location(-83331, 148563, -3148), 0),
		new TelPosList(1010026, new Location(-78405, 152723, -3181), 0),
		new TelPosList(1010028, new Location(-77460, 155995, -3194), 0),
		new TelPosList(1010027, new Location(-85138, 152749, -3160), 0),
		new TelPosList(1010175, new Location(-75437, 168800, -3632), 500),
		new TelPosList(1010096, new Location(-63736, 101522, -3552), 500),
		new TelPosList(1010094, new Location(-88539, 83389, -2864), 500),
		new TelPosList(1010098, new Location(-49853, 147089, -2784), 500),
		new TelPosList(1010621, new Location(-16526, 208032, -3664), 500)
	};
	
	private static final int npcId = 35392;
	
	public Bremmer() {
		super(npcId);
		
		super.position1 = _position1;
		super.position2 = _position2;
	}
}