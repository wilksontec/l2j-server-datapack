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
package com.l2jserver.datapack.quests.Q00039_RedEyedInvaders;

import static com.l2jserver.gameserver.model.quest.QuestDroplist.singleDropItem;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Red-eyed Invaders (39)
 * @author janiko
 */
public final class Q00039_RedEyedInvaders extends Quest {
	// NPCs
	private static final int CAPTAIN_BATHIA = 30332;
	private static final int GUARD_BABENCO = 30334;
	// Monsters
	private static final int MALE_LIZARDMAN = 20919;
	private static final int MALE_LIZARDMAN_SCOUT = 20920;
	private static final int MALE_LIZARDMAN_GUARD = 20921;
	private static final int GIANT_ARANE = 20925;
	// Items
	private static final QuestItemChanceHolder LIZ_NECKLACE_A = new QuestItemChanceHolder(7178, 50.0, 100L);
	private static final QuestItemChanceHolder LIZ_NECKLACE_B = new QuestItemChanceHolder(7179, 50.0, 50L);
	private static final QuestItemChanceHolder LIZ_PERFUME = new QuestItemChanceHolder(7180, 25.0, 30L);
	private static final QuestItemChanceHolder LIZ_GEM = new QuestItemChanceHolder(7181, 30.0, 30L);
	// Rewards
	private static final ItemHolder GREEN_HIGH_LURE = new ItemHolder(6521, 60);
	private static final ItemHolder BABYDUCK_ROD = new ItemHolder(6529, 1);
	private static final ItemHolder FISHING_SHOT_NONE = new ItemHolder(6535, 500);
	// Misc
	private static final int MIN_LVL = 20;
	
	public Q00039_RedEyedInvaders() {
		super(39);
		bindStartNpc(GUARD_BABENCO);
		bindTalk(GUARD_BABENCO, CAPTAIN_BATHIA);
		bindKill(MALE_LIZARDMAN_GUARD, MALE_LIZARDMAN_SCOUT, MALE_LIZARDMAN, GIANT_ARANE);
		registerQuestItems(LIZ_NECKLACE_A.getId(), LIZ_NECKLACE_B.getId(), LIZ_PERFUME.getId(), LIZ_GEM.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null) {
			return htmltext;
		}
		switch (event) {
			case "30334-03.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					htmltext = event;
				}
				break;
			}
			case "30332-02.html": {
				if (qs.isCond(1)) {
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30332-05.html": {
				if (qs.isCond(3)) {
					if (hasAllItems(player, true, LIZ_NECKLACE_A, LIZ_NECKLACE_B)) {
						qs.setCond(4, true);
						takeAllItems(player, LIZ_NECKLACE_A, LIZ_NECKLACE_B);
						htmltext = event;
					} else {
						htmltext = "30332-06.html";
					}
				}
				break;
			}
			case "30332-09.html": {
				if (qs.isCond(5)) {
					if (hasAllItems(player, true, LIZ_PERFUME, LIZ_GEM)) {
						rewardItems(player, GREEN_HIGH_LURE);
						rewardItems(player, BABYDUCK_ROD);
						rewardItems(player, FISHING_SHOT_NONE);
						addExpAndSp(player, 62366, 2783);
						qs.exitQuest(false, true);
						htmltext = event;
					} else {
						htmltext = "30332-10.html";
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
		switch (npc.getId()) {
			case CAPTAIN_BATHIA: {
				switch (qs.getCond()) {
					case 1: {
						htmltext = "30332-01.html";
						break;
					}
					case 2: {
						htmltext = "30332-03.html";
						break;
					}
					case 3: {
						htmltext = "30332-04.html";
						break;
					}
					case 4: {
						htmltext = "30332-07.html";
						break;
					}
					case 5: {
						htmltext = "30332-08.html";
						break;
					}
				}
				break;
			}
			case GUARD_BABENCO: {
				if (qs.isCreated()) {
					htmltext = (talker.getLevel() >= MIN_LVL) ? "30334-01.htm" : "30334-02.htm";
				} else if (qs.isStarted() && qs.isCond(1)) {
					htmltext = "30334-04.html";
				} else if (qs.isCompleted()) {
					htmltext = getAlreadyCompletedMsg(talker);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		switch (npc.getId()) {
			case MALE_LIZARDMAN -> {
				final QuestState qs = getRandomPartyMemberState(killer, 2, 3, npc);
				if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, LIZ_NECKLACE_A, true)) {
					if (hasItem(qs.getPlayer(), LIZ_NECKLACE_B)) {
						qs.setCond(3);
					}
				}
			}
			case MALE_LIZARDMAN_SCOUT -> {
				if (getRandomBoolean()) {
					final QuestState qs = getRandomPartyMemberState(killer, 2, 3, npc);
					if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, LIZ_NECKLACE_A, true)) {
						if (hasItem(qs.getPlayer(), LIZ_NECKLACE_B)) {
							qs.setCond(3);
						}
					}
				} else {
					final QuestState qs = getRandomPartyMemberState(killer, 4, 3, npc);
					if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, LIZ_PERFUME, true)) {
						if (hasItem(qs.getPlayer(), LIZ_GEM)) {
							qs.setCond(5);
						}
					}
				}
			}
			case MALE_LIZARDMAN_GUARD -> {
				if (getRandomBoolean()) {
					final QuestState qs = getRandomPartyMemberState(killer, 2, 3, npc);
					if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, LIZ_NECKLACE_B, true)) {
						if (hasItem(qs.getPlayer(), LIZ_NECKLACE_A)) {
							qs.setCond(3);
						}
					}
				} else {
					final QuestState qs = getRandomPartyMemberState(killer, 4, 3, npc);
					if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, singleDropItem(LIZ_PERFUME, 30.0), LIZ_PERFUME.getLimit(), true)) {
						if (hasItem(qs.getPlayer(), LIZ_GEM)) {
							qs.setCond(5);
						}
					}
				}
			}
			case GIANT_ARANE -> {
				final QuestState qs = getRandomPartyMemberState(killer, 4, 3, npc);
				if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, LIZ_GEM, true)) {
					if (hasItem(qs.getPlayer(), LIZ_PERFUME)) {
						qs.setCond(5);
					}
				}
			}
		}
	}
}
