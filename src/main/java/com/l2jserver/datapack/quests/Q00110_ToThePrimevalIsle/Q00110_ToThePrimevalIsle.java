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
package com.l2jserver.datapack.quests.Q00110_ToThePrimevalIsle;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * To the Primeval Isle (110)
 * @author Adry_85
 */
public class Q00110_ToThePrimevalIsle extends Quest {
	// NPCs
	private static final int ANTON = 31338;
	private static final int MARQUEZ = 32113;
	// Item
	private static final int ANCIENT_BOOK = 8777;
	
	public Q00110_ToThePrimevalIsle() {
		super(110);
		bindStartNpc(ANTON);
		bindTalk(ANTON, MARQUEZ);
		registerQuestItems(ANCIENT_BOOK);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "31338-1.html":
				st.giveItems(ANCIENT_BOOK, 1);
				st.startQuest();
				break;
			case "32113-2.html":
			case "32113-2a.html":
				st.giveAdena(191678, true);
				st.addExpAndSp(251602, 25245);
				st.exitQuest(false, true);
				break;
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case ANTON:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() < 75) ? "31338-0a.htm" : "31338-0b.htm";
						break;
					case State.STARTED:
						htmltext = "31338-1a.html";
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case MARQUEZ:
				if (st.isCond(1)) {
					htmltext = "32113-1.html";
				}
				break;
		}
		return htmltext;
	}
}
