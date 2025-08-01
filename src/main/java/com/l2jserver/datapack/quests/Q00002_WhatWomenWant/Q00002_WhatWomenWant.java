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
package com.l2jserver.datapack.quests.Q00002_WhatWomenWant;

import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;

/**
 * What Women Want (2)
 * @author malyelfik
 */
public class Q00002_WhatWomenWant extends Quest {
	// NPCs
	private static final int ARUJIEN = 30223;
	private static final int MIRABEL = 30146;
	private static final int HERBIEL = 30150;
	private static final int GREENIS = 30157;
	// Items
	private static final int ARUJIENS_LETTER1 = 1092;
	private static final int ARUJIENS_LETTER2 = 1093;
	private static final int ARUJIENS_LETTER3 = 1094;
	private static final int POETRY_BOOK = 689;
	private static final int GREENIS_LETTER = 693;
	private static final int EARRING = 113;
	// Misc
	private static final int MIN_LEVEL = 2;
	
	private static final int GUIDE_MISSION = 41;
	
	public Q00002_WhatWomenWant() {
		super(2);
		bindStartNpc(ARUJIEN);
		bindTalk(ARUJIEN, MIRABEL, HERBIEL, GREENIS);
		registerQuestItems(ARUJIENS_LETTER1, ARUJIENS_LETTER2, ARUJIENS_LETTER3, POETRY_BOOK, GREENIS_LETTER);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "30223-04.htm":
				st.startQuest();
				giveItems(player, ARUJIENS_LETTER1, 1);
				break;
			case "30223-08.html":
				takeItems(player, ARUJIENS_LETTER3, -1);
				giveItems(player, POETRY_BOOK, 1);
				st.setCond(4, true);
				break;
			case "30223-09.html":
				// Newbie Guide
				final var newbieGuide = QuestManager.getInstance().getQuest(NewbieGuide.class.getSimpleName());
				if (newbieGuide != null) {
					final var newbieGuideQs = newbieGuide.getQuestState(player, true);
					if (!newbieGuideQs.haveNRMemo(player, GUIDE_MISSION)) {
						newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
						newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, 1);
						
						showOnScreenMsg(player, NpcStringId.DELIVERY_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
					} else {
						if ((newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) % 10) != 1) {
							newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
							newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) + 1);
							
							showOnScreenMsg(player, NpcStringId.DELIVERY_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
						}
					}
				}
				
				giveAdena(player, 450, true);
				addExpAndSp(player, 4254, 335);
				giveAdena(player, 1850, true);
				st.exitQuest(false, true);
				break;
			case "30223-03.html":
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case ARUJIEN:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = ((player.getRace() != Race.ELF) && (player.getRace() != Race.HUMAN)) ? "30223-00.htm" : (player.getLevel() >= MIN_LEVEL) ? "30223-02.htm" : "30223-01.html";
						break;
					case State.STARTED:
						switch (st.getCond()) {
							case 1:
								htmltext = "30223-05.html";
								break;
							case 2:
								htmltext = "30223-06.html";
								break;
							case 3:
								htmltext = "30223-07.html";
								break;
							case 4:
								htmltext = "30223-10.html";
								break;
							case 5:
								// Newbie Guide
								final var newbieGuide = QuestManager.getInstance().getQuest(NewbieGuide.class.getSimpleName());
								if (newbieGuide != null) {
									final var newbieGuideQs = newbieGuide.getQuestState(player, true);
									if (!newbieGuideQs.haveNRMemo(player, GUIDE_MISSION)) {
										newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
										newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, 1);
									} else {
										if ((newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) % 10) != 1) {
											newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
											newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) + 1);
										}
									}
								}
								
								showOnScreenMsg(player, NpcStringId.DELIVERY_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
								
								giveItems(player, EARRING, 1);
								addExpAndSp(player, 4254, 335);
								giveAdena(player, 1850, true);
								st.exitQuest(false, true);
								
								htmltext = "30223-11.html";
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case MIRABEL:
				if (st.isStarted()) {
					if (st.isCond(1)) {
						st.setCond(2, true);
						takeItems(player, ARUJIENS_LETTER1, -1);
						giveItems(player, ARUJIENS_LETTER2, 1);
						htmltext = "30146-01.html";
					} else {
						htmltext = "30146-02.html";
					}
				}
				break;
			case HERBIEL:
				if (st.isStarted() && (st.getCond() > 1)) {
					if (st.isCond(2)) {
						st.setCond(3, true);
						takeItems(player, ARUJIENS_LETTER2, -1);
						giveItems(player, ARUJIENS_LETTER3, 1);
						htmltext = "30150-01.html";
					} else {
						htmltext = "30150-02.html";
					}
				}
				break;
			case GREENIS:
				if (st.isStarted()) {
					if (st.isCond(4)) {
						st.setCond(5, true);
						takeItems(player, POETRY_BOOK, -1);
						giveItems(player, GREENIS_LETTER, 1);
						htmltext = "30157-02.html";
					} else if (st.isCond(5)) {
						htmltext = "30157-03.html";
					} else {
						htmltext = "30157-01.html";
					}
				}
				break;
		}
		return htmltext;
	}
}