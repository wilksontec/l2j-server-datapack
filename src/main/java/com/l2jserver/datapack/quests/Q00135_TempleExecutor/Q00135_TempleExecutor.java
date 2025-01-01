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
package com.l2jserver.datapack.quests.Q00135_TempleExecutor;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Temple Executor (135)
 * @author malyelfik
 */
public class Q00135_TempleExecutor extends Quest {
	// NPCs
	private static final int SHEGFIELD = 30068;
	private static final int PANO = 30078;
	private static final int ALEX = 30291;
	private static final int SONIN = 31773;
	// Items
	private static final int SONINS_CREDENTIALS = 10331;
	private static final int PANOS_CREDENTIALS = 10332;
	private static final int ALEXS_CREDENTIALS = 10333;
	private static final int BADGE_TEMPLE_EXECUTOR = 10334;
	private static final QuestItemChanceHolder STOLEN_CARGO = new QuestItemChanceHolder(10328, 10L);
	private static final QuestItemChanceHolder HATE_CRYSTAL = new QuestItemChanceHolder(10329, 10L);
	private static final QuestItemChanceHolder OLD_TREASURE_MAP = new QuestItemChanceHolder(10330, 10L);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(20781, STOLEN_CARGO, 43.9)
		.addSingleDrop(20781, HATE_CRYSTAL, 43.9)
		.addSingleDrop(20781, OLD_TREASURE_MAP, 43.9) // Delu Lizardman Shaman
		.addSingleDrop(21104, STOLEN_CARGO, 43.9)
		.addSingleDrop(21104, HATE_CRYSTAL, 43.9)
		.addSingleDrop(21104, OLD_TREASURE_MAP, 43.9) // Delu Lizardman Supplier
		.addSingleDrop(21105, STOLEN_CARGO, 50.4)
		.addSingleDrop(21105, HATE_CRYSTAL, 50.4)
		.addSingleDrop(21105, OLD_TREASURE_MAP, 50.4) // Delu Lizardman Special Agent
		.addSingleDrop(21106, STOLEN_CARGO, 42.3)
		.addSingleDrop(21106, HATE_CRYSTAL, 42.3)
		.addSingleDrop(21106, OLD_TREASURE_MAP, 42.3) // Cursed Seer
		.addSingleDrop(21107, STOLEN_CARGO, 90.2)
		.addSingleDrop(21107, HATE_CRYSTAL, 90.2)
		.addSingleDrop(21107, OLD_TREASURE_MAP, 90.2) // Delu Lizardman Commander
		.build();
	// Misc
	private static final int MIN_LEVEL = 35;
	private static final int MAX_REWARD_LEVEL = 41;
	
	public Q00135_TempleExecutor() {
		super(135, Q00135_TempleExecutor.class.getSimpleName(), "Temple Executor");
		bindStartNpc(SHEGFIELD);
		bindTalk(SHEGFIELD, ALEX, SONIN, PANO);
		bindKill(DROPLIST.getNpcIds());
		registerQuestItems(STOLEN_CARGO.getId(), HATE_CRYSTAL.getId(), OLD_TREASURE_MAP.getId(), SONINS_CREDENTIALS, PANOS_CREDENTIALS, ALEXS_CREDENTIALS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = event;
		switch (event) {
			case "30291-02a.html":
			case "30291-04.html":
			case "30291-05.html":
			case "30291-06.html":
			case "30068-08.html":
			case "30068-09.html":
			case "30068-10.html":
				break;
			case "30068-03.htm":
				st.startQuest();
				break;
			case "30068-04.html":
				st.setCond(2, true);
				break;
			case "30291-07.html":
				st.unset("talk");
				st.setCond(3, true);
				break;
			case "30068-11.html":
				st.giveItems(BADGE_TEMPLE_EXECUTOR, 1);
				st.giveAdena(16924, true);
				if (player.getLevel() < MAX_REWARD_LEVEL) {
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
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		final L2PcInstance member = getRandomPartyMember(player, 3);
		if (member == null) {
			return super.onKill(npc, player, isSummon);
		}
		final QuestState st = getQuestState(member, false);
		if (!hasItemsAtLimit(st.getPlayer(), STOLEN_CARGO)) {
			giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc), false);
		} else if (!hasItemsAtLimit(st.getPlayer(), HATE_CRYSTAL)) {
			giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc, HATE_CRYSTAL), false);
		} else {
			giveItemRandomly(st.getPlayer(), npc, DROPLIST.get(npc, OLD_TREASURE_MAP), false);
		}
		
		if (hasItemsAtLimit(st.getPlayer(), STOLEN_CARGO, HATE_CRYSTAL, OLD_TREASURE_MAP)) {
			st.setCond(4, true);
		} else {
			playSound(st.getPlayer(), Sound.ITEMSOUND_QUEST_ITEMGET);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		switch (npc.getId()) {
			case SHEGFIELD:
				switch (st.getState()) {
					case State.CREATED:
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "30068-01.htm" : "30068-02.htm";
						break;
					case State.STARTED:
						switch (st.getCond()) {
							case 1: // 1
								st.setCond(2, true);
								htmltext = "30068-04.html";
								break;
							case 2: // 2, 3
							case 3: // 4
								htmltext = "30068-05.html";
								break;
							case 4: // 5
								htmltext = "30068-06.html";
								break;
							case 5:
								if (st.isSet("talk")) {
									htmltext = "30068-08.html";
								} else if (st.hasQuestItems(PANOS_CREDENTIALS, SONINS_CREDENTIALS, ALEXS_CREDENTIALS)) {
									st.takeItems(SONINS_CREDENTIALS, -1);
									st.takeItems(PANOS_CREDENTIALS, -1);
									st.takeItems(ALEXS_CREDENTIALS, -1);
									st.set("talk", "1");
									htmltext = "30068-07.html";
								} else {
									htmltext = "30068-06.html";
								}
								break;
						}
						break;
					case State.COMPLETED:
						htmltext = getAlreadyCompletedMsg(player);
						break;
				}
				break;
			case ALEX:
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1:
							htmltext = "30291-01.html";
							break;
						case 2:
							if (st.isSet("talk")) {
								htmltext = "30291-03.html";
							} else {
								st.set("talk", "1");
								htmltext = "30291-02.html";
							}
							break;
						case 3:
							htmltext = "30291-08.html"; // 4
							break;
						case 4:
							if (st.hasQuestItems(PANOS_CREDENTIALS, SONINS_CREDENTIALS)) {
								if (!hasItemsAtLimit(st.getPlayer(), OLD_TREASURE_MAP)) {
									return htmltext;
								}
								st.setCond(5, true);
								st.takeItems(OLD_TREASURE_MAP.getId(), -1);
								st.giveItems(ALEXS_CREDENTIALS, 1);
								htmltext = "30291-10.html";
							} else {
								htmltext = "30291-09.html";
							}
							break;
						case 5:
							htmltext = "30291-11.html";
							break;
					}
				}
				break;
			case PANO:
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1:
							htmltext = "30078-01.html";
							break;
						case 2:
							htmltext = "30078-02.html";
							break;
						case 3:
							htmltext = "30078-03.html";
							break;
						case 4:
							if (!st.isSet("Pano")) {
								if (!hasItemsAtLimit(st.getPlayer(), HATE_CRYSTAL)) {
									return htmltext;
								}
								st.takeItems(HATE_CRYSTAL.getId(), -1);
								st.giveItems(PANOS_CREDENTIALS, 1);
								st.set("Pano", "1");
								htmltext = "30078-04.html";
								break;
							}
						case 5:
							htmltext = "30078-05.html";
							break;
					}
				}
				break;
			case SONIN:
				if (st.isStarted()) {
					switch (st.getCond()) {
						case 1:
							htmltext = "31773-01.html";
							break;
						case 2:
							htmltext = "31773-02.html";
							break;
						case 3:
							htmltext = "31773-03.html";
							break;
						case 4:
							if (!st.isSet("Sonin")) {
								if (!hasItemsAtLimit(st.getPlayer(), STOLEN_CARGO)) {
									return htmltext;
								}
								st.takeItems(STOLEN_CARGO.getId(), -1);
								st.giveItems(SONINS_CREDENTIALS, 1);
								st.set("Sonin", "1");
								htmltext = "31773-04.html";
								break;
							}
						case 5:
							htmltext = "31773-05.html";
							break;
					}
				}
				break;
		}
		return htmltext;
	}
}
