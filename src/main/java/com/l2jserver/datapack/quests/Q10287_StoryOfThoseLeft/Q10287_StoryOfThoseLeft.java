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
package com.l2jserver.datapack.quests.Q10287_StoryOfThoseLeft;

import com.l2jserver.datapack.quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Story of Those Left (10287)
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class Q10287_StoryOfThoseLeft extends Quest {
	// NPCs
	private static final int RAFFORTY = 32020;
	private static final int JINIA = 32760;
	private static final int KEGOR = 32761;
	// Misc
	private static final int MIN_LEVEL = 82;
	// Location
	private static final Location EXIT_LOC = new Location(113793, -109342, -845, 0);
	
	public Q10287_StoryOfThoseLeft() {
		super(10287);
		bindStartNpc(RAFFORTY);
		bindTalk(RAFFORTY, JINIA, KEGOR);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		if (st == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "32020-02.htm": {
				st.startQuest();
				st.setMemoState(1);
				st.setMemoStateEx(1, 0);
				st.setMemoStateEx(2, 0);
				htmltext = event;
				break;
			}
			case "32020-08.html": {
				if (st.isMemoState(2)) {
					htmltext = event;
				}
				break;
			}
			case "32760-02.html": {
				if (st.isMemoState(1)) {
					htmltext = event;
				}
				break;
			}
			case "32760-03.html": {
				if (st.isMemoState(1)) {
					st.setMemoStateEx(1, 1);
					st.setCond(3, true);
					htmltext = event;
				}
				break;
			}
			case "32760-06.html": {
				if (st.isMemoState(2)) {
					st.setCond(5, true);
					player.teleToLocation(EXIT_LOC, 0);
					htmltext = event; // TODO: missing "jinia_npc_q10287_06.htm"
				}
				break;
			}
			case "32761-02.html": {
				if (st.isMemoState(1) && st.isMemoStateEx(1, 1) && st.isMemoStateEx(2, 0)) {
					htmltext = event;
				}
				break;
			}
			case "32761-03.html": {
				if (st.isMemoState(1) && st.isMemoStateEx(1, 1) && st.isMemoStateEx(2, 0)) {
					st.setMemoStateEx(2, 1);
					st.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "10549":
			case "10550":
			case "10551":
			case "10552":
			case "10553":
			case "14219": {
				if (st.isMemoState(2)) {
					st.rewardItems(Integer.parseInt(event), 1);
					htmltext = "32020-09.html";
					st.exitQuest(false, true);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (st.isCompleted()) {
			if (npc.getId() == RAFFORTY) {
				htmltext = "32020-04.html";
			}
		} else if (st.isCreated()) {
			if (npc.getId() == RAFFORTY) {
				htmltext = ((player.getLevel() >= MIN_LEVEL) && player.hasQuestCompleted(Q10286_ReunionWithSirra.class.getSimpleName())) ? "32020-01.htm" : "32020-03.htm";
			}
		} else if (st.isStarted()) {
			switch (npc.getId()) {
				case RAFFORTY: {
					if (st.isMemoState(1)) {
						htmltext = (player.getLevel() >= MIN_LEVEL) ? "32020-05.html" : "32020-06.html";
					} else if (st.isMemoState(2)) {
						htmltext = "32020-07.html";
					}
					break;
				}
				case JINIA: {
					if (st.isMemoState(1)) {
						final int memoStateEx1 = st.getMemoStateEx(1);
						final int memoStateEx2 = st.getMemoStateEx(2);
						if ((memoStateEx1 == 0) && (memoStateEx2 == 0)) {
							htmltext = "32760-01.html";
						} else if ((memoStateEx1 == 1) && (memoStateEx2 == 0)) {
							htmltext = "32760-04.html";
						} else if ((memoStateEx1 == 1) && (memoStateEx2 == 1)) {
							st.setCond(5, true);
							st.setMemoState(2);
							st.setMemoStateEx(1, 0);
							st.setMemoStateEx(2, 0);
							final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
							world.removeAllowed(player.getObjectId());
							player.setInstanceId(0);
							htmltext = "32760-05.html";
						}
					}
					break;
				}
				case KEGOR: {
					if (st.isMemoState(1)) {
						final int memoStateEx1 = st.getMemoStateEx(1);
						final int memoStateEx2 = st.getMemoStateEx(2);
						if ((memoStateEx1 == 1) && (memoStateEx2 == 0)) {
							htmltext = "32761-01.html";
						} else if ((memoStateEx1 == 0) && (memoStateEx2 == 0)) {
							htmltext = "32761-04.html";
						} else if ((memoStateEx1 == 1) && (memoStateEx2 == 1)) {
							htmltext = "32761-05.html";
						}
					}
					break;
				}
			}
		}
		return htmltext;
	}
}
