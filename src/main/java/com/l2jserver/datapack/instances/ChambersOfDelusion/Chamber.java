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
package com.l2jserver.datapack.instances.ChambersOfDelusion;

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcEventReceived;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcSkillFinished;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.Earthquake;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Chambers of Delusion superclass.
 * @author GKR
 */
public abstract class Chamber extends AbstractInstance {
	protected class CDWorld extends InstanceWorld {
		protected int currentRoom;
		protected final L2Party partyInside;
		protected final ScheduledFuture<?> _banishTask;
		protected ScheduledFuture<?> _roomChangeTask;
		
		protected CDWorld(L2Party party) {
			currentRoom = 0;
			partyInside = party;
			_banishTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new BanishTask(), 60000, 60000);
		}
		
		protected L2Party getPartyInside() {
			return partyInside;
		}
		
		protected void scheduleRoomChange(boolean bossRoom) {
			final Instance inst = InstanceManager.getInstance().getInstance(getInstanceId());
			final long nextInterval = bossRoom ? 60000L : (ROOM_CHANGE_INTERVAL + getRandom(ROOM_CHANGE_RANDOM_TIME)) * 1000L;
			
			// Schedule next room change only if remaining time is enough
			if ((inst.getInstanceEndTime() - System.currentTimeMillis()) > nextInterval) {
				_roomChangeTask = ThreadPoolManager.getInstance().scheduleGeneral(new ChangeRoomTask(), nextInterval - 5000);
			}
		}
		
		protected void stopBanishTask() {
			_banishTask.cancel(true);
		}
		
		protected void stopRoomChangeTask() {
			_roomChangeTask.cancel(true);
		}
		
		protected class BanishTask implements Runnable {
			@Override
			public void run() {
				final Instance inst = InstanceManager.getInstance().getInstance(getInstanceId());
				
				if ((inst == null) || ((inst.getInstanceEndTime() - System.currentTimeMillis()) < 60000)) {
					_banishTask.cancel(false);
				} else {
					for (int objId : inst.getPlayers()) {
						final L2PcInstance pl = L2World.getInstance().getPlayer(objId);
						if ((pl != null) && pl.isOnline()) {
							if ((partyInside == null) || !pl.isInParty() || (partyInside != pl.getParty())) {
								exitInstance(pl);
							}
						}
					}
				}
			}
		}
		
		protected class ChangeRoomTask implements Runnable {
			private static final Logger LOG = LoggerFactory.getLogger(ChangeRoomTask.class);
			
			@Override
			public void run() {
				try {
					earthQuake(CDWorld.this);
					Thread.sleep(5000);
					changeRoom(CDWorld.this);
				} catch (Exception ex) {
					LOG.warn("ChangeRoomTask exception!", ex);
				}
			}
		}
	}
	
	// Items
	private static final int ENRIA = 4042;
	private static final int ASOFE = 4043;
	private static final int THONS = 4044;
	private static final int LEONARD = 9628;
	private static final int DELUSION_MARK = 15311;
	
	// NPCs
	private final int ENTRANCE_GATEKEEPER;
	private final int ROOM_GATEKEEPER_FIRST;
	private final int ROOM_GATEKEEPER_LAST;
	private final int AENKINEL;
	private final int BOX;
	
	// Skills
	private static final SkillHolder SUCCESS_SKILL = new SkillHolder(5758);
	private static final SkillHolder FAIL_SKILL = new SkillHolder(5376, 4);
	
	private static final int ROOM_CHANGE_INTERVAL = 480; // 8 min
	private static final int ROOM_CHANGE_RANDOM_TIME = 120; // 2 min
	
	// Instance restart time
	private static final int RESET_HOUR = 6;
	private static final int RESET_MIN = 30;
	
	// Following values vary between scripts
	private final int INSTANCEID;
	private final String INSTANCE_TEMPLATE;
	
	protected Location[] ROOM_ENTER_POINTS;
	// Misc
	private static final String RETURN = Chamber.class.getSimpleName() + "_return";
	
	protected Chamber(int instanceId, String instanceTemplateName, int entranceGKId, int roomGKFirstId, int roomGKLastId, int aenkinelId, int boxId) {
		INSTANCEID = instanceId;
		INSTANCE_TEMPLATE = instanceTemplateName;
		ENTRANCE_GATEKEEPER = entranceGKId;
		ROOM_GATEKEEPER_FIRST = roomGKFirstId;
		ROOM_GATEKEEPER_LAST = roomGKLastId;
		AENKINEL = aenkinelId;
		BOX = boxId;
		
		bindStartNpc(ENTRANCE_GATEKEEPER);
		bindTalk(ENTRANCE_GATEKEEPER);
		for (int i = ROOM_GATEKEEPER_FIRST; i <= ROOM_GATEKEEPER_LAST; i++) {
			bindStartNpc(i);
			bindTalk(i);
		}
		bindKill(AENKINEL);
		bindAttack(BOX);
		bindSpellFinished(BOX);
		bindEventReceived(BOX);
	}
	
	private boolean isBigChamber() {
		return ((INSTANCEID == 131) || (INSTANCEID == 132));
	}
	
	private boolean isBossRoom(CDWorld world) {
		return (world.currentRoom == (ROOM_ENTER_POINTS.length - 1));
	}
	
	@Override
	protected boolean checkConditions(L2PcInstance player) {
		final L2Party party = player.getParty();
		if (party == null) {
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_IN_PARTY_CANT_ENTER));
			return false;
		}
		
		if (party.getLeader() != player) {
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER));
			return false;
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if (partyMember.getLevel() < 80) {
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (!Util.checkIfInRange(1000, player, partyMember, true)) {
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (isBigChamber()) {
				final long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), INSTANCEID);
				
				if (System.currentTimeMillis() < reentertime) {
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET);
					sm.addPcName(partyMember);
					party.broadcastPacket(sm);
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void markRestriction(InstanceWorld world) {
		if (world instanceof CDWorld) {
			final Calendar reenter = Calendar.getInstance();
			final Calendar now = Calendar.getInstance();
			reenter.set(Calendar.MINUTE, RESET_MIN);
			reenter.set(Calendar.HOUR_OF_DAY, RESET_HOUR);
			if (reenter.before(now)) {
				reenter.add(Calendar.DAY_OF_WEEK, 1);
			}
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_FROM_HERE_S1_S_ENTRY_HAS_BEEN_RESTRICTED);
			sm.addString(InstanceManager.getInstance().getInstanceIdName(world.getTemplateId()));
			// set instance reenter time for all allowed players
			for (int objectId : world.getAllowed()) {
				final L2PcInstance player = L2World.getInstance().getPlayer(objectId);
				if ((player != null) && player.isOnline()) {
					InstanceManager.getInstance().setInstanceTime(objectId, world.getTemplateId(), reenter.getTimeInMillis());
					player.sendPacket(sm);
				}
			}
		}
	}
	
	protected void changeRoom(CDWorld world) {
		final L2Party party = world.getPartyInside();
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		
		if ((party == null) || (inst == null)) {
			return;
		}
		
		int newRoom = world.currentRoom;
		
		// Do nothing, if there are raid room of Sqare or Tower Chamber
		if (isBigChamber() && isBossRoom(world)) {
			return;
		}
		
		// Teleport to raid room 10 min or lesser before instance end time for Tower and Square Chambers
		else if (isBigChamber() && ((inst.getInstanceEndTime() - System.currentTimeMillis()) < 600000)) {
			newRoom = ROOM_ENTER_POINTS.length - 1;
		}
		
		// 10% chance for teleport to raid room if not here already for Northern, Southern, Western and Eastern Chambers
		else if (!isBigChamber() && !isBossRoom(world) && (getRandom(100) < 10)) {
			newRoom = ROOM_ENTER_POINTS.length - 1;
		}
		
		else {
			while (newRoom == world.currentRoom) // otherwise teleport to another room, except current
			{
				newRoom = getRandom(ROOM_ENTER_POINTS.length - 1);
			}
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if (world.getInstanceId() == partyMember.getInstanceId()) {
				partyMember.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				teleportPlayer(partyMember, ROOM_ENTER_POINTS[newRoom], world.getInstanceId());
			}
		}
		
		world.currentRoom = newRoom;
		
		// Do not schedule room change for Square and Tower Chambers, if raid room is reached
		if (isBigChamber() && isBossRoom(world)) {
			inst.setDuration((int) ((inst.getInstanceEndTime() - System.currentTimeMillis()) + 1200000)); // Add 20 min to instance time if raid room is reached
			
			for (L2Npc npc : inst.getNpcs()) {
				if (npc.getId() == ROOM_GATEKEEPER_LAST) {
					npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.NPC_ALL, npc.getId(), NpcStringId.N21_MINUTES_ARE_ADDED_TO_THE_REMAINING_TIME_IN_THE_INSTANT_ZONE));
				}
			}
		} else {
			world.scheduleRoomChange(false);
		}
	}
	
	private void enter(CDWorld world) {
		final L2Party party = world.getPartyInside();
		
		if (party == null) {
			return;
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if (hasQuestItems(partyMember, DELUSION_MARK)) {
				takeItems(partyMember, DELUSION_MARK, -1);
			}
			
			if (party.isLeader(partyMember)) {
				giveItems(partyMember, DELUSION_MARK, 1);
			}
			
			// Save location for teleport back into main hall
			partyMember.getVariables().set(RETURN, Integer.toString(partyMember.getX()) + ";" + Integer.toString(partyMember.getY()) + ";" + Integer.toString(partyMember.getZ()));
			
			partyMember.setInstanceId(world.getInstanceId());
			world.addAllowed(partyMember.getObjectId());
		}
		
		changeRoom(world);
	}
	
	protected void earthQuake(CDWorld world) {
		final L2Party party = world.getPartyInside();
		
		if (party == null) {
			return;
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if (world.getInstanceId() == partyMember.getInstanceId()) {
				partyMember.sendPacket(new Earthquake(partyMember.getX(), partyMember.getY(), partyMember.getZ(), 20, 10));
			}
		}
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			enter((CDWorld) world);
		} else {
			final CDWorld currentWorld = (CDWorld) world;
			teleportPlayer(player, ROOM_ENTER_POINTS[currentWorld.currentRoom], world.getInstanceId());
		}
	}
	
	protected void exitInstance(L2PcInstance player) {
		if ((player == null) || !player.isOnline() || (player.getInstanceId() == 0)) {
			return;
		}
		final Instance inst = InstanceManager.getInstance().getInstance(player.getInstanceId());
		Location ret = inst.getExitLoc();
		final String return_point = player.getVariables().getString(RETURN, null);
		if (return_point != null) {
			String[] coords = return_point.split(";");
			if (coords.length == 3) {
				try {
					int x = Integer.parseInt(coords[0]);
					int y = Integer.parseInt(coords[1]);
					int z = Integer.parseInt(coords[2]);
					ret.setLocation(new Location(x, y, z));
				} catch (Exception e) {
				}
			}
		}
		
		teleportPlayer(player, ret, 0);
		final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null) {
			world.removeAllowed((player.getObjectId()));
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = "";
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if ((player != null) && (tmpworld != null) && (tmpworld instanceof CDWorld world) && (npc.getId() >= ROOM_GATEKEEPER_FIRST) && (npc.getId() <= ROOM_GATEKEEPER_LAST)) {
			// Change room from dialog
			if (event.equals("next_room")) {
				if (player.getParty() == null) {
					htmltext = getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/instances/ChambersOfDelusion/no_party.html");
				}
				
				else if (player.getParty().getLeaderObjectId() != player.getObjectId()) {
					htmltext = getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/instances/ChambersOfDelusion/no_leader.html");
				}
				
				else if (hasQuestItems(player, DELUSION_MARK)) {
					takeItems(player, DELUSION_MARK, 1);
					world.stopRoomChangeTask();
					changeRoom(world);
				}
				
				else {
					htmltext = getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/instances/ChambersOfDelusion/no_item.html");
				}
			} else if (event.equals("go_out")) {
				if (player.getParty() == null) {
					htmltext = getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/instances/ChambersOfDelusion/no_party.html");
				} else if (player.getParty().getLeaderObjectId() != player.getObjectId()) {
					htmltext = getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/instances/ChambersOfDelusion/no_leader.html");
				} else {
					final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
					
					world.stopRoomChangeTask();
					world.stopBanishTask();
					
					for (L2PcInstance partyMember : player.getParty().getMembers()) {
						exitInstance(partyMember);
					}
					
					inst.setEmptyDestroyTime(0);
				}
			} else if (event.equals("look_party")) {
				if ((player.getParty() != null) && (player.getParty() == world.getPartyInside())) {
					teleportPlayer(player, ROOM_ENTER_POINTS[world.currentRoom], world.getInstanceId(), false);
				}
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttack(final L2Npc npc, final L2PcInstance attacker, final int damage, final boolean isPet, final Skill skill) {
		if (!npc.isBusy() && (npc.getCurrentHp() < (npc.getMaxHp() / 10))) {
			npc.setBusy(true);
			if (getRandom(100) < (25 * rates().getQuestDropChanceMultiplier())) // 25% chance to reward
			{
				if (getRandom(100) < (33 * rates().getQuestDropChanceMultiplier())) {
					npc.dropItem(attacker, ENRIA, (int) (3 * rates().getQuestDropAmountMultiplier()));
				}
				if (getRandom(100) < (50 * rates().getQuestDropChanceMultiplier())) {
					npc.dropItem(attacker, THONS, (int) (4 * rates().getQuestDropAmountMultiplier()));
				}
				if (getRandom(100) < (50 * rates().getQuestDropChanceMultiplier())) {
					npc.dropItem(attacker, ASOFE, (int) (4 * rates().getQuestDropAmountMultiplier()));
				}
				if (getRandom(100) < (16 * rates().getQuestDropChanceMultiplier())) {
					npc.dropItem(attacker, LEONARD, (int) (2 * rates().getQuestDropAmountMultiplier()));
				}
				
				npc.broadcastScriptEvent("SCE_LUCKY", 2000);
				npc.doCast(SUCCESS_SKILL);
			} else {
				npc.broadcastScriptEvent("SCE_DREAM_FIRE_IN_THE_HOLE", 2000);
			}
		}
	}
	
	@Override
	public void onEventReceived(NpcEventReceived event) {
		switch (event.eventName()) {
			case "SCE_LUCKY":
				event.receiver().setBusy(true);
				event.receiver().doCast(SUCCESS_SKILL);
				break;
			case "SCE_DREAM_FIRE_IN_THE_HOLE":
				event.receiver().setBusy(true);
				event.receiver().doCast(FAIL_SKILL);
				break;
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isPet) {
		final InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if ((tmpworld != null) && (tmpworld instanceof CDWorld world)) {
			final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
			
			if (isBigChamber()) {
				markRestriction(world); // Set reenter restriction
				if ((inst.getInstanceEndTime() - System.currentTimeMillis()) > 300000) {
					inst.setDuration(300000); // Finish instance in 5 minutes
				}
			} else {
				world.stopRoomChangeTask();
				world.scheduleRoomChange(true);
			}
			
			inst.spawnGroup("boxes");
		}
	}
	
	@Override
	public void onSpellFinished(NpcSkillFinished event) {
		if ((event.npc().getId() == BOX) && ((event.skill().getId() == 5376) || (event.skill().getId() == 5758)) && !event.npc().isDead()) {
			event.npc().doDie(event.player());
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		int npcId = npc.getId();
		QuestState st = getQuestState(player, false);
		
		if (st == null) {
			st = newQuestState(player);
		}
		
		if (npcId == ENTRANCE_GATEKEEPER) {
			if (checkConditions(player)) {
				final L2Party party = player.getParty();
				enterInstance(player, new CDWorld(party), INSTANCE_TEMPLATE, INSTANCEID);
			}
		}
		
		return "";
	}
}
