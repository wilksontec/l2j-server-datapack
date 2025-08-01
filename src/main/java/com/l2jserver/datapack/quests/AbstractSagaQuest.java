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
package com.l2jserver.datapack.quests;

import static com.l2jserver.gameserver.config.Configuration.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Abstract Saga quest.
 * @author Kerberos
 */
public abstract class AbstractSagaQuest extends Quest {
	protected int[] _npc;
	protected int[] _items;
	protected int[] _mob;
	protected int[] _classId;
	protected int[] _previousClass;
	protected Location[] _npcSpawnLocations;
	protected String[] _text;
	private static final Map<L2Npc, Integer> SPAWN_LIST = new HashMap<>();
	// @formatter:off
	private static final int[][] QUEST_CLASS =
	{
		{ 0x7f }, { 0x80, 0x81 }, { 0x82 }, { 0x05 }, { 0x14 }, { 0x15 },
		{ 0x02 }, { 0x03 }, { 0x2e }, { 0x30 }, { 0x33 }, { 0x34 }, { 0x08 },
		{ 0x17 }, { 0x24 }, { 0x09 }, { 0x18 }, { 0x25 }, { 0x10 }, { 0x11 },
		{ 0x1e }, { 0x0c }, { 0x1b }, { 0x28 }, { 0x0e }, { 0x1c }, { 0x29 },
		{ 0x0d }, { 0x06 }, { 0x22 }, { 0x21 }, { 0x2b }, { 0x37 }, { 0x39 }
	};
	// @formatter:on
	
	protected AbstractSagaQuest(int id) {
		super(id);
	}
	
	private QuestState findQuest(L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		if (st != null) {
			if (getId() == 68) {
				for (int q = 0; q < 2; q++) {
					if (player.getClassId().getId() == QUEST_CLASS[1][q]) {
						return st;
					}
				}
			} else if (player.getClassId().getId() == QUEST_CLASS[getId() - 67][0]) {
				return st;
			}
		}
		return null;
	}
	
	private QuestState findRightState(L2Npc npc) {
		L2PcInstance player = null;
		QuestState st = null;
		if (SPAWN_LIST.containsKey(npc)) {
			player = L2World.getInstance().getPlayer(SPAWN_LIST.get(npc));
			if (player != null) {
				st = player.getQuestState(getName());
			}
		}
		return st;
	}
	
	private int getClassId(L2PcInstance player) {
		if (player.getClassId().getId() == 0x81) {
			return _classId[1];
		}
		return _classId[0];
	}
	
	private int getPrevClass(L2PcInstance player) {
		if (player.getClassId().getId() == 0x81) {
			if (_previousClass.length == 1) {
				return -1;
			}
			return _previousClass[1];
		}
		return _previousClass[0];
	}
	
	private void giveHalishaMark(QuestState st2) {
		if (st2.getInt("spawned") == 0) {
			if (st2.getQuestItemsCount(_items[3]) >= 700) {
				st2.takeItems(_items[3], 20);
				int xx = st2.getPlayer().getX();
				int yy = st2.getPlayer().getY();
				int zz = st2.getPlayer().getZ();
				L2Npc Archon = st2.addSpawn(_mob[1], xx, yy, zz);
				addSpawn(st2, Archon);
				st2.set("spawned", "1");
				st2.startQuestTimer("Archon Hellisha has despawned", 600000, Archon);
				autoChat(Archon, _text[13].replace("PLAYERNAME", st2.getPlayer().getName()));
				((L2Attackable) Archon).addDamageHate(st2.getPlayer(), 0, 99999);
				Archon.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, st2.getPlayer(), null);
			} else {
				st2.giveItems(_items[3], getRandom(1, 4));
			}
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		QuestState st = getQuestState(player, false);
		String htmltext = null;
		if (st != null) {
			switch (event) {
				case "0-011.htm":
				case "0-012.htm":
				case "0-013.htm":
				case "0-014.htm":
				case "0-015.htm":
					htmltext = event;
					break;
				case "accept":
					st.startQuest();
					giveItems(player, _items[10], 1);
					htmltext = "0-03.htm";
					break;
				case "0-1":
					if (player.getLevel() < 76) {
						htmltext = "0-02.htm";
						if (st.isCreated()) {
							st.exitQuest(true);
						}
					} else {
						htmltext = "0-05.htm";
					}
					break;
				case "0-2":
					if (player.getLevel() < 76) {
						takeItems(player, _items[10], -1);
						st.setCond(20, true);
						htmltext = "0-08.htm";
					} else {
						takeItems(player, _items[10], -1);
						addExpAndSp(player, 2299404, 0);
						giveAdena(player, 5000000, true);
						giveItems(player, 6622, 1);
						int Class = getClassId(player);
						int prevClass = getPrevClass(player);
						player.setClassId(Class);
						if (!player.isSubClassActive() && (player.getBaseClass() == prevClass)) {
							player.setBaseClass(Class);
						}
						player.broadcastUserInfo();
						cast(npc, player, 4339, 1);
						st.exitQuest(false);
						htmltext = "0-07.htm";
					}
					break;
				case "1-3":
					st.setCond(3);
					htmltext = "1-05.htm";
					break;
				case "1-4":
					st.setCond(4);
					takeItems(player, _items[0], 1);
					if (_items[11] != 0) {
						takeItems(player, _items[11], 1);
					}
					giveItems(player, _items[1], 1);
					htmltext = "1-06.htm";
					break;
				case "2-1":
					st.setCond(2);
					htmltext = "2-05.htm";
					break;
				case "2-2":
					st.setCond(5);
					takeItems(player, _items[1], 1);
					giveItems(player, _items[4], 1);
					htmltext = "2-06.htm";
					break;
				case "3-5":
					htmltext = "3-07.htm";
					break;
				case "3-6":
					st.setCond(11);
					htmltext = "3-02.htm";
					break;
				case "3-7":
					st.setCond(12);
					htmltext = "3-03.htm";
					break;
				case "3-8":
					st.setCond(13);
					takeItems(player, _items[2], 1);
					giveItems(player, _items[7], 1);
					htmltext = "3-08.htm";
					break;
				case "4-1":
					htmltext = "4-010.htm";
					break;
				case "4-2":
					giveItems(player, _items[9], 1);
					st.setCond(18, true);
					htmltext = "4-011.htm";
					break;
				case "4-3":
					giveItems(player, _items[9], 1);
					st.setCond(18, true);
					autoChat(npc, _text[13].replace("PLAYERNAME", player.getName()));
					st.set("Quest0", "0");
					cancelQuestTimer("Mob_2 has despawned", npc, player);
					DeleteSpawn(st, npc);
					return null;
				case "5-1":
					st.setCond(6, true);
					takeItems(player, _items[4], 1);
					cast(npc, player, 4546, 1);
					htmltext = "5-02.htm";
					break;
				case "6-1":
					st.setCond(8, true);
					st.set("spawned", "0");
					takeItems(player, _items[5], 1);
					cast(npc, player, 4546, 1);
					htmltext = "6-03.htm";
					break;
				case "7-1":
					if (st.getInt("spawned") == 1) {
						htmltext = "7-03.htm";
					} else if (st.getInt("spawned") == 0) {
						L2Npc Mob_1 = addSpawn(_mob[0], _npcSpawnLocations[0], false, 0);
						st.set("spawned", "1");
						st.startQuestTimer("Mob_1 Timer 1", 500, Mob_1);
						st.startQuestTimer("Mob_1 has despawned", 300000, Mob_1);
						addSpawn(st, Mob_1);
						htmltext = "7-02.htm";
					} else {
						htmltext = "7-04.htm";
					}
					break;
				case "7-2":
					st.setCond(10, true);
					takeItems(player, _items[6], 1);
					cast(npc, player, 4546, 1);
					htmltext = "7-06.htm";
					break;
				case "8-1":
					st.setCond(14, true);
					takeItems(player, _items[7], 1);
					cast(npc, player, 4546, 1);
					htmltext = "8-02.htm";
					break;
				case "9-1":
					st.setCond(17, true);
					st.set("Quest0", "0");
					st.set("Tab", "0");
					takeItems(player, _items[8], 1);
					cast(npc, player, 4546, 1);
					htmltext = "9-03.htm";
					break;
				case "10-1":
					if (st.getInt("Quest0") == 0) {
						L2Npc Mob_3 = addSpawn(_mob[2], _npcSpawnLocations[1], false, 0);
						L2Npc Mob_2 = addSpawn(_npc[4], _npcSpawnLocations[2], false, 0);
						addSpawn(st, Mob_3);
						addSpawn(st, Mob_2);
						st.set("Mob_2", String.valueOf(Mob_2.getObjectId()));
						st.set("Quest0", "1");
						st.set("Quest1", "45");
						st.startRepeatingQuestTimer("Mob_3 Timer 1", 500, Mob_3);
						st.startQuestTimer("Mob_3 has despawned", 59000, Mob_3);
						st.startQuestTimer("Mob_2 Timer 1", 500, Mob_2);
						st.startQuestTimer("Mob_2 has despawned", 60000, Mob_2);
						htmltext = "10-02.htm";
					} else if (st.getInt("Quest1") == 45) {
						htmltext = "10-03.htm";
					} else {
						htmltext = "10-04.htm";
					}
					break;
				case "10-2":
					st.setCond(19, true);
					takeItems(player, _items[9], 1);
					cast(npc, player, 4546, 1);
					htmltext = "10-06.htm";
					break;
				case "11-9":
					st.setCond(15);
					htmltext = "11-03.htm";
					break;
				case "Mob_1 Timer 1":
					autoChat(npc, _text[0].replace("PLAYERNAME", player.getName()));
					return null;
				case "Mob_1 has despawned":
					autoChat(npc, _text[1].replace("PLAYERNAME", player.getName()));
					st.set("spawned", "0");
					DeleteSpawn(st, npc);
					return null;
				case "Archon Hellisha has despawned":
					autoChat(npc, _text[6].replace("PLAYERNAME", player.getName()));
					st.set("spawned", "0");
					DeleteSpawn(st, npc);
					return null;
				case "Mob_3 Timer 1":
					L2Npc Mob_2 = FindSpawn(player, (L2Npc) L2World.getInstance().findObject(st.getInt("Mob_2")));
					if (npc.getKnownList().knowsObject(Mob_2)) {
						((L2Attackable) npc).addDamageHate(Mob_2, 0, 99999);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, Mob_2, null);
						Mob_2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
						autoChat(npc, _text[14].replace("PLAYERNAME", player.getName()));
						cancelQuestTimer("Mob_3 Timer 1", npc, player);
					}
					return null;
				case "Mob_3 has despawned":
					autoChat(npc, _text[15].replace("PLAYERNAME", player.getName()));
					st.set("Quest0", "2");
					DeleteSpawn(st, npc);
					return null;
				case "Mob_2 Timer 1":
					autoChat(npc, _text[7].replace("PLAYERNAME", player.getName()));
					st.startQuestTimer("Mob_2 Timer 2", 1500, npc);
					if (st.getInt("Quest1") == 45) {
						st.set("Quest1", "0");
					}
					return null;
				case "Mob_2 Timer 2":
					autoChat(npc, _text[8].replace("PLAYERNAME", player.getName()));
					st.startQuestTimer("Mob_2 Timer 3", 10000, npc);
					return null;
				case "Mob_2 Timer 3":
					if (st.getInt("Quest0") == 0) {
						st.startQuestTimer("Mob_2 Timer 3", 13000, npc);
						if (getRandomBoolean()) {
							autoChat(npc, _text[9].replace("PLAYERNAME", player.getName()));
						} else {
							autoChat(npc, _text[10].replace("PLAYERNAME", player.getName()));
						}
					}
					return null;
				case "Mob_2 has despawned":
					st.set("Quest1", String.valueOf(st.getInt("Quest1") + 1));
					if ((st.getInt("Quest0") == 1) || (st.getInt("Quest0") == 2) || (st.getInt("Quest1") > 3)) {
						st.set("Quest0", "0");
						// TODO this IF will never be true
						if (st.getInt("Quest0") == 1) {
							autoChat(npc, _text[11].replace("PLAYERNAME", player.getName()));
						} else {
							autoChat(npc, _text[12].replace("PLAYERNAME", player.getName()));
						}
						DeleteSpawn(st, npc);
					} else {
						st.startQuestTimer("Mob_2 has despawned", 1000, npc);
					}
					return null;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon) {
		QuestState st2 = findRightState(npc);
		if (st2 != null) {
			int cond = st2.getCond();
			QuestState st = getQuestState(player, false);
			int npcId = npc.getId();
			if ((npcId == _mob[2]) && (st == st2) && (cond == 17)) {
				int quest0 = st.getInt("Quest0") + 1;
				if (quest0 == 1) {
					autoChat(npc, _text[16].replace("PLAYERNAME", player.getName()));
				}
				
				if (quest0 > 15) {
					quest0 = 1;
					autoChat(npc, _text[17].replace("PLAYERNAME", player.getName()));
					cancelQuestTimer("Mob_3 has despawned", npc, st2.getPlayer());
					st.set("Tab", "1");
					DeleteSpawn(st, npc);
				}
				
				st.set("Quest0", Integer.toString(quest0));
			} else if ((npcId == _mob[1]) && (cond == 15)) {
				if ((st != st2) || ((st == st2) && player.isInParty())) {
					autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
					cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
					st2.set("spawned", "0");
					DeleteSpawn(st2, npc);
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = "";
		QuestState st = getQuestState(player, false);
		int npcId = npc.getId();
		if (st != null) {
			if (npcId == _npc[4]) {
				int cond = st.getCond();
				if (cond == 17) {
					QuestState st2 = findRightState(npc);
					if (st2 != null) {
						player.setLastQuestNpcObject(npc.getObjectId());
						int tab = st.getInt("Tab");
						int quest0 = st.getInt("Quest0");
						
						if (st == st2) {
							if (tab == 1) {
								if (quest0 == 0) {
									htmltext = "4-04.htm";
								} else if (quest0 == 1) {
									htmltext = "4-06.htm";
								}
							} else if (quest0 == 0) {
								htmltext = "4-01.htm";
							} else if (quest0 == 1) {
								htmltext = "4-03.htm";
							}
						} else if (tab == 1) {
							if (quest0 == 0) {
								htmltext = "4-05.htm";
							} else if (quest0 == 1) {
								htmltext = "4-07.htm";
							}
						} else if (quest0 == 0) {
							htmltext = "4-02.htm";
						}
					}
				} else if (cond == 18) {
					htmltext = "4-08.htm";
				}
			}
		}
		if (htmltext == "") {
			npc.showChatWindow(player);
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {
		int npcId = npc.getId();
		QuestState st = getQuestState(player, false);
		for (int Archon_Minion = 21646; Archon_Minion < 21652; Archon_Minion++) {
			if (npcId == Archon_Minion) {
				L2Party party = player.getParty();
				if (party != null) {
					List<QuestState> partyQuestMembers = new ArrayList<>();
					for (L2PcInstance player1 : party.getMembers()) {
						QuestState st1 = findQuest(player1);
						if ((st1 != null) && player1.isInsideRadius(player, character().getPartyRange2(), false, false)) {
							if (st1.isCond(15)) {
								partyQuestMembers.add(st1);
							}
						}
					}
					if (partyQuestMembers.size() > 0) {
						QuestState st2 = partyQuestMembers.get(getRandom(partyQuestMembers.size()));
						giveHalishaMark(st2);
					}
				} else {
					QuestState st1 = findQuest(player);
					if (st1 != null) {
						if (st1.isCond(15)) {
							giveHalishaMark(st1);
						}
					}
				}
				return;
			}
		}
		
		int[] Archon_Hellisha_Norm = {
			18212,
			18214,
			18215,
			18216,
			18218
		};
		for (int element : Archon_Hellisha_Norm) {
			if (npcId == element) {
				QuestState st1 = findQuest(player);
				if (st1 != null) {
					if (st1.isCond(15)) {
						// This is just a guess....not really sure what it actually says, if anything
						autoChat(npc, _text[4].replace("PLAYERNAME", st1.getPlayer().getName()));
						st1.giveItems(_items[8], 1);
						st1.takeItems(_items[3], -1);
						st1.setCond(16, true);
					}
				}
				return;
			}
		}
		
		for (int Guardian_Angel = 27214; Guardian_Angel < 27217; Guardian_Angel++) {
			if (npcId == Guardian_Angel) {
				QuestState st1 = findQuest(player);
				if ((st1 != null) && st1.isCond(6)) {
					int kills = st1.getInt("kills");
					if (kills < 9) {
						st1.set("kills", Integer.toString(kills + 1));
					} else {
						st1.giveItems(_items[5], 1);
						st.setCond(7, true);
					}
				}
				return;
			}
		}
		if ((st != null) && (npcId != _mob[2])) {
			QuestState st2 = findRightState(npc);
			if (st2 != null) {
				int cond = st.getCond();
				if ((npcId == _mob[0]) && (cond == 8)) {
					if (!player.isInParty()) {
						if (st == st2) {
							autoChat(npc, _text[12].replace("PLAYERNAME", player.getName()));
							giveItems(player, _items[6], 1);
							st.setCond(9, true);
						}
					}
					cancelQuestTimer("Mob_1 has despawned", npc, st2.getPlayer());
					st2.set("spawned", "0");
					DeleteSpawn(st2, npc);
				} else if ((npcId == _mob[1]) && (cond == 15)) {
					if (!player.isInParty()) {
						if (st == st2) {
							autoChat(npc, _text[4].replace("PLAYERNAME", player.getName()));
							giveItems(player, _items[8], 1);
							takeItems(player, _items[3], -1);
							st.setCond(16, true);
						} else {
							autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
						}
					}
					cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
					st2.set("spawned", "0");
					DeleteSpawn(st2, npc);
				}
			}
		} else if (npcId == _mob[0]) {
			st = findRightState(npc);
			if (st != null) {
				cancelQuestTimer("Mob_1 has despawned", npc, st.getPlayer());
				st.set("spawned", "0");
				DeleteSpawn(st, npc);
			}
		} else if (npcId == _mob[1]) {
			st = findRightState(npc);
			if (st != null) {
				cancelQuestTimer("Archon Hellisha has despawned", npc, st.getPlayer());
				st.set("spawned", "0");
				DeleteSpawn(st, npc);
			}
		}
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance player, Skill skill, List<L2Object> targets, boolean isSummon) {
		if (SPAWN_LIST.containsKey(npc) && (SPAWN_LIST.get(npc) != player.getObjectId())) {
			L2PcInstance quest_player = (L2PcInstance) L2World.getInstance().findObject(SPAWN_LIST.get(npc));
			if (quest_player == null) {
				return;
			}
			
			for (L2Object obj : targets) {
				if ((obj == quest_player) || (obj == npc)) {
					QuestState st2 = findRightState(npc);
					if (st2 == null) {
						return;
					}
					autoChat(npc, _text[5].replace("PLAYERNAME", player.getName()));
					cancelQuestTimer("Archon Hellisha has despawned", npc, st2.getPlayer());
					st2.set("spawned", "0");
					DeleteSpawn(st2, npc);
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		int npcId = npc.getId();
		if ((npcId == _npc[0]) && st.isCompleted()) {
			htmltext = getAlreadyCompletedMsg(player);
		} else if (player.getClassId().getId() == getPrevClass(player)) {
			switch (st.getCond()) {
				case 0:
					if (npcId == _npc[0]) {
						htmltext = "0-01.htm";
					}
					break;
				case 1:
					if (npcId == _npc[0]) {
						htmltext = "0-04.htm";
					} else if (npcId == _npc[2]) {
						htmltext = "2-01.htm";
					}
					break;
				case 2:
					if (npcId == _npc[2]) {
						htmltext = "2-02.htm";
					} else if (npcId == _npc[1]) {
						htmltext = "1-01.htm";
					}
					break;
				case 3:
					if ((npcId == _npc[1]) && hasQuestItems(player, _items[0])) {
						if ((_items[11] == 0) || hasQuestItems(player, _items[11])) {
							htmltext = "1-03.htm";
						} else {
							htmltext = "1-02.htm";
						}
					}
					break;
				case 4:
					if (npcId == _npc[1]) {
						htmltext = "1-04.htm";
					} else if (npcId == _npc[2]) {
						htmltext = "2-03.htm";
					}
					break;
				case 5:
					if (npcId == _npc[2]) {
						htmltext = "2-04.htm";
					} else if (npcId == _npc[5]) {
						htmltext = "5-01.htm";
					}
					break;
				case 6:
					if (npcId == _npc[5]) {
						htmltext = "5-03.htm";
					} else if (npcId == _npc[6]) {
						htmltext = "6-01.htm";
					}
					break;
				case 7:
					if (npcId == _npc[6]) {
						htmltext = "6-02.htm";
					}
					break;
				case 8:
					if (npcId == _npc[6]) {
						htmltext = "6-04.htm";
					} else if (npcId == _npc[7]) {
						htmltext = "7-01.htm";
					}
					break;
				case 9:
					if (npcId == _npc[7]) {
						htmltext = "7-05.htm";
					}
					break;
				case 10:
					if (npcId == _npc[7]) {
						htmltext = "7-07.htm";
					} else if (npcId == _npc[3]) {
						htmltext = "3-01.htm";
					}
					break;
				case 11:
				case 12:
					if (npcId == _npc[3]) {
						if (hasQuestItems(player, _items[2])) {
							htmltext = "3-05.htm";
						} else {
							htmltext = "3-04.htm";
						}
					}
					break;
				case 13:
					if (npcId == _npc[3]) {
						htmltext = "3-06.htm";
					} else if (npcId == _npc[8]) {
						htmltext = "8-01.htm";
					}
					break;
				case 14:
					if (npcId == _npc[8]) {
						htmltext = "8-03.htm";
					} else if (npcId == _npc[11]) {
						htmltext = "11-01.htm";
					}
					break;
				case 15:
					if (npcId == _npc[11]) {
						htmltext = "11-02.htm";
					} else if (npcId == _npc[9]) {
						htmltext = "9-01.htm";
					}
					break;
				case 16:
					if (npcId == _npc[9]) {
						htmltext = "9-02.htm";
					}
					break;
				case 17:
					if (npcId == _npc[9]) {
						htmltext = "9-04.htm";
					} else if (npcId == _npc[10]) {
						htmltext = "10-01.htm";
					}
					break;
				case 18:
					if (npcId == _npc[10]) {
						htmltext = "10-05.htm";
					}
					break;
				case 19:
					if (npcId == _npc[10]) {
						htmltext = "10-07.htm";
					} else if (npcId == _npc[0]) {
						htmltext = "0-06.htm";
					}
					break;
				case 20:
					if (npcId == _npc[0]) {
						if (player.getLevel() >= 76) {
							htmltext = "0-09.htm";
							if ((getClassId(player) < 131) || (getClassId(player) > 135)) // in Kamael quests, npc wants to chat for a bit before changing class
							{
								st.exitQuest(false);
								addExpAndSp(player, 2299404, 0);
								giveAdena(player, 5000000, true);
								giveItems(player, 6622, 1); // XXX rewardItems?
								int classId = getClassId(player);
								int prevClass = getPrevClass(player);
								player.setClassId(classId);
								if (!player.isSubClassActive() && (player.getBaseClass() == prevClass)) {
									player.setBaseClass(classId);
								}
								player.broadcastUserInfo();
								cast(npc, player, 4339, 1);
							}
						} else {
							htmltext = "0-010.htm";
						}
					}
					break;
			}
		}
		return htmltext;
	}
	
	public void registerNPCs() {
		bindStartNpc(_npc[0]);
		bindAttack(_mob[2], _mob[1]);
		bindSkillSee(_mob[1]);
		bindFirstTalk(_npc[4]);
		bindTalk(_npc);
		bindKill(_mob);
		final int[] questItemIds = _items.clone();
		questItemIds[0] = 0;
		questItemIds[2] = 0; // remove Ice Crystal and Divine Stone of Wisdom
		registerQuestItems(questItemIds);
		for (int Archon_Minion = 21646; Archon_Minion < 21652; Archon_Minion++) {
			bindKill(Archon_Minion);
		}
		int[] Archon_Hellisha_Norm = {
			18212,
			18214,
			18215,
			18216,
			18218
		};
		bindKill(Archon_Hellisha_Norm);
		for (int Guardian_Angel = 27214; Guardian_Angel < 27217; Guardian_Angel++) {
			bindKill(Guardian_Angel);
		}
	}
	
	private static void addSpawn(QuestState st, L2Npc mob) {
		SPAWN_LIST.put(mob, st.getPlayer().getObjectId());
	}
	
	private static void autoChat(L2Npc npc, String text) {
		npc.broadcastPacket(new NpcSay(npc.getObjectId(), 0, npc.getId(), text));
	}
	
	private static void cast(L2Npc npc, L2Character target, int skillId, int level) {
		target.broadcastPacket(new MagicSkillUse(target, target, skillId, level, 6000, 1));
		target.broadcastPacket(new MagicSkillUse(npc, npc, skillId, level, 6000, 1));
	}
	
	private static void DeleteSpawn(QuestState st, L2Npc npc) {
		if (SPAWN_LIST.containsKey(npc)) {
			SPAWN_LIST.remove(npc);
			npc.deleteMe();
		}
	}
	
	private static L2Npc FindSpawn(L2PcInstance player, L2Npc npc) {
		if (SPAWN_LIST.containsKey(npc) && (SPAWN_LIST.get(npc) == player.getObjectId())) {
			return npc;
		}
		return null;
	}
}
