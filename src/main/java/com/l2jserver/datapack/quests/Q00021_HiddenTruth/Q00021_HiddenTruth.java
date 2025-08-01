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
package com.l2jserver.datapack.quests.Q00021_HiddenTruth;

import com.l2jserver.datapack.quests.Q00022_TragedyInVonHellmannForest.Q00022_TragedyInVonHellmannForest;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.instancemanager.WalkingManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Hidden Truth (21)
 * @author xban1x
 */
public class Q00021_HiddenTruth extends Quest {
	// NPCs
	private static final int INNOCENTIN = 31328;
	private static final int AGRIPEL = 31348;
	private static final int BENEDICT = 31349;
	private static final int DOMINIC = 31350;
	private static final int MYSTERIOUS_WIZARD = 31522;
	private static final int TOMBSTONE = 31523;
	private static final int GHOST_OF_VON_HELLMAN = 31524;
	private static final int GHOST_OF_VON_HELLMANS_PAGE = 31525;
	private static final int BROKEN_BOOKSHELF = 31526;
	// Location
	private static final Location GHOST_LOC = new Location(51432, -54570, -3136, 0);
	private static final Location PAGE_LOC = new Location(51446, -54514, -3136, 0);
	// Items
	private static final int CROSS_OF_EINHASAD = 7140;
	private static final int CROSS_OF_EINHASAD2 = 7141;
	// Misc
	private static final int MIN_LVL = 63;
	private static final String PAGE_ROUTE_NAME = "rune_ghost1b";
	private static int PAGE_COUNT = 0;
	private static boolean GHOST_SPAWNED = false;
	private boolean PAGE_SPAWNED = false;
	private boolean MOVE_ENDED = false;
	
	public Q00021_HiddenTruth() {
		super(21);
		bindStartNpc(MYSTERIOUS_WIZARD);
		bindTalk(MYSTERIOUS_WIZARD, TOMBSTONE, GHOST_OF_VON_HELLMAN, GHOST_OF_VON_HELLMANS_PAGE, BROKEN_BOOKSHELF, AGRIPEL, BENEDICT, DOMINIC, INNOCENTIN);
		bindSeeCreature(GHOST_OF_VON_HELLMANS_PAGE);
		bindRouteFinished(GHOST_OF_VON_HELLMANS_PAGE);
		registerQuestItems(CROSS_OF_EINHASAD);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			switch (event) {
				case "31328-02.html":
				case "31328-03.html":
				case "31328-04.html":
				case "31522-01.htm":
				case "31522-04.html":
				case "31523-02.html":
				case "31524-02.html":
				case "31524-03.html":
				case "31524-04.html":
				case "31524-05.html":
				case "31526-01.html":
				case "31526-02.html":
				case "31526-04.html":
				case "31526-05.html":
				case "31526-06.html":
				case "31526-12.html":
				case "31526-13.html": {
					htmltext = event;
					break;
				}
				case "31328-05.html": {
					if (st.isCond(7)) {
						st.giveItems(CROSS_OF_EINHASAD2, 1);
						st.addExpAndSp(131228, 11978);
						st.exitQuest(false, true);
						htmltext = event;
					}
					break;
				}
				case "31522-02.htm": {
					if (player.getLevel() < MIN_LVL) {
						htmltext = "31522-03.htm";
					} else {
						st.startQuest();
						htmltext = event;
					}
					break;
				}
				case "31523-03.html": {
					if (GHOST_SPAWNED) {
						htmltext = "31523-04.html";
						st.playSound(Sound.SKILLSOUND_HORROR_2);
					} else {
						final L2Npc ghost = addSpawn(GHOST_OF_VON_HELLMAN, GHOST_LOC, false, 0);
						ghost.broadcastPacket(new NpcSay(ghost.getObjectId(), 0, ghost.getId(), NpcStringId.WHO_AWOKE_ME));
						GHOST_SPAWNED = true;
						st.startQuestTimer("DESPAWN_GHOST", 1000 * 300, ghost);
						st.setCond(2);
						st.playSound(Sound.SKILLSOUND_HORROR_2);
						htmltext = event;
					}
					break;
				}
				case "31524-06.html": {
					if (PAGE_COUNT < 5) {
						final L2Npc page = addSpawn(GHOST_OF_VON_HELLMANS_PAGE, PAGE_LOC, false, 0);
						page.setScriptValue(player.getObjectId());
						page.broadcastPacket(new NpcSay(page.getObjectId(), Say2.NPC_ALL, page.getId(), NpcStringId.MY_MASTER_HAS_INSTRUCTED_ME_TO_BE_YOUR_GUIDE_S1).addStringParameter(player.getName()));
						WalkingManager.getInstance().startMoving(page, PAGE_ROUTE_NAME);
						PAGE_COUNT++;
						st.setCond(3);
						htmltext = event;
					} else {
						htmltext = "31524-06a.html";
					}
					break;
				}
				case "31526-03.html": {
					st.playSound(Sound.ITEMSOUND_ARMOR_CLOTH);
					htmltext = event;
					break;
				}
				case "31526-07.html": {
					st.setCond(4);
					htmltext = event;
					break;
				}
				case "31526-08.html": {
					if (!st.isCond(5)) {
						st.playSound(Sound.AMDSOUND_ED_CHIMES);
						st.setCond(5);
						htmltext = event;
					} else {
						htmltext = "31526-09.html";
					}
					break;
				}
				case "31526-14.html": {
					st.giveItems(CROSS_OF_EINHASAD, 1);
					st.setCond(6);
					htmltext = event;
					break;
				}
				case "DESPAWN_GHOST": {
					GHOST_SPAWNED = false;
					npc.deleteMe();
					break;
				}
				case "DESPAWN": {
					PAGE_COUNT--;
					npc.deleteMe();
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (npc.getId()) {
			case MYSTERIOUS_WIZARD: {
				switch (st.getState()) {
					case State.CREATED: {
						htmltext = "31522-01.htm";
						break;
					}
					case State.STARTED: {
						htmltext = "31522-05.html";
						break;
					}
					case State.COMPLETED: {
						htmltext = getAlreadyCompletedMsg(player);
						break;
					}
				}
				break;
			}
			case TOMBSTONE: {
				htmltext = "31523-01.html";
				break;
			}
			case GHOST_OF_VON_HELLMAN: {
				switch (st.getCond()) {
					case 2: {
						htmltext = "31524-01.html";
						break;
					}
					case 3: {
						if (PAGE_SPAWNED) {
							htmltext = "31524-07b.html";
						} else {
							if (PAGE_COUNT < 5) {
								final L2Npc PAGE = addSpawn(GHOST_OF_VON_HELLMANS_PAGE, PAGE_LOC, true, 0);
								PAGE_COUNT++;
								PAGE_SPAWNED = true;
								PAGE.setScriptValue(player.getObjectId());
								WalkingManager.getInstance().startMoving(PAGE, PAGE_ROUTE_NAME);
								htmltext = "31524-07.html";
							} else {
								htmltext = "31524-07a.html";
							}
						}
						break;
					}
					case 4: {
						htmltext = "31524-07c.html";
						break;
					}
				}
				break;
			}
			case GHOST_OF_VON_HELLMANS_PAGE: {
				if (st.isCond(3)) {
					if (MOVE_ENDED) {
						htmltext = "31525-02.html";
						st.startQuestTimer("DESPAWN", 3000, npc);
					} else {
						htmltext = "31525-01.html";
					}
				}
				break;
			}
			case BROKEN_BOOKSHELF: {
				switch (st.getCond()) {
					case 3: {
						htmltext = "31526-01.html";
						break;
					}
					case 4: {
						st.setCond(5);
						st.playSound(Sound.AMDSOUND_ED_CHIMES);
						htmltext = "31526-10.html";
						break;
					}
					case 5: {
						htmltext = "31526-11.html";
						break;
					}
					case 6: {
						htmltext = "31526-15.html";
						break;
					}
				}
				break;
			}
			case AGRIPEL: {
				if (st.hasQuestItems(CROSS_OF_EINHASAD) && st.isCond(6)) {
					st.set("AGRIPEL", "1");
					if ((st.getInt("AGRIPEL") == 1) && (st.getInt("DOMINIC") == 1) && (st.getInt("BENEDICT") == 1)) {
						htmltext = "31348-03.html";
						st.setCond(7);
					} else if ((st.getInt("DOMINIC") == 1) || (st.getInt("BENEDICT") == 1)) {
						htmltext = "31348-02.html";
					} else {
						htmltext = "31348-01.html";
					}
				} else if (st.isCond(7)) {
					htmltext = "31348-03.html";
				}
				break;
			}
			
			case BENEDICT: {
				if (st.hasQuestItems(CROSS_OF_EINHASAD) && st.isCond(6)) {
					
					st.set("BENEDICT", "1");
					if ((st.getInt("AGRIPEL") == 1) && (st.getInt("DOMINIC") == 1) && (st.getInt("BENEDICT") == 1)) {
						htmltext = "31349-03.html";
						st.setCond(7);
					} else if ((st.getInt("AGRIPEL") == 1) || (st.getInt("DOMINIC") == 1)) {
						htmltext = "31349-02.html";
					} else {
						htmltext = "31349-01.html";
					}
				} else if (st.isCond(7)) {
					htmltext = "31349-03.html";
				}
				break;
			}
			case DOMINIC: {
				if (st.hasQuestItems(CROSS_OF_EINHASAD) && st.isCond(6)) {
					st.set("DOMINIC", "1");
					if ((st.getInt("AGRIPEL") == 1) && (st.getInt("DOMINIC") == 1) && (st.getInt("BENEDICT") == 1)) {
						htmltext = "31350-03.html";
						st.setCond(7);
					} else if ((st.getInt("AGRIPEL") == 1) || (st.getInt("BENEDICT") == 1)) {
						htmltext = "31350-02.html";
					} else {
						htmltext = "31350-01.html";
					}
				} else if (st.isCond(7)) {
					htmltext = "31350-03.html";
				}
				break;
			}
			case INNOCENTIN: {
				if (st.isCond(7) && st.hasQuestItems(CROSS_OF_EINHASAD)) {
					htmltext = "31328-01.html";
				} else if (st.isCompleted()) {
					st = player.getQuestState(Q00022_TragedyInVonHellmannForest.class.getSimpleName());
					if (st == null) {
						htmltext = "31328-06.html";
					}
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (creature.isPlayer()) {
			playSound((L2PcInstance) creature, Sound.HORROR_01);
		}
	}
	
	@Override
	public void onRouteFinished(L2Npc npc) {
		final QuestState st = L2World.getInstance().getPlayer(npc.getScriptValue()).getQuestState(getName());
		if (st != null) {
			st.startQuestTimer("DESPAWN", 15000, npc);
			MOVE_ENDED = true;
		}
	}
}
