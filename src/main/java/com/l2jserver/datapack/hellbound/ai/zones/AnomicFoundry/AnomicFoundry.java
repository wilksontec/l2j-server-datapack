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
package com.l2jserver.datapack.hellbound.ai.zones.AnomicFoundry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.AttackableAggroRangeEnter;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Anomic Foundry.
 * @author GKR
 */
public final class AnomicFoundry extends AbstractNpcAI {
	// NPCs
	private static int LABORER = 22396;
	private static int FOREMAN = 22397;
	private static int LESSER_EVIL = 22398;
	private static int GREATER_EVIL = 22399;
	// Misc
	private final Map<Integer, Integer> _atkIndex = new ConcurrentHashMap<>();
	// npcId, x, y, z, heading, max count
	//@formatter:off
	private static int[][] SPAWNS =
	{
		{LESSER_EVIL, 27883, 248613, -3209, -13248, 5},
		{LESSER_EVIL, 26142, 246442, -3216, 7064, 5},
		{LESSER_EVIL, 27335, 246217, -3668, -7992, 5},
		{LESSER_EVIL, 28486, 245913, -3698, 0, 10},
		{GREATER_EVIL, 28684, 244118, -3700, -22560, 10},
	};
	//@formatter:on
	
	private int respawnTime = 60000;
	private final int respawnMin = 20000;
	private final int respawnMax = 300000;
	
	private final int[] _spawned = {
		0,
		0,
		0,
		0,
		0
	};
	
	public AnomicFoundry() {
		bindAggroRangeEnter(LABORER);
		bindAttack(LABORER);
		bindKill(LABORER, LESSER_EVIL, GREATER_EVIL);
		bindSpawn(LABORER, LESSER_EVIL, GREATER_EVIL);
		bindTeleport(LABORER, LESSER_EVIL, GREATER_EVIL);
		startQuestTimer("make_spawn_1", respawnTime, null, null);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("make_spawn_1")) {
			if (HellboundEngine.getInstance().getLevel() >= 10) {
				int idx = getRandom(3);
				if (_spawned[idx] < SPAWNS[idx][5]) {
					addSpawn(SPAWNS[idx][0], SPAWNS[idx][1], SPAWNS[idx][2], SPAWNS[idx][3], SPAWNS[idx][4], false, 0, false);
					respawnTime += 10000;
				}
				startQuestTimer("make_spawn_1", respawnTime, null, null);
			}
		} else if (event.equalsIgnoreCase("make_spawn_2")) {
			if (_spawned[4] < SPAWNS[4][5]) {
				addSpawn(SPAWNS[4][0], SPAWNS[4][1], SPAWNS[4][2], SPAWNS[4][3], SPAWNS[4][4], false, 0, false);
			}
		} else if (event.equalsIgnoreCase("return_laborer")) {
			if ((npc != null) && !npc.isDead()) {
				((L2Attackable) npc).returnHome();
			}
		} else if (event.equalsIgnoreCase("reset_respawn_time")) {
			respawnTime = 60000;
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAggroRangeEnter(AttackableAggroRangeEnter event) {
		if (getRandom(10000) < 2000) {
			requestHelp(event.npc(), event.player(), 500, FOREMAN);
			requestHelp(event.npc(), event.player(), 500, LESSER_EVIL);
			requestHelp(event.npc(), event.player(), 500, GREATER_EVIL);
		}
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		int atkIndex = _atkIndex.containsKey(npc.getObjectId()) ? _atkIndex.get(npc.getObjectId()) : 0;
		if (atkIndex == 0) {
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.ENEMY_INVASION_HURRY_UP);
			cancelQuestTimer("return_laborer", npc, null);
			startQuestTimer("return_laborer", 60000, npc, null);
			
			if (respawnTime > respawnMin) {
				respawnTime -= 5000;
			} else if ((respawnTime <= respawnMin) && (getQuestTimer("reset_respawn_time", null, null) == null)) {
				startQuestTimer("reset_respawn_time", 600000, null, null);
			}
		}
		
		if (getRandom(10000) < 2000) {
			atkIndex++;
			_atkIndex.put(npc.getObjectId(), atkIndex);
			requestHelp(npc, attacker, 1000 * atkIndex, FOREMAN);
			requestHelp(npc, attacker, 1000 * atkIndex, LESSER_EVIL);
			requestHelp(npc, attacker, 1000 * atkIndex, GREATER_EVIL);
			if (getRandom(10) < 1) {
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location((npc.getX() + getRandom(-800, 800)), (npc.getY() + getRandom(-800, 800)), npc.getZ(), npc.getHeading()));
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (getSpawnGroup(npc) >= 0) {
			_spawned[getSpawnGroup(npc)]--;
			SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
		} else if (npc.getId() == LABORER) {
			if (getRandom(10000) < 8000) {
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.PROCESS_SHOULDNT_BE_DELAYED_BECAUSE_OF_ME);
				if (respawnTime < respawnMax) {
					respawnTime += 10000;
				} else if ((respawnTime >= respawnMax) && (getQuestTimer("reset_respawn_time", null, null) == null)) {
					startQuestTimer("reset_respawn_time", 600000, null, null);
				}
			}
			_atkIndex.remove(npc.getObjectId());
		}
		
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		SpawnTable.getInstance().addNewSpawn(npc.getSpawn(), false);
		if (getSpawnGroup(npc) >= 0) {
			_spawned[getSpawnGroup(npc)]++;
		}
		
		if (npc.getId() == LABORER) {
			npc.setIsNoRndWalk(true);
		}
	}
	
	@Override
	protected void onTeleport(L2Npc npc) {
		if ((getSpawnGroup(npc) >= 0) && (getSpawnGroup(npc) <= 2)) {
			_spawned[getSpawnGroup(npc)]--;
			SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
			npc.scheduleDespawn(100);
			if (_spawned[3] < SPAWNS[3][5]) {
				addSpawn(SPAWNS[3][0], SPAWNS[3][1], SPAWNS[3][2], SPAWNS[3][3], SPAWNS[3][4], false, 0, false);
			}
		} else if (getSpawnGroup(npc) == 3) {
			startQuestTimer("make_spawn_2", respawnTime * 2, null, null);
			_spawned[3]--;
			SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
			npc.scheduleDespawn(100);
		}
	}
	
	private static int getSpawnGroup(L2Npc npc) {
		final int coordX = npc.getSpawn().getX();
		final int coordY = npc.getSpawn().getY();
		final int npcId = npc.getId();
		
		for (int i = 0; i < 5; i++) {
			if ((SPAWNS[i][0] == npcId) && (SPAWNS[i][1] == coordX) && (SPAWNS[i][2] == coordY)) {
				return i;
			}
		}
		return -1;
	}
	
	// Zoey76: TODO: This should be done with onFactionCall(..)
	private static void requestHelp(L2Npc requester, L2PcInstance agressor, int range, int helperId) {
		for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(helperId)) {
			final L2MonsterInstance monster = (L2MonsterInstance) spawn.getLastSpawn();
			if ((monster != null) && (agressor != null) && !monster.isDead() && monster.isInsideRadius(requester, range, true, false) && !agressor.isDead()) {
				monster.addDamageHate(agressor, 0, 1000);
			}
		}
	}
}