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
package com.l2jserver.datapack.quests.Q00509_AClansFame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * A Clan's Fame (509)
 * @author Adry_85
 */
public class Q00509_AClansFame extends Quest {
	// NPC
	private static final int VALDIS = 31331;
	
	private static final Map<Integer, List<Integer>> REWARD_POINTS = new HashMap<>();
	
	static {
		REWARD_POINTS.put(1, Arrays.asList(25290, 8489, 1378)); // Daimon The White-Eyed
		REWARD_POINTS.put(2, Arrays.asList(25293, 8490, 1378)); // Hestia, Guardian Deity Of The Hot Springs
		REWARD_POINTS.put(3, Arrays.asList(25523, 8491, 1070)); // Plague Golem
		REWARD_POINTS.put(4, Arrays.asList(25322, 8492, 782)); // Demon's Agent Falston
	}
	
	private static final int[] RAID_BOSS = {
		25290,
		25293,
		25523,
		25322
	};
	
	public Q00509_AClansFame() {
		super(509);
		bindStartNpc(VALDIS);
		bindTalk(VALDIS);
		bindKill(RAID_BOSS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st == null) {
			return getNoQuestMsg(player);
		}
		
		switch (event) {
			case "31331-0.html":
				st.startQuest();
				break;
			case "31331-1.html":
				st.set("raid", "1");
				showRadar(player, 186304, -43744, -3193, 2);
				break;
			case "31331-2.html":
				st.set("raid", "2");
				showRadar(player, 134672, -115600, -1216, 2);
				break;
			case "31331-3.html":
				st.set("raid", "3");
				showRadar(player, 170000, -60000, -3500, 2);
				break;
			case "31331-4.html":
				st.set("raid", "4");
				showRadar(player, 93296, -75104, -1824, 2);
				break;
			case "31331-5.html":
				st.exitQuest(true, true);
				break;
		}
		return event;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		if (player.getClan() == null) {
			return;
		}
		
		QuestState st = null;
		if (player.isClanLeader()) {
			st = player.getQuestState(getName());
		} else {
			L2PcInstance pleader = player.getClan().getLeader().getPlayerInstance();
			if ((pleader != null) && player.isInsideRadius(pleader, 1500, true, false)) {
				st = pleader.getQuestState(getName());
			}
		}
		
		if ((st != null) && st.isStarted()) {
			int raid = st.getInt("raid");
			if (REWARD_POINTS.containsKey(raid)) {
				if ((npc.getId() == REWARD_POINTS.get(raid).get(0)) && !st.hasQuestItems(REWARD_POINTS.get(raid).get(1))) {
					st.rewardItems(REWARD_POINTS.get(raid).get(1), 1);
					st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		L2Clan clan = player.getClan();
		switch (st.getState()) {
			case State.CREATED:
				htmltext = ((clan == null) || !player.isClanLeader() || (clan.getLevel() < 6)) ? "31331-0a.htm" : "31331-0b.htm";
				break;
			case State.STARTED:
				if ((clan == null) || !player.isClanLeader()) {
					st.exitQuest(true);
					return "31331-6.html";
				}
				
				int raid = st.getInt("raid");
				
				if (REWARD_POINTS.containsKey(raid)) {
					if (st.hasQuestItems(REWARD_POINTS.get(raid).get(1))) {
						htmltext = "31331-" + raid + "b.html";
						st.playSound(Sound.ITEMSOUND_QUEST_FANFARE_1);
						st.takeItems(REWARD_POINTS.get(raid).get(1), -1);
						final int rep = REWARD_POINTS.get(raid).get(2);
						clan.addReputationScore(rep, true);
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLAN_QUEST_COMPLETED_AND_S1_POINTS_GAINED).addInt(rep));
						clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
					} else {
						htmltext = "31331-" + raid + "a.html";
					}
				} else {
					htmltext = "31331-0.html";
				}
				break;
			default:
				break;
		}
		return htmltext;
	}
}
