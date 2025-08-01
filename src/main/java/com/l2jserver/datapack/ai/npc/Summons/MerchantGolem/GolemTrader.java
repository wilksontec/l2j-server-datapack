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
package com.l2jserver.datapack.ai.npc.Summons.MerchantGolem;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Golem Trader AI.
 * @author Zoey76
 */
public final class GolemTrader extends AbstractNpcAI {
	// NPC
	private static final int GOLEM_TRADER = 13128;
	// Misc
	private static final long DESPAWN = 180000;
	
	public GolemTrader() {
		bindSpawn(GOLEM_TRADER);
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		npc.scheduleDespawn(DESPAWN);
	}
}
