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
package com.l2jserver.datapack.quests.Q00263_OrcSubjugation;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Orc Subjugation (263)
 * @author ivantotov
 */
public final class Q00263_OrcSubjugation extends Quest {
	// NPCs
	private static final int KAYLEEN = 30346;
	// Items
	private static final int ORC_AMULET = 1116;
	private static final int ORC_NECKLACE = 1117;
	// Misc
	private static final int MIN_LEVEL = 8;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static {
		MONSTERS.put(20385, ORC_AMULET); // Balor Orc Archer
		MONSTERS.put(20386, ORC_NECKLACE); // Balor Orc Fighter
		MONSTERS.put(20387, ORC_NECKLACE); // Balor Orc Fighter Leader
		MONSTERS.put(20388, ORC_NECKLACE); // Balor Orc Lieutenant
	}
	
	public Q00263_OrcSubjugation() {
		super(263);
		bindStartNpc(KAYLEEN);
		bindTalk(KAYLEEN);
		bindKill(MONSTERS.keySet());
		registerQuestItems(ORC_AMULET, ORC_NECKLACE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "30346-04.htm": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30346-07.html": {
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30346-08.html": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && (getRandom(10) > 4)) {
			st.giveItems(MONSTERS.get(npc.getId()), 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.DARK_ELF) ? (player.getLevel() >= MIN_LEVEL) ? "30346-03.htm" : "30346-02.htm" : "30346-01.htm";
				break;
			}
			case State.STARTED: {
				if (hasAtLeastOneQuestItem(player, getRegisteredItemIds())) {
					final long amulets = st.getQuestItemsCount(ORC_AMULET);
					final long necklaces = st.getQuestItemsCount(ORC_NECKLACE);
					st.giveAdena(((amulets * 20) + (necklaces * 30) + ((amulets + necklaces) >= 10 ? 1100 : 0)), true);
					takeItems(player, -1, getRegisteredItemIds());
					htmltext = "30346-06.html";
				} else {
					htmltext = "30346-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}