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
package com.l2jserver.datapack.quests.Q00133_ThatsBloodyHot;

import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.datapack.quests.Q00131_BirdInACage.Q00131_BirdInACage;
import com.l2jserver.gameserver.instancemanager.GlobalVariablesManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * That's Bloody Hot! (133)
 * @author Zoey76
 */
public class Q00133_ThatsBloodyHot extends Quest {
	// NPCs
	private static final int KANIS = 32264;
	private static final int GALATE = 32292;
	// Item
	private static final int REFINED_CRYSTAL_SAMPLE = 9785;
	// Misc
	private static final int MIN_LEVEL = 78;
	
	public Q00133_ThatsBloodyHot() {
		super(133);
		bindStartNpc(KANIS);
		bindTalk(KANIS, GALATE);
		registerQuestItems(REFINED_CRYSTAL_SAMPLE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "32264-04.html": {
				if (player.getLevel() >= MIN_LEVEL) {
					st.startQuest();
					htmltext = event;
				}
				break;
			}
			case "32264-06.html":
			case "32264-07.html": {
				if (st.isCond(1)) {
					htmltext = event;
				}
				break;
			}
			case "32264-08.html": {
				st.setCond(2);
				htmltext = event;
				break;
			}
			case "32264-10.html":
			case "32264-11.html": {
				if (st.isCond(2)) {
					htmltext = event;
				}
				break;
			}
			case "32264-12.html": {
				if (st.isCond(2)) {
					st.giveItems(REFINED_CRYSTAL_SAMPLE, 1);
					st.setCond(3);
					htmltext = event;
				}
				break;
			}
			case "32292-03.html": {
				if (st.isCond(3)) {
					htmltext = event;
				}
				break;
			}
			case "32292-05.html": {
				if (st.isCond(3) && st.hasQuestItems(REFINED_CRYSTAL_SAMPLE)) {
					st.takeItems(REFINED_CRYSTAL_SAMPLE, -1);
					htmltext = event;
					st.setCond(4);
				}
				break;
			}
			case "32292-06.html": {
				if (st.isCond(4)) {
					if (GlobalVariablesManager.getInstance().hasVariable("HBLevel")) {
						st.giveAdena(254247, true);
						st.addExpAndSp(331457, 32524);
						st.exitQuest(false, true);
						htmltext = event;
					} else {
						HellboundEngine.getInstance().setLevel(1);
						st.giveAdena(254247, true);
						st.addExpAndSp(325881, 32524);
						st.exitQuest(false, true);
						htmltext = "32292-07.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
			case State.CREATED: {
				if (npc.getId() == KANIS) {
					if (player.hasQuestCompleted(Q00131_BirdInACage.class.getSimpleName())) {
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32264-01.htm" : "32264-02.html";
					} else {
						htmltext = "32264-03.html";
					}
				}
				break;
			}
			case State.STARTED: {
				if (npc.getId() == KANIS) {
					if (st.isCond(1)) {
						htmltext = "32264-05.html";
					} else if (st.isCond(2)) {
						htmltext = "32264-09.html";
					} else if (st.getCond() >= 3) {
						htmltext = "32264-13.html";
					}
				} else if (npc.getId() == GALATE) {
					if (st.getCond() < 3) {
						htmltext = "32292-01.html";
					} else if (st.isCond(3)) {
						htmltext = "32292-02.html";
					} else if (st.isCond(4)) {
						htmltext = "32292-04.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
