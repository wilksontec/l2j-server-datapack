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
package com.l2jserver.datapack.quests.Q00052_WilliesSpecialBait;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Willie's Special Bait (52)<br>
 * Original Jython script by Kilkenny.
 * @author nonom
 */
public class Q00052_WilliesSpecialBait extends Quest {
	// NPCs
	private static final int WILLIE = 31574;
	private static final int TARLK_BASILISK = 20573;
	// Items
	private static final int EARTH_FISHING_LURE = 7612;
	private static final QuestItemChanceHolder TARLK_EYE = new QuestItemChanceHolder(7623, 33.0, 100L);
	
	public Q00052_WilliesSpecialBait() {
		super(52);
		bindStartNpc(WILLIE);
		bindTalk(WILLIE);
		bindKill(TARLK_BASILISK);
		registerQuestItems(TARLK_EYE.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		String htmltext = event;
		switch (event) {
			case "31574-03.htm":
				st.startQuest();
				break;
			case "31574-07.html":
				if (st.isCond(2) && hasItemsAtLimit(st.getPlayer(), TARLK_EYE)) {
					htmltext = "31574-06.htm";
					st.giveItems(EARTH_FISHING_LURE, 4);
					st.exitQuest(false, true);
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		QuestState st = getRandomPartyMemberState(player, 1, 1, npc);
		if ((st != null) && giveItemRandomly(st.getPlayer(), npc, TARLK_EYE, true)) {
			st.setCond(2);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.COMPLETED:
				htmltext = getAlreadyCompletedMsg(player);
				break;
			case State.CREATED:
				htmltext = (player.getLevel() >= 48) ? "31574-01.htm" : "31574-02.html";
				break;
			case State.STARTED:
				htmltext = (st.isCond(1)) ? "31574-05.html" : "31574-04.html";
				break;
		}
		return htmltext;
	}
}
