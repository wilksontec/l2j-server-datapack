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
package com.l2jserver.datapack.quests.Q00258_BringWolfPelts;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Bring Wolf Pelts (258)
 * @author xban1x
 */
public final class Q00258_BringWolfPelts extends Quest {
	// Npc
	private static final int LECTOR = 30001;
	// Item
	private static final int WOLF_PELT = 702;
	// Monsters
	private static final int[] MONSTERS = new int[] {
		20120, // Wolf
		20442, // Elder Wolf
	};
	// Rewards
	private static final Map<Integer, Integer> REWARDS = new HashMap<>();
	static {
		REWARDS.put(390, 1); // Cotton Shirt
		REWARDS.put(29, 6); // Leather Pants
		REWARDS.put(22, 9); // Leather Shirt
		REWARDS.put(1119, 13); // Short Leather Gloves
		REWARDS.put(426, 16); // Tunic
	}
	// Misc
	private static final int MIN_LVL = 3;
	private static final int WOLF_PELT_COUNT = 40;
	
	public Q00258_BringWolfPelts() {
		super(258);
		bindStartNpc(LECTOR);
		bindTalk(LECTOR);
		bindKill(MONSTERS);
		registerQuestItems(WOLF_PELT);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equalsIgnoreCase("30001-03.html")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1)) {
			st.giveItems(WOLF_PELT, 1);
			if (st.getQuestItemsCount(WOLF_PELT) >= WOLF_PELT_COUNT) {
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
				htmltext = (player.getLevel() >= MIN_LVL) ? "30001-02.htm" : "30001-01.html";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "30001-04.html";
						break;
					}
					case 2: {
						if (st.getQuestItemsCount(WOLF_PELT) >= WOLF_PELT_COUNT) {
							final int chance = getRandom(16);
							for (Map.Entry<Integer, Integer> reward : REWARDS.entrySet()) {
								if (chance < reward.getValue()) {
									st.giveItems(reward.getKey(), 1);
									break;
								}
							}
							st.exitQuest(true, true);
							htmltext = "30001-05.html";
							break;
						}
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
