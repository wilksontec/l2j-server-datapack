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
package com.l2jserver.datapack.quests.Q00701_ProofOfExistence;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Proof of Existence (701)
 * @author malyelfik
 */
public class Q00701_ProofOfExistence extends Quest {
	// NPC
	private static final int ARTIUS = 32559;
	// Items
	private static final int DEADMANS_REMAINS = 13875;
	private static final int BANSHEE_QUEENS_EYE = 13876;
	// Monsters
	private static final int ENIRA = 25625;
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	static {
		MOBS.put(22606, 518); // Floating Skull
		MOBS.put(22607, 858); // Floating Skull
		MOBS.put(22608, 482); // Floating Zombie
		MOBS.put(22609, 466); // Floating Zombie
		MOBS.put(25629, 735); // Floating Skull (Enira's Evil Spirit)
		MOBS.put(25630, 391); // Floating Zombie (Enira's Evil Spirit)
	}
	// Misc
	private static final int MIN_LEVEL = 78;
	
	public Q00701_ProofOfExistence() {
		super(701);
		bindStartNpc(ARTIUS);
		bindTalk(ARTIUS);
		bindKill(MOBS.keySet());
		bindKill(ENIRA);
		registerQuestItems(DEADMANS_REMAINS, BANSHEE_QUEENS_EYE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		String htmltext = event;
		switch (event) {
			case "32559-03.htm":
			case "32559-08.html":
				break;
			case "32559-04.htm":
				st.startQuest();
				break;
			case "32559-09.html":
				st.exitQuest(true, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance member = getRandomPartyMember(player, 1);
		if (member == null) {
			return;
		}
		final QuestState st = getQuestState(member, false);
		if (npc.getId() == ENIRA) {
			final int chance = getRandom(1000);
			final int count;
			if (chance < 708) {
				count = getRandom(2) + 1;
			} else if (chance < 978) {
				count = getRandom(3) + 3;
			} else if (chance < 994) {
				count = getRandom(4) + 6;
			} else if (chance < 998) {
				count = getRandom(4) + 10;
			} else {
				count = getRandom(5) + 14;
			}
			st.giveItems(BANSHEE_QUEENS_EYE, count);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		} else if (getRandom(1000) < MOBS.get(npc.getId())) {
			st.giveItems(DEADMANS_REMAINS, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				htmltext = ((player.getLevel() >= MIN_LEVEL) && player.hasQuestCompleted(Q10273_GoodDayToFly.class.getSimpleName())) ? "32559-01.htm" : "32559-02.htm";
				break;
			case State.STARTED:
				if (st.hasQuestItems(BANSHEE_QUEENS_EYE)) {
					st.giveAdena((st.getQuestItemsCount(DEADMANS_REMAINS) * 2500) + (st.getQuestItemsCount(BANSHEE_QUEENS_EYE) * 50000) + 23835, true);
					st.takeItems(BANSHEE_QUEENS_EYE, -1);
					st.takeItems(DEADMANS_REMAINS, -1);
					htmltext = "32559-07.html";
				} else if (st.hasQuestItems(DEADMANS_REMAINS)) {
					st.giveAdena(st.getQuestItemsCount(DEADMANS_REMAINS) * 2500, true);
					st.takeItems(DEADMANS_REMAINS, -1);
					htmltext = "32559-06.html";
				} else {
					htmltext = "32559-05.html";
				}
				break;
		}
		return htmltext;
	}
}