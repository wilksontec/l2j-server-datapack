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
package com.l2jserver.datapack.hellbound.ai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Demon Prince's AI.
 * @author GKR
 */
public final class DemonPrince extends AbstractNpcAI {
	// NPCs
	private static final int DEMON_PRINCE = 25540;
	private static final int FIEND = 25541;
	// Skills
	private static final SkillHolder UD = new SkillHolder(5044, 2);
	private static final SkillHolder[] AOE = {
		new SkillHolder(5376, 4),
		new SkillHolder(5376, 5),
		new SkillHolder(5376, 6),
	};
	
	private static final Map<Integer, Boolean> ATTACK_STATE = new ConcurrentHashMap<>();
	
	public DemonPrince() {
		bindAttack(DEMON_PRINCE);
		bindKill(DEMON_PRINCE);
		bindSpawn(FIEND);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("cast") && (npc != null) && (npc.getId() == FIEND) && !npc.isDead()) {
			npc.doCast(AOE[getRandom(AOE.length)]);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if (!npc.isDead()) {
			if (!ATTACK_STATE.containsKey(npc.getObjectId()) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))) {
				npc.doCast(UD);
				spawnMinions(npc);
				ATTACK_STATE.put(npc.getObjectId(), false);
			} else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.1)) && ATTACK_STATE.containsKey(npc.getObjectId()) && !ATTACK_STATE.get(npc.getObjectId())) {
				npc.doCast(UD);
				spawnMinions(npc);
				ATTACK_STATE.put(npc.getObjectId(), true);
			}
			
			if (getRandom(1000) < 10) {
				spawnMinions(npc);
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		ATTACK_STATE.remove(npc.getObjectId());
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == FIEND) {
			startQuestTimer("cast", 15000, npc, null);
		}
	}
	
	private void spawnMinions(L2Npc master) {
		if ((master != null) && !master.isDead()) {
			final int instanceId = master.getInstanceId();
			final int x = master.getX();
			final int y = master.getY();
			final int z = master.getZ();
			addSpawn(FIEND, x + 200, y, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 200, y, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 100, y - 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x - 100, y + 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x + 100, y - 140, z, 0, false, 0, false, instanceId);
			addSpawn(FIEND, x + 100, y + 140, z, 0, false, 0, false, instanceId);
		}
	}
}