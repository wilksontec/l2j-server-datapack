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
import com.l2jserver.gameserver.util.Util;

/**
 * Dust Rider AI.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class DustRider extends AbstractNpcAI {
	private static final int DUST_RIDER = 25719;
	// Skills
	private static final SkillHolder NPC_HASTE_LVL_3 = new SkillHolder(6914, 3);
	// Variables
	private static final String CAST_FLAG = "CAST_FLAG";
	// Misc
	private static final int MAX_CHASE_DIST = 2500;
	private static final double MIN_HP_PERCENTAGE = 0.30;
	
	public DustRider() {
		bindAttack(DUST_RIDER);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (Util.calculateDistance(npc, npc.getSpawn(), false, false) > MAX_CHASE_DIST) {
			npc.teleToLocation(npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ());
		}
		
		if (!npc.getVariables().getBoolean(CAST_FLAG, false) && (npc.getCurrentHp() < (npc.getMaxHp() * MIN_HP_PERCENTAGE))) {
			npc.getVariables().set(CAST_FLAG, true);
			addSkillCastDesire(npc, npc, NPC_HASTE_LVL_3, 99999999999000000L);
		}
	}
}
