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
package com.l2jserver.datapack.quests.Q00310_OnlyWhatRemains;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.quests.Q00240_ImTheOnlyOneYouCanTrust.Q00240_ImTheOnlyOneYouCanTrust;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Only What Remains (310)<br>
 * Original Jython script by Gnacik
 * @author malyelfik
 */
public class Q00310_OnlyWhatRemains extends Quest {
	// NPC
	private static final int KINTAIJIN = 32640;
	// Items
	private static final int GROW_ACCELERATOR = 14832;
	private static final int MULTI_COLORED_JEWEL = 14835;
	private static final int DIRTY_BEAD = 14880;
	// Monsters
	private static final Map<Integer, Integer> MOBS = new HashMap<>();
	
	static {
		MOBS.put(22617, 646);
		MOBS.put(22618, 646);
		MOBS.put(22619, 646);
		MOBS.put(22620, 666);
		MOBS.put(22621, 630);
		MOBS.put(22622, 940);
		MOBS.put(22623, 622);
		MOBS.put(22624, 630);
		MOBS.put(22625, 678);
		MOBS.put(22626, 940);
		MOBS.put(22627, 646);
		MOBS.put(22628, 646);
		MOBS.put(22629, 646);
		MOBS.put(22630, 638);
		MOBS.put(22631, 880);
		MOBS.put(22632, 722);
		MOBS.put(22633, 638);
	}
	
	public Q00310_OnlyWhatRemains() {
		super(310);
		bindStartNpc(KINTAIJIN);
		bindTalk(KINTAIJIN);
		bindKill(MOBS.keySet());
		registerQuestItems(DIRTY_BEAD);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "32640-04.htm":
				st.startQuest();
				break;
			case "32640-quit.html":
				st.exitQuest(true, true);
				break;
			case "32640-02.htm":
			case "32640-03.htm":
			case "32640-05.html":
			case "32640-06.html":
			case "32640-07.html":
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance partyMember = getRandomPartyMember(player, 1);
		
		if (partyMember == null) {
			return;
		}
		
		final QuestState st = getQuestState(partyMember, false);
		
		if (getRandom(1000) < MOBS.get(npc.getId())) {
			st.giveItems(DIRTY_BEAD, 1);
			st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED:
				htmltext = ((player.getLevel() >= 81) && player.hasQuestCompleted(Q00240_ImTheOnlyOneYouCanTrust.class.getSimpleName())) ? "32640-01.htm" : "32640-00.htm";
				break;
			case State.STARTED:
				if (!st.hasQuestItems(DIRTY_BEAD)) {
					htmltext = "32640-08.html";
				} else if (st.getQuestItemsCount(DIRTY_BEAD) < 500) {
					htmltext = "32640-09.html";
				} else {
					st.takeItems(DIRTY_BEAD, 500);
					st.giveItems(GROW_ACCELERATOR, 1);
					st.giveItems(MULTI_COLORED_JEWEL, 1);
					htmltext = "32640-10.html";
				}
				break;
		}
		return htmltext;
	}
}