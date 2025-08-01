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
package com.l2jserver.datapack.gracia.vehicles;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.AirShipManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.VehiclePathPoint;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2AirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.type.L2ScriptZone;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

public abstract class AirShipController extends Quest {
	private static final Logger LOG = LoggerFactory.getLogger(AirShipController.class);
	
	protected final class DecayTask implements Runnable {
		@Override
		public void run() {
			if (_dockedShip != null) {
				_dockedShip.deleteMe();
			}
		}
	}
	
	protected final class DepartTask implements Runnable {
		@Override
		public void run() {
			if ((_dockedShip != null) && _dockedShip.isInDock() && !_dockedShip.isMoving()) {
				if (_departPath != null) {
					_dockedShip.executePath(_departPath);
				} else {
					_dockedShip.deleteMe();
				}
			}
		}
	}
	
	protected int _dockZone = 0;
	protected int _shipSpawnX = 0;
	protected int _shipSpawnY = 0;
	
	protected int _shipSpawnZ = 0;
	
	protected int _shipHeading = 0;
	protected Location _oustLoc = null;
	protected int _locationId = 0;
	
	protected VehiclePathPoint[] _arrivalPath = null;
	protected VehiclePathPoint[] _departPath = null;
	
	protected VehiclePathPoint[][] _teleportsTable = null;
	
	protected int[] _fuelTable = null;
	
	protected int _movieId = 0;
	
	protected boolean _isBusy = false;
	protected L2ControllableAirShipInstance _dockedShip = null;
	private final Runnable _decayTask = new DecayTask();
	
	private final Runnable _departTask = new DepartTask();
	
	private Future<?> _departSchedule = null;
	
	private NpcSay _arrivalMessage = null;
	private static final int DEPART_INTERVAL = 300000; // 5 min
	private static final int LICENSE = 13559;
	
	private static final int STARSTONE = 13277;
	private static final int SUMMON_COST = 5;
	
	private static final SystemMessage SM_NEED_MORE = SystemMessage.getSystemMessage(SystemMessageId.THE_AIRSHIP_NEED_MORE_S1).addItemName(STARSTONE);
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if ("summon".equalsIgnoreCase(event)) {
			if (_dockedShip != null) {
				if (_dockedShip.isOwner(player)) {
					player.sendPacket(SystemMessageId.THE_AIRSHIP_IS_ALREADY_EXISTS);
				}
				return null;
			}
			if (_isBusy) {
				player.sendPacket(SystemMessageId.ANOTHER_AIRSHIP_ALREADY_SUMMONED);
				return null;
			}
			if (!player.hasClanPrivilege(ClanPrivilege.CL_SUMMON_AIRSHIP)) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_NO_PRIVILEGES);
				return null;
			}
			int ownerId = player.getClanId();
			if (!AirShipManager.getInstance().hasAirShipLicense(ownerId)) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_NEED_LICENSE_TO_SUMMON);
				return null;
			}
			if (AirShipManager.getInstance().hasAirShip(ownerId)) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_ALREADY_USED);
				return null;
			}
			if (!player.destroyItemByItemId("AirShipSummon", STARSTONE, SUMMON_COST, npc, true)) {
				player.sendPacket(SM_NEED_MORE);
				return null;
			}
			
			_isBusy = true;
			final L2AirShipInstance ship = AirShipManager.getInstance().getNewAirShip(_shipSpawnX, _shipSpawnY, _shipSpawnZ, _shipHeading, ownerId);
			if (ship != null) {
				if (_arrivalPath != null) {
					ship.executePath(_arrivalPath);
				}
				
				if (_arrivalMessage == null) {
					_arrivalMessage = new NpcSay(npc.getObjectId(), Say2.NPC_SHOUT, npc.getId(), NpcStringId.THE_AIRSHIP_HAS_BEEN_SUMMONED_IT_WILL_AUTOMATICALLY_DEPART_IN_5_MINUTES);
				}
				
				npc.broadcastPacket(_arrivalMessage);
			} else {
				_isBusy = false;
			}
			
			return null;
		} else if ("board".equalsIgnoreCase(event)) {
			if (player.isTransformed()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED);
				return null;
			} else if (player.isStunned()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED);
				return null;
			} else if (player.isDead() || player.isFakeDeath()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD);
				return null;
			} else if (player.isFishing()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING);
				return null;
			} else if (player.isInCombat()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE);
				return null;
			} else if (player.isInDuel()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL);
				return null;
			} else if (player.isSitting()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING);
				return null;
			} else if (player.isCastingNow()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING);
				return null;
			} else if (player.isCursedWeaponEquipped()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
				return null;
			} else if (player.isCombatFlagEquipped()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG);
				return null;
			} else if (player.hasSummon() || player.isMounted()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED);
				return null;
			} else if (player.isFlyingMounted()) {
				player.sendPacket(SystemMessageId.YOU_CANNOT_BOARD_NOT_MEET_REQUEIREMENTS);
				return null;
			}
			
			if (_dockedShip != null) {
				_dockedShip.addPassenger(player);
			}
			
			return null;
		} else if ("register".equalsIgnoreCase(event)) {
			if ((player.getClan() == null) || (player.getClan().getLevel() < 5)) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_NEED_CLANLVL_5_TO_SUMMON);
				return null;
			}
			if (!player.isClanLeader()) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_NO_PRIVILEGES);
				return null;
			}
			final int ownerId = player.getClanId();
			if (AirShipManager.getInstance().hasAirShipLicense(ownerId)) {
				player.sendPacket(SystemMessageId.THE_AIRSHIP_SUMMON_LICENSE_ALREADY_ACQUIRED);
				return null;
			}
			if (!player.destroyItemByItemId("AirShipLicense", LICENSE, 1, npc, true)) {
				player.sendPacket(SM_NEED_MORE);
				return null;
			}
			
			AirShipManager.getInstance().registerLicense(ownerId);
			player.sendPacket(SystemMessageId.THE_AIRSHIP_SUMMON_LICENSE_ENTERED);
			return null;
		} else {
			return event;
		}
	}
	
	@Override
	public void onEnterZone(L2Character character, L2ZoneType zone) {
		if (character instanceof L2ControllableAirShipInstance airship) {
			if (_dockedShip == null) {
				_dockedShip = airship;
				_dockedShip.setInDock(_dockZone);
				_dockedShip.setOustLoc(_oustLoc);
				
				// Ship is not empty - display movie to passengers and dock
				if (!_dockedShip.isEmpty()) {
					if (_movieId != 0) {
						for (L2PcInstance passenger : _dockedShip.getPassengers()) {
							if (passenger != null) {
								passenger.showQuestMovie(_movieId);
							}
						}
					}
					
					ThreadPoolManager.getInstance().scheduleGeneral(_decayTask, 1000);
				} else {
					_departSchedule = ThreadPoolManager.getInstance().scheduleGeneral(_departTask, DEPART_INTERVAL);
				}
			}
		}
	}
	
	@Override
	public void onExitZone(L2Character character, L2ZoneType zone) {
		if (character instanceof L2ControllableAirShipInstance) {
			if (character.equals(_dockedShip)) {
				if (_departSchedule != null) {
					_departSchedule.cancel(false);
					_departSchedule = null;
				}
				
				_dockedShip.setInDock(0);
				_dockedShip = null;
				_isBusy = false;
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return npc.getId() + ".htm";
	}
	
	protected void validityCheck() {
		L2ScriptZone zone = ZoneManager.getInstance().getZoneById(_dockZone, L2ScriptZone.class);
		if (zone == null) {
			LOG.warn("Invalid zone {}, controller disabled", _dockZone);
			_isBusy = true;
			return;
		}
		
		VehiclePathPoint p;
		if (_arrivalPath != null) {
			if (_arrivalPath.length == 0) {
				LOG.warn("Zero arrival path length.");
				_arrivalPath = null;
			} else {
				p = _arrivalPath[_arrivalPath.length - 1];
				if (!zone.isInsideZone(p.getLocation())) {
					LOG.warn("Arrival path finish point ({},{},{}) not in zone {}", p.getX(), p.getY(), p.getZ(), _dockZone);
					_arrivalPath = null;
				}
			}
		}
		if (_arrivalPath == null) {
			if (!ZoneManager.getInstance().getZoneById(_dockZone, L2ScriptZone.class).isInsideZone(_shipSpawnX, _shipSpawnY, _shipSpawnZ)) {
				LOG.warn("Arrival path is null and spawn point not in zone {}, controller disabled", _dockZone);
				_isBusy = true;
				return;
			}
		}
		
		if (_departPath != null) {
			if (_departPath.length == 0) {
				LOG.warn("Zero depart path length.");
				_departPath = null;
			} else {
				p = _departPath[_departPath.length - 1];
				if (zone.isInsideZone(p.getLocation())) {
					LOG.warn("Departure path finish point ({},{},{}) in zone {}", p.getX(), p.getY(), p.getZ(), _dockZone);
					_departPath = null;
				}
			}
		}
		
		if (_teleportsTable != null) {
			if (_fuelTable == null) {
				LOG.warn("Fuel consumption not defined.");
			} else {
				if (_teleportsTable.length != _fuelTable.length) {
					LOG.warn("Fuel consumption not match teleport list.");
				} else {
					AirShipManager.getInstance().registerAirShipTeleportList(_dockZone, _locationId, _teleportsTable, _fuelTable);
				}
			}
		}
	}
}