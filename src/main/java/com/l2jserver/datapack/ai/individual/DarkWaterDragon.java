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

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Dark Water Dragon's AI.
 */
public final class DarkWaterDragon extends AbstractNpcAI {
	private static final int DRAGON = 22267;
	private static final int SHADE1 = 22268;
	private static final int SHADE2 = 22269;
	private static final int FAFURION = 18482;
	private static final int DETRACTOR1 = 22270;
	private static final int DETRACTOR2 = 22271;
	private static final Set<Integer> SECOND_SPAWN = ConcurrentHashMap.newKeySet(); // Used to track if second Shades were already spawned
	// Items
	private static final int BLUE_SEED_OF_EVIL = 9595;
	private static final int RED_SEED_OF_EVIL = 9596;
	private static final int WATER_DRAGON_SCALE = 9691;
	private static final int WATER_DRAGON_CLAW = 9700;
	private static final int SPIRIT_OF_THE_LAKE = 9689;
	private static final Set<Integer> MY_TRACKING_SET = ConcurrentHashMap.newKeySet(); // Used to track instances of npcs
	private static final Map<Integer, L2PcInstance> ID_MAP = new ConcurrentHashMap<>(); // Used to track instances of npcs
	
	public DarkWaterDragon() {
		bindKill(DRAGON, SHADE1, SHADE2, FAFURION, DETRACTOR1, DETRACTOR2);
		bindAttack(DRAGON, SHADE1, SHADE2, FAFURION, DETRACTOR1, DETRACTOR2);
		bindSpawn(DRAGON, SHADE1, SHADE2, FAFURION, DETRACTOR1, DETRACTOR2);
		MY_TRACKING_SET.clear();
		SECOND_SPAWN.clear();
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (npc != null) {
			if (event.equalsIgnoreCase("first_spawn")) { // timer to start timer "1"
				startQuestTimer("1", 40000, npc, null, true); // spawns detractor every 40 seconds
			} else if (event.equalsIgnoreCase("second_spawn")) { // timer to start timer "2"
				startQuestTimer("2", 40000, npc, null, true); // spawns detractor every 40 seconds
			} else if (event.equalsIgnoreCase("third_spawn")) { // timer to start timer "3"
				startQuestTimer("3", 40000, npc, null, true); // spawns detractor every 40 seconds
			} else if (event.equalsIgnoreCase("fourth_spawn")) { // timer to start timer "4"
				startQuestTimer("4", 40000, npc, null, true); // spawns detractor every 40 seconds
			} else if (event.equalsIgnoreCase("1")) { // spawns a detractor
				addSpawn(DETRACTOR1, (npc.getX() + 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
			} else if (event.equalsIgnoreCase("2")) { // spawns a detractor
				addSpawn(DETRACTOR2, (npc.getX() + 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
			} else if (event.equalsIgnoreCase("3")) { // spawns a detractor
				addSpawn(DETRACTOR1, (npc.getX() - 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
			} else if (event.equalsIgnoreCase("4")) { // spawns a detractor
				addSpawn(DETRACTOR2, (npc.getX() - 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
			} else if (event.equalsIgnoreCase("fafurion_despawn")) { // Fafurion Kindred disappears and drops reward
				cancelQuestTimer("fafurion_poison", npc, null);
				cancelQuestTimer("1", npc, null);
				cancelQuestTimer("2", npc, null);
				cancelQuestTimer("3", npc, null);
				cancelQuestTimer("4", npc, null);
				
				MY_TRACKING_SET.remove(npc.getObjectId());
				player = ID_MAP.remove(npc.getObjectId());
				if (player != null) {
					calculateDrop(npc, player, WATER_DRAGON_SCALE, 100.0);
					calculateDrop(npc, player, WATER_DRAGON_CLAW, 33.0);
				}
				
				npc.deleteMe();
			} else if (event.equalsIgnoreCase("fafurion_poison")) { // Reduces Fafurions hp like it is poisoned
				if (npc.getCurrentHp() <= 500) {
					cancelQuestTimer("fafurion_despawn", npc, null);
					cancelQuestTimer("first_spawn", npc, null);
					cancelQuestTimer("second_spawn", npc, null);
					cancelQuestTimer("third_spawn", npc, null);
					cancelQuestTimer("fourth_spawn", npc, null);
					cancelQuestTimer("1", npc, null);
					cancelQuestTimer("2", npc, null);
					cancelQuestTimer("3", npc, null);
					cancelQuestTimer("4", npc, null);
					MY_TRACKING_SET.remove(npc.getObjectId());
					ID_MAP.remove(npc.getObjectId());
				}
				npc.reduceCurrentHp(500, npc, null); // poison kills Fafurion if he is not healed
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		int npcId = npc.getId();
		int npcObjId = npc.getObjectId();
		if (npcId == DRAGON) {
			final L2Character originalAttacker = isSummon ? attacker.getSummon() : attacker;
			if (!MY_TRACKING_SET.contains(npcObjId)) { // this allows to handle multiple instances of npc
				MY_TRACKING_SET.add(npcObjId);
				// Spawn first 5 shades on first attack on Dark Water Dragon
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			} else if ((npc.getCurrentHp() < (npc.getMaxHp() / 2.0)) && !(SECOND_SPAWN.contains(npcObjId))) {
				SECOND_SPAWN.add(npcObjId);
				// Spawn second 5 shades on half hp of on Dark Water Dragon
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		int npcId = npc.getId();
		int npcObjId = npc.getObjectId();
		switch (npcId) {
			case DRAGON -> {
				MY_TRACKING_SET.remove(npcObjId);
				SECOND_SPAWN.remove(npcObjId);
				L2Attackable faf = (L2Attackable) addSpawn(FAFURION, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0); // spawns Fafurion Kindred when Dard Water Dragon is dead
				ID_MAP.put(faf.getObjectId(), killer);
				calculateDrop(npc, killer, RED_SEED_OF_EVIL, 77.0);
			}
			case FAFURION -> {
				cancelQuestTimer("fafurion_poison", npc, null);
				cancelQuestTimer("fafurion_despawn", npc, null);
				cancelQuestTimer("first_spawn", npc, null);
				cancelQuestTimer("second_spawn", npc, null);
				cancelQuestTimer("third_spawn", npc, null);
				cancelQuestTimer("fourth_spawn", npc, null);
				cancelQuestTimer("1", npc, null);
				cancelQuestTimer("2", npc, null);
				cancelQuestTimer("3", npc, null);
				cancelQuestTimer("4", npc, null);
				MY_TRACKING_SET.remove(npcObjId);
				ID_MAP.remove(npcObjId);
			}
			case SHADE1 -> calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 9.87);
			case SHADE2 -> calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 9.95);
			case DETRACTOR1, DETRACTOR2 -> {
				calculateDrop(npc, killer, SPIRIT_OF_THE_LAKE, 100.0);
				calculateDrop(npc, killer, BLUE_SEED_OF_EVIL, 10.08);
			}
		}
	}
	
	private void calculateDrop(final L2Npc npc, final L2PcInstance killer, final int itemId, final double dropRate) {
		final int finalRate = (int) ((dropRate * 100) * rates().getDeathDropChanceMultiplier());
		if (Rnd.get(10000) <= finalRate) {
			int finalAmount = (int) rates().getDeathDropAmountMultiplier();
			if (finalRate > 10000) {
				finalAmount *= (finalRate / 10000) + (Rnd.get(10000) <= (finalRate % 10000) ? 1 : 0);
			}
			npc.dropItem(killer, itemId, finalAmount);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == FAFURION) {
			if (!MY_TRACKING_SET.contains(npc.getObjectId())) {
				MY_TRACKING_SET.add(npc.getObjectId());
				// Spawn 4 Detractors on spawn of Fafurion
				int x = npc.getX();
				int y = npc.getY();
				addSpawn(DETRACTOR2, x + 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x + 100, y - 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR2, x - 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x - 100, y - 100, npc.getZ(), 0, false, 40000);
				startQuestTimer("first_spawn", 2000, npc, null); // timer to delay timer "1"
				startQuestTimer("second_spawn", 4000, npc, null); // timer to delay timer "2"
				startQuestTimer("third_spawn", 8000, npc, null); // timer to delay timer "3"
				startQuestTimer("fourth_spawn", 10000, npc, null); // timer to delay timer "4"
				startQuestTimer("fafurion_poison", 3000, npc, null, true); // Every three seconds reduces Fafurions hp like it is poisoned
				startQuestTimer("fafurion_despawn", 120000, npc, null); // Fafurion Kindred disappears after two minutes
			}
		}
	}
	
	private void spawnShade(L2Character attacker, int npcId, int x, int y, int z) {
		final L2Npc shade = addSpawn(npcId, x, y, z, 0, false, 0);
		shade.setRunning();
		((L2Attackable) shade).addDamageHate(attacker, 0, 999);
		shade.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
	}
}
