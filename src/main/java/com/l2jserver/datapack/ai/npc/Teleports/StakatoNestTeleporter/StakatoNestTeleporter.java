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
package com.l2jserver.datapack.ai.npc.Teleports.StakatoNestTeleporter;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q00240_ImTheOnlyOneYouCanTrust.Q00240_ImTheOnlyOneYouCanTrust;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Stakato Nest Teleport AI.
 * @author Charus
 */
public final class StakatoNestTeleporter extends AbstractNpcAI {
	// Locations
	private final static Location[] LOCS = {
		new Location(80456, -52322, -5640),
		new Location(88718, -46214, -4640),
		new Location(87464, -54221, -5120),
		new Location(80848, -49426, -5128),
		new Location(87682, -43291, -4128)
	};
	// NPC
	private final static int KINTAIJIN = 32640;
	
	public StakatoNestTeleporter() {
		bindStartNpc(KINTAIJIN);
		bindTalk(KINTAIJIN);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		int index = Integer.parseInt(event) - 1;
		
		if (LOCS.length > index) {
			Location loc = LOCS[index];
			
			if (player.getParty() != null) {
				for (L2PcInstance partyMember : player.getParty().getMembers()) {
					if (partyMember.isInsideRadius(player, 1000, true, true)) {
						partyMember.teleToLocation(loc, true);
					}
				}
			}
			player.teleToLocation(loc, false);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		return (player.hasQuestCompleted(Q00240_ImTheOnlyOneYouCanTrust.class.getSimpleName()) ? "32640.htm" : "32640-no.htm");
	}
}