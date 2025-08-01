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
package com.l2jserver.datapack.quests.Q00275_DarkWingedSpies;

import static com.l2jserver.gameserver.model.quest.QuestDroplist.singleDropItem;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.util.Util;

/**
 * Dark Winged Spies (275)
 * @author xban1x
 */
public final class Q00275_DarkWingedSpies extends Quest {
	// Npc
	private static final int NERUGA_CHIEF_TANTUS = 30567;
	// Items
	private static final int VARANGKAS_PARASITE = 1479;
	private static final QuestItemChanceHolder DARKWING_BAT_FANG = new QuestItemChanceHolder(1478, 70L);
	// Monsters
	private static final int DARKWING_BAT = 20316;
	private static final int VARANGKAS_TRACKER = 27043;
	// Misc
	private static final int MIN_LVL = 11;
	private static final int FANG_PRICE = 60;
	
	public Q00275_DarkWingedSpies() {
		super(275);
		bindStartNpc(NERUGA_CHIEF_TANTUS);
		bindTalk(NERUGA_CHIEF_TANTUS);
		bindKill(DARKWING_BAT, VARANGKAS_TRACKER);
		bindSeeCreature(VARANGKAS_TRACKER);
		registerQuestItems(DARKWING_BAT_FANG.getId(), VARANGKAS_PARASITE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if ((st != null) && event.equals("30567-03.htm")) {
			st.startQuest();
			return event;
		}
		return null;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st != null) && st.isCond(1) && Util.checkIfInRange(1500, npc, killer, true)) {
			final long count = st.getQuestItemsCount(DARKWING_BAT_FANG.getId());
			
			switch (npc.getId()) {
				case DARKWING_BAT -> {
					if (giveItemRandomly(st.getPlayer(), npc, DARKWING_BAT_FANG, true)) {
						st.setCond(2);
					} else if ((count > 10) && (count < 66) && (getRandom(100) < 10)) {
						st.addSpawn(VARANGKAS_TRACKER);
						st.giveItems(VARANGKAS_PARASITE, 1);
					}
				}
				case VARANGKAS_TRACKER -> {
					if ((count < 66) && st.hasQuestItems(VARANGKAS_PARASITE)) {
						if (giveItemRandomly(st.getPlayer(), npc, singleDropItem(DARKWING_BAT_FANG, 5L), DARKWING_BAT_FANG.getLimit(), true)) {
							st.setCond(2);
						}
						st.takeItems(VARANGKAS_PARASITE, -1);
					}
				}
			}
		}
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (creature.isPlayer()) {
			npc.setRunning();
			((L2Attackable) npc).addDamageHate(creature, 0, 1);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState st = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (st.getState()) {
			case State.CREATED: {
				htmltext = (talker.getRace() == Race.ORC) ? (talker.getLevel() >= MIN_LVL) ? "30567-02.htm" : "30567-01.htm" : "30567-00.htm";
				break;
			}
			case State.STARTED: {
				switch (st.getCond()) {
					case 1: {
						htmltext = "30567-05.html";
						break;
					}
					case 2: {
						final long count = st.getQuestItemsCount(DARKWING_BAT_FANG.getId());
						if (count >= DARKWING_BAT_FANG.getLimit()) {
							st.giveAdena(count * FANG_PRICE, true);
							st.exitQuest(true, true);
							htmltext = "30567-05.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
