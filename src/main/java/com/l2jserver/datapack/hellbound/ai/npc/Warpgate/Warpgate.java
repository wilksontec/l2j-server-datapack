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
package com.l2jserver.datapack.hellbound.ai.npc.Warpgate;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.datapack.quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import com.l2jserver.datapack.quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneType;

/**
 * Warpgate teleport AI.
 * @author _DS_
 */
public final class Warpgate extends AbstractNpcAI {
	// NPCs
	private static final int[] WARPGATES = {
		32314,
		32315,
		32316,
		32317,
		32318,
		32319,
	};
	// Locations
	private static final Location ENTER_LOC = new Location(-11272, 236464, -3248);
	private static final Location REMOVE_LOC = new Location(-16555, 209375, -3670);
	// Item
	private static final int MAP = 9994;
	// Misc
	private static final int ZONE = 40101;
	
	public Warpgate() {
		bindStartNpc(WARPGATES);
		bindFirstTalk(WARPGATES);
		bindTalk(WARPGATES);
		bindEnterZone(ZONE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("enter")) {
			if (canEnter(player)) {
				player.teleToLocation(ENTER_LOC, true);
			} else {
				return "Warpgate-03.html";
			}
		} else if (event.equals("TELEPORT")) {
			player.teleToLocation(REMOVE_LOC, true);
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return HellboundEngine.getInstance().isLocked() ? "Warpgate-01.html" : "Warpgate-02.html";
	}
	
	@Override
	public void onEnterZone(L2Character character, L2ZoneType zone) {
		if (character.isPlayer()) {
			final L2PcInstance player = character.getActingPlayer();
			
			if (!canEnter(player) && !player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isOnEvent()) {
				startQuestTimer("TELEPORT", 1000, null, player);
			} else if (!player.isMinimapAllowed() && hasAtLeastOneQuestItem(player, MAP)) {
				player.setMinimapAllowed(true);
			}
		}
	}
	
	private static boolean canEnter(L2PcInstance player) {
		if (player.isFlying()) {
			return false;
		}
		
		if (general().hellboundWithoutQuest()) {
			return true;
		}
		return (player.hasQuestCompleted(Q00130_PathToHellbound.class.getSimpleName()) || player.hasQuestCompleted(Q00133_ThatsBloodyHot.class.getSimpleName()));
	}
}