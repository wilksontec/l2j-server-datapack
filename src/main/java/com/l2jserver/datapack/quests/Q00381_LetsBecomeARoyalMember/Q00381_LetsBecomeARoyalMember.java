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
package com.l2jserver.datapack.quests.Q00381_LetsBecomeARoyalMember;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

/**
 * Let's Become a Royal Member! (381)
 * @author Pandragon
 */
public final class Q00381_LetsBecomeARoyalMember extends Quest {
	// NPCs
	private static final int SANDRA = 30090;
	private static final int SORINT = 30232;
	// Items
	private static final int COLLECTOR_MEMBERSHIP_1 = 3813;
	private static final int COIN_ALBUM = 5900;
	private static final int FOUR_LEAF_COIN = 7569;
	private static final QuestItemChanceHolder KAILS_COIN = new QuestItemChanceHolder(5899, 5.0, 1L);
	// Monsters
	private static final int ANCIENT_GARGOYLE = 21018;
	private static final int FALLEN_CHIEF_VERGUS = 27316;
	// Reward
	private static final int ROYAL_MEMBERSHIP = 5898;
	// Misc
	private static final int MIN_LVL = 55;
	
	public Q00381_LetsBecomeARoyalMember() {
		super(381);
		bindStartNpc(SORINT);
		bindTalk(SORINT, SANDRA);
		bindKill(ANCIENT_GARGOYLE, FALLEN_CHIEF_VERGUS);
		registerQuestItems(KAILS_COIN.getId(), FOUR_LEAF_COIN);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null) {
			return htmltext;
		}
		switch (event) {
			case "30232-03.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					qs.setMemoState(1);
					htmltext = event;
				}
				break;
			}
			case "30090-02.html": {
				if (qs.isMemoState(1) && !hasQuestItems(player, COIN_ALBUM)) {
					qs.setMemoState(2);
					playSound(player, Sound.ITEMSOUND_QUEST_MIDDLE);
					htmltext = event;
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
		switch (npc.getId()) {
			case SORINT: {
				if (qs.isCreated()) {
					if ((talker.getLevel() < MIN_LVL) || !hasQuestItems(talker, COLLECTOR_MEMBERSHIP_1)) {
						htmltext = "30232-02.html";
					} else if (!hasQuestItems(talker, ROYAL_MEMBERSHIP)) {
						htmltext = "30232-01.htm";
					}
					// TODO this quest is not visible in quest list if neither of these IF blocks are true
				} else if (qs.isStarted()) {
					final boolean hasAlbum = hasQuestItems(talker, COIN_ALBUM);
					final boolean hasCoin = hasQuestItems(talker, KAILS_COIN.getId());
					
					if (hasAlbum && hasCoin) {
						takeItems(talker, 1, KAILS_COIN.getId(), COIN_ALBUM);
						giveItems(talker, ROYAL_MEMBERSHIP, 1);
						qs.exitQuest(false, true);
						htmltext = "30232-06.html";
					} else if (hasAlbum || hasCoin) {
						htmltext = "30232-05.html";
					} else {
						htmltext = "30232-04.html";
					}
				} else {
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
			case SANDRA: {
				switch (qs.getMemoState()) {
					case 1:
						htmltext = "30090-01.html";
						break;
					case 2:
						if (hasQuestItems(talker, COIN_ALBUM)) {
							htmltext = "30090-05.html";
						} else if (hasQuestItems(talker, FOUR_LEAF_COIN)) {
							takeItems(talker, FOUR_LEAF_COIN, 1);
							giveItems(talker, COIN_ALBUM, 1);
							playSound(talker, Sound.ITEMSOUND_QUEST_MIDDLE);
							htmltext = "30090-04.html";
						} else {
							htmltext = "30090-03.html";
						}
						break;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isStarted() && Util.checkIfInRange(1500, npc, qs.getPlayer(), true)) {
			if (npc.getId() == ANCIENT_GARGOYLE) {
				giveItemRandomly(killer, npc, KAILS_COIN, true);
			} else if (qs.isMemoState(2) && !hasQuestItems(killer, FOUR_LEAF_COIN)) {
				giveItems(killer, FOUR_LEAF_COIN, 1);
				playSound(killer, Sound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
	}
}
