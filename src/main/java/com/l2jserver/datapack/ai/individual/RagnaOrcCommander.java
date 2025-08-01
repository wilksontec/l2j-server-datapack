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

/**
 * Ragna Orc Commander AI.
 * @author Zealar
 */
public final class RagnaOrcCommander extends AbstractNpcAI {
	private static final int RAGNA_ORC_COMMANDER = 22694;
	
	public RagnaOrcCommander() {
		bindSpawn(RAGNA_ORC_COMMANDER);
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		spawnMinions(npc, "Privates1");
		spawnMinions(npc, getRandomBoolean() ? "Privates2" : "Privates3");
	}
}
