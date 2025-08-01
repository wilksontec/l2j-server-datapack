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
package com.l2jserver.datapack.quests.Q00195_SevenSignsSecretRitualOfThePriests;

import com.l2jserver.datapack.quests.Q00194_SevenSignsMammonsContract.Q00194_SevenSignsMammonsContract;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Seven Signs, Secret Ritual of the Priests (195)
 * @author Adry_85
 */
public final class Q00195_SevenSignsSecretRitualOfThePriests extends Quest {
	// NPCs
	private static final int RAYMOND = 30289;
	private static final int IASON_HEINE = 30969;
	private static final int CLAUDIA_ATHEBALDT = 31001;
	private static final int LIGHT_OF_DAWN = 32575;
	private static final int JOHN = 32576;
	private static final int PASSWORD_ENTRY_DEVICE = 32577;
	private static final int IDENTITY_CONFIRM_DEVICE = 32578;
	private static final int DARKNESS_OF_DAWN = 32579;
	private static final int SHELF = 32580;
	// Items
	private static final int IDENTITY_CARD = 13822;
	private static final int SHUNAIMANS_CONTRACT = 13823;
	// Misc
	private static final int MIN_LEVEL = 79;
	// Skills
	// private static SkillHolder TRANSFORM_DISPEL = new SkillHolder(6200, 1);
	private static final SkillHolder TRANSFORMATION = new SkillHolder(6204);
	
	public Q00195_SevenSignsSecretRitualOfThePriests() {
		super(195);
		bindFirstTalk(IDENTITY_CONFIRM_DEVICE, PASSWORD_ENTRY_DEVICE, DARKNESS_OF_DAWN, SHELF);
		bindStartNpc(CLAUDIA_ATHEBALDT);
		bindTalk(CLAUDIA_ATHEBALDT, JOHN, RAYMOND, IASON_HEINE, LIGHT_OF_DAWN, DARKNESS_OF_DAWN, IDENTITY_CONFIRM_DEVICE, PASSWORD_ENTRY_DEVICE, SHELF);
		registerQuestItems(IDENTITY_CARD, SHUNAIMANS_CONTRACT);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "31001-03.html":
			case "31001-04.html":
			case "31001-05.html":
			case "32580-03.html": {
				htmltext = event;
				break;
			}
			case "31001-06.html": {
				st.startQuest();
				htmltext = event;
				break;
			}
			case "32576-02.html": {
				if (st.isCond(1)) {
					st.giveItems(IDENTITY_CARD, 1);
					st.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30289-02.html":
			case "30289-03.html":
			case "30289-05.html": {
				if (st.isCond(2)) {
					htmltext = event;
				}
				break;
			}
			case "30289-04.html": {
				if (st.isCond(2)) {
					npc.setTarget(player);
					npc.doCast(TRANSFORMATION);
					st.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30289-07.html": {
				if (st.isCond(3)) {
					htmltext = event;
				}
				break;
			}
			case "30289-08.html": {
				if (st.isCond(3) && st.hasQuestItems(IDENTITY_CARD) && st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
					st.takeItems(IDENTITY_CARD, -1);
					st.setCond(4, true);
					htmltext = event;
					if (player.getTransformationId() == 113) {
						// player.doCast(TRANSFORM_DISPEL);
						player.stopAllEffects();
					}
				}
				break;
			}
			case "30289-10.html": {
				if (st.isCond(3)) {
					npc.setTarget(player);
					npc.doCast(TRANSFORMATION);
					htmltext = event;
				}
				break;
			}
			case "30289-11.html": {
				if (st.isCond(3)) {
					// player.doCast(TRANSFORM_DISPEL);
					player.stopAllEffects();
					htmltext = event;
				}
				break;
			}
			case "30969-02.html": {
				if (st.isCond(4) && st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
					htmltext = event;
				}
				break;
			}
			case "reward": {
				if (st.isCond(4) && st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
					if (player.getLevel() >= MIN_LEVEL) {
						st.addExpAndSp(52518015, 5817677);
						st.exitQuest(false, true);
						htmltext = "30969-03.html";
					} else {
						htmltext = "level_check.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (npc.getId()) {
			case IDENTITY_CONFIRM_DEVICE: {
				htmltext = "32578-01.html";
				break;
			}
			case PASSWORD_ENTRY_DEVICE: {
				htmltext = "32577-01.html";
				break;
			}
			case DARKNESS_OF_DAWN: {
				htmltext = "32579-01.html";
				break;
			}
			case SHELF: {
				htmltext = "32580-01.html";
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState()) {
			case State.COMPLETED: {
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
			case State.CREATED: {
				if (npc.getId() == CLAUDIA_ATHEBALDT) {
					htmltext = ((player.getLevel() >= MIN_LEVEL) && player.hasQuestCompleted(Q00194_SevenSignsMammonsContract.class.getSimpleName())) ? "31001-01.htm" : "31001-02.html";
				}
				break;
			}
			case State.STARTED: {
				switch (npc.getId()) {
					case CLAUDIA_ATHEBALDT: {
						if (st.isCond(1)) {
							htmltext = "31001-07.html";
						}
						break;
					}
					case JOHN: {
						switch (st.getCond()) {
							case 1: {
								htmltext = "32576-01.html";
								break;
							}
							case 2: {
								htmltext = "32576-03.html";
								break;
							}
						}
						break;
					}
					case RAYMOND: {
						switch (st.getCond()) {
							case 2: {
								if (st.hasQuestItems(IDENTITY_CARD) && (player.getTransformationId() != 113)) {
									htmltext = "30289-01.html";
								}
								break;
							}
							case 3: {
								if (st.hasQuestItems(IDENTITY_CARD)) {
									htmltext = st.hasQuestItems(SHUNAIMANS_CONTRACT) ? "30289-06.html" : "30289-09.html";
								}
								break;
							}
							case 4: {
								htmltext = "30289-12.html";
								break;
							}
						}
						break;
					}
					case LIGHT_OF_DAWN: {
						if (st.isCond(3)) {
							if (st.hasQuestItems(IDENTITY_CARD)) {
								htmltext = "31001-07.html";
							}
						}
						break;
					}
					case PASSWORD_ENTRY_DEVICE: {
						if (st.isCond(3) && st.hasQuestItems(IDENTITY_CARD)) {
							htmltext = "32577-02.html";
							player.teleToLocation(-78240, 205858, -7856);
						}
						break;
					}
					case SHELF: {
						if (st.isCond(3) && !st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
							st.giveItems(SHUNAIMANS_CONTRACT, 1);
							htmltext = "32580-02.html";
						}
						break;
					}
					case DARKNESS_OF_DAWN: {
						if (st.isCond(3) && !st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
							htmltext = "32579-02.html";
						}
						break;
					}
					case IASON_HEINE: {
						if (st.isCond(4) && st.hasQuestItems(SHUNAIMANS_CONTRACT)) {
							htmltext = "30969-01.html";
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
