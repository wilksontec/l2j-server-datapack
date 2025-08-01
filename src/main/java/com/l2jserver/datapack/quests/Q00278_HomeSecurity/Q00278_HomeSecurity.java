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
package com.l2jserver.datapack.quests.Q00278_HomeSecurity;

import static com.l2jserver.gameserver.model.quest.QuestDroplist.singleDropItem;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Home Security (278)
 * @author malyelfik
 */
public class Q00278_HomeSecurity extends Quest {
	// NPC
	private static final int TUNATUN = 31537;
	private static final int[] MONSTER = {
		18905,
		18906,
		18907
	};
	// Item
	private static final QuestItemChanceHolder SEL_MAHUM_MANE = new QuestItemChanceHolder(15531, 300L);
	
	public Q00278_HomeSecurity() {
		super(278);
		bindStartNpc(TUNATUN);
		bindTalk(TUNATUN);
		bindKill(MONSTER);
		registerQuestItems(SEL_MAHUM_MANE.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "31537-02.htm": {
				htmltext = (player.getLevel() >= 82) ? "31537-02.htm" : "31537-03.html";
				break;
			}
			case "31537-04.htm": {
				st.startQuest();
				break;
			}
			case "31537-07.html": {
				int i0 = getRandom(100);
				
				if (i0 < 10) {
					st.giveItems(960, 1);
				} else if (i0 < 19) {
					st.giveItems(960, 2);
				} else if (i0 < 27) {
					st.giveItems(960, 3);
				} else if (i0 < 34) {
					st.giveItems(960, 4);
				} else if (i0 < 40) {
					st.giveItems(960, 5);
				} else if (i0 < 45) {
					st.giveItems(960, 6);
				} else if (i0 < 49) {
					st.giveItems(960, 7);
				} else if (i0 < 52) {
					st.giveItems(960, 8);
				} else if (i0 < 54) {
					st.giveItems(960, 9);
				} else if (i0 < 55) {
					st.giveItems(960, 10);
				} else if (i0 < 75) {
					st.giveItems(9553, 1);
				} else if (i0 < 90) {
					st.giveItems(9553, 2);
				} else {
					st.giveItems(959, 1);
				}
				
				st.exitQuest(true, true);
				htmltext = "31537-07.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if (st != null) {
			switch (npc.getId()) {
				case 18905 -> { // Farm Ravager (Crazy)
					final long itemCount = ((getRandom(1000) < 486) ? getRandom(6) + 1 : getRandom(5) + 1);
					if (giveItemRandomly(st.getPlayer(), npc, singleDropItem(SEL_MAHUM_MANE, itemCount), SEL_MAHUM_MANE.getLimit(), true)) {
						st.setCond(2);
					}
				}
				case 18906, 18907 -> { // Farm Bandit, Beast Devourer
					if (giveItemRandomly(st.getPlayer(), npc, singleDropItem(SEL_MAHUM_MANE, 85.0), SEL_MAHUM_MANE.getLimit(), true)) {
						st.setCond(2);
					}
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st.isCreated()) {
			htmltext = "31537-01.htm";
		} else if (st.isStarted()) {
			if (st.isCond(1) || !hasItemsAtLimit(player, SEL_MAHUM_MANE)) {
				htmltext = "31537-06.html";
			} else if (st.isCond(2) && hasItemsAtLimit(player, SEL_MAHUM_MANE)) {
				htmltext = "31537-05.html";
			}
		}
		return htmltext;
	}
}
