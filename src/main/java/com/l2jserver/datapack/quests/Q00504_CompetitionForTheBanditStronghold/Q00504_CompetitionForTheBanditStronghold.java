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
package com.l2jserver.datapack.quests.Q00504_CompetitionForTheBanditStronghold;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.ClanHallSiegeManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

/**
 * Competition for the Bandit Stronghold (504)
 * @author BiggBoss, Zoey76
 */
public final class Q00504_CompetitionForTheBanditStronghold extends Quest {
	// Misc
	private static final SiegableHall BANDIT_STRONGHOLD = ClanHallSiegeManager.getInstance().getSiegableHall(35);
	// NPC
	private static final int MESSENGER = 35437;
	// Monsters
	private static final Map<Integer, Integer> MONSTERS = new HashMap<>();
	// Items
	private static final int TARLK_AMULET = 4332;
	private static final int CONTEST_CERTIFICATE = 4333;
	private static final int TROPHY_OF_ALLIANCE = 5009;
	
	static {
		MONSTERS.put(20570, 6); // Tarlk Bugbear
		MONSTERS.put(20571, 7); // Tarlk Bugbear Warrior
		MONSTERS.put(20572, 8); // Tarlk Bugbear High Warrior
		MONSTERS.put(20573, 9); // Tarlk Basilisk
		MONSTERS.put(20574, 7); // Elder Tarlk Basilisk
	}
	
	public Q00504_CompetitionForTheBanditStronghold() {
		super(504);
		bindStartNpc(MESSENGER);
		bindTalk(MESSENGER);
		bindKill(MONSTERS.keySet());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, false);
		String htmltext = null;
		if ((st != null) && event.equals("35437-02.htm")) {
			st.startQuest();
			st.giveItems(CONTEST_CERTIFICATE, 1);
			htmltext = "35437-02.htm";
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState st = getQuestState(killer, false);
		if ((st == null) || !st.hasQuestItems(CONTEST_CERTIFICATE) || !st.isStarted()) {
			return;
		}
		
		if (getRandom(10) < MONSTERS.get(npc.getId())) {
			st.giveItems(TARLK_AMULET, 1);
			if (st.getQuestItemsCount(TARLK_AMULET) < 30) {
				st.playSound(Sound.ITEMSOUND_QUEST_ITEMGET);
			} else {
				st.playSound(Sound.ITEMSOUND_QUEST_MIDDLE);
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState st = getQuestState(player, true);
		final L2Clan clan = player.getClan();
		String htmltext = getNoQuestMsg(player);
		if (!BANDIT_STRONGHOLD.isWaitingBattle()) {
			htmltext = getHtm(player.getHtmlPrefix(), "35437-09.html");
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			htmltext = htmltext.replaceAll("%nextSiege%", sdf.format(BANDIT_STRONGHOLD.getSiegeDate().getTime()));
		} else if ((clan == null) || (clan.getLevel() < 4)) {
			htmltext = "35437-04.html";
		} else if (!player.isClanLeader()) {
			htmltext = "35437-05.html";
		} else if ((clan.getHideoutId() > 0) || (clan.getFortId() > 0) || (clan.getCastleId() > 0)) {
			htmltext = "35437-10.html";
		} else {
			switch (st.getState()) {
				case State.CREATED: {
					if (!BANDIT_STRONGHOLD.isWaitingBattle()) {
						htmltext = getHtm(player.getHtmlPrefix(), "35437-03.html");
						final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						htmltext = htmltext.replaceAll("%nextSiege%", sdf.format(BANDIT_STRONGHOLD.getSiegeDate().getTime()));
					} else {
						htmltext = "35437-01.htm";
					}
					break;
				}
				case State.STARTED: {
					if (st.getQuestItemsCount(TARLK_AMULET) < 30) {
						htmltext = "35437-07.html";
					} else {
						st.takeItems(TARLK_AMULET, 30);
						st.rewardItems(TROPHY_OF_ALLIANCE, 1);
						st.exitQuest(true);
						htmltext = "35437-08.html";
					}
					break;
				}
				case State.COMPLETED: {
					htmltext = "35437-07a.html";
					break;
				}
			}
		}
		return htmltext;
	}
}
