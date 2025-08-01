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
package com.l2jserver.datapack.quests.Q00238_SuccessFailureOfBusiness;

import com.l2jserver.datapack.quests.Q00239_WontYouJoinUs.Q00239_WontYouJoinUs;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Success/Failure Of Business (238)<br>
 * Original Jython script by Bloodshed.
 * @author Joxit
 */
public class Q00238_SuccessFailureOfBusiness extends Quest {
	// NPCs
	private static final int HELVETICA = 32641;
	// Mobs
	private static final int BRAZIER_OF_PURITY = 18806;
	private static final int EVIL_SPIRITS = 22658;
	private static final int GUARDIAN_SPIRITS = 22659;
	// Items
	private static final int VICINITY_OF_FOS = 14865;
	private static final int BROKEN_PIECE_OF_MAGIC_FORCE = 14867;
	private static final int GUARDIAN_SPIRIT_FRAGMENT = 14868;
	// Misc
	private static final int BROKEN_PIECE_OF_MAGIC_FORCE_NEEDED = 10;
	private static final int GUARDIAN_SPIRIT_FRAGMENT_NEEDED = 20;
	private static final int CHANCE_FOR_FRAGMENT = 80;
	private static final int MIN_LEVEL = 82;
	
	public Q00238_SuccessFailureOfBusiness() {
		super(238);
		bindStartNpc(HELVETICA);
		bindTalk(HELVETICA);
		bindKill(BRAZIER_OF_PURITY, EVIL_SPIRITS, GUARDIAN_SPIRITS);
		registerQuestItems(BROKEN_PIECE_OF_MAGIC_FORCE, GUARDIAN_SPIRIT_FRAGMENT);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "32461-02.htm":
				htmltext = event;
				break;
			case "32461-03.html":
				st.startQuest();
				htmltext = event;
				break;
			case "32461-06.html":
				if (st.isCond(2)) {
					st.setCond(3, true);
					htmltext = event;
				}
				break;
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.getId() == BRAZIER_OF_PURITY) {
			final L2PcInstance partyMember = getRandomPartyMember(killer, 1);
			if (partyMember != null) {
				final QuestState st = getQuestState(partyMember, false);
				if (st.getQuestItemsCount(BROKEN_PIECE_OF_MAGIC_FORCE) < BROKEN_PIECE_OF_MAGIC_FORCE_NEEDED) {
					st.giveItems(BROKEN_PIECE_OF_MAGIC_FORCE, 1);
				}
				if (st.getQuestItemsCount(BROKEN_PIECE_OF_MAGIC_FORCE) == BROKEN_PIECE_OF_MAGIC_FORCE_NEEDED) {
					st.setCond(2, true);
				} else {
					st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		} else {
			final L2PcInstance partyMember = getRandomPartyMember(killer, 3);
			if ((partyMember != null) && (getRandom(100) < CHANCE_FOR_FRAGMENT)) {
				final QuestState st = getQuestState(partyMember, false);
				if (st.getQuestItemsCount(GUARDIAN_SPIRIT_FRAGMENT) < GUARDIAN_SPIRIT_FRAGMENT_NEEDED) {
					st.giveItems(GUARDIAN_SPIRIT_FRAGMENT, 1);
				}
				if (st.getQuestItemsCount(GUARDIAN_SPIRIT_FRAGMENT) == GUARDIAN_SPIRIT_FRAGMENT_NEEDED) {
					st.setCond(4, true);
				} else {
					st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		String htmltext = getNoQuestMsg(talker);
		final QuestState st = getQuestState(talker, true);
		switch (st.getState()) {
			case State.COMPLETED:
				htmltext = "32461-09.html";
				break;
			case State.CREATED:
				if (st.getPlayer().hasQuestCompleted(Q00239_WontYouJoinUs.class.getSimpleName())) {
					htmltext = "32461-10.html";
				} else if (st.getPlayer().hasQuestCompleted(Q00239_WontYouJoinUs.class.getSimpleName()) && (talker.getLevel() >= MIN_LEVEL) && st.hasQuestItems(VICINITY_OF_FOS)) {
					htmltext = "32461-01.htm";
				} else {
					htmltext = "32461-00.html";
				}
				break;
			case State.STARTED:
				switch (st.getCond()) {
					case 1:
						htmltext = "32461-04.html";
						break;
					case 2:
						if (st.getQuestItemsCount(BROKEN_PIECE_OF_MAGIC_FORCE) == BROKEN_PIECE_OF_MAGIC_FORCE_NEEDED) {
							htmltext = "32461-05.html";
							st.takeItems(BROKEN_PIECE_OF_MAGIC_FORCE, -1);
						}
						break;
					case 3:
						htmltext = "32461-07.html";
						break;
					case 4:
						if (st.getQuestItemsCount(GUARDIAN_SPIRIT_FRAGMENT) == GUARDIAN_SPIRIT_FRAGMENT_NEEDED) {
							htmltext = "32461-08.html";
							st.giveAdena(283346, true);
							st.takeItems(VICINITY_OF_FOS, 1);
							st.addExpAndSp(1319736, 103553);
							st.exitQuest(false, true);
						}
						break;
				}
				break;
		}
		return htmltext;
	}
}