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
package com.l2jserver.datapack.quests.Q00313_CollectSpores;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Collect Spores (313)
 * @author ivantotov
 */
public final class Q00313_CollectSpores extends Quest {
	// NPC
	private static final int HERBIEL = 30150;
	// Item
	private static final QuestItemChanceHolder SPORE_SAC = new QuestItemChanceHolder(1118, 40.0, 10L);
	// Misc
	private static final int MIN_LEVEL = 8;
	// Monster
	private static final int SPORE_FUNGUS = 20509;
	
	public Q00313_CollectSpores() {
		super(313);
		bindStartNpc(HERBIEL);
		bindTalk(HERBIEL);
		bindKill(SPORE_FUNGUS);
		registerQuestItems(SPORE_SAC.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		String htmltext = null;
		switch (event) {
			case "30150-05.htm": {
				if (st.isCreated()) {
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30150-04.htm": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, st.getPlayer(), false)) {
			if (giveItemRandomly(st.getPlayer(), npc, SPORE_SAC, true)) {
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
				htmltext = player.getLevel() >= MIN_LEVEL ? "30150-03.htm" : "30150-02.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						if (!hasItemsAtLimit(st.getPlayer(), SPORE_SAC)) {
							htmltext = "30150-06.html";
						}
						break;
					}
					case 2: {
						if (hasItemsAtLimit(st.getPlayer(), SPORE_SAC)) {
							st.giveAdena(3500, true);
							st.exitQuest(true, true);
							htmltext = "30150-07.html";
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
