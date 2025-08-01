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

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Outpost Captain's AI.
 * @author DS
 */
public final class OutpostCaptain extends AbstractNpcAI {
	// NPCs
	private static final int CAPTAIN = 18466;
	private static final int[] DEFENDERS = {
		22357, // Enceinte Defender
		22358, // Enceinte Defender
	};
	private static final int DOORKEEPER = 32351;
	
	public OutpostCaptain() {
		bindKill(CAPTAIN);
		bindSpawn(CAPTAIN, DOORKEEPER);
		bindSpawn(DEFENDERS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("LEVEL_UP")) {
			npc.deleteMe();
			HellboundEngine.getInstance().setLevel(9);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (HellboundEngine.getInstance().getLevel() == 8) {
			addSpawn(DOORKEEPER, npc.getSpawn().getLocation(), false, 0, false);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		npc.setIsNoRndWalk(true);
		
		if (npc.getId() == CAPTAIN) {
			final L2DoorInstance door = DoorData.getInstance().getDoor(20250001);
			if (door != null) {
				door.closeMe();
			}
		} else if (npc.getId() == DOORKEEPER) {
			startQuestTimer("LEVEL_UP", 3000, npc, null);
		}
	}
}