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
package com.l2jserver.datapack.quests.Q00603_DaimonTheWhiteEyedPart1;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Daimon the White-Eyed - Part 1 (603)
 * @author Pandragon
 */
public final class Q00603_DaimonTheWhiteEyedPart1 extends Quest {
	// NPC
	private static final int EYE_OF_ARGOS = 31683;
	private static final int TABLET_1 = 31548;
	private static final int TABLET_2 = 31549;
	private static final int TABLET_3 = 31550;
	private static final int TABLET_4 = 31551;
	private static final int TABLET_5 = 31552;
	// Items
	private static final int BROKEN_CRYSTAL = 7191;
	private static final QuestItemChanceHolder SPIRIT_OF_DARKNESS = new QuestItemChanceHolder(7190, 200L);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(21297, SPIRIT_OF_DARKNESS, 50.0) // Canyon Bandersnatch Slave
		.addSingleDrop(21299, SPIRIT_OF_DARKNESS, 51.9) // Buffalo Slave
		.addSingleDrop(21304, SPIRIT_OF_DARKNESS, 67.3) // Grendel Slave
		.build();
	// Reward
	private static final int UNFINISHED_CRYSTAL = 7192;
	// Misc
	private static final int MIN_LVL = 73;
	
	public Q00603_DaimonTheWhiteEyedPart1() {
		super(603);
		bindStartNpc(EYE_OF_ARGOS);
		bindTalk(EYE_OF_ARGOS, TABLET_1, TABLET_2, TABLET_3, TABLET_4, TABLET_5);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(SPIRIT_OF_DARKNESS.getId(), BROKEN_CRYSTAL);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null) {
			return htmltext;
		}
		
		switch (event) {
			case "31683-03.htm": {
				if (qs.isCreated()) {
					qs.set("tablet_" + TABLET_1, 0);
					qs.set("tablet_" + TABLET_2, 0);
					qs.set("tablet_" + TABLET_3, 0);
					qs.set("tablet_" + TABLET_4, 0);
					qs.set("tablet_" + TABLET_5, 0);
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "31548-02.html":
			case "31549-02.html":
			case "31550-02.html":
			case "31551-02.html":
			case "31552-02.html": {
				if (qs.getCond() < 6) {
					giveItems(player, BROKEN_CRYSTAL, 1);
					qs.set("TABLET_" + npc.getId(), 1);
					qs.setCond(qs.getCond() + 1, true);
					htmltext = event;
				}
				break;
			}
			case "31683-06.html": {
				if (qs.isCond(6) && (getQuestItemsCount(player, BROKEN_CRYSTAL) >= 5)) {
					takeItems(player, BROKEN_CRYSTAL, -1);
					qs.setCond(7, true);
					htmltext = event;
				}
				break;
			}
			case "31683-10.html": {
				if (qs.isCond(8)) {
					if (hasItemsAtLimit(player, SPIRIT_OF_DARKNESS)) {
						takeItems(player, SPIRIT_OF_DARKNESS.getId(), -1);
						giveItems(player, UNFINISHED_CRYSTAL, 1);
						qs.exitQuest(true, true);
						htmltext = event;
					} else {
						htmltext = "31683-11.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (qs.getState()) {
			case State.CREATED: {
				if (npc.getId() == EYE_OF_ARGOS) {
					htmltext = ((talker.getLevel() < MIN_LVL) ? "31683-02.html" : "31683-01.htm");
				}
				break;
			}
			case State.STARTED: {
				if (npc.getId() == EYE_OF_ARGOS) {
					switch (qs.getCond()) {
						case 1:
						case 2:
						case 3:
						case 4:
						case 5: {
							htmltext = "31683-04.html";
							break;
						}
						case 6: {
							htmltext = "31683-05.html";
							break;
						}
						case 7: {
							htmltext = "31683-07.html";
							break;
						}
						case 8: {
							htmltext = "31683-08.html";
							break;
						}
					}
				} else if (qs.getInt("TABLET_" + npc.getId()) == 0) {
					htmltext = npc.getId() + "-01.html";
				} else {
					htmltext = npc.getId() + "-03.html";
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(talker);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, 7, 3, npc);
		if (qs != null) {
			if (giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true)) {
				qs.setCond(8);
			}
		}
	}
}
