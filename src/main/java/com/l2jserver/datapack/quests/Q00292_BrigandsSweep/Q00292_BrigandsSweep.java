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
package com.l2jserver.datapack.quests.Q00292_BrigandsSweep;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Brigands Sweep (292)
 * @author xban1x
 */
public final class Q00292_BrigandsSweep extends Quest {
	// NPC's
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;
	// Items
	private static final int GOBLIN_NECKLACE = 1483;
	private static final int GOBLIN_PENDANT = 1484;
	private static final int GOBLIN_LORD_PENDANT = 1485;
	private static final int SUSPICIOUS_CONTRACT = 1487;
	private static final QuestItemChanceHolder SUSPICIOUS_MEMO = new QuestItemChanceHolder(1486, 3L);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(20322, GOBLIN_NECKLACE) // Goblin Brigand
		.addSingleDrop(20323, GOBLIN_PENDANT) // Goblin Brigand Leader
		.addSingleDrop(20324, GOBLIN_NECKLACE) // Goblin Brigand Lieutenant
		.addSingleDrop(20327, GOBLIN_NECKLACE) // Goblin Snooper
		.addSingleDrop(20528, GOBLIN_LORD_PENDANT) // Goblin Lord
		.build();
	// Misc
	private static final int MIN_LVL = 5;
	
	public Q00292_BrigandsSweep() {
		super(292);
		bindStartNpc(SPIRON);
		bindTalk(SPIRON, BALANKI);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT, SUSPICIOUS_MEMO.getId(), SUSPICIOUS_CONTRACT);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String html = null;
		if (qs == null) {
			return html;
		}
		
		switch (event) {
			case "30532-03.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					html = event;
				}
				break;
			}
			case "30532-06.html": {
				if (qs.isStarted()) {
					qs.exitQuest(true, true);
					html = event;
				}
				break;
			}
			case "30532-07.html": {
				if (qs.isStarted()) {
					html = event;
				}
				break;
			}
		}
		return html;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, qs.getPlayer(), true)) {
			final int chance = getRandom(10);
			if (chance > 5) {
				giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true);
			} else if (qs.isCond(1) && (chance > 4) && !hasQuestItems(killer, SUSPICIOUS_CONTRACT)) {
				final long memos = getQuestItemsCount(killer, SUSPICIOUS_MEMO.getId());
				if (memos < 3) {
					if (giveItemRandomly(qs.getPlayer(), npc, SUSPICIOUS_MEMO, false)) {
						playSound(killer, Sound.ITEMSOUND_QUEST_ITEMGET);
						giveItems(killer, SUSPICIOUS_CONTRACT, 1);
						takeItems(killer, SUSPICIOUS_MEMO.getId(), -1);
						qs.setCond(2, true);
					} else {
						playSound(killer, Sound.ITEMSOUND_QUEST_ITEMGET);
					}
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		switch (npc.getId()) {
			case SPIRON: {
				switch (qs.getState()) {
					case State.CREATED: {
						html = (talker.getRace() == Race.DWARF) ? (talker.getLevel() >= MIN_LVL) ? "30532-02.htm" : "30532-01.htm" : "30532-00.htm";
						break;
					}
					case State.STARTED: {
						if (!hasAtLeastOneQuestItem(talker, getRegisteredItemIds())) {
							html = "30532-04.html";
						} else {
							final long necklaces = getQuestItemsCount(talker, GOBLIN_NECKLACE);
							final long pendants = getQuestItemsCount(talker, GOBLIN_PENDANT);
							final long lordPendants = getQuestItemsCount(talker, GOBLIN_LORD_PENDANT);
							final long sum = necklaces + pendants + lordPendants;
							if (sum > 0) {
								giveAdena(talker, (necklaces * 12) + (pendants * 36) + (lordPendants * 33) + (sum >= 10 ? 1000 : 0), true);
								takeItems(talker, -1, GOBLIN_NECKLACE, GOBLIN_PENDANT, GOBLIN_LORD_PENDANT);
							}
							if ((sum > 0) && !hasAtLeastOneQuestItem(talker, SUSPICIOUS_MEMO.getId(), SUSPICIOUS_CONTRACT)) {
								html = "30532-05.html";
							} else {
								final long memos = getQuestItemsCount(talker, SUSPICIOUS_MEMO.getId());
								if ((memos == 0) && hasQuestItems(talker, SUSPICIOUS_CONTRACT)) {
									giveAdena(talker, 1120, true);
									takeItems(talker, -1, SUSPICIOUS_CONTRACT); // Retail like, reward is given in 2 pieces if both conditions are meet.
									html = "30532-10.html";
								} else {
									if (memos == 1) {
										html = "30532-08.html";
									} else if (memos >= 2) {
										html = "30532-09.html";
									}
								}
							}
						}
					}
				}
				break;
			}
			case BALANKI: {
				if (qs.isStarted()) {
					if (hasQuestItems(talker, SUSPICIOUS_CONTRACT)) {
						giveAdena(talker, 620, true);
						takeItems(talker, 1487, -1);
						html = "30533-02.html";
					} else {
						html = "30533-01.html";
					}
				}
				break;
			}
		}
		return html;
	}
}
