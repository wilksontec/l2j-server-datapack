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
package com.l2jserver.datapack.quests.Q00188_SealRemoval;

import com.l2jserver.datapack.quests.Q00184_ArtOfPersuasion.Q00184_ArtOfPersuasion;
import com.l2jserver.datapack.quests.Q00185_NikolasCooperation.Q00185_NikolasCooperation;
import com.l2jserver.datapack.quests.Q00186_ContractExecution.Q00186_ContractExecution;
import com.l2jserver.datapack.quests.Q00187_NikolasHeart.Q00187_NikolasHeart;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Seal Removal (188)
 * @author ivantotov
 */
public final class Q00188_SealRemoval extends Quest {
	// NPCs
	private static final int MAESTRO_NIKOLA = 30621;
	private static final int RESEARCHER_LORAIN = 30673;
	private static final int DOROTHY_LOCKSMITH = 30970;
	// Items
	private static final int LORAINES_CERTIFICATE = 10362;
	private static final int BROKEN_METAL_PIECES = 10369;
	// Misc
	private static final int MIN_LEVEL = 41;
	private static final int MAX_LEVEL_FOR_EXP_SP = 47;
	
	public Q00188_SealRemoval() {
		super(188);
		bindStartNpc(RESEARCHER_LORAIN);
		bindTalk(RESEARCHER_LORAIN, MAESTRO_NIKOLA, DOROTHY_LOCKSMITH);
		registerQuestItems(BROKEN_METAL_PIECES);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30673-03.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					qs.setMemoState(1);
					giveItems(player, BROKEN_METAL_PIECES, 1);
					htmltext = event;
				}
				break;
			}
			case "30621-02.html": {
				if (qs.isMemoState(1)) {
					htmltext = event;
				}
				break;
			}
			case "30621-03.html": {
				if (qs.isMemoState(1)) {
					qs.setMemoState(2);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30621-04.html": {
				if (qs.isMemoState(2)) {
					htmltext = event;
				}
				break;
			}
			case "30970-02.html": {
				if (qs.isMemoState(2)) {
					htmltext = event;
				}
				break;
			}
			case "30970-03.html": {
				if (qs.isMemoState(2)) {
					giveAdena(player, 98583, true);
					if (player.getLevel() < MAX_LEVEL_FOR_EXP_SP) {
						addExpAndSp(player, 285935, 18711);
					}
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			if (npc.getId() == RESEARCHER_LORAIN) {
				if (!hasQuestItems(player, LORAINES_CERTIFICATE)) {
					final QuestState q186 = player.getQuestState(Q00186_ContractExecution.class.getSimpleName());
					final QuestState q187 = player.getQuestState(Q00187_NikolasHeart.class.getSimpleName());
					if (player.hasQuestCompleted(Q00184_ArtOfPersuasion.class.getSimpleName()) || (player.hasQuestCompleted(Q00185_NikolasCooperation.class.getSimpleName()) && (q186 == null) && (q187 == null))) {
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30673-01.htm" : "30673-02.htm";
					}
				}
			}
		} else if (qs.isStarted()) {
			switch (npc.getId()) {
				case RESEARCHER_LORAIN: {
					htmltext = "30673-04.html";
					break;
				}
				case MAESTRO_NIKOLA: {
					if (qs.isMemoState(1)) {
						htmltext = "30621-01.html";
					} else if (qs.isMemoState(2)) {
						htmltext = "30621-05.html";
					}
					break;
				}
				case DOROTHY_LOCKSMITH: {
					if (qs.isMemoState(2)) {
						htmltext = "30970-01.html";
					}
					break;
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == RESEARCHER_LORAIN) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}