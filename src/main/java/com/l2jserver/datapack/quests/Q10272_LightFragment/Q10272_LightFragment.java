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
package com.l2jserver.datapack.quests.Q10272_LightFragment;

import com.l2jserver.datapack.quests.Q10271_TheEnvelopingDarkness.Q10271_TheEnvelopingDarkness;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Light Fragment (10272)
 * @author Gladicek
 */
public class Q10272_LightFragment extends Quest {
	// NPCs
	private static final int ORBYU = 32560;
	private static final int ARTIUS = 32559;
	private static final int GINBY = 32566;
	private static final int LELRIKIA = 32567;
	private static final int LEKON = 32557;
	// Items
	private static final int LIGHT_FRAGMENT_POWDER = 13854;
	private static final int LIGHT_FRAGMENT = 13855;
	private static final QuestItemChanceHolder FRAGMENT_POWDER = new QuestItemChanceHolder(13853, 60.0, 100L);
	// Monsters
	private static final int[] MOBS = {
		22536, // Royal Guard Captain
		22537, // Dragon Steed Troop Grand Magician
		22538, // Dragon Steed Troop Commander
		22539, // Dragon Steed Troops No 1 Battalion Commander
		22540, // White Dragon Leader
		22541, // Dragon Steed Troop Infantry
		22542, // Dragon Steed Troop Magic Leader
		22543, // Dragon Steed Troop Magician
		22544, // Dragon Steed Troop Magic Soldier
		22547, // Dragon Steed Troop Healer
		22550, // Savage Warrior
		22551, // Priest of Darkness
		22552, // Mutation Drake
		22596 // White Dragon Leader
	};
	
	public Q10272_LightFragment() {
		super(10272);
		bindStartNpc(ORBYU);
		bindTalk(ORBYU, ARTIUS, GINBY, LELRIKIA, LEKON);
		bindKill(MOBS);
		registerQuestItems(FRAGMENT_POWDER.getId(), LIGHT_FRAGMENT_POWDER);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "32560-06.html": {
				st.startQuest();
				break;
			}
			case "32559-03.html": {
				st.setCond(2, true);
				break;
			}
			case "32559-07.html": {
				st.setCond(3, true);
				break;
			}
			case "pay": {
				if (st.getQuestItemsCount(Inventory.ADENA_ID) >= 10000) {
					st.takeItems(Inventory.ADENA_ID, 10000);
					event = "32566-05.html";
				} else {
					event = "32566-04a.html";
				}
				break;
			}
			case "32567-04.html": {
				st.setCond(4, true);
				break;
			}
			case "32559-12.html": {
				st.setCond(5, true);
				break;
			}
			case "32557-03.html": {
				if (st.getQuestItemsCount(LIGHT_FRAGMENT_POWDER) >= 100) {
					st.takeItems(LIGHT_FRAGMENT_POWDER, 100);
					st.set("wait", "1");
				} else {
					event = "32557-04.html";
				}
				break;
			}
			default:
				break;
		}
		return event;
	}
	
	@Override
	public final void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(5)) {
			giveItemRandomly(player, npc, FRAGMENT_POWDER, true);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case ORBYU: {
				switch (st.getState()) {
					case State.CREATED:
						if (player.getLevel() < 75) {
							htmltext = "32560-03.html";
						} else {
							htmltext = (player.hasQuestCompleted(Q10271_TheEnvelopingDarkness.class.getSimpleName())) ? "32560-01.htm" : "32560-02.html";
						}
						break;
					case State.STARTED:
						htmltext = "32560-06.html";
						break;
					case State.COMPLETED:
						htmltext = "32560-04.html";
						break;
				}
				break;
			}
			case ARTIUS: {
				if (st.isCompleted()) {
					htmltext = "32559-19.html";
				} else {
					switch (st.getCond()) {
						case 1:
							htmltext = "32559-01.html";
							break;
						case 2:
							htmltext = "32559-04.html";
							break;
						case 3:
							htmltext = "32559-08.html";
							break;
						case 4:
							htmltext = "32559-10.html";
							break;
						case 5:
							if (hasItemsAtLimit(st.getPlayer(), FRAGMENT_POWDER)) {
								htmltext = "32559-15.html";
								st.setCond(6, true);
							} else {
								htmltext = st.hasQuestItems(FRAGMENT_POWDER.getId()) ? "32559-14.html" : "32559-13.html";
							}
							break;
						case 6:
							if (st.getQuestItemsCount(LIGHT_FRAGMENT_POWDER) < 100) {
								htmltext = "32559-16.html";
							} else {
								htmltext = "32559-17.html";
								st.setCond(7, true);
							}
							break;
						case 7:
							// TODO Nothing here?
							break;
						case 8:
							htmltext = "32559-18.html";
							st.giveAdena(556980, true);
							st.addExpAndSp(1009016, 91363);
							st.exitQuest(false, true);
							break;
					}
				}
				break;
			}
			case GINBY: {
				switch (st.getCond()) {
					case 1:
					case 2:
						htmltext = "32566-02.html";
						break;
					case 3:
						htmltext = "32566-01.html";
						break;
					case 4:
						htmltext = "32566-09.html";
						break;
					case 5:
						htmltext = "32566-10.html";
						break;
					case 6:
						htmltext = "32566-10.html";
						break;
				}
				break;
			}
			case LELRIKIA: {
				switch (st.getCond()) {
					case 3:
						htmltext = "32567-01.html";
						break;
					case 4:
						htmltext = "32567-05.html";
						break;
				}
				break;
			}
			case LEKON: {
				switch (st.getCond()) {
					case 7:
						if (st.getInt("wait") == 1) {
							htmltext = "32557-05.html";
							st.unset("wait");
							st.setCond(8, true);
							st.giveItems(LIGHT_FRAGMENT, 1);
						} else {
							htmltext = "32557-01.html";
						}
						break;
					case 8:
						htmltext = "32557-06.html";
						break;
				}
				break;
			}
		}
		return htmltext;
	}
}
