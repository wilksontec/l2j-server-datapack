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
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.AttackableAggroRangeEnter;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Giant's Cave AI.
 * @author Gnacik, St3eT
 */
public final class GiantsCave extends AbstractNpcAI {
	// NPC
	private static final int[] SCOUTS = {
		22668, // Gamlin (Scout)
		22669, // Leogul (Scout)
	};
	
	public GiantsCave() {
		bindAttack(SCOUTS);
		bindAggroRangeEnter(SCOUTS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("ATTACK") && (player != null) && (npc != null) && !npc.isDead()) {
			if (npc.getId() == SCOUTS[0]) // Gamlin
			{
				broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId._INTRUDER_DETECTED);
			} else {
				broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.OH_GIANTS_AN_INTRUDER_HAS_BEEN_DISCOVERED);
			}
			
			for (L2Character characters : npc.getKnownList().getKnownCharactersInRadius(450)) {
				if ((characters != null) && (characters.isAttackable()) && (getRandomBoolean())) {
					addAttackDesire((L2Npc) characters, player);
				}
			}
		} else if (event.equals("CLEAR") && (npc != null) && !npc.isDead()) {
			npc.setScriptValue(0);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (npc.isScriptValue(0)) {
			npc.setScriptValue(1);
			startQuestTimer("ATTACK", 6000, npc, attacker);
			startQuestTimer("CLEAR", 120000, npc, null);
		}
	}
	
	@Override
	public void onAggroRangeEnter(AttackableAggroRangeEnter event) {
		final var npc = event.npc();
		if (npc.isScriptValue(0)) {
			npc.setScriptValue(1);
			if (getRandomBoolean()) {
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.YOU_GUYS_ARE_DETECTED);
			} else {
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.WHAT_KIND_OF_CREATURES_ARE_YOU);
			}
			startQuestTimer("ATTACK", 6000, npc, event.player());
			startQuestTimer("CLEAR", 120000, npc, null);
		}
	}
}