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
package com.l2jserver.datapack.quests.Q00174_SupplyCheck;

import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Supply Check (174)
 * @author malyelfik
 */
public class Q00174_SupplyCheck extends Quest {
	// NPCs
	private static final int NIKA = 32167;
	private static final int BENIS = 32170;
	private static final int MARCELA = 32173;
	
	// Items
	private static final int WAREHOUSE_MANIFEST = 9792;
	private static final int GROCERY_STORE_MANIFEST = 9793;
	private static final int[] REWARD = {
		23, // Wooden Breastplate
		43, // Wooden Helmet
		49, // Gloves
		2386, // Wooden Gaiters
		37, // Leather Shoes
	};
	
	// Misc
	private static final int MIN_LEVEL = 2;
	
	private static final int GUIDE_MISSION = 41;
	
	public Q00174_SupplyCheck() {
		super(174);
		bindStartNpc(MARCELA);
		bindTalk(MARCELA, BENIS, NIKA);
		
		registerQuestItems(WAREHOUSE_MANIFEST, GROCERY_STORE_MANIFEST);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		if (event.equalsIgnoreCase("32173-03.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case MARCELA:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32173-01.htm" : "32173-02.htm";
						break;
					case State.STARTED:
						switch (st.getCond()) {
							case 1:
								htmltext = "32173-04.html";
								break;
							case 2:
								st.setCond(3, true);
								st.takeItems(WAREHOUSE_MANIFEST, -1);
								htmltext = "32173-05.html";
								break;
							case 3:
								htmltext = "32173-06.html";
								break;
							case 4:
								for (int itemId : REWARD) {
									st.giveItems(itemId, 1);
								}
								
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
								
								st.addExpAndSp(5672, 446);
								st.giveAdena(2466, true);
								st.exitQuest(false, true);
								
								htmltext = "32173-07.html";
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case BENIS:
				if (st.isStarted()) {
					htmltext = switch (st.getCond()) {
						case 1 -> {
							st.setCond(2, true);
							st.giveItems(WAREHOUSE_MANIFEST, 1);
							yield "32170-01.html";
						}
						case 2 -> "32170-02.html";
						default -> "32170-03.html";
					};
				}
				break;
			case NIKA:
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1:
						case 2:
							htmltext = "32167-01.html";
							break;
						case 3:
							st.setCond(4, true);
							st.giveItems(GROCERY_STORE_MANIFEST, 1);
							htmltext = "32167-02.html";
							break;
						case 4:
							htmltext = "32167-03.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}