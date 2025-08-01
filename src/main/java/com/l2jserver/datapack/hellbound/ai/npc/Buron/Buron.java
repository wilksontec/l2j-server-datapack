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
package com.l2jserver.datapack.hellbound.ai.npc.Buron;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Buron AI.
 * @author DS
 */
public final class Buron extends AbstractNpcAI {
	private static final int BURON = 32345;
	private static final int HELMET = 9669;
	private static final int TUNIC = 9670;
	private static final int PANTS = 9671;
	private static final int DARION_BADGE = 9674;
	
	public Buron() {
		bindFirstTalk(BURON);
		bindStartNpc(BURON);
		bindTalk(BURON);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		if ("Rumor".equalsIgnoreCase(event)) {
			htmltext = "32345-" + HellboundEngine.getInstance().getLevel() + "r.htm";
		} else {
			if (HellboundEngine.getInstance().getLevel() < 2) {
				htmltext = "32345-lowlvl.htm";
			} else {
				if (getQuestItemsCount(player, DARION_BADGE) >= 10) {
					takeItems(player, DARION_BADGE, 10);
					if (event.equalsIgnoreCase("Tunic")) {
						player.addItem("Quest", TUNIC, 1, npc, true);
					} else if (event.equalsIgnoreCase("Helmet")) {
						player.addItem("Quest", HELMET, 1, npc, true);
					} else if (event.equalsIgnoreCase("Pants")) {
						player.addItem("Quest", PANTS, 1, npc, true);
					}
					htmltext = null;
				} else {
					htmltext = "32345-noitems.htm";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		getQuestState(player, true);
		return switch (HellboundEngine.getInstance().getLevel()) {
			case 1 -> "32345-01.htm";
			case 2, 3, 4 -> "32345-02.htm";
			default -> "32345-01a.htm";
		};
	}
}