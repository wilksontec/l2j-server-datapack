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
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Gordon AI
 * @author TOFIZ, malyelfik
 */
public final class Gordon extends AbstractNpcAI {
	private static final int GORDON = 29095;
	
	public Gordon() {
		bindSpawn(GORDON);
		bindSeeCreature(GORDON);
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (creature.isPlayer() && ((L2PcInstance) creature).isCursedWeaponEquipped()) {
			addAttackDesire(npc, creature);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		((L2Attackable) npc).setCanReturnToSpawnPoint(false);
	}
}