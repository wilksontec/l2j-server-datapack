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
package com.l2jserver.datapack.quests.Q00307_ControlDeviceOfTheGiants;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Control Device of the Giants (307)
 * @author Gladicek, malyelfik
 */
public class Q00307_ControlDeviceOfTheGiants extends Quest {
	// NPC
	private final static int DROPH = 32711;
	// RB
	private final static int GORGOLOS = 25681;
	private final static int LAST_TITAN_UTENUS = 25684;
	private final static int GIANT_MARPANAK = 25680;
	private final static int HEKATON_PRIME = 25687;
	// Items
	private final static int SUPPORT_ITEMS = 14850;
	private final static int CET_1_SHEET = 14851;
	private final static int CET_2_SHEET = 14852;
	private final static int CET_3_SHEET = 14853;
	// Misc
	private final static int RESPAWN_DELAY = 3600000; // 1 hour
	private static L2Npc hekaton;
	
	public Q00307_ControlDeviceOfTheGiants() {
		super(307);
		bindStartNpc(DROPH);
		bindTalk(DROPH);
		bindKill(GORGOLOS, LAST_TITAN_UTENUS, GIANT_MARPANAK, HEKATON_PRIME);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "32711-04.html":
				if (player.getLevel() >= 79) {
					st.startQuest();
					htmltext = (st.hasQuestItems(CET_1_SHEET, CET_2_SHEET, CET_3_SHEET)) ? "32711-04a.html" : "32711-04.html";
				}
				break;
			case "32711-05a.html":
				showRadar(player, 186214, 61591, -4152, 2);
				break;
			case "32711-05b.html":
				showRadar(player, 187554, 60800, -4984, 2);
				break;
			case "32711-05c.html":
				showRadar(player, 193432, 53922, -4368, 2);
				break;
			case "spawn":
				if (!hasQuestItems(player, CET_1_SHEET, CET_2_SHEET, CET_3_SHEET)) {
					return getNoQuestMsg(player);
				} else if ((hekaton != null) && !hekaton.isDead()) {
					return "32711-09.html";
				}
				String respawn = loadGlobalQuestVar("Respawn");
				long remain = (!respawn.isEmpty()) ? Long.parseLong(respawn) - System.currentTimeMillis() : 0;
				if (remain > 0) {
					return "32711-09a.html";
				}
				st.takeItems(CET_1_SHEET, 1);
				st.takeItems(CET_2_SHEET, 1);
				st.takeItems(CET_3_SHEET, 1);
				hekaton = addSpawn(HEKATON_PRIME, 191777, 56197, -7624, 0, false, 0);
				htmltext = "32711-09.html";
				break;
			case "32711-03.htm":
			case "32711-05.html":
			case "32711-06.html":
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		if (partyMember == null) {
			return;
		}
		final QuestState st = getQuestState(partyMember, false);
		
		switch (npc.getId()) {
			case GORGOLOS: {
				st.giveItems(CET_1_SHEET, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				break;
			}
			case LAST_TITAN_UTENUS: {
				st.giveItems(CET_2_SHEET, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				break;
			}
			case GIANT_MARPANAK: {
				st.giveItems(CET_3_SHEET, 1);
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				break;
			}
			case HEKATON_PRIME: {
				if (player.isInParty()) {
					for (L2PcInstance pl : player.getParty().getMembers()) {
						final QuestState qs = getQuestState(pl, false);
						
						if ((qs != null) && qs.isCond(1)) {
							qs.setCond(2, true);
						}
					}
					saveGlobalQuestVar("Respawn", Long.toString(System.currentTimeMillis() + RESPAWN_DELAY));
				}
				break;
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getLevel() >= 79) ? "32711-01.htm" : "32711-02.htm";
				break;
			}
			case State.STARTED: {
				if ((hekaton != null) && !hekaton.isDead()) {
					htmltext = "32711-09.html";
				} else if (st.isCond(1)) {
					htmltext = (!hasQuestItems(player, CET_1_SHEET, CET_2_SHEET, CET_3_SHEET)) ? "32711-07.html" : "32711-08.html";
				} else if (st.isCond(2)) {
					st.giveItems(SUPPORT_ITEMS, 1);
					st.exitQuest(true, true);
					htmltext = "32711-10.html";
				}
				break;
			}
		}
		return htmltext;
	}
}