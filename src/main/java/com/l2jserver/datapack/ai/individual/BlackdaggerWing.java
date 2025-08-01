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

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcSkillFinished;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.util.Util;

/**
 * Blackdagger Wing AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class BlackdaggerWing extends AbstractNpcAI {
	// NPCs
	private static final int BLACKDAGGER_WING = 25721;
	// Skills
	private static final SkillHolder POWER_STRIKE = new SkillHolder(6833, 1);
	private static final SkillHolder RANGE_MAGIC_ATTACK = new SkillHolder(6834, 1);
	// Variables
	private static final String MID_HP_FLAG = "MID_HP_FLAG";
	private static final String POWER_STRIKE_CAST_COUNT = "POWER_STRIKE_CAST_COUNT";
	// Timers
	private static final String DAMAGE_TIMER = "DAMAGE_TIMER";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MID_HP_PERCENTAGE = 0.50;
	
	public BlackdaggerWing() {
		bindAttack(BLACKDAGGER_WING);
		bindSeeCreature(BLACKDAGGER_WING);
		bindSpellFinished(BLACKDAGGER_WING);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST) {
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if ((npc.getCurrentHp() < (npc.getMaxHp() * MID_HP_PERCENTAGE)) && !npc.getVariables().getBoolean(MID_HP_FLAG, false)) {
			npc.getVariables().set(MID_HP_FLAG, true);
			startQuestTimer(DAMAGE_TIMER, 10000, npc, attacker);
		}
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (npc.getVariables().getBoolean(MID_HP_FLAG, false)) {
			final L2Character mostHated = ((L2Attackable) npc).getMostHated();
			if ((mostHated != null) && mostHated.isPlayer() && (mostHated != creature)) {
				if (getRandom(5) < 1) {
					addSkillCastDesire(npc, creature, RANGE_MAGIC_ATTACK, 9999900000000000L);
				}
			}
		}
	}
	
	@Override
	public void onSpellFinished(NpcSkillFinished event) {
		if (event.skill().getId() == POWER_STRIKE.getSkillId()) {
			final var npc = event.npc();
			npc.getVariables().set(POWER_STRIKE_CAST_COUNT, npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) + 1);
			if (npc.getVariables().getInt(POWER_STRIKE_CAST_COUNT) > 3) {
				addSkillCastDesire(npc, event.player(), RANGE_MAGIC_ATTACK, 9999900000000000L);
				npc.getVariables().set(POWER_STRIKE_CAST_COUNT, 0);
			}
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (DAMAGE_TIMER.equals(event)) {
			npc.getAI().setIntention(AI_INTENTION_ATTACK);
			startQuestTimer(DAMAGE_TIMER, 30000, npc, player);
		}
		return super.onEvent(event, npc, player);
	}
}
