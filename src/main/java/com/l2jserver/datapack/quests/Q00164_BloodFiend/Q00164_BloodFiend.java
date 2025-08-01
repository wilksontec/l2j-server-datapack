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
package com.l2jserver.datapack.quests.Q00164_BloodFiend;

import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Blood Fiend (164)
 * @author xban1x
 */
public class Q00164_BloodFiend extends Quest {
	// NPC
	private static final int CREAMEES = 30149;
	// Monster
	private static final int KIRUNAK = 27021;
	// Item
	private static final int KIRUNAK_SKULL = 1044;
	// Misc
	private static final int MIN_LVL = 21;
	
	public Q00164_BloodFiend() {
		super(164);
		bindStartNpc(CREAMEES);
		bindTalk(CREAMEES);
		bindKill(KIRUNAK);
		registerQuestItems(KIRUNAK_SKULL);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30149-04.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1)) {
			npc.broadcastPacket(new NpcSay(npc, Say2.ALL, NpcStringId.I_HAVE_FULFILLED_MY_CONTRACT_WITH_TRADER_CREAMEES));
			st.giveItems(KIRUNAK_SKULL, 1);
			st.setCond(2, true);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (player.getRace() != Race.DARK_ELF) ? player.getLevel() >= MIN_LVL ? "30149-03.htm" : "30149-02.htm" : "30149-00.htm";
				break;
			}
			case State.STARTED: {
				if (st.isCond(2) && st.hasQuestItems(KIRUNAK_SKULL)) {
					st.giveAdena(42130, true);
					st.addExpAndSp(35637, 1854);
					st.exitQuest(false, true);
					htmltext = "30149-06.html";
				} else {
					htmltext = "30149-05.html";
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
