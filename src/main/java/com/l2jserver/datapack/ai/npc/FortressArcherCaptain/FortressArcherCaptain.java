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
package com.l2jserver.datapack.ai.npc.FortressArcherCaptain;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Fortress Archer Captain AI.
 * @author St3eT
 */
public final class FortressArcherCaptain extends AbstractNpcAI {
	// NPCs
	private static final int[] ARCHER_CAPTAIN = {
		35661, // Shanty Fortress
		35692, // Southern Fortress
		35730, // Hive Fortress
		35761, // Valley Fortress
		35799, // Ivory Fortress
		35830, // Narsell Fortress
		35861, // Bayou Fortress
		35899, // White Sands Fortress
		35930, // Borderland Fortress
		35968, // Swamp Fortress
		36006, // Archaic Fortress
		36037, // Floran Fortress
		36075, // Cloud Mountain
		36113, // Tanor Fortress
		36144, // Dragonspine Fortress
		36175, // Antharas's Fortress
		36213, // Western Fortress
		36251, // Hunter's Fortress
		36289, // Aaru Fortress
		36320, // Demon Fortress
		36358, // Monastic Fortress
	};
	
	public FortressArcherCaptain() {
		bindStartNpc(ARCHER_CAPTAIN);
		bindFirstTalk(ARCHER_CAPTAIN);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final int fortOwner = npc.getFort().getOwnerClan() == null ? 0 : npc.getFort().getOwnerClan().getId();
		return ((player.getClan() != null) && (player.getClanId() == fortOwner)) ? "FortressArcherCaptain.html" : "FortressArcherCaptain-01.html";
	}
}