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
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Simple AI that manages special conditions for Divine Beast summon.
 * @author UnAfraid
 */
public final class DivineBeast extends AbstractNpcAI {
	private static final int DIVINE_BEAST = 14870;
	private static final int TRANSFORMATION_ID = 258;
	private static final int CHECK_TIME = 2 * 1000;
	
	public DivineBeast() {
		bindSummonSpawn(DIVINE_BEAST);
	}
	
	@Override
	public void onSummonSpawn(L2Summon summon) {
		startQuestTimer("VALIDATE_TRANSFORMATION", CHECK_TIME, null, summon.getActingPlayer(), true);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if ((player == null) || !player.hasServitor()) {
			cancelQuestTimer(event, npc, player);
		} else if (player.getTransformationId() != TRANSFORMATION_ID) {
			cancelQuestTimer(event, npc, player);
			player.getSummon().unSummon(player);
		}
		
		return super.onEvent(event, npc, player);
	}
}
