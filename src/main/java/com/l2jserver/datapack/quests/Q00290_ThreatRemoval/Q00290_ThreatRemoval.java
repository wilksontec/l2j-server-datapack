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
package com.l2jserver.datapack.quests.Q00290_ThreatRemoval;

import com.l2jserver.datapack.quests.Q00251_NoSecrets.Q00251_NoSecrets;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Threat Removal (290)
 * @author Adry_85
 */
public class Q00290_ThreatRemoval extends Quest {
	// NPC
	private static final int PINAPS = 30201;
	// Items
	private static final int ENCHANT_WEAPON_S = 959;
	private static final int ENCHANT_ARMOR_S = 960;
	private static final int FIRE_CRYSTAL = 9552;
	private static final int SEL_MAHUM_ID_TAG = 15714;
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.bulkAddSingleDrop(SEL_MAHUM_ID_TAG, 93.2)
		.withNpcs(22775, 22777, 22778)
		.build() // Sel Mahum Drill Sergeant
		.bulkAddSingleDrop(SEL_MAHUM_ID_TAG, 36.3)
		.withNpcs(22780, 22782, 22784)
		.build() // Sel Mahum Recruit
		.addSingleDrop(22776, SEL_MAHUM_ID_TAG, 39.7) // Sel Mahum Training Officer
		.addSingleDrop(22781, SEL_MAHUM_ID_TAG, 48.3) // Sel Mahum Soldier
		.addSingleDrop(22783, SEL_MAHUM_ID_TAG, 35.2) // Sel Mahum Soldier
		.addSingleDrop(22785, SEL_MAHUM_ID_TAG, 16.9) // Sel Mahum Soldier
		.build();
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public Q00290_ThreatRemoval() {
		super(290);
		bindStartNpc(PINAPS);
		bindTalk(PINAPS);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(SEL_MAHUM_ID_TAG);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30201-02.html": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "30201-06.html": {
				if (st.isCond(1)) {
					st.takeItems(SEL_MAHUM_ID_TAG, 400);
					switch (getRandom(10)) {
						case 0: {
							st.rewardItems(ENCHANT_WEAPON_S, 1);
							break;
						}
						case 1:
						case 2:
						case 3: {
							st.rewardItems(ENCHANT_ARMOR_S, 1);
							break;
						}
						case 4:
						case 5: {
							st.rewardItems(ENCHANT_ARMOR_S, 2);
							break;
						}
						case 6: {
							st.rewardItems(ENCHANT_ARMOR_S, 3);
							break;
						}
						case 7:
						case 8: {
							st.rewardItems(FIRE_CRYSTAL, 1);
							break;
						}
						case 9:
						case 10: {
							st.rewardItems(FIRE_CRYSTAL, 2);
							break;
						}
					}
					htmltext = event;
				}
				break;
			}
			case "30201-07.html": {
				if (st.isCond(1)) {
					htmltext = event;
				}
				break;
			}
			case "exit": {
				if (st.isCond(1)) {
					if (st.hasQuestItems(SEL_MAHUM_ID_TAG)) {
						htmltext = "30201-08.html";
					} else {
						st.exitQuest(true, true);
						htmltext = "30201-09.html";
					}
				}
				break;
			}
			case "30201-10.html": {
				if (st.isCond(1)) {
					st.exitQuest(true, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		QuestState st = getRandomPartyMemberState(player, 1, 1, npc);
		if (st != null) {
			giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc), true);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = ((player.getLevel() >= MIN_LEVEL) && player.hasQuestCompleted(Q00251_NoSecrets.class.getSimpleName())) ? "30201-01.htm" : "30201-03.html";
				break;
			}
			case State.STARTED: {
				if (st.isCond(1)) {
					htmltext = (st.getQuestItemsCount(SEL_MAHUM_ID_TAG) < 400) ? "30201-04.html" : "30201-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
