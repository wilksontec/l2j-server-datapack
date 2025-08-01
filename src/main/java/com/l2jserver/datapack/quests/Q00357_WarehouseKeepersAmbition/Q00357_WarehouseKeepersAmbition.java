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
package com.l2jserver.datapack.quests.Q00357_WarehouseKeepersAmbition;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Warehouse Keeper's Ambition (357)
 * @author Janiko, Pandragon
 */
public final class Q00357_WarehouseKeepersAmbition extends Quest {
	// NPC
	private static final int SILVA = 30686;
	// Item
	private static final int JADE_CRYSTAL = 5867;
	// Droplist
	private final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(20594, JADE_CRYSTAL, 57.7) // Forest Runner
		.addSingleDrop(20595, JADE_CRYSTAL, 60.0) // Fline Elder
		.addSingleDrop(20596, JADE_CRYSTAL, 63.8) // Liele Elder
		.addSingleDrop(20597, JADE_CRYSTAL, 62.0) // Valley Treant Elder
		.build();
	// Misc
	private static final int MIN_LVL = 47;
	
	public Q00357_WarehouseKeepersAmbition() {
		super(357);
		bindStartNpc(SILVA);
		bindTalk(SILVA);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(JADE_CRYSTAL);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs != null) {
			switch (event) {
				case "30686-01.htm":
				case "30686-03.htm":
				case "30686-04.htm":
				case "30686-10.html": {
					htmltext = event;
					break;
				}
				case "30686-05.htm": {
					if (qs.isCreated()) {
						qs.startQuest();
						htmltext = event;
					}
					break;
				}
				case "30686-09.html": {
					final long crystalCount = getQuestItemsCount(player, JADE_CRYSTAL);
					if (crystalCount > 0) {
						long adenaReward = crystalCount * 425;
						if (crystalCount < 100) {
							adenaReward += 13500;
							htmltext = "30686-08.html";
						} else {
							adenaReward += 40500;
							htmltext = event;
						}
						giveAdena(player, adenaReward, true);
						takeItems(player, JADE_CRYSTAL, -1);
					}
					break;
				}
				case "30686-11.html": {
					final long crystalCount = getQuestItemsCount(player, JADE_CRYSTAL);
					if (crystalCount > 0) {
						giveAdena(player, (crystalCount * 425) + ((crystalCount >= 100) ? 40500 : 0), true);
						takeItems(player, JADE_CRYSTAL, -1);
					}
					qs.exitQuest(true, true);
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		if (qs.isCreated()) {
			htmltext = ((talker.getLevel() < MIN_LVL) ? "30686-01.html" : "30686-02.htm");
		} else if (qs.isStarted()) {
			htmltext = (hasQuestItems(talker, JADE_CRYSTAL)) ? "30686-07.html" : "30686-06.html";
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
