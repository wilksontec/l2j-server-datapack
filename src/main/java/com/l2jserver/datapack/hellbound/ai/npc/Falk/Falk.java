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
package com.l2jserver.datapack.hellbound.ai.npc.Falk;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Falk AI.
 * @author DS
 */
public final class Falk extends AbstractNpcAI {
	// NPCs
	private static final int FALK = 32297;
	// Items
	private static final int DARION_BADGE = 9674;
	private static final int BASIC_CERT = 9850; // Basic Caravan Certificate
	private static final int STANDART_CERT = 9851; // Standard Caravan Certificate
	private static final int PREMIUM_CERT = 9852; // Premium Caravan Certificate
	
	public Falk() {
		bindFirstTalk(FALK);
		bindStartNpc(FALK);
		bindTalk(FALK);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
			return "32297-01a.htm";
		}
		return "32297-01.htm";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
			return "32297-01a.htm";
		}
		return "32297-02.htm";
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("badges")) {
			if (!hasAtLeastOneQuestItem(player, BASIC_CERT, STANDART_CERT, PREMIUM_CERT)) {
				if (getQuestItemsCount(player, DARION_BADGE) >= 20) {
					takeItems(player, DARION_BADGE, 20);
					giveItems(player, BASIC_CERT, 1);
					return "32297-02a.htm";
				}
				return "32297-02b.htm";
			}
		}
		return super.onEvent(event, npc, player);
	}
}