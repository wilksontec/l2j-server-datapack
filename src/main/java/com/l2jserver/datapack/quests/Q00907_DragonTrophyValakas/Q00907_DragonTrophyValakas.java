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
package com.l2jserver.datapack.quests.Q00907_DragonTrophyValakas;

import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Dragon Trophy - Valakas (907)
 * @author Zoey76
 */
public class Q00907_DragonTrophyValakas extends Quest {
	// NPC
	private static final int KLEIN = 31540;
	// Monster
	private static final int VALAKAS = 29028;
	// Items
	private static final int MEDAL_OF_GLORY = 21874;
	private static final int VACUALITE_FLOATING_STONE = 7267;
	// Misc
	private static final int MIN_LEVEL = 84;
	
	public Q00907_DragonTrophyValakas() {
		super(907);
		bindStartNpc(KLEIN);
		bindTalk(KLEIN);
		bindKill(VALAKAS);
	}
	
	@Override
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, player, false)) {
			st.setCond(2, true);
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		if ((player.getLevel() >= MIN_LEVEL) && st.hasQuestItems(VACUALITE_FLOATING_STONE)) {
			switch (event) {
				case "31540-05.htm":
				case "31540-06.htm": {
					htmltext = event;
					break;
				}
				case "31540-07.html": {
					st.startQuest();
					htmltext = event;
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		executeForEachPlayer(killer, npc, isSummon, true, true);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				if (player.getLevel() < MIN_LEVEL) {
					htmltext = "31540-02.html";
				} else if (!st.hasQuestItems(VACUALITE_FLOATING_STONE)) {
					htmltext = "31540-04.html";
				} else {
					htmltext = "31540-01.htm";
				}
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "31540-08.html";
						break;
					}
					case 2: {
						st.giveItems(MEDAL_OF_GLORY, 30);
						st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
						st.exitQuest(QuestType.DAILY, true);
						htmltext = "31540-09.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED: {
				if (!st.isNowAvailable()) {
					htmltext = "31540-03.html";
				} else {
					st.setState(State.CREATED);
					if (player.getLevel() < MIN_LEVEL) {
						htmltext = "31540-02.html";
					} else if (!st.hasQuestItems(VACUALITE_FLOATING_STONE)) {
						htmltext = "31540-04.html";
					} else {
						htmltext = "31540-01.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
