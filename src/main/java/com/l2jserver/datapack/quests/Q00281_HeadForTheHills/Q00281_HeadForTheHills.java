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
package com.l2jserver.datapack.quests.Q00281_HeadForTheHills;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.enums.audio.Voice;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;

/**
 * Head for the Hills! (281)
 * @author xban1x
 */
public final class Q00281_HeadForTheHills extends Quest {
	// Item
	private static final int CLAWS = 9796;
	// NPC
	private static final int MERCELA = 32173;
	// Misc
	private static final int MIN_LVL = 6;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	// Rewards
	private static final int[] REWARDS = new int[] {
		115, // Earring of Wisdom
		876, // Ring of Anguish
		907, // Necklace of Anguish
		22, // Leather Shirt
		428, // Feriotic Tunic
		1100, // Cotton Tunic
		29, // Leather Pants
		463, // Feriotic Stockings
		1103, // Cotton Stockings
		736, // Scroll of Escape
	};
	private static final ItemHolder SOULSHOTS_NO_GRADE_FOR_ROOKIES = new ItemHolder(5789, 6000);
	private static final ItemHolder SPIRITSHOTS_NO_GRADE_FOR_ROOKIES = new ItemHolder(5790, 3000);
	
	static {
		MONSTERS.put(22234, 390); // Green Goblin
		MONSTERS.put(22235, 450); // Mountain Werewolf
		MONSTERS.put(22236, 650); // Muertos Archer
		MONSTERS.put(22237, 720); // Mountain Fungus
		MONSTERS.put(22238, 920); // Mountain Werewolf Chief
		MONSTERS.put(22239, 990); // Muertos Guard
	}
	
	private static final int GUIDE_MISSION = 41;
	
	public Q00281_HeadForTheHills() {
		super(281);
		bindStartNpc(MERCELA);
		bindTalk(MERCELA);
		bindKill(MONSTERS.keySet());
		registerQuestItems(CLAWS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st == null) {
			return htmltext;
		}
		
		switch (event) {
			case "32173-03.htm": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "32173-06.html": {
				if (st.hasQuestItems(CLAWS)) {
					final long claws = st.getQuestItemsCount(CLAWS);
					st.giveAdena(((claws * 23) + (claws >= 10 ? 400 : 0)), true);
					st.takeItems(CLAWS, -1);
					giveNewbieReward(player);
					
					// Newbie Guide
					final var newbieGuide = QuestManager.getInstance().getQuest(NewbieGuide.class.getSimpleName());
					if (newbieGuide != null) {
						final var newbieGuideQs = newbieGuide.getQuestState(player, true);
						if (!newbieGuideQs.haveNRMemo(player, GUIDE_MISSION)) {
							newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
							newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, 1000);
							
							showOnScreenMsg(player, NpcStringId.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
						} else {
							if (((newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) % 10000) / 1000) != 1) {
								newbieGuideQs.setNRMemo(player, GUIDE_MISSION);
								newbieGuideQs.setNRMemoState(player, GUIDE_MISSION, newbieGuideQs.getNRMemoState(player, GUIDE_MISSION) + 1000);
								
								showOnScreenMsg(player, NpcStringId.ACQUISITION_OF_SOULSHOT_FOR_BEGINNERS_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
							}
						}
					}
					
					htmltext = event;
				} else {
					htmltext = "32173-07.html";
				}
				break;
			}
			case "32173-08.html": {
				htmltext = event;
				break;
			}
			case "32173-09.html": {
				st.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "32173-11.html": {
				if (st.getQuestItemsCount(CLAWS) >= 50) {
					if (getRandom(1000) <= 360) {
						st.giveItems(REWARDS[getRandom(9)], 1);
					} else {
						st.giveItems(REWARDS[9], 1);
					}
					st.takeItems(CLAWS, 50);
					giveNewbieReward(player);
					htmltext = event;
				} else {
					htmltext = "32173-10.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && (getRandom(1000) <= MONSTERS.get(npc.getId()))) {
			st.giveItems(CLAWS, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getLevel() >= MIN_LVL) ? "32173-01.htm" : "32173-02.htm";
				break;
			}
			case State.STARTED: {
				htmltext = st.hasQuestItems(CLAWS) ? "32173-05.html" : "32173-04.html";
				break;
			}
		}
		return htmltext;
	}
	
	/**
	 * Give basic newbie reward.
	 * @param player the player to reward
	 */
	public void giveNewbieReward(L2PcInstance player) {
		if ((player.getLevel() < 25) && (getOneTimeQuestFlag(player, 57) == 0)) {
			if (player.isMageClass()) {
				giveItems(player, SPIRITSHOTS_NO_GRADE_FOR_ROOKIES);
				playSound(player, Voice.TUTORIAL_VOICE_027_1000);
			} else {
				giveItems(player, SOULSHOTS_NO_GRADE_FOR_ROOKIES);
				playSound(player, Voice.TUTORIAL_VOICE_026_1000);
			}
			
			setOneTimeQuestFlag(player, 57, 1);
		}
	}
}
