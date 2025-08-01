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
package com.l2jserver.datapack.quests.Q00184_ArtOfPersuasion;

import com.l2jserver.datapack.quests.Q00183_RelicExploration.Q00183_RelicExploration;
import com.l2jserver.datapack.quests.Q00185_NikolasCooperation.Q00185_NikolasCooperation;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Art Of Persuasion (184)
 * @author ivantotov
 */
public final class Q00184_ArtOfPersuasion extends Quest {
	// NPCs
	private static final int MAESTRO_NIKOLA = 30621;
	private static final int RESEARCHER_LORAIN = 30673;
	private static final int DESTROYED_DEVICE = 32366;
	private static final int ALARM_OF_GIANT = 32367;
	// Items
	private static final int METALLOGRAPH = 10359;
	private static final int BROKEN_METAL_PIECES = 10360;
	private static final int NIKOLAS_MAP = 10361;
	// Reward
	private static final int LORAINES_CERTIFICATE = 10362;
	// Misc
	private static final int MIN_LEVEL = 40;
	private static final int MAX_LEVEL_FOR_EXP_SP = 46;
	
	public Q00184_ArtOfPersuasion() {
		super(184);
		bindStartNpc(MAESTRO_NIKOLA);
		bindTalk(MAESTRO_NIKOLA, RESEARCHER_LORAIN, DESTROYED_DEVICE, ALARM_OF_GIANT);
		registerQuestItems(METALLOGRAPH, BROKEN_METAL_PIECES, NIKOLAS_MAP);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30621-06.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					qs.setMemoState(1);
					giveItems(player, NIKOLAS_MAP, 1);
					htmltext = event;
				}
				break;
			}
			case "30621-03.htm": {
				if (player.getLevel() >= MIN_LEVEL) {
					htmltext = event;
				} else {
					htmltext = "30621-03a.htm";
				}
				break;
			}
			case "30621-04.htm":
			case "30621-05.htm": {
				htmltext = event;
				break;
			}
			case "30673-02.html": {
				if (qs.isMemoState(1)) {
					htmltext = event;
				}
				break;
			}
			case "30673-03.html": {
				if (qs.isMemoState(1)) {
					takeItems(player, NIKOLAS_MAP, -1);
					qs.setMemoState(2);
					qs.setCond(2, true);
					htmltext = event;
				}
				break;
			}
			case "30673-05.html": {
				if (qs.isMemoState(2)) {
					qs.setMemoState(3);
					qs.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "30673-08.html": {
				if (qs.isMemoState(6)) {
					htmltext = event;
				}
				break;
			}
			case "30673-09.html": {
				if (qs.isMemoState(6)) {
					if (hasQuestItems(player, METALLOGRAPH)) {
						giveItems(player, LORAINES_CERTIFICATE, 1);
						qs.exitQuest(false, true);
						htmltext = event;
					} else {
						htmltext = "30673-10.htm";
						qs.exitQuest(false, true);
					}
					if (player.getLevel() < MAX_LEVEL_FOR_EXP_SP) {
						giveAdena(player, 72527, true);
						addExpAndSp(player, 203717, 14032);
					} else {
						giveAdena(player, 72527, true);
					}
				}
				break;
			}
			case "32366-03.html": {
				if (qs.isMemoState(3) && !npc.getVariables().getBoolean("SPAWNED", false)) {
					npc.getVariables().set("SPAWNED", true);
					npc.getVariables().set("PLAYER_ID", player.getObjectId());
					L2Npc alarm = addSpawn(ALARM_OF_GIANT, player.getX() + 80, player.getY() + 60, player.getZ(), 16384, false, 0);
					alarm.getVariables().set("player0", player);
					alarm.getVariables().set("npc0", npc);
				}
				break;
			}
			case "32366-06.html": {
				if (qs.isMemoState(4)) {
					giveItems(player, METALLOGRAPH, 1);
					qs.setMemoState(6);
					qs.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "32366-08.html": {
				if (qs.isMemoState(5)) {
					giveItems(player, BROKEN_METAL_PIECES, 1);
					qs.setMemoState(6);
					qs.setCond(5, true);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		final int memoState = qs.getMemoState();
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated()) {
			if (npc.getId() == MAESTRO_NIKOLA) {
				final QuestState q184 = player.getQuestState(Q00184_ArtOfPersuasion.class.getSimpleName());
				final QuestState q185 = player.getQuestState(Q00185_NikolasCooperation.class.getSimpleName());
				if (player.hasQuestCompleted(Q00183_RelicExploration.class.getSimpleName()) && (q184 != null) && (q185 != null)) {
					htmltext = (player.getLevel() >= MIN_LEVEL) ? "30621-01.htm" : "30621-02.html";
				}
			}
		} else if (qs.isStarted()) {
			switch (npc.getId()) {
				case MAESTRO_NIKOLA: {
					if (memoState == 1) {
						htmltext = "30621-07.html";
					}
					break;
				}
				case RESEARCHER_LORAIN: {
					if (memoState == 1) {
						htmltext = "30673-01.html";
					} else if (memoState == 2) {
						htmltext = "30673-04.html";
					} else if ((memoState >= 3) && (memoState <= 5)) {
						htmltext = "30673-06.html";
					} else if (memoState == 6) {
						htmltext = "30673-07.html";
					}
					break;
				}
				case DESTROYED_DEVICE: {
					if (memoState == 3) {
						if (!npc.getVariables().getBoolean("SPAWNED", false)) {
							htmltext = "32366-01.html";
						} else if (npc.getVariables().getInt("PLAYER_ID") == player.getObjectId()) {
							htmltext = "32366-03.html";
						} else {
							htmltext = "32366-04.html";
						}
					} else if (memoState == 4) {
						htmltext = "32366-05.html";
					} else if (memoState == 5) {
						htmltext = "32366-07.html";
					}
					break;
				}
			}
		} else if (qs.isCompleted()) {
			if (npc.getId() == MAESTRO_NIKOLA) {
				htmltext = getAlreadyCompletedMsg(player);
			}
		}
		return htmltext;
	}
}