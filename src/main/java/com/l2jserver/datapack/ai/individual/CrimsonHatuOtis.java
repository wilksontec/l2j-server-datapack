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

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * AI for Kamaloka (33) - Crimson Hatu Otis
 * @author Gladicek
 */
public final class CrimsonHatuOtis extends AbstractNpcAI {
	// NPC
	private static final int CRIMSON_HATU_OTIS = 18558;
	// Skills
	private static final SkillHolder BOSS_SPINING_SLASH = new SkillHolder(4737);
	private static final SkillHolder BOSS_HASTE = new SkillHolder(4175);
	
	public CrimsonHatuOtis() {
		bindAttack(CRIMSON_HATU_OTIS);
		bindKill(CRIMSON_HATU_OTIS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "SKILL": {
				if (npc.isDead()) {
					cancelQuestTimer("SKILL", npc, null);
					return null;
				}
				npc.setTarget(player);
				npc.doCast(BOSS_SPINING_SLASH);
				startQuestTimer("SKILL", 60000, npc, null);
				break;
			}
			case "BUFF": {
				if (npc.isScriptValue(2)) {
					npc.setTarget(npc);
					npc.doCast(BOSS_HASTE);
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (npc.isScriptValue(0)) {
			npc.setScriptValue(1);
			startQuestTimer("SKILL", 5000, npc, null);
		} else if (npc.isScriptValue(1) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.3))) {
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.IVE_HAD_IT_UP_TO_HERE_WITH_YOU_ILL_TAKE_CARE_OF_YOU);
			npc.setScriptValue(2);
			startQuestTimer("BUFF", 1000, npc, null);
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		cancelQuestTimer("SKILL", npc, null);
		cancelQuestTimer("BUFF", npc, null);
	}
}