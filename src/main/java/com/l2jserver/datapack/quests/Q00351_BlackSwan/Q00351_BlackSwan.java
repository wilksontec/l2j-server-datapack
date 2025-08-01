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
package com.l2jserver.datapack.quests.Q00351_BlackSwan;

import static com.l2jserver.gameserver.model.quest.QuestDroplist.singleDropItem;

import com.l2jserver.datapack.quests.Q00345_MethodToRaiseTheDead.Q00345_MethodToRaiseTheDead;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Black Swan (351)
 * @author Adry_85
 */
public final class Q00351_BlackSwan extends Quest {
	// NPCs
	private static final int ROMAN = 30897;
	private static final int GOSTA = 30916;
	private static final int IASON_HEINE = 30969;
	// Monsters
	private static final int TASABA_LIZARDMAN1 = 20784;
	private static final int TASABA_LIZARDMAN_SHAMAN1 = 20785;
	private static final int TASABA_LIZARDMAN2 = 21639;
	private static final int TASABA_LIZARDMAN_SHAMAN2 = 21640;
	// Items
	private static final int ORDER_OF_GOSTA = 4296;
	private static final int LIZARD_FANG = 4297;
	private static final int BARREL_OF_LEAGUE = 4298;
	private static final int BILL_OF_IASON_HEINE = 4407;
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(TASABA_LIZARDMAN1, BARREL_OF_LEAGUE, 4.0)
		.addSingleDrop(TASABA_LIZARDMAN_SHAMAN1, BARREL_OF_LEAGUE, 3.0)
		.addSingleDrop(TASABA_LIZARDMAN2, BARREL_OF_LEAGUE, 4.0)
		.addSingleDrop(TASABA_LIZARDMAN_SHAMAN2, BARREL_OF_LEAGUE, 3.0)
		.build();
	// Misc
	private static final int MIN_LEVEL = 32;
	
	public Q00351_BlackSwan() {
		super(351);
		bindStartNpc(GOSTA, ROMAN);
		bindTalk(GOSTA, IASON_HEINE, ROMAN);
		bindKill(TASABA_LIZARDMAN1, TASABA_LIZARDMAN_SHAMAN1, TASABA_LIZARDMAN2, TASABA_LIZARDMAN_SHAMAN2);
		registerQuestItems(ORDER_OF_GOSTA, LIZARD_FANG, BARREL_OF_LEAGUE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30916-02.htm":
			case "30969-03.html": {
				htmltext = event;
				break;
			}
			case "30916-03.htm": {
				giveItems(player, ORDER_OF_GOSTA, 1);
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30969-02.html": {
				final long lizardFangCount = getQuestItemsCount(player, LIZARD_FANG);
				
				if (lizardFangCount == 0) {
					htmltext = event;
				} else {
					final long adenaBonus = (lizardFangCount >= 10) ? 3880 : 0;
					giveAdena(player, adenaBonus + (20 * lizardFangCount), true);
					takeItems(player, LIZARD_FANG, -1);
					htmltext = "30969-04.html";
				}
				break;
			}
			case "30969-05.html": {
				final long barrelOfLeagueCount = getQuestItemsCount(player, BARREL_OF_LEAGUE);
				
				if (barrelOfLeagueCount == 0) {
					htmltext = event;
				} else {
					giveItems(player, BILL_OF_IASON_HEINE, barrelOfLeagueCount);
					giveAdena(player, 3880, true);
					takeItems(player, BARREL_OF_LEAGUE, -1);
					qs.setCond(2);
					htmltext = "30969-06.html";
				}
				break;
			}
			case "30969-07.html": {
				htmltext = (!hasQuestItems(player, BARREL_OF_LEAGUE, LIZARD_FANG)) ? event : "30969-08.html";
				break;
			}
			case "30969-09.html": {
				htmltext = event;
				qs.exitQuest(true, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs == null) {
			return;
		}
		
		final int random = getRandom(20);
		if (random < 10) {
			giveItemRandomly(qs.getPlayer(), npc, LIZARD_FANG, true);
			
			if (getRandom(20) == 0) {
				giveItemRandomly(qs.getPlayer(), npc, BARREL_OF_LEAGUE, false);
			}
		} else if (random < 15) {
			giveItemRandomly(qs.getPlayer(), npc, singleDropItem(LIZARD_FANG, 2L), 0, true);
			
			if (getRandom(20) == 0) {
				giveItemRandomly(qs.getPlayer(), npc, BARREL_OF_LEAGUE, false);
			}
		} else {
			giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState qs = getQuestState(player, true);
		QuestState qs2 = player.getQuestState(Q00345_MethodToRaiseTheDead.class.getSimpleName());
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId()) {
			case GOSTA: {
				if (qs.isCreated()) {
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "30916-01.htm" : "30916-04.html";
				} else if (qs.isStarted()) {
					htmltext = "30916-05.html";
				}
				break;
			}
			case IASON_HEINE: {
				if (qs.isStarted()) {
					htmltext = "30969-01.html";
				}
				break;
			}
			case ROMAN: {
				if (qs.isStarted() || ((qs2 != null) && qs2.isStarted())) {
					htmltext = (hasQuestItems(player, BILL_OF_IASON_HEINE)) ? "30897-01.html" : "30897-02.html";
				}
				break;
			}
		}
		return htmltext;
	}
}
