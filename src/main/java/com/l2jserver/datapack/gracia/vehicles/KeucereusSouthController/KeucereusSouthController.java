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
package com.l2jserver.datapack.gracia.vehicles.KeucereusSouthController;

import com.l2jserver.datapack.gracia.vehicles.AirShipController;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.VehiclePathPoint;

public final class KeucereusSouthController extends AirShipController {
	private static final int DOCK_ZONE = 50603;
	private static final int LOCATION = 100;
	private static final int CONTROLLER_ID = 32517;
	
	private static final VehiclePathPoint[] ARRIVAL = {
		new VehiclePathPoint(-185312, 246544, 2500),
		new VehiclePathPoint(-185312, 246544, 1336)
	};
	
	private static final VehiclePathPoint[] DEPART = {
		new VehiclePathPoint(-185312, 246544, 1700, 280, 2000),
		new VehiclePathPoint(-186900, 251699, 1700, 280, 2000)
	};
	
	private static final VehiclePathPoint[][] TELEPORTS = {
		{
			new VehiclePathPoint(-185312, 246544, 1700, 280, 2000),
			new VehiclePathPoint(-186900, 251699, 1700, 280, 2000),
			new VehiclePathPoint(-186373, 234000, 2500, 0, 0)
		},
		{
			new VehiclePathPoint(-185312, 246544, 1700, 280, 2000),
			new VehiclePathPoint(-186900, 251699, 1700, 280, 2000),
			new VehiclePathPoint(-206692, 220997, 3000, 0, 0)
		},
		{
			new VehiclePathPoint(-185312, 246544, 1700, 280, 2000),
			new VehiclePathPoint(-186900, 251699, 1700, 280, 2000),
			new VehiclePathPoint(-235693, 248843, 5100, 0, 0)
		}
	};
	
	private static final int[] FUEL = {
		0,
		50,
		100
	};
	
	public KeucereusSouthController() {
		bindStartNpc(CONTROLLER_ID);
		bindFirstTalk(CONTROLLER_ID);
		bindTalk(CONTROLLER_ID);
		
		_dockZone = DOCK_ZONE;
		bindEnterZone(DOCK_ZONE);
		bindExitZone(DOCK_ZONE);
		
		_shipSpawnX = -184527;
		_shipSpawnY = 243611;
		_shipSpawnZ = 3000;
		
		_locationId = LOCATION;
		_arrivalPath = ARRIVAL;
		_departPath = DEPART;
		_teleportsTable = TELEPORTS;
		_fuelTable = FUEL;
		
		_oustLoc = new Location(-186148, 246296, 1360);
		
		_movieId = 1000;
		
		validityCheck();
	}
}