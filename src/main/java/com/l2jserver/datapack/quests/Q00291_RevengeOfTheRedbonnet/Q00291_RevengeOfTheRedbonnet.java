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
package com.l2jserver.datapack.quests.Q00291_RevengeOfTheRedbonnet;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

/**
 * Revenge of the Redbonnet (291).
 * @author xban1x
 */
public final class Q00291_RevengeOfTheRedbonnet extends Quest {
	// NPC
	private static final int MARYSE_REDBONNET = 30553;
	// Item
	private static final QuestItemChanceHolder BLACK_WOLF_PELT = new QuestItemChanceHolder(1482, 40L);
	// Monster
	private static final int BLACK_WOLF = 20317;
	// Rewards
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int GRANDMAS_PEARL = 1502;
	private static final int GRANDMAS_MIRROR = 1503;
	private static final int GRANDMAS_NECKLACE = 1504;
	private static final int GRANDMAS_HAIRPIN = 1505;
	// Misc
	private static final int MIN_LVL = 4;
	
	public Q00291_RevengeOfTheRedbonnet() {
		super(291);
		bindStartNpc(MARYSE_REDBONNET);
		bindTalk(MARYSE_REDBONNET);
		bindKill(BLACK_WOLF);
		registerQuestItems(BLACK_WOLF_PELT.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && event.equals("30553-03.htm")) {
			qs.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1) && Util.checkIfInRange(1500, npc, qs.getPlayer(), true)) {
			if (giveItemRandomly(qs.getPlayer(), npc, BLACK_WOLF_PELT, true)) {
				qs.setCond(2);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		String html = getNoQuestMsg(player);
		if (qs.isCreated()) {
			html = ((player.getLevel() >= MIN_LVL) ? "30553-02.htm" : "30553-01.htm");
		} else if (qs.isStarted()) {
			if (qs.isCond(2) && hasItemsAtLimit(player, BLACK_WOLF_PELT)) {
				takeItems(player, BLACK_WOLF_PELT.getId(), -1);
				final int chance = getRandom(100);
				if (chance <= 2) {
					giveItems(player, GRANDMAS_PEARL, 1);
				} else if (chance <= 20) {
					giveItems(player, GRANDMAS_MIRROR, 1);
				} else if (chance <= 45) {
					giveItems(player, GRANDMAS_NECKLACE, 1);
				} else {
					giveItems(player, GRANDMAS_HAIRPIN, 1);
					giveItems(player, SCROLL_OF_ESCAPE, 1);
				}
				qs.exitQuest(true, true);
				html = "30553-05.html";
			} else {
				html = "30553-04.html";
			}
		}
		return html;
	}
}
