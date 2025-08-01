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
package com.l2jserver.datapack.quests.Q00267_WrathOfVerdure;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Wrath of Verdure (267)
 * @author xban1x
 */
public final class Q00267_WrathOfVerdure extends Quest {
	// NPC
	private static final int TREANT_BREMEC = 31853;
	// Item
	private static final int GOBLIN_CLUB = 1335;
	// Monster
	private static final int GOBLIN_RAIDER = 20325;
	// Reward
	private static final int SILVERY_LEAF = 1340;
	// Misc
	private static final int MIN_LVL = 4;
	
	public Q00267_WrathOfVerdure() {
		super(267);
		bindStartNpc(TREANT_BREMEC);
		bindTalk(TREANT_BREMEC);
		bindKill(GOBLIN_RAIDER);
		registerQuestItems(GOBLIN_CLUB);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			switch (event) {
				case "31853-04.htm": {
					st.startQuest();
					htmltext = event;
					break;
				}
				case "31853-07.html": {
					st.exitQuest(true, true);
					htmltext = event;
					break;
				}
				case "31853-08.html": {
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
		if ((st != null) && (getRandom(10) < 5)) {
			st.giveItems(GOBLIN_CLUB, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.ELF) ? (player.getLevel() >= MIN_LVL) ? "31853-03.htm" : "31853-02.htm" : "31853-01.htm";
				break;
			}
			case State.STARTED: {
				if (st.hasQuestItems(GOBLIN_CLUB)) {
					final long count = st.getQuestItemsCount(GOBLIN_CLUB);
					st.rewardItems(SILVERY_LEAF, count);
					if (count >= 10) {
						st.giveAdena(600, true);
					}
					st.takeItems(GOBLIN_CLUB, -1);
					htmltext = "31853-06.html";
				} else {
					htmltext = "31853-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
