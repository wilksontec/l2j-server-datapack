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
package com.l2jserver.datapack.ai.individual;

import static com.l2jserver.gameserver.config.Configuration.grandBoss;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.MountType;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.SpecialCamera;
import com.l2jserver.gameserver.util.Util;

/**
 * Valakas' AI.
 * @author Tryskell
 */
public final class Valakas extends AbstractNpcAI {
	// NPC
	private static final int VALAKAS = 29028;
	// Skills
	private static final SkillHolder HINDER_STRIDER = new SkillHolder(4258);
	private static final SkillHolder VALAKAS_LAVA_SKIN = new SkillHolder(4680);
	private static final SkillHolder VALAKAS_REGENERATION_1 = new SkillHolder(4691);
	private static final SkillHolder VALAKAS_REGENERATION_2 = new SkillHolder(4691, 2);
	private static final SkillHolder VALAKAS_REGENERATION_3 = new SkillHolder(4691, 3);
	private static final SkillHolder VALAKAS_REGENERATION_4 = new SkillHolder(4691, 4);
	
	private static final SkillHolder[] VALAKAS_REGULAR_SKILLS = {
		new SkillHolder(4681), // Valakas Trample
		new SkillHolder(4682), // Valakas Trample
		new SkillHolder(4683), // Valakas Dragon Breath
		new SkillHolder(4689), // Valakas Fear TODO: has two levels only level one is used.
	};
	
	private static final SkillHolder[] VALAKAS_LOWHP_SKILLS = {
		new SkillHolder(4681), // Valakas Trample
		new SkillHolder(4682), // Valakas Trample
		new SkillHolder(4683), // Valakas Dragon Breath
		new SkillHolder(4689), // Valakas Fear TODO: has two levels only level one is used.
		new SkillHolder(4690), // Valakas Meteor Storm
	};
	
	private static final SkillHolder[] VALAKAS_AOE_SKILLS = {
		new SkillHolder(4683), // Valakas Dragon Breath
		new SkillHolder(4684), // Valakas Dragon Breath
		new SkillHolder(4685), // Valakas Tail Stomp
		new SkillHolder(4686), // Valakas Tail Stomp
		new SkillHolder(4688), // Valakas Stun
		new SkillHolder(4689), // Valakas Fear TODO: has two levels only level one is used.
		new SkillHolder(4690), // Valakas Meteor Storm
	};
	
	// Locations
	private static final Location TELEPORT_CUBE_LOCATIONS[] = {
		new Location(214880, -116144, -1644),
		new Location(213696, -116592, -1644),
		new Location(212112, -116688, -1644),
		new Location(211184, -115472, -1664),
		new Location(210336, -114592, -1644),
		new Location(211360, -113904, -1644),
		new Location(213152, -112352, -1644),
		new Location(214032, -113232, -1644),
		new Location(214752, -114592, -1644),
		new Location(209824, -115568, -1421),
		new Location(210528, -112192, -1403),
		new Location(213120, -111136, -1408),
		new Location(215184, -111504, -1392),
		new Location(215456, -117328, -1392),
		new Location(213200, -118160, -1424)
	};
	private static final Location ATTACKER_REMOVE = new Location(150037, -57255, -2976);
	private static final Location VALAKAS_LAIR = new Location(212852, -114842, -1632);
	private static final Location VALAKAS_REGENERATION_LOC = new Location(-105200, -253104, -15264);
	// Valakas status.
	private static final byte DORMANT = 0; // Valakas is spawned and no one has entered yet. Entry is unlocked.
	private static final byte WAITING = 1; // Valakas is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	private static final byte FIGHTING = 2; // Valakas is engaged in battle, annihilating his foes. Entry is locked.
	private static final byte DEAD = 3; // Valakas has been killed. Entry is locked.
	// Misc
	private long _timeTracker = 0; // Time tracker for last attack on Valakas.
	private L2Playable _actualVictim; // Actual target of Valakas.
	private static L2BossZone ZONE;
	
	public Valakas() {
		registerMobs(VALAKAS);
		
		ZONE = GrandBossManager.getInstance().getZone(212852, -114842, -1632);
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
		final int status = GrandBossManager.getInstance().getBossStatus(VALAKAS);
		
		if (status == DEAD) {
			// load the unlock date and time for valakas from DB
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0) {
				// The time has not yet expired. Mark Valakas as currently locked (dead).
				startQuestTimer("valakas_unlock", temp, null, null);
			} else {
				// The time has expired while the server was offline. Spawn valakas in his cave as DORMANT.
				final L2Npc valakas = addSpawn(VALAKAS, -105200, -253104, -15264, 0, false, 0);
				GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
				GrandBossManager.getInstance().addBoss((L2GrandBossInstance) valakas);
				
				valakas.setIsInvul(true);
				valakas.setRunning();
				
				valakas.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}
		} else {
			final int loc_x = info.getInt("loc_x");
			final int loc_y = info.getInt("loc_y");
			final int loc_z = info.getInt("loc_z");
			final int heading = info.getInt("heading");
			final int hp = info.getInt("currentHP");
			final int mp = info.getInt("currentMP");
			
			final L2Npc valakas = addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0);
			GrandBossManager.getInstance().addBoss((L2GrandBossInstance) valakas);
			
			valakas.setCurrentHpMp(hp, mp);
			valakas.setRunning();
			
			// Start timers.
			if (status == FIGHTING) {
				// stores current time for inactivity task.
				_timeTracker = System.currentTimeMillis();
				
				startQuestTimer("regen_task", 60000, valakas, null, true);
				startQuestTimer("skill_task", 2000, valakas, null, true);
			} else {
				valakas.setIsInvul(true);
				valakas.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				
				// Start timer to lock entry after 30 minutes
				if (status == WAITING) {
					startQuestTimer("beginning", grandBoss().getValakasWaitTime(), valakas, null);
				}
			}
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (npc != null) {
			if (event.equalsIgnoreCase("beginning")) {
				// Stores current time
				_timeTracker = System.currentTimeMillis();
				
				// Teleport Valakas to his lair.
				npc.teleToLocation(VALAKAS_LAIR);
				
				// Sound + socialAction.
				for (L2PcInstance plyr : ZONE.getPlayersInside()) {
					plyr.sendPacket(Music.BS03_A_10000.getPacket());
					plyr.sendPacket(new SocialAction(npc.getObjectId(), 3));
				}
				
				// Launch the cinematic, and tasks (regen + skill).
				startQuestTimer("spawn_1", 1700, npc, null); // 1700
				startQuestTimer("spawn_2", 3200, npc, null); // 1500
				startQuestTimer("spawn_3", 6500, npc, null); // 3300
				startQuestTimer("spawn_4", 9400, npc, null); // 2900
				startQuestTimer("spawn_5", 12100, npc, null); // 2700
				startQuestTimer("spawn_6", 12430, npc, null); // 330
				startQuestTimer("spawn_7", 15430, npc, null); // 3000
				startQuestTimer("spawn_8", 16830, npc, null); // 1400
				startQuestTimer("spawn_9", 23530, npc, null); // 6700 - end of cinematic
				startQuestTimer("spawn_10", 26000, npc, null); // 2500 - AI + unlock
			}
			// Regeneration && inactivity task
			else if (event.equalsIgnoreCase("regen_task")) {
				// Inactivity task - 15min
				if (GrandBossManager.getInstance().getBossStatus(VALAKAS) == FIGHTING) {
					if ((_timeTracker + 900000) < System.currentTimeMillis()) {
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
						npc.teleToLocation(VALAKAS_REGENERATION_LOC);
						
						GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
						npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
						
						// Drop all players from the zone.
						ZONE.oustAllPlayers();
						
						// Cancel skill_task and regen_task.
						cancelQuestTimer("regen_task", npc, null);
						cancelQuestTimer("skill_task", npc, null);
						return null;
					}
				}
				
				// Verify if "Valakas Regeneration" skill is active.
				final BuffInfo info = npc.getEffectList().getBuffInfoBySkillId(VALAKAS_REGENERATION_1.getSkillId());
				final int lvl = info != null ? info.getSkill().getLevel() : 0;
				
				// Current HPs are inferior to 25% ; apply lvl 4 of regen skill.
				if ((npc.getCurrentHp() < (npc.getMaxHp() / 4)) && (lvl != 4)) {
					npc.setTarget(npc);
					npc.doCast(VALAKAS_REGENERATION_4);
				}
				// Current HPs are inferior to 50% ; apply lvl 3 of regen skill.
				else if ((npc.getCurrentHp() < ((npc.getMaxHp() * 2) / 4.0)) && (lvl != 3)) {
					npc.setTarget(npc);
					npc.doCast(VALAKAS_REGENERATION_3);
				}
				// Current HPs are inferior to 75% ; apply lvl 2 of regen skill.
				else if ((npc.getCurrentHp() < ((npc.getMaxHp() * 3) / 4.0)) && (lvl != 2)) {
					npc.setTarget(npc);
					npc.doCast(VALAKAS_REGENERATION_2);
				}
				// Apply lvl 1.
				else if (lvl != 1) {
					npc.setTarget(npc);
					npc.doCast(VALAKAS_REGENERATION_1);
				}
			}
			// Spawn cinematic, regen_task and choose of skill.
			else if (event.equalsIgnoreCase("spawn_1")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1800, 180, -1, 1500, 15000, 10000, 0, 0, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_2")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 180, -5, 3000, 15000, 10000, 0, -5, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_3")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 500, 180, -8, 600, 15000, 10000, 0, 60, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_4")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 800, 180, -8, 2700, 15000, 10000, 0, 30, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_5")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 200, 250, 70, 0, 15000, 10000, 30, 80, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_6")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 250, 70, 2500, 15000, 10000, 30, 80, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_7")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 700, 150, 30, 0, 15000, 10000, -10, 60, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_8")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 150, 20, 2900, 15000, 10000, -10, 30, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_9")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 750, 170, -10, 3400, 15000, 4000, 10, -15, 1, 0, 0));
			} else if (event.equalsIgnoreCase("spawn_10")) {
				GrandBossManager.getInstance().setBossStatus(VALAKAS, FIGHTING);
				npc.setIsInvul(false);
				
				startQuestTimer("regen_task", 60000, npc, null, true);
				startQuestTimer("skill_task", 2000, npc, null, true);
			}
			// Death cinematic, spawn of Teleport Cubes.
			else if (event.equalsIgnoreCase("die_1")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 2000, 130, -1, 0, 15000, 10000, 0, 0, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_2")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1100, 210, -5, 3000, 15000, 10000, -13, 0, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_3")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1300, 200, -8, 3000, 15000, 10000, 0, 15, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_4")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1000, 190, 0, 500, 15000, 10000, 0, 10, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_5")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 120, 0, 2500, 15000, 10000, 12, 40, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_6")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 20, 0, 700, 15000, 10000, 10, 10, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_7")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 1000, 15000, 10000, 20, 70, 1, 1, 0));
			} else if (event.equalsIgnoreCase("die_8")) {
				ZONE.broadcastPacket(new SpecialCamera(npc, 1700, 10, 0, 300, 15000, 250, 20, -20, 1, 1, 0));
				
				for (Location loc : TELEPORT_CUBE_LOCATIONS) {
					addSpawn(31759, loc, false, 900000);
				}
				
				startQuestTimer("remove_players", 900000, null, null);
			} else if (event.equalsIgnoreCase("skill_task")) {
				callSkillAI(npc);
			}
		} else {
			if (event.equalsIgnoreCase("valakas_unlock")) {
				final L2Npc valakas = addSpawn(VALAKAS, -105200, -253104, -15264, 32768, false, 0);
				GrandBossManager.getInstance().addBoss((L2GrandBossInstance) valakas);
				GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
			} else if (event.equalsIgnoreCase("remove_players")) {
				ZONE.oustAllPlayers();
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		npc.disableCoreAI(true);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (!ZONE.isInsideZone(attacker)) {
			attacker.doDie(attacker);
			return;
		}
		
		if (npc.isInvul()) {
			return;
		}
		
		if (GrandBossManager.getInstance().getBossStatus(VALAKAS) != FIGHTING) {
			attacker.teleToLocation(ATTACKER_REMOVE);
			return;
		}
		
		// Debuff strider-mounted players.
		if (attacker.getMountType() == MountType.STRIDER) {
			if (!attacker.isAffectedBySkill(HINDER_STRIDER.getSkillId())) {
				npc.setTarget(attacker);
				npc.doCast(HINDER_STRIDER);
			}
		}
		_timeTracker = System.currentTimeMillis();
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		// Cancel skill_task and regen_task.
		cancelQuestTimer("regen_task", npc, null);
		cancelQuestTimer("skill_task", npc, null);
		
		// Launch death animation.
		ZONE.broadcastPacket(Music.B03_D_10000.getPacket());
		ZONE.broadcastPacket(new SpecialCamera(npc, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
		
		startQuestTimer("die_1", 300, npc, null); // 300
		startQuestTimer("die_2", 600, npc, null); // 300
		startQuestTimer("die_3", 3800, npc, null); // 3200
		startQuestTimer("die_4", 8200, npc, null); // 4400
		startQuestTimer("die_5", 8700, npc, null); // 500
		startQuestTimer("die_6", 13300, npc, null); // 4600
		startQuestTimer("die_7", 14000, npc, null); // 700
		startQuestTimer("die_8", 16500, npc, null); // 2500
		
		GrandBossManager.getInstance().setBossStatus(VALAKAS, DEAD);
		// Calculate Min and Max respawn times randomly.
		final long respawnTime = grandBoss().getIntervalOfValakasSpawn() + getRandom(-(int) grandBoss().getRandomOfValakasSpawn(), (int) grandBoss().getRandomOfValakasSpawn());
		
		startQuestTimer("valakas_unlock", respawnTime, null, null);
		// also save the respawn time so that the info is maintained past reboots
		StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
		info.set("respawn_time", (System.currentTimeMillis() + respawnTime));
		GrandBossManager.getInstance().setStatsSet(VALAKAS, info);
	}
	
	private void callSkillAI(L2Npc npc) {
		if (npc.isInvul() || npc.isCastingNow()) {
			return;
		}
		
		// Pickup a target if no or dead victim. 10% luck he decides to reconsiders his target.
		if ((_actualVictim == null) || _actualVictim.isDead() || !(npc.getKnownList().knowsObject(_actualVictim)) || (getRandom(10) == 0)) {
			_actualVictim = getRandomTarget(npc);
		}
		
		// If result is still null, Valakas will roam. Don't go deeper in skill AI.
		if (_actualVictim == null) {
			if (getRandom(10) == 0) {
				int x = npc.getX();
				int y = npc.getY();
				int z = npc.getZ();
				
				int posX = x + getRandom(-1400, 1400);
				int posY = y + getRandom(-1400, 1400);
				
				if (GeoData.getInstance().canMove(x, y, z, posX, posY, z, npc.getInstanceId())) {
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(posX, posY, z, 0));
				}
			}
			return;
		}
		
		final SkillHolder skill = getRandomSkill(npc);
		
		// Cast the skill or follow the target.
		if (Util.checkIfInRange((skill.getSkill().getCastRange() < 600) ? 600 : skill.getSkill().getCastRange(), npc, _actualVictim, true)) {
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			npc.setIsCastingNow(true);
			npc.setTarget(_actualVictim);
			npc.doCast(skill);
		} else {
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, _actualVictim, null);
			npc.setIsCastingNow(false);
		}
	}
	
	/**
	 * Pick a random skill.<br>
	 * Valakas will mostly use utility skills. If Valakas feels surrounded, he will use AoE skills.<br>
	 * Lower than 50% HPs, he will begin to use Meteor skill.
	 * @param npc valakas
	 * @return a skill holder
	 */
	private SkillHolder getRandomSkill(L2Npc npc) {
		final int hpRatio = (int) ((npc.getCurrentHp() / npc.getMaxHp()) * 100);
		
		// Valakas Lava Skin has priority.
		if ((hpRatio < 75) && (getRandom(150) == 0) && !npc.isAffectedBySkill(VALAKAS_LAVA_SKIN.getSkillId())) {
			return VALAKAS_LAVA_SKIN;
		}
		
		// Valakas will use mass spells if he feels surrounded.
		if (Util.getPlayersCountInRadius(1200, npc, false, false) >= 20) {
			return VALAKAS_AOE_SKILLS[getRandom(VALAKAS_AOE_SKILLS.length)];
		}
		
		if (hpRatio > 50) {
			return VALAKAS_REGULAR_SKILLS[getRandom(VALAKAS_REGULAR_SKILLS.length)];
		}
		
		return VALAKAS_LOWHP_SKILLS[getRandom(VALAKAS_LOWHP_SKILLS.length)];
	}
	
	/**
	 * Pickup a random L2Playable from the zone, deads targets aren't included.
	 * @param npc
	 * @return a random L2Playable.
	 */
	private L2Playable getRandomTarget(L2Npc npc) {
		List<L2Playable> result = new ArrayList<>();
		
		for (L2Character obj : npc.getKnownList().getKnownCharacters()) {
			if ((obj == null) || obj.isPet()) {
				continue;
			} else if (!obj.isDead() && obj.isPlayable()) {
				result.add((L2Playable) obj);
			}
		}
		
		return (result.isEmpty()) ? null : result.get(getRandom(result.size()));
	}
}