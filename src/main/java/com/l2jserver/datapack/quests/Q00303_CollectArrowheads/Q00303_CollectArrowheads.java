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
package com.l2jserver.datapack.quests.Q00303_CollectArrowheads;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Collect Arrowheads (303)
 * @author ivantotov
 */
public final class Q00303_CollectArrowheads extends Quest {
	// NPC
	private static final int MINIA = 30029;
	// Item
	private static final QuestItemChanceHolder ORCISH_ARROWHEAD = new QuestItemChanceHolder(963, 40.0, 10L);
	// Misc
	private static final int MIN_LEVEL = 10;
	// Monster
	private static final int TUNATH_ORC_MARKSMAN = 20361;
	
	public Q00303_CollectArrowheads() {
		super(303);
		bindStartNpc(MINIA);
		bindTalk(MINIA);
		bindKill(TUNATH_ORC_MARKSMAN);
		registerQuestItems(ORCISH_ARROWHEAD.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30029-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		QuestState st = getRandomPartyMemberState(killer, 1, 1, npc);
		if (st != null) {
			if (giveItemRandomly(st.getPlayer(), npc, ORCISH_ARROWHEAD, true)) {
				st.setCond(2);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = player.getLevel() >= MIN_LEVEL ? "30029-03.htm" : "30029-02.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						if (!hasItemsAtLimit(st.getPlayer(), ORCISH_ARROWHEAD)) {
							htmltext = "30029-05.html";
						}
						break;
					}
					case 2: {
						if (hasItemsAtLimit(st.getPlayer(), ORCISH_ARROWHEAD)) {
							st.giveAdena(1000, true);
							st.addExpAndSp(2000, 0);
							st.exitQuest(true, true);
							htmltext = "30029-06.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
