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
package com.l2jserver.datapack.quests.Q00320_BonesTellTheFuture;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Bones Tell The Future (320)
 * @author ivantotov
 */
public final class Q00320_BonesTellTheFuture extends Quest {
	// NPC
	private static final int TETRACH_KAITAR = 30359;
	// Item
	private static final QuestItemChanceHolder BONE_FRAGMENT = new QuestItemChanceHolder(809, 18.0, 10L);
	// Misc
	private static final int MIN_LEVEL = 10;
	// Monsters
	private static final int[] MONSTERS = {
		20517, // Skeleton Hunter
		20518, // Skeleton Hunter Archer
	};
	
	public Q00320_BonesTellTheFuture() {
		super(320);
		bindStartNpc(TETRACH_KAITAR);
		bindTalk(TETRACH_KAITAR);
		bindKill(MONSTERS);
		registerQuestItems(BONE_FRAGMENT.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30359-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, 1, 3, npc);
		if ((qs != null) && giveItemRandomly(qs.getPlayer(), npc, BONE_FRAGMENT, true)) {
			qs.setCond(2);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() == Race.DARK_ELF) ? (player.getLevel() >= MIN_LEVEL) ? "30359-03.htm" : "30359-02.htm" : "30359-00.htm";
				break;
			}
			case State.STARTED: {
				if (hasItemsAtLimit(st.getPlayer(), BONE_FRAGMENT)) {
					htmltext = "30359-06.html";
					st.giveAdena(8470, true);
					st.exitQuest(true, true);
				} else {
					htmltext = "30359-05.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
