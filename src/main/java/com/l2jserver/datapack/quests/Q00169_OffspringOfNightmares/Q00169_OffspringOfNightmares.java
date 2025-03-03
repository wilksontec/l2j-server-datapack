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
package com.l2jserver.datapack.quests.Q00169_OffspringOfNightmares;

import static com.l2jserver.gameserver.model.quest.QuestDroplist.singleDropItem;

import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Offspring of Nightmares (169)
 * @author xban1x
 */
public class Q00169_OffspringOfNightmares extends Quest {
	// NPC
	private static final int VLASTY = 30145;
	// Monsters
	private static final int LESSER_DARK_HORROR = 20025;
	private static final int DARK_HORROR = 20105;
	// Items
	private static final int BONE_GAITERS = 31;
	private static final int CRACKED_SKULL = 1030;
	private static final QuestItemChanceHolder PERFECT_SKULL = new QuestItemChanceHolder(1031, 20.0, 1L);
	// Misc
	private static final int MIN_LVL = 15;
	
	private static final int GUIDE_MISSION = 41;
	
	public Q00169_OffspringOfNightmares() {
		super(169);
		bindStartNpc(VLASTY);
		bindTalk(VLASTY);
		bindKill(LESSER_DARK_HORROR, DARK_HORROR);
		registerQuestItems(CRACKED_SKULL, PERFECT_SKULL.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			switch (event) {
				case "30145-03.htm": {
					st.startQuest();
					htmltext = event;
					break;
				}
				case "30145-07.html": {
					if (st.isCond(2) && st.hasQuestItems(PERFECT_SKULL.getId())) {
						st.giveItems(BONE_GAITERS, 1);
						st.addExpAndSp(17475, 818);
						st.giveAdena(17030 + (10 * st.getQuestItemsCount(CRACKED_SKULL)), true);
						st.exitQuest(false, true);
						
						// Newbie Guide
						final var newbieGuide = QuestManager.getInstance().getQuest(NewbieGuide.class.getSimpleName());
						if (newbieGuide != null) {
							final var newbieGuideQs = newbieGuide.getQuestState(player, true);
							if (!newbieGuideQs.haveNRMemo(player, GUIDE_MISSION)) {
								newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
								newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, 100000);
								
								showOnScreenMsg(player, NpcStringId.LAST_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
							} else {
								if (((newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) % 100000000) / 10000000) != 1) {
									newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
									newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) + 10000000);
									
									showOnScreenMsg(player, NpcStringId.LAST_DUTY_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
								}
							}
						}
						
						htmltext = event;
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isStarted()) {
			if (!hasQuestItems(st.getPlayer(), PERFECT_SKULL.getId()) && giveItemRandomly(st.getPlayer(), npc, PERFECT_SKULL, true)) {
				st.setCond(2);
			} else {
				giveItemRandomly(st.getPlayer(), npc, singleDropItem(CRACKED_SKULL, 50.0), 0, true);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.DARK_ELF) ? (player.getLevel() >= MIN_LVL) ? "30145-02.htm" : "30145-01.htm" : "30145-00.htm";
				break;
			}
			case State.STARTED: {
				if (st.hasQuestItems(CRACKED_SKULL) && !st.hasQuestItems(PERFECT_SKULL.getId())) {
					htmltext = "30145-05.html";
				} else if (st.isCond(2) && st.hasQuestItems(PERFECT_SKULL.getId())) {
					htmltext = "30145-06.html";
				} else if (!st.hasQuestItems(CRACKED_SKULL, PERFECT_SKULL.getId())) {
					htmltext = "30145-04.html";
				}
				break;
			}
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
