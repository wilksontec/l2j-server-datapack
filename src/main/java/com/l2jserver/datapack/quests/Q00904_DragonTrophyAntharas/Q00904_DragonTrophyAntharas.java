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
package com.l2jserver.datapack.quests.Q00904_DragonTrophyAntharas;

import com.l2jserver.gameserver.enums.QuestType;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Dragon Trophy - Antharas (904)
 * @author Zoey76
 */
public final class Q00904_DragonTrophyAntharas extends Quest {
	// NPC
	private static final int THEODRIC = 30755;
	// Monster
	private static final int ANTHARAS = 29068;
	// Items
	private static final int MEDAL_OF_GLORY = 21874;
	private static final int PORTAL_STONE = 3865;
	// Misc
	private static final int MIN_LEVEL = 84;
	
	public Q00904_DragonTrophyAntharas() {
		super(904);
		bindStartNpc(THEODRIC);
		bindTalk(THEODRIC);
		bindKill(ANTHARAS);
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
		if ((player.getLevel() >= MIN_LEVEL) && st.hasQuestItems(PORTAL_STONE)) {
			switch (event) {
				case "30755-05.htm":
				case "30755-06.htm": {
					htmltext = event;
					break;
				}
				case "30755-07.html": {
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
					htmltext = "30755-02.html";
				} else if (!st.hasQuestItems(PORTAL_STONE)) {
					htmltext = "30755-04.html";
				} else {
					htmltext = "30755-01.htm";
				}
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "30755-08.html";
						break;
					}
					case 2: {
						st.giveItems(MEDAL_OF_GLORY, 30);
						st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
						st.exitQuest(QuestType.DAILY, true);
						htmltext = "30755-09.html";
						break;
					}
				}
				break;
			}
			case State.COMPLETED: {
				if (!st.isNowAvailable()) {
					htmltext = "30755-03.html";
				} else {
					st.setState(State.CREATED);
					if (player.getLevel() < MIN_LEVEL) {
						htmltext = "30755-02.html";
					} else if (!st.hasQuestItems(PORTAL_STONE)) {
						htmltext = "30755-04.html";
					} else {
						htmltext = "30755-01.htm";
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
