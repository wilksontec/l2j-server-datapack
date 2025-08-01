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
package com.l2jserver.datapack.quests.Q00356_DigUpTheSeaOfSpores;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

/**
 * Dig Up the Sea of Spores! (356)
 * @author Adry_85
 */
public final class Q00356_DigUpTheSeaOfSpores extends Quest {
	// NPC
	private static final int GAUEN = 30717;
	// Monsters
	private static final int ROTTING_TREE = 20558;
	private static final int SPORE_ZOMBIE = 20562;
	// Items
	private static final QuestItemChanceHolder CARNIVORE_SPORE = new QuestItemChanceHolder(5865, 50L);
	private static final QuestItemChanceHolder HERBIVOROUS_SPORE = new QuestItemChanceHolder(5866, 50L);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(ROTTING_TREE, HERBIVOROUS_SPORE, 73.0)
		.addSingleDrop(SPORE_ZOMBIE, CARNIVORE_SPORE, 94.0)
		.build();
	// Misc
	private static final int MIN_LEVEL = 43;
	
	public Q00356_DigUpTheSeaOfSpores() {
		super(356);
		bindStartNpc(GAUEN);
		bindTalk(GAUEN);
		bindKill(ROTTING_TREE, SPORE_ZOMBIE);
		registerQuestItems(HERBIVOROUS_SPORE.getId(), CARNIVORE_SPORE.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30717-02.htm":
			case "30717-03.htm":
			case "30717-04.htm":
			case "30717-10.html":
			case "30717-18.html": {
				htmltext = event;
				break;
			}
			case "30717-05.htm": {
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30717-09.html": {
				addExpAndSp(player, 31850, 0);
				takeItems(player, CARNIVORE_SPORE.getId(), -1);
				takeItems(player, HERBIVOROUS_SPORE.getId(), -1);
				htmltext = event;
				break;
			}
			case "30717-11.html": {
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "30717-14.html": {
				addExpAndSp(player, 45500, 2600);
				qs.exitQuest(true, true);
				htmltext = event;
				break;
			}
			case "FINISH": {
				final int value = getRandom(100);
				int adena;
				if (value < 20) {
					adena = 44000;
					htmltext = "30717-15.html";
				} else if (value < 70) {
					adena = 20950;
					htmltext = "30717-16.html";
				} else {
					adena = 10400;
					htmltext = "30717-17.html";
				}
				giveAdena(player, adena, true);
				qs.exitQuest(true, true);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !Util.checkIfInRange(1500, npc, killer, true)) {
			return;
		}
		
		if (giveItemRandomly(qs.getPlayer(), npc, DROPLIST.get(npc), true)) {
			if (hasItemsAtLimit(killer, CARNIVORE_SPORE, HERBIVOROUS_SPORE)) {
				qs.setCond(3);
			} else {
				qs.setCond(2);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			htmltext = (player.getLevel() >= MIN_LEVEL) ? "30717-01.htm" : "30717-06.htm";
		} else if (qs.isStarted()) {
			final boolean hasAllHerbSpores = hasItemsAtLimit(player, HERBIVOROUS_SPORE);
			final boolean hasAllCarnSpores = hasItemsAtLimit(player, CARNIVORE_SPORE);
			
			if (hasAllHerbSpores && hasAllCarnSpores) {
				htmltext = "30717-13.html";
			} else if (hasAllCarnSpores) {
				htmltext = "30717-12.html";
			} else if (hasAllHerbSpores) {
				htmltext = "30717-08.html";
			} else {
				htmltext = "30717-07.html";
			}
		}
		return htmltext;
	}
}
