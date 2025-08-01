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
package com.l2jserver.datapack.gracia.instances.SeedOfInfinity.HallOfSuffering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.instances.AbstractInstance;
import com.l2jserver.datapack.quests.Q00695_DefendTheHallOfSuffering.Q00695_DefendTheHallOfSuffering;
import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Seed of Infinity (Hall of Suffering) instance zone.<br>
 * TODO:<br>
 * - after 15mins mobs are despawned<br>
 * @author Gigiikun, ZakaX, Didldak
 */
public final class HallOfSuffering extends AbstractInstance {
	
	private static final Logger LOG = LoggerFactory.getLogger(HallOfSuffering.class);
	
	protected static class HSWorld extends InstanceWorld {
		protected Map<L2Npc, Boolean> npcList = new HashMap<>();
		protected L2Npc klodekus = null;
		protected L2Npc klanikus = null;
		protected boolean isBossesAttacked = false;
		protected long startTime = 0;
		protected String ptLeaderName = "";
		protected int rewardItemId = -1;
		protected String rewardHtm = "";
		protected boolean isRewarded = false;
	}
	
	// NPCs
	private static final int MOUTH_OF_EKIMUS = 32537;
	private static final int TEPIOS = 32530;
	// Location
	private static final Location ENTER_TELEPORT = new Location(-187567, 205570, -9538);
	// Skill
	private static final SkillHolder PRESENTATION_DISTRICT1_BOSS_ARISE = new SkillHolder(5824);
	// Monsters
	private static final int KLODEKUS = 25665;
	private static final int KLANIKUS = 25666;
	private static final int TUMOR_ALIVE = 18704;
	private static final int TUMOR_DEAD = 18705;
	private static final int[] TUMOR_MOBIDS = {
		22509,
		22510,
		22511,
		22512,
		22513,
		22514,
		22515
	};
	private static final int[] TWIN_MOBIDS = {
		22509,
		22510,
		22511,
		22512,
		22513
	};
	// Doors/Walls/Zones
	// @formatter:off
	private static final int[][] ROOM_1_MOBS =
	{
		{ 22509, -186296, 208200, -9544 },
		{ 22509, -186161, 208345, -9544 },
		{ 22509, -186296, 208403, -9544 },
		{ 22510, -186107, 208113, -9528 },
		{ 22510, -186350, 208200, -9544 }
	};
	private static final int[][] ROOM_2_MOBS =
	{
		{ 22511, -184433, 210953, -9536 },
		{ 22511, -184406, 211301, -9536 },
		{ 22509, -184541, 211272, -9544 },
		{ 22510, -184244, 211098, -9536 },
		{ 22510, -184352, 211243, -9536 },
		{ 22510, -184298, 211330, -9528 }
	};
	private static final int[][] ROOM_3_MOBS =
	{
		{ 22512, -182611, 213984, -9520 },
		{ 22512, -182908, 214071, -9520 },
		{ 22512, -182962, 213868, -9512 },
		{ 22509, -182881, 213955, -9512 },
		{ 22511, -182827, 213781, -9504 },
		{ 22511, -182530, 213984, -9528 },
		{ 22510, -182935, 213723, -9512 },
		{ 22510, -182557, 213868, -9520 }
	};
	private static final int[][] ROOM_4_MOBS =
	{
		{ 22514, -180958, 216860, -9544 },
		{ 22514, -181012, 216628, -9536 },
		{ 22514, -181120, 216715, -9536 },
		{ 22513, -180661, 216599, -9536 },
		{ 22513, -181039, 216599, -9536 },
		{ 22511, -180715, 216599, -9536 },
		{ 22511, -181012, 216889, -9536 },
		{ 22512, -180931, 216918, -9536 },
		{ 22512, -180742, 216628, -9536 }
	};
	private static final int[][] ROOM_5_MOBS =
	{
		{ 22512, -177372, 217854, -9536 },
		{ 22512, -177237, 218140, -9536 },
		{ 22512, -177021, 217647, -9528 },
		{ 22513, -177372, 217792, -9544 },
		{ 22513, -177372, 218053, -9536 },
		{ 22514, -177291, 217734, -9544 },
		{ 22514, -177264, 217792, -9544 },
		{ 22514, -177264, 218053, -9536 },
		{ 22515, -177156, 217792, -9536 },
		{ 22515, -177075, 217647, -9528 }
	};
	// @formatter:on
	private static final Location[] TUMOR_SPAWNS = {
		new Location(-186327, 208286, -9544),
		new Location(-184429, 211155, -9544),
		new Location(-182811, 213871, -9496),
		new Location(-181039, 216633, -9528),
		new Location(-177264, 217760, -9544)
	};
	private static final Location KLODEKUS_SPAWN = new Location(-173727, 218169, -9536, -16384);
	private static final Location KLANIKUS_SPAWN = new Location(-173727, 218049, -9538, 16360);
	private static final Location TEPIOS_SPAWN = new Location(-173727, 218109, -9536);
	// Boss
	private static final int BOSS_INVUL_TIME = 30000; // In Milliseconds.
	private static final int BOSS_MINION_SPAWN_TIME = 60000; // In Milliseconds.
	private static final int BOSS_RESSURECT_TIME = 20000; // In Milliseconds.
	// Misc
	private static final int TEMPLATE_ID = 115;
	private static final int MIN_LEVEL = 75;
	private static final int MAX_LEVEL = 82;
	
	public HallOfSuffering() {
		bindStartNpc(MOUTH_OF_EKIMUS, TEPIOS);
		bindTalk(MOUTH_OF_EKIMUS, TEPIOS);
		bindFirstTalk(TEPIOS);
		bindKill(TUMOR_ALIVE, KLODEKUS, KLANIKUS);
		bindAttack(KLODEKUS, KLANIKUS);
		bindSkillSee(TUMOR_MOBIDS);
		bindKill(TUMOR_MOBIDS);
	}
	
	@Override
	protected boolean checkConditions(L2PcInstance player) {
		if (player.canOverrideCond(PcCondOverride.INSTANCE_CONDITIONS)) {
			return true;
		}
		
		final L2Party party = player.getParty();
		if (party == null) {
			player.sendPacket(SystemMessageId.NOT_IN_PARTY_CANT_ENTER);
			return false;
		}
		
		if (party.getLeader() != player) {
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		
		for (L2PcInstance partyMember : party.getMembers()) {
			if ((partyMember.getLevel() < MIN_LEVEL) || (partyMember.getLevel() > MAX_LEVEL)) {
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
			if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), TEMPLATE_ID)) {
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
		if (firstEntrance) {
			if (!player.isInParty()) {
				teleportPlayer(player, ENTER_TELEPORT, world.getInstanceId());
				world.addAllowed(player.getObjectId());
			} else {
				for (L2PcInstance players : player.getParty().getMembers()) {
					teleportPlayer(players, ENTER_TELEPORT, world.getInstanceId());
					world.addAllowed(players.getObjectId());
					getQuestState(players, true);
				}
			}
			
			runTumors((HSWorld) world);
		} else {
			teleportPlayer(player, ENTER_TELEPORT, world.getInstanceId());
		}
	}
	
	private boolean checkKillProgress(L2Npc mob, HSWorld world) {
		if (world.npcList.containsKey(mob)) {
			world.npcList.put(mob, true);
		}
		for (boolean isDead : world.npcList.values()) {
			if (!isDead) {
				return false;
			}
		}
		return true;
	}
	
	private int[][] getRoomSpawns(int room) {
		switch (room) {
			case 0:
				return ROOM_1_MOBS;
			case 1:
				return ROOM_2_MOBS;
			case 2:
				return ROOM_3_MOBS;
			case 3:
				return ROOM_4_MOBS;
			case 4:
				return ROOM_5_MOBS;
		}
		LOG.warn("Invalid room Id!");
		return new int[][] {};
	}
	
	private void runTumors(HSWorld world) {
		for (int[] mob : getRoomSpawns(world.getStatus())) {
			final L2Npc npc = addSpawn(mob[0], mob[1], mob[2], mob[3], 0, false, 0, false, world.getInstanceId());
			world.npcList.put(npc, false);
		}
		
		final L2Npc mob = addSpawn(TUMOR_ALIVE, TUMOR_SPAWNS[world.getStatus()], false, 0, false, world.getInstanceId());
		mob.disableCoreAI(true);
		mob.setIsImmobilized(true);
		mob.setCurrentHp(mob.getMaxHp() * 0.5);
		world.npcList.put(mob, false);
		world.incStatus();
	}
	
	private void runTwins(HSWorld world) {
		world.incStatus();
		world.klodekus = addSpawn(KLODEKUS, KLODEKUS_SPAWN, false, 0, false, world.getInstanceId());
		world.klanikus = addSpawn(KLANIKUS, KLANIKUS_SPAWN, false, 0, false, world.getInstanceId());
		world.klanikus.setIsMortal(false);
		world.klodekus.setIsMortal(false);
	}
	
	private void bossSimpleDie(L2Npc boss) {
		// killing is only possible one time
		synchronized (this) {
			if (boss.isDead()) {
				return;
			}
			// now reset currentHp to zero
			boss.setCurrentHp(0);
			boss.setIsDead(true);
		}
		
		// Set target to null and cancel Attack or Cast
		boss.setTarget(null);
		
		// Stop movement
		boss.stopMove(null);
		
		// Stop HP/MP/CP Regeneration task
		boss.getStatus().stopHpMpRegeneration();
		
		boss.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		// Send the Server->Client packet StatusUpdate with current HP and MP to all other L2PcInstance to inform
		boss.broadcastStatusUpdate();
		
		// Notify L2Character AI
		boss.getAI().notifyEvent(CtrlEvent.EVT_DEAD);
		
		if (boss.getWorldRegion() != null) {
			boss.getWorldRegion().onDeath(boss);
		}
	}
	
	private void calcRewardItemId(HSWorld world) {
		long finishDiff = System.currentTimeMillis() - world.startTime;
		if (finishDiff < 1200000) {
			world.rewardHtm = "32530-00.htm";
			world.rewardItemId = 13777;
		} else if (finishDiff <= 1260000) {
			world.rewardHtm = "32530-01.htm";
			world.rewardItemId = 13778;
		} else if (finishDiff <= 1320000) {
			world.rewardHtm = "32530-02.htm";
			world.rewardItemId = 13779;
		} else if (finishDiff <= 1380000) {
			world.rewardHtm = "32530-03.htm";
			world.rewardItemId = 13780;
		} else if (finishDiff <= 1440000) {
			world.rewardHtm = "32530-04.htm";
			world.rewardItemId = 13781;
		} else if (finishDiff <= 1500000) {
			world.rewardHtm = "32530-05.htm";
			world.rewardItemId = 13782;
		} else if (finishDiff <= 1560000) {
			world.rewardHtm = "32530-06.htm";
			world.rewardItemId = 13783;
		} else if (finishDiff <= 1620000) {
			world.rewardHtm = "32530-07.htm";
			world.rewardItemId = 13784;
		} else if (finishDiff <= 1680000) {
			world.rewardHtm = "32530-08.htm";
			world.rewardItemId = 13785;
		} else {
			world.rewardHtm = "32530-09.htm";
			world.rewardItemId = 13786;
		}
	}
	
	private String getPtLeaderText(L2PcInstance player, HSWorld world) {
		String htmltext = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "com/l2jserver/datapack/gracia/instances/SeedOfInfinity/HallOfSuffering/32530-10.htm");
		htmltext = htmltext.replaceAll("%ptLeader%", String.valueOf(world.ptLeaderName));
		return htmltext;
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if (skill.hasEffectType(L2EffectType.REBALANCE_HP, L2EffectType.HP)) {
			int hate = 2 * skill.getEffectPoint();
			if (hate < 2) {
				hate = 1000;
			}
			((L2Attackable) npc).addDamageHate(caster, 0, hate);
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HSWorld world) {
			if (event.equalsIgnoreCase("spawnBossGuards")) {
				if (!world.klanikus.isInCombat() && !world.klodekus.isInCombat()) {
					world.isBossesAttacked = false;
					return null;
				}
				L2Npc mob = addSpawn(TWIN_MOBIDS[getRandom(TWIN_MOBIDS.length)], KLODEKUS_SPAWN, false, 0, false, npc.getInstanceId());
				((L2Attackable) mob).addDamageHate(((L2Attackable) npc).getMostHated(), 0, 1);
				if (getRandom(100) < 33) {
					mob = addSpawn(TWIN_MOBIDS[getRandom(TWIN_MOBIDS.length)], KLANIKUS_SPAWN, false, 0, false, npc.getInstanceId());
					((L2Attackable) mob).addDamageHate(((L2Attackable) npc).getMostHated(), 0, 1);
				}
				startQuestTimer("spawnBossGuards", BOSS_MINION_SPAWN_TIME, npc, null);
			} else if (event.equalsIgnoreCase("isTwinSeparated")) {
				if (Util.checkIfInRange(500, world.klanikus, world.klodekus, false)) {
					world.klanikus.setIsInvul(false);
					world.klodekus.setIsInvul(false);
				} else {
					world.klanikus.setIsInvul(true);
					world.klodekus.setIsInvul(true);
				}
				startQuestTimer("isTwinSeparated", 10000, npc, null);
			} else if (event.equalsIgnoreCase("ressurectTwin")) {
				L2Npc aliveTwin = (world.klanikus == npc ? world.klodekus : world.klanikus);
				npc.doRevive();
				npc.doCast(PRESENTATION_DISTRICT1_BOSS_ARISE);
				npc.setCurrentHp(aliveTwin.getCurrentHp());
				
				// get most hated of other boss
				L2Character hated = ((L2MonsterInstance) aliveTwin).getMostHated();
				if (hated != null) {
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, 1000);
				}
				
				aliveTwin.setIsInvul(true); // make other boss invul
				startQuestTimer("uninvul", BOSS_INVUL_TIME, aliveTwin, null);
			} else if (event.equals("uninvul")) {
				npc.setIsInvul(false);
			}
		}
		return null;
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HSWorld world) {
			if (!world.isBossesAttacked) {
				world.isBossesAttacked = true;
				startQuestTimer("spawnBossGuards", BOSS_MINION_SPAWN_TIME, npc, null);
				startQuestTimer("isTwinSeparated", 10000, npc, null);
			} else if (damage >= npc.getCurrentHp()) {
				if (world.klanikus.isDead()) {
					world.klanikus.setIsDead(false);
					world.klanikus.doDie(attacker);
					world.klodekus.doDie(attacker);
				} else if (((HSWorld) tmpworld).klodekus.isDead()) {
					world.klodekus.setIsDead(false);
					world.klodekus.doDie(attacker);
					world.klanikus.doDie(attacker);
				} else {
					bossSimpleDie(npc);
					startQuestTimer("ressurectTwin", BOSS_RESSURECT_TIME, npc, null);
				}
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HSWorld world) {
			if (npc.getId() == TUMOR_ALIVE) {
				addSpawn(TUMOR_DEAD, npc, false, 0, false, npc.getInstanceId());
			}
			if (world.getStatus() < 5) {
				if (checkKillProgress(npc, world)) {
					runTumors(world);
				}
			} else if (world.getStatus() == 5) {
				if (checkKillProgress(npc, world)) {
					runTwins(world);
				}
			} else if ((world.getStatus() == 6) && ((npc.getId() == KLODEKUS) || (npc.getId() == KLANIKUS))) {
				if (world.klanikus.isDead() && world.klodekus.isDead()) {
					world.incStatus();
					// instance end
					calcRewardItemId(world);
					world.klanikus = null;
					world.klodekus = null;
					cancelQuestTimers("ressurectTwin");
					cancelQuestTimers("spawnBossGuards");
					cancelQuestTimers("isTwinSeparated");
					addSpawn(TEPIOS, TEPIOS_SPAWN, false, 0, false, world.getInstanceId());
					finishInstance(world);
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (npc.getId() == TEPIOS) {
			InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
			if (((HSWorld) world).rewardItemId == -1) {
				LOG.warn("{} is try to cheat!", player);
				return getPtLeaderText(player, (HSWorld) world);
			} else if (((HSWorld) world).isRewarded) {
				return "32530-11.htm";
			} else if ((player.getParty() != null) && (player.getParty().getLeaderObjectId() == player.getObjectId())) {
				return ((HSWorld) world).rewardHtm;
			}
			
			return getPtLeaderText(player, (HSWorld) world);
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		getQuestState(talker, true);
		if (npc.getId() == MOUTH_OF_EKIMUS) {
			enterInstance(talker, new HSWorld(), "HallOfSuffering.xml", TEMPLATE_ID);
		} else if (npc.getId() == TEPIOS) {
			InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(talker);
			if (((HSWorld) world).rewardItemId == -1) {
				LOG.warn("{} is try to cheat!", talker);
				return getPtLeaderText(talker, (HSWorld) world);
			} else if (((HSWorld) world).isRewarded) {
				return "32530-11.htm";
			} else if ((talker.getParty() != null) && (talker.getParty().getLeaderObjectId() == talker.getObjectId())) {
				((HSWorld) world).isRewarded = true;
				for (L2PcInstance member : talker.getParty().getMembers()) {
					final QuestState st = member.getQuestState(Q00695_DefendTheHallOfSuffering.class.getSimpleName());
					if ((st != null) && st.isMemoState(2)) {
						giveItems(member, 736, 1); // Scroll of Escape
						giveItems(member, ((HSWorld) world).rewardItemId, 1);
						st.exitQuest(true);
					}
				}
				return "";
			}
			
			return getPtLeaderText(talker, (HSWorld) world);
		}
		return super.onTalk(npc, talker);
	}
}
