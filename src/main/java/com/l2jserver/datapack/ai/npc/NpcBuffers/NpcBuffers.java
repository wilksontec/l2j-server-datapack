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
package com.l2jserver.datapack.ai.npc.NpcBuffers;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public final class NpcBuffers extends AbstractNpcAI {
	private final NpcBuffersData _npcBuffers = new NpcBuffersData();
	
	public NpcBuffers() {
		for (int npcId : _npcBuffers.getNpcBufferIds()) {
			// TODO: Cleanup once npc rework is finished and default html is configurable.
			bindFirstTalk(npcId);
			bindSpawn(npcId);
		}
	}
	
	// TODO: Cleanup once npc rework is finished and default html is configurable.
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return null;
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		final NpcBufferData data = _npcBuffers.getNpcBuffer(npc.getId());
		for (NpcBufferSkillData skill : data.getSkills()) {
			ThreadPoolManager.getInstance().scheduleAi(new NpcBufferAI(npc, skill), skill.getInitialDelay());
		}
	}
}
