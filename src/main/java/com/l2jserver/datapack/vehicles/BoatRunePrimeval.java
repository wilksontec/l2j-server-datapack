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
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.vehicles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.BoatManager;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * @author DS
 */
public class BoatRunePrimeval implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(BoatRunePrimeval.class);
	
	// Time: 239s
	private static final VehiclePathPoint[] RUNE_TO_PRIMEVAL = {
		new VehiclePathPoint(32750, -39300, -3610, 180, 800),
		new VehiclePathPoint(27440, -39328, -3610, 250, 1000),
		new VehiclePathPoint(19616, -39360, -3610, 270, 1000),
		new VehiclePathPoint(3840, -38528, -3610, 270, 1000),
		new VehiclePathPoint(1664, -37120, -3610, 270, 1000),
		new VehiclePathPoint(896, -34560, -3610, 180, 1800),
		new VehiclePathPoint(832, -31104, -3610, 180, 180),
		new VehiclePathPoint(2240, -29132, -3610, 150, 1800),
		new VehiclePathPoint(4160, -27828, -3610, 150, 1800),
		new VehiclePathPoint(5888, -27279, -3610, 150, 1800),
		new VehiclePathPoint(7000, -27279, -3610, 150, 1800),
		new VehiclePathPoint(10342, -27279, -3610, 150, 1800)
	};
	
	// Time: 221s
	private static final VehiclePathPoint[] PRIMEVAL_TO_RUNE = {
		new VehiclePathPoint(15528, -27279, -3610, 180, 800),
		new VehiclePathPoint(22304, -29664, -3610, 290, 800),
		new VehiclePathPoint(33824, -26880, -3610, 290, 800),
		new VehiclePathPoint(38848, -21792, -3610, 240, 1200),
		new VehiclePathPoint(43424, -22080, -3610, 180, 1800),
		new VehiclePathPoint(44320, -25152, -3610, 180, 1800),
		new VehiclePathPoint(40576, -31616, -3610, 250, 800),
		new VehiclePathPoint(36819, -35315, -3610, 220, 800)
	};
	
	private static final VehiclePathPoint[] RUNE_DOCK = {
		new VehiclePathPoint(34381, -37680, -3610, 220, 800)
	};
	
	private static final VehiclePathPoint PRIMEVAL_DOCK = RUNE_TO_PRIMEVAL[RUNE_TO_PRIMEVAL.length - 1];
	
	private final L2BoatInstance _boat;
	private int _cycle = 0;
	private int _shoutCount = 0;
	
	private final CreatureSay ARRIVED_AT_RUNE;
	private final CreatureSay ARRIVED_AT_RUNE_2;
	private final CreatureSay LEAVING_RUNE;
	private final CreatureSay ARRIVED_AT_PRIMEVAL;
	private final CreatureSay ARRIVED_AT_PRIMEVAL_2;
	private final CreatureSay LEAVING_PRIMEVAL;
	private final CreatureSay BUSY_RUNE;
	
	public BoatRunePrimeval(L2BoatInstance boat) {
		_boat = boat;
		
		ARRIVED_AT_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.ARRIVED_AT_RUNE);
		ARRIVED_AT_RUNE_2 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_FOR_PRIMEVAL_3_MINUTES);
		LEAVING_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_RUNE_FOR_PRIMEVAL_NOW);
		ARRIVED_AT_PRIMEVAL = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_ARRIVED_AT_PRIMEVAL);
		ARRIVED_AT_PRIMEVAL_2 = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_FOR_RUNE_3_MINUTES);
		LEAVING_PRIMEVAL = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_LEAVING_PRIMEVAL_FOR_RUNE_NOW);
		BUSY_RUNE = new CreatureSay(0, Say2.BOAT, 801, SystemMessageId.FERRY_FROM_PRIMEVAL_TO_RUNE_DELAYED);
	}
	
	@Override
	public void run() {
		try {
			switch (_cycle) {
				case 0:
					BoatManager.getInstance().dockShip(BoatManager.RUNE_HARBOR, false);
					BoatManager.getInstance().broadcastPackets(RUNE_DOCK[0], PRIMEVAL_DOCK, LEAVING_RUNE, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					_boat.payForRide(8925, 1, 34513, -38009, -3640);
					_boat.executePath(RUNE_TO_PRIMEVAL);
					break;
				case 1:
					BoatManager.getInstance().broadcastPackets(PRIMEVAL_DOCK, RUNE_DOCK[0], ARRIVED_AT_PRIMEVAL, ARRIVED_AT_PRIMEVAL_2, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 180000);
					break;
				case 2:
					BoatManager.getInstance().broadcastPackets(PRIMEVAL_DOCK, RUNE_DOCK[0], LEAVING_PRIMEVAL, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					_boat.payForRide(8924, 1, 10447, -24982, -3664);
					_boat.executePath(PRIMEVAL_TO_RUNE);
					break;
				case 3:
					if (BoatManager.getInstance().dockBusy(BoatManager.RUNE_HARBOR)) {
						if (_shoutCount == 0) {
							BoatManager.getInstance().broadcastPacket(RUNE_DOCK[0], PRIMEVAL_DOCK, BUSY_RUNE);
						}
						
						_shoutCount++;
						if (_shoutCount > 35) {
							_shoutCount = 0;
						}
						
						ThreadPoolManager.getInstance().scheduleGeneral(this, 5000);
						return;
					}
					_boat.executePath(RUNE_DOCK);
					break;
				case 4:
					BoatManager.getInstance().dockShip(BoatManager.RUNE_HARBOR, true);
					BoatManager.getInstance().broadcastPackets(RUNE_DOCK[0], PRIMEVAL_DOCK, ARRIVED_AT_RUNE, ARRIVED_AT_RUNE_2, Sound.ITEMSOUND_SHIP_ARRIVAL_DEPARTURE.withObject(_boat));
					ThreadPoolManager.getInstance().scheduleGeneral(this, 180000);
					break;
			}
			_shoutCount = 0;
			_cycle++;
			if (_cycle > 4) {
				_cycle = 0;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		final L2BoatInstance boat = BoatManager.getInstance().getNewBoat(5, 34381, -37680, -3610, 40785);
		if (boat != null) {
			boat.registerEngine(new BoatRunePrimeval(boat));
			boat.runEngine(180000);
			BoatManager.getInstance().dockShip(BoatManager.RUNE_HARBOR, true);
		}
	}
}
