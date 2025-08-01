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
package com.l2jserver.datapack.quests.Q00259_RequestFromTheFarmOwner;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Request from the Farm Owner (259)
 * @author xban1x
 */
public final class Q00259_RequestFromTheFarmOwner extends Quest {
	// Npcs
	private static final int EDMOND = 30497;
	private static final int MARIUS = 30405;
	// Monsters
	private static final int[] MONSTERS = new int[] {
		20103, // Giant Spider
		20106, // Talon Spider
		20108, // Blade Spider
	};
	// Items
	private static final int SPIDER_SKIN = 1495;
	// Misc
	private static final int MIN_LVL = 15;
	private static final int SKIN_COUNT = 10;
	private static final int SKIN_REWARD = 25;
	private static final int SKIN_BONUS = 250;
	private static final Map<String, ItemHolder> CONSUMABLES = new HashMap<>();
	static {
		CONSUMABLES.put("30405-04.html", new ItemHolder(1061, 2)); // Greater Healing Potion
		CONSUMABLES.put("30405-05.html", new ItemHolder(17, 250)); // Wooden Arrow
		CONSUMABLES.put("30405-05a.html", new ItemHolder(1835, 60)); // Soulshot: No Grade
		CONSUMABLES.put("30405-05c.html", new ItemHolder(2509, 30)); // Spiritshot: No Grade
	}
	
	public Q00259_RequestFromTheFarmOwner() {
		super(259);
		bindStartNpc(EDMOND);
		bindTalk(EDMOND, MARIUS);
		bindKill(MONSTERS);
		registerQuestItems(SPIDER_SKIN);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "30405-03.html":
			case "30405-05b.html":
			case "30405-05d.html":
			case "30497-07.html": {
				htmltext = event;
				break;
			}
			case "30405-04.html":
			case "30405-05.html":
			case "30405-05a.html":
			case "30405-05c.html": {
				if (st.getQuestItemsCount(SPIDER_SKIN) >= SKIN_COUNT) {
					st.giveItems(CONSUMABLES.get(event));
					st.takeItems(SPIDER_SKIN, SKIN_COUNT);
					htmltext = event;
				}
				break;
			}
			case "30405-06.html": {
				htmltext = (st.getQuestItemsCount(SPIDER_SKIN) >= SKIN_COUNT) ? event : "30405-07.html";
				break;
			}
			case "30497-03.html": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30497-06.html": {
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if (st != null) {
			st.giveItems(SPIDER_SKIN, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId()) {
			case EDMOND: {
				switch (st.getState()) {
					case State.CREATED: {
						htmltext = (player.getLevel() >= MIN_LVL) ? "30497-02.htm" : "30497-01.html";
						break;
					}
					case State.STARTED: {
						if (st.hasQuestItems(SPIDER_SKIN)) {
							final long skins = st.getQuestItemsCount(SPIDER_SKIN);
							st.giveAdena((skins * SKIN_REWARD) + ((skins >= 10) ? SKIN_BONUS : 0), true);
							st.takeItems(SPIDER_SKIN, -1);
							htmltext = "30497-05.html";
						} else {
							htmltext = "30497-04.html";
						}
						break;
					}
				}
				break;
			}
			case MARIUS: {
				htmltext = (st.getQuestItemsCount(SPIDER_SKIN) >= SKIN_COUNT) ? "30405-02.html" : "30405-01.html";
				break;
			}
		}
		return htmltext;
	}
}
