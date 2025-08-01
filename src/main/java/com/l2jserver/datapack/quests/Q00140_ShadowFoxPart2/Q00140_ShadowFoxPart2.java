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
package com.l2jserver.datapack.quests.Q00140_ShadowFoxPart2;

import com.l2jserver.datapack.quests.Q00139_ShadowFoxPart1.Q00139_ShadowFoxPart1;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Shadow Fox - 2 (140)
 * @author Nono
 */
public class Q00140_ShadowFoxPart2 extends Quest {
	// NPCs
	private static final int KLUCK = 30895;
	private static final int XENOVIA = 30912;
	// Items
	private static final int DARK_CRYSTAL = 10347;
	private static final int DARK_OXYDE = 10348;
	private static final int CRYPTOGRAM_OF_THE_GODDESS_SWORD = 10349;
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(20789, DARK_CRYSTAL, 45.0) // Crokian
		.addSingleDrop(20790, DARK_CRYSTAL, 58.0) // Dailaon
		.addSingleDrop(20791, DARK_CRYSTAL, 100.0) // Crokian Warrior
		.addSingleDrop(20792, DARK_CRYSTAL, 92.0) // Farhite
		.build();
	// Misc
	private static final int MIN_LEVEL = 37;
	private static final int MAX_REWARD_LEVEL = 42;
	private static final int CHANCE = 8;
	private static final int CRYSTAL_COUNT = 5;
	private static final int OXYDE_COUNT = 2;
	
	public Q00140_ShadowFoxPart2() {
		super(140);
		bindStartNpc(KLUCK);
		bindTalk(KLUCK, XENOVIA);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(DARK_CRYSTAL, DARK_OXYDE, CRYPTOGRAM_OF_THE_GODDESS_SWORD);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "30895-05.html":
			case "30895-06.html":
			case "30912-03.html":
			case "30912-04.html":
			case "30912-05.html":
			case "30912-08.html":
			case "30895-10.html":
				break;
			case "30895-03.htm":
				st.startQuest();
				break;
			case "30895-07.html":
				st.setCond(2, true);
				break;
			case "30912-06.html":
				st.set("talk", "1");
				break;
			case "30912-09.html":
				st.unset("talk");
				st.setCond(3, true);
				break;
			case "30912-14.html":
				if (getRandom(10) < CHANCE) {
					if (st.getQuestItemsCount(DARK_OXYDE) < OXYDE_COUNT) {
						st.giveItems(DARK_OXYDE, 1);
						st.takeItems(DARK_CRYSTAL, 5);
						return "30912-12.html";
					}
					st.giveItems(CRYPTOGRAM_OF_THE_GODDESS_SWORD, 1);
					st.takeItems(DARK_CRYSTAL, -1);
					st.takeItems(DARK_OXYDE, -1);
					st.setCond(4, true);
					return "30912-13.html";
				}
				st.takeItems(DARK_CRYSTAL, 5);
				break;
			case "30895-11.html":
				st.giveAdena(18775, true);
				if (player.getLevel() <= MAX_REWARD_LEVEL) {
					st.addExpAndSp(30000, 2000);
				}
				st.exitQuest(false, true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		QuestState st = getRandomPartyMemberState(player, 3, 1, npc);
		if (st != null) {
			giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc), true);
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case KLUCK:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? (player.hasQuestCompleted(Q00139_ShadowFoxPart1.class.getSimpleName())) ? "30895-01.htm" : "30895-00.htm" : "30895-02.htm";
						break;
					case State.STARTED:
						switch (st.getCond()) {
							case 1:
								htmltext = "30895-04.html";
								break;
							case 2:
							case 3:
								htmltext = "30895-08.html";
								break;
							case 4:
								if (st.isSet("talk")) {
									htmltext = "30895-10.html";
								} else {
									st.takeItems(CRYPTOGRAM_OF_THE_GODDESS_SWORD, -1);
									st.set("talk", "1");
									htmltext = "30895-09.html";
								}
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			
			case XENOVIA:
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1:
							htmltext = "30912-01.html";
							break;
						case 2:
							htmltext = (st.isSet("talk")) ? "30912-07.html" : "30912-02.html";
							break;
						case 3:
							htmltext = (st.getQuestItemsCount(DARK_CRYSTAL) >= CRYSTAL_COUNT) ? "30912-11.html" : "30912-10.html";
							break;
						case 4:
							htmltext = "30912-15.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}
