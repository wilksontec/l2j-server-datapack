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
package com.l2jserver.datapack.quests.Q00009_IntoTheCityOfHumans;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Into the City of Humans (9)
 * @author malyelfik
 */
public class Q00009_IntoTheCityOfHumans extends Quest {
	// NPCs
	private static final int PETUKAI = 30583;
	private static final int TANAPI = 30571;
	private static final int TAMIL = 30576;
	// Items
	private static final int SCROLL_OF_ESCAPE_GIRAN = 7559;
	private static final int MARK_OF_TRAVELER = 7570;
	// Misc
	private static final int MIN_LEVEL = 3;
	
	public Q00009_IntoTheCityOfHumans() {
		super(9);
		bindStartNpc(PETUKAI);
		bindTalk(PETUKAI, TANAPI, TAMIL);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "30583-04.htm":
				st.startQuest();
				break;
			case "30576-02.html":
				st.giveItems(MARK_OF_TRAVELER, 1);
				st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
				st.exitQuest(false, true);
				break;
			case "30571-02.html":
				st.setCond(2, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case PETUKAI:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? (player.getRace() == Race.ORC) ? "30583-01.htm" : "30583-02.html" : "30583-03.html";
						break;
					case State.STARTED:
						if (st.isCond(1)) {
							htmltext = "30583-05.html";
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case TANAPI:
				if (st.isStarted()) {
					htmltext = (st.isCond(1)) ? "30571-01.html" : "30571-03.html";
				}
				break;
			case TAMIL:
				if (st.isStarted() && st.isCond(2)) {
					htmltext = "30576-01.html";
				}
				break;
		}
		return htmltext;
	}
}