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
package com.l2jserver.datapack.quests.Q00157_RecoverSmuggledGoods;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Recover Smuggled Goods (157)
 * @author xban1x
 */
public class Q00157_RecoverSmuggledGoods extends Quest {
	// NPC
	private static final int WILFORD = 30005;
	// Monster
	private static final int GIANT_TOAD = 20121;
	// Items
	private static final int BUCKLER = 20;
	private static final int ADAMANTITE_ORE = 1024;
	// Misc
	private static final int MIN_LVL = 5;
	
	public Q00157_RecoverSmuggledGoods() {
		super(157);
		bindStartNpc(WILFORD);
		bindTalk(WILFORD);
		bindKill(GIANT_TOAD);
		registerQuestItems(ADAMANTITE_ORE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			switch (event) {
				case "30005-03.htm": {
					htmltext = event;
					break;
				}
				case "30005-04.htm": {
					st.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1) && (getRandom(10) < 4) && (st.getQuestItemsCount(ADAMANTITE_ORE) < 20)) {
			st.giveItems(ADAMANTITE_ORE, 1);
			if (st.getQuestItemsCount(ADAMANTITE_ORE) >= 20) {
				st.setCond(2, true);
			} else {
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = player.getLevel() >= MIN_LVL ? "30005-02.htm" : "30005-01.htm";
				break;
			}
			case State.STARTED: {
				if (st.isCond(2) && (st.getQuestItemsCount(ADAMANTITE_ORE) >= 20)) {
					st.giveItems(BUCKLER, 1);
					st.exitQuest(false, true);
					htmltext = "30005-06.html";
				} else {
					htmltext = "30005-05.html";
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
