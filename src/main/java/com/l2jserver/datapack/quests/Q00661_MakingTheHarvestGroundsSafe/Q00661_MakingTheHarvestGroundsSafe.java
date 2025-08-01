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
package com.l2jserver.datapack.quests.Q00661_MakingTheHarvestGroundsSafe;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Making the Harvest Grounds Safe (661)
 * @author Pandragon
 */
public final class Q00661_MakingTheHarvestGroundsSafe extends Quest {
	// NPC
	private static final int NORMAN = 30210;
	// Items
	private static final int BIG_HORNET_STING = 8283;
	private static final int CLOUD_GEM = 8284;
	private static final int YOUNG_ARANEID_CLAW = 8285;
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(21095, BIG_HORNET_STING, 50.8) // Giant Poison Bee
		.addSingleDrop(21096, CLOUD_GEM, 50.0) // Cloudy Beast
		.addSingleDrop(21097, YOUNG_ARANEID_CLAW, 51.6) // Young Araneid
		.build();
	// Misc
	private static final int MIN_LVL = 21;
	
	public Q00661_MakingTheHarvestGroundsSafe() {
		super(661);
		bindStartNpc(NORMAN);
		bindTalk(NORMAN);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(BIG_HORNET_STING, CLOUD_GEM, YOUNG_ARANEID_CLAW);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null) {
			return htmltext;
		}
		
		switch (event) {
			case "30210-01.htm":
			case "30210-02.htm":
			case "30210-04.html":
			case "30210-06.html": {
				htmltext = event;
				break;
			}
			case "30210-03.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30210-08.html": {
				long stingCount = getQuestItemsCount(player, BIG_HORNET_STING);
				long gemCount = getQuestItemsCount(player, CLOUD_GEM);
				long clawCount = getQuestItemsCount(player, YOUNG_ARANEID_CLAW);
				long reward = (57 * stingCount) + (56 * gemCount) + (60 * clawCount);
				if ((stingCount + gemCount + clawCount) >= 10) {
					reward += 5773;
				}
				takeItems(player, BIG_HORNET_STING, -1);
				takeItems(player, CLOUD_GEM, -1);
				takeItems(player, YOUNG_ARANEID_CLAW, -1);
				giveAdena(player, reward, true);
				htmltext = event;
				break;
			}
			case "30210-09.html": {
				qs.exitQuest(true, true);
				htmltext = event;
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
				htmltext = (talker.getLevel() >= MIN_LVL) ? "30210-01.htm" : "30210-02.htm";
				break;
			}
			case State.STARTED: {
				if (hasQuestItems(talker, BIG_HORNET_STING, CLOUD_GEM, YOUNG_ARANEID_CLAW)) {
					htmltext = "30210-04.html";
				} else {
					htmltext = "30210-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs != null) {
			giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true);
		}
	}
}
