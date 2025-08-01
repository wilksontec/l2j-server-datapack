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
package com.l2jserver.datapack.quests.Q00358_IllegitimateChildOfTheGoddess;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Illegitimate Child of the Goddess (358)
 * @author Adry_85
 */
public final class Q00358_IllegitimateChildOfTheGoddess extends Quest {
	// NPC
	private static final int OLTRAN = 30862;
	// Item
	private static final QuestItemChanceHolder SNAKE_SCALE = new QuestItemChanceHolder(5868, 108L);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(20672, SNAKE_SCALE, 71.0) // trives
		.addSingleDrop(20673, SNAKE_SCALE, 74.0) // falibati
		.build();
	// Misc
	private static final int MIN_LEVEL = 63;
	// Rewards
	private static final int[] REWARDS = new int[] {
		5364, // Recipe: Sealed Dark Crystal Shield(60%)
		5366, // Recipe: Sealed Shield of Nightmare(60%)
		6329, // Recipe: Sealed Phoenix Necklace(70%)
		6331, // Recipe: Sealed Phoenix Earring(70%)
		6333, // Recipe: Sealed Phoenix Ring(70%)
		6335, // Recipe: Sealed Majestic Necklace(70%)
		6337, // Recipe: Sealed Majestic Earring(70%)
		6339, // Recipe: Sealed Majestic Ring(70%)
	};
	
	public Q00358_IllegitimateChildOfTheGoddess() {
		super(358);
		bindStartNpc(OLTRAN);
		bindTalk(OLTRAN);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(SNAKE_SCALE.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30862-02.htm":
			case "30862-03.htm": {
				htmltext = event;
				break;
			}
			case "30862-04.htm": {
				st.startQuest();
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((st != null) && giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc), true)) {
			st.setCond(2);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st.isCreated()) {
			htmltext = ((player.getLevel() >= MIN_LEVEL) ? "30862-01.htm" : "30862-05.html");
		} else if (st.isStarted()) {
			if (!hasItemsAtLimit(player, SNAKE_SCALE)) {
				htmltext = "30862-06.html";
			} else {
				rewardItems(player, REWARDS[getRandom(REWARDS.length)], 1);
				st.exitQuest(true, true);
				htmltext = "30862-07.html";
			}
		}
		return htmltext;
	}
}
