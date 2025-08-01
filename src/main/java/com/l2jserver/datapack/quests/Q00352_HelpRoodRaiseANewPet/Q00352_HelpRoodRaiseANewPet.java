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
package com.l2jserver.datapack.quests.Q00352_HelpRoodRaiseANewPet;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

/**
 * Help Rood Raise A New Pet! (352)
 * @author Adry_85
 */
public final class Q00352_HelpRoodRaiseANewPet extends Quest {
	// NPC
	private static final int ROOD = 31067;
	// Items
	private static final int LIENRIK_EGG1 = 5860;
	private static final int LIENRIK_EGG2 = 5861;
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addGroupedDrop(20786, 48.0) // lienrik
		.withDropItem(LIENRIK_EGG1, 95.83)
		.withDropItem(LIENRIK_EGG2, 4.17)
		.build()
		.addGroupedDrop(21644, 48.0) // lienrik_a
		.withDropItem(LIENRIK_EGG1, 95.83)
		.withDropItem(LIENRIK_EGG2, 4.17)
		.build()
		.addGroupedDrop(21645, 71.0) // lienrik_lad_a
		.withDropItem(LIENRIK_EGG1, 97.18)
		.withDropItem(LIENRIK_EGG2, 2.82)
		.build()
		.build();
	// Misc
	private static final int MIN_LEVEL = 39;
	
	public Q00352_HelpRoodRaiseANewPet() {
		super(352);
		bindStartNpc(ROOD);
		bindTalk(ROOD);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(LIENRIK_EGG1, LIENRIK_EGG2);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "31067-02.htm":
			case "31067-03.htm":
			case "31067-07.html":
			case "31067-10.html": {
				htmltext = event;
				break;
			}
			case "31067-04.htm": {
				qs.setMemoState(1);
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "31067-08.html": {
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !Util.checkIfInRange(1500, npc, killer, true)) {
			return;
		}
		
		giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "31067-01.htm" : "31067-05.html";
		} else if (qs.isStarted()) {
			final long LienrikEgg1Count = getQuestItemsCount(player, LIENRIK_EGG1);
			final long LienrikEgg2Count = getQuestItemsCount(player, LIENRIK_EGG2);
			
			if ((LienrikEgg1Count == 0) && (LienrikEgg2Count == 0)) {
				htmltext = "31067-06.html";
			} else if ((LienrikEgg1Count >= 1) && (LienrikEgg2Count == 0)) {
				if (LienrikEgg1Count >= 10) {
					giveAdena(player, (LienrikEgg1Count * 34) + 4000, true);
				} else {
					giveAdena(player, (LienrikEgg1Count * 34) + 2000, true);
				}
				
				takeItems(player, LIENRIK_EGG1, -1);
				htmltext = "31067-10.html";
			} else if (LienrikEgg1Count >= 1) {
				giveAdena(player, 4000 + ((LienrikEgg1Count * 34) + (LienrikEgg2Count * 1025)), true);
				takeItems(player, LIENRIK_EGG1, -1);
				takeItems(player, LIENRIK_EGG2, -1);
				htmltext = "31067-11.html";
			}
		}
		return htmltext;
	}
}
