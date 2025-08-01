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
package com.l2jserver.datapack.quests.Q00324_SweetestVenom;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Sweetest Venom (324)
 * @author xban1x
 */
public class Q00324_SweetestVenom extends Quest {
	// NPCs
	private static final int ASTARON = 30351;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	static {
		MONSTERS.put(20034, 26);
		MONSTERS.put(20038, 29);
		MONSTERS.put(20043, 30);
	}
	// Items
	private static final int VENOM_SAC = 1077;
	// Misc
	private static final int MIN_LVL = 18;
	private static final int REQUIRED_COUNT = 10;
	private static final int ADENA_COUNT = 5810;
	
	public Q00324_SweetestVenom() {
		super(324);
		bindStartNpc(ASTARON);
		bindTalk(ASTARON);
		bindKill(MONSTERS.keySet());
		registerQuestItems(VENOM_SAC);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			if (event.equals("30351-04.htm")) {
				st.startQuest();
				htmltext = event;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = player.getLevel() < MIN_LVL ? "30351-02.html" : "30351-03.htm";
				break;
			}
			case State.STARTED: {
				if (st.isCond(2)) {
					st.giveAdena(ADENA_COUNT, true);
					st.exitQuest(true, true);
					htmltext = "30351-06.html";
				} else {
					htmltext = "30351-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isPet) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1)) {
			long sacs = st.getQuestItemsCount(VENOM_SAC);
			if (sacs < REQUIRED_COUNT) {
				if (getRandom(100) < MONSTERS.get(npc.getId())) {
					st.giveItems(VENOM_SAC, 1);
					if ((++sacs) < REQUIRED_COUNT) {
						st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
					} else {
						st.setCond(2, true);
					}
				}
			}
		}
	}
}
