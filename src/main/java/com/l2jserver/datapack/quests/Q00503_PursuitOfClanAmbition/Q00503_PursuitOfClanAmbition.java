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
package com.l2jserver.datapack.quests.Q00503_PursuitOfClanAmbition;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.util.Util;

/**
 * Pursuit Of Clan Ambition (503)
 * @author ivantotov, Zoey76
 */
public final class Q00503_PursuitOfClanAmbition extends Quest {
	// NPCs
	private static final int HEAD_BLACKSMITH_KUSTO = 30512;
	private static final int MARTIEN = 30645;
	private static final int WITCH_ATHREA = 30758;
	private static final int WITCH_KALIS = 30759;
	private static final int SIR_GUSTAV_ATHEBALDT = 30760;
	private static final int CORPSE_OF_FRITZ = 30761;
	private static final int CORPSE_OF_LUTZ = 30762;
	private static final int CORPSE_OF_KURTZ = 30763;
	private static final int BALTHAZAR = 30764;
	private static final int IMPERIAL_COFFER = 30765;
	private static final int WITCH_CLEO = 30766;
	private static final int SIR_ERIC_RODEMAI = 30868;
	// Monsters
	private static final int DRAKE = 20137;
	private static final int DRAKE2 = 20285;
	private static final int THUNDER_WYRM = 20243;
	private static final int THUNDER_WYRM2 = 20282;
	private static final int GRAVE_GUARD = 20668;
	private static final int SPITEFUL_SOUL_LEADER = 20974;
	// Quest Monster
	private static final int GRAVE_KEYMASTER = 27179;
	private static final int IMPERIAL_GRAVEKEEPER = 27181;
	private static final int BLITZ_WYRM = 27178;
	// Items
	private static final int BROOCH_OF_THE_MAGPIE = 3843;
	private static final int GUSTAVS_1ST_LETTER = 3866;
	private static final int GUSTAVS_2ND_LETTER = 3867;
	private static final int GUSTAVS_3RD_LETTER = 3868;
	private static final int SCEPTER_OF_JUDGMENT = 3869;
	private static final int BLACK_ANVIL_COIN = 3871;
	private static final int RECIPE_SPITEFUL_SOUL_ENERGY = 14854;
	private static final int SPITEFUL_SOUL_VENGEANCE = 14856;
	private static final QuestItemChanceHolder MIST_DRAKES_EGG = new QuestItemChanceHolder(3839, 10.0, 10);
	private static final QuestItemChanceHolder BLITZ_WYRM_EGG = new QuestItemChanceHolder(3840, 10L);
	private static final QuestItemChanceHolder DRAKES_EGG = new QuestItemChanceHolder(3841, 50.0, 10);
	private static final QuestItemChanceHolder THUNDER_WYRM_EGG = new QuestItemChanceHolder(3842, 50.0, 10);
	private static final QuestItemChanceHolder IMPERIAL_KEY = new QuestItemChanceHolder(3847, 6L);
	private static final QuestItemChanceHolder SPITEFUL_SOUL_ENERGY = new QuestItemChanceHolder(14855, 10L);
	// Reward
	private static final int SEAL_OF_ASPIRATION = 3870;
	
	public Q00503_PursuitOfClanAmbition() {
		super(503);
		bindStartNpc(SIR_GUSTAV_ATHEBALDT);
		bindTalk(SIR_GUSTAV_ATHEBALDT, HEAD_BLACKSMITH_KUSTO, MARTIEN, WITCH_ATHREA, WITCH_KALIS, CORPSE_OF_FRITZ, CORPSE_OF_LUTZ, CORPSE_OF_KURTZ, BALTHAZAR, IMPERIAL_COFFER, WITCH_CLEO, SIR_ERIC_RODEMAI);
		bindKill(DRAKE, DRAKE2, THUNDER_WYRM, THUNDER_WYRM2, GRAVE_GUARD, SPITEFUL_SOUL_LEADER, GRAVE_KEYMASTER, BLITZ_WYRM, IMPERIAL_GRAVEKEEPER);
		bindSpawn(WITCH_ATHREA, WITCH_KALIS, IMPERIAL_COFFER, BLITZ_WYRM);
		registerQuestItems(MIST_DRAKES_EGG.getId(), BLITZ_WYRM_EGG.getId(), DRAKES_EGG.getId(), THUNDER_WYRM_EGG.getId(), BROOCH_OF_THE_MAGPIE, IMPERIAL_KEY.getId(), GUSTAVS_1ST_LETTER, GUSTAVS_2ND_LETTER, GUSTAVS_3RD_LETTER, SCEPTER_OF_JUDGMENT, BLACK_ANVIL_COIN, RECIPE_SPITEFUL_SOUL_ENERGY, SPITEFUL_SOUL_ENERGY
			.getId(), SPITEFUL_SOUL_VENGEANCE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.startsWith("DESPAWN")) {
			npc.deleteMe();
			return super.onEvent(event, npc, player);
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30760-08.html": {
				if (qs.isCreated()) {
					giveItems(player, GUSTAVS_1ST_LETTER, 1);
					qs.startQuest();
					qs.setMemoState(1000);
					htmltext = event;
				}
				break;
			}
			case "30760-12.html": {
				giveItems(player, GUSTAVS_2ND_LETTER, 1);
				qs.setMemoState(4000);
				qs.setCond(4);
				htmltext = event;
				break;
			}
			case "30760-16.html": {
				giveItems(player, GUSTAVS_3RD_LETTER, 1);
				qs.setMemoState(7000);
				qs.setCond(7);
				htmltext = event;
				break;
			}
			case "30760-20.html": {
				if (hasQuestItems(player, SCEPTER_OF_JUDGMENT)) {
					giveItems(player, SEAL_OF_ASPIRATION, 1);
					addExpAndSp(player, 0, 250000);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "30760-22.html": {
				qs.setMemoState(10000);
				qs.setCond(12);
				htmltext = event;
				break;
			}
			case "30760-23.html": {
				if (hasQuestItems(player, SCEPTER_OF_JUDGMENT)) {
					giveItems(player, SEAL_OF_ASPIRATION, 1);
					addExpAndSp(player, 0, 250000);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "30512-03.html": {
				if (hasQuestItems(player, BROOCH_OF_THE_MAGPIE)) {
					takeItems(player, BROOCH_OF_THE_MAGPIE, -1);
					giveItems(player, BLACK_ANVIL_COIN, 1);
				}
				htmltext = event;
				break;
			}
			case "30645-03.html": {
				takeItems(player, GUSTAVS_1ST_LETTER, -1);
				qs.setMemoState(2000);
				qs.setCond(2, true);
				htmltext = event;
				break;
			}
			case "30761-02.html": {
				if (qs.isMemoState(2000) || qs.isMemoState(2011) || qs.isMemoState(2010) || qs.isMemoState(2001)) {
					giveItems(player, BLITZ_WYRM_EGG, 3);
					qs.setMemoState(qs.getMemoState() + 100);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					startQuestTimer("DESPAWN", 10000, npc, player);
					htmltext = event;
				} else if (qs.isMemoState(2100) || qs.isMemoState(2111) || qs.isMemoState(2110) || qs.isMemoState(2101)) {
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					startQuestTimer("DESPAWN", 10000, npc, player);
					htmltext = "30761-03.html";
				}
				break;
			}
			case "30762-02.html": {
				if (qs.isMemoState(2000) || qs.isMemoState(2101) || qs.isMemoState(2001) || qs.isMemoState(2100)) {
					giveItems(player, BLITZ_WYRM_EGG, 3);
					giveItems(player, MIST_DRAKES_EGG, 4);
					qs.setMemoState(qs.getMemoState() + 10);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					startQuestTimer("DESPAWN", 10000, npc, player);
					htmltext = event;
				} else if (qs.isMemoState(2100) || qs.isMemoState(2111) || qs.isMemoState(2011) || qs.isMemoState(2110)) {
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					addAttackDesire(addSpawn(BLITZ_WYRM, npc, true, 0, false), player);
					startQuestTimer("DESPAWN", 10000, npc, player);
					htmltext = "30762-03.html";
				}
				break;
			}
			case "30763-02.html": {
				if (qs.isMemoState(2000) || qs.isMemoState(2110) || qs.isMemoState(2010) || qs.isMemoState(2100)) {
					giveItems(player, BROOCH_OF_THE_MAGPIE, 1);
					giveItems(player, MIST_DRAKES_EGG, 6);
					qs.setMemoState(qs.getMemoState() + 1);
					npc.deleteMe();
					htmltext = event;
				}
				break;
			}
			case "30764-03.html": {
				takeItems(player, GUSTAVS_2ND_LETTER, -1);
				qs.setMemoState(5000);
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30764-06.html": {
				takeItems(player, GUSTAVS_2ND_LETTER, -1);
				takeItems(player, BLACK_ANVIL_COIN, -1);
				giveItems(player, RECIPE_SPITEFUL_SOUL_ENERGY, 1);
				qs.setMemoState(5000);
				qs.setCond(5, true);
				htmltext = event;
				break;
			}
			case "30765-04.html": {
				takeItems(player, IMPERIAL_KEY.getId(), -1);
				giveItems(player, SCEPTER_OF_JUDGMENT, 1);
				qs.setMemoState(8700);
				htmltext = event;
				break;
			}
			case "30766-04.html": {
				qs.setMemoState(8100);
				qs.setCond(9, true);
				npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BLOOD_AND_HONOR));
				startQuestTimer("SPAWN_WITCH", 5000, npc, player);
				htmltext = event;
				break;
			}
			case "30766-08.html": {
				if (hasQuestItems(player, SCEPTER_OF_JUDGMENT)) {
					giveItems(player, SEAL_OF_ASPIRATION, 1);
					addExpAndSp(player, 0, 250000);
					qs.exitQuest(false, true);
					htmltext = event;
				}
				break;
			}
			case "30868-04.html": {
				takeItems(player, GUSTAVS_3RD_LETTER, -1);
				qs.setMemoState(8000);
				qs.setCond(8, true);
				htmltext = event;
				break;
			}
			case "30868-10.html": {
				qs.setMemoState(9000);
				qs.setCond(11, true);
				htmltext = event;
				break;
			}
			case "30645-06.html":
			case "30760-05.html":
			case "30760-06.html":
			case "30760-07.html":
			case "30760-21.html":
			case "30764-05.html":
			case "30765-02.html":
			case "30765-05a.html":
			case "30766-03.html":
			case "30868-03.html":
			case "30868-06a.html": {
				htmltext = event;
				break;
			}
			case "SPAWN_WITCH": {
				final L2Npc athrea = addSpawn(WITCH_ATHREA, 160688, 21296, -3714, 0, false, 0);
				athrea.setScriptValue(50301);
				final L2Npc kalis = addSpawn(WITCH_KALIS, 160690, 21176, -3712, 0, false, 0);
				kalis.setScriptValue(50302);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !qs.isStarted() || !Util.checkIfInRange(1500, npc, killer, true)) {
			return;
		}
		
		final L2Clan clan = killer.getClan();
		if (clan == null) {
			return;
		}
		
		final L2PcInstance leader = clan.getLeader().getPlayerInstance();
		if (!Util.checkIfInRange(1500, npc, leader, true)) {
			return;
		}
		
		final QuestState leaderQS = getQuestState(leader, false);
		if (leaderQS == null) {
			return;
		}
		
		switch (npc.getId()) {
			case DRAKE, DRAKE2 -> {
				if ((leaderQS.getMemoState() >= 2000) || (leaderQS.getMemoState() < 3000)) {
					giveItemRandomly(leader, npc, MIST_DRAKES_EGG, true);
					giveItemRandomly(leader, npc, DRAKES_EGG, true);
				}
			}
			case THUNDER_WYRM, THUNDER_WYRM2 -> {
				if ((leaderQS.getMemoState() >= 2000) || (leaderQS.getMemoState() < 3000)) {
					giveItemRandomly(leader, npc, THUNDER_WYRM_EGG, true);
				}
			}
			case GRAVE_GUARD -> {
				if ((leaderQS.getMemoState() < 8511) || (leaderQS.getMemoState() >= 8500)) {
					leaderQS.setMemoState(leaderQS.getMemoState() + 1);
					
					if ((leaderQS.getMemoState() >= 8505) && (getRandom(100) < 50)) {
						leaderQS.setMemoState(8500);
						addSpawn(GRAVE_KEYMASTER, npc, true, 0, false);
					} else if (leaderQS.getMemoState() >= 8510) {
						leaderQS.setMemoState(8500);
						addSpawn(GRAVE_KEYMASTER, npc, true, 0, false);
					}
				}
			}
			case SPITEFUL_SOUL_LEADER -> {
				if (leaderQS.getMemoState() == 5000) {
					final int rand = getRandom(100);
					if (rand < 10) {
						giveItemRandomly(leader, npc, SPITEFUL_SOUL_ENERGY, false);
					} else if (rand < 60) {
						giveItems(leader, SPITEFUL_SOUL_VENGEANCE, 1);
					}
				}
			}
			case BLITZ_WYRM -> {
				if ((leaderQS.getMemoState() >= 2000) || (leaderQS.getMemoState() < 3000)) {
					giveItemRandomly(leader, npc, BLITZ_WYRM_EGG, true);
				}
			}
			case GRAVE_KEYMASTER -> {
				if (leaderQS.getMemoState() >= 8500) {
					giveItemRandomly(leader, npc, IMPERIAL_KEY, true);
				}
			}
			case IMPERIAL_GRAVEKEEPER -> {
				if ((leaderQS.getMemoState() < 8511) || (leaderQS.getMemoState() >= 8500)) {
					addSpawn(IMPERIAL_COFFER, npc, true, 0, false);
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		final QuestState lqs = getLeaderQuestState(player, getName());
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated() || qs.isCompleted()) {
			if (npc.getId() == SIR_GUSTAV_ATHEBALDT) {
				if (lqs != null) {
					if (player.isClanLeader()) {
						L2Clan clan = player.getClan();
						if (clan != null) {
							if (clan.getLevel() < 4) {
								htmltext = "30760-01.html";
							} else if (clan.getLevel() >= 5) {
								htmltext = "30760-02.html";
							} else if ((clan.getLevel() == 4) && hasQuestItems(player, SEAL_OF_ASPIRATION)) {
								htmltext = "30760-03.html";
							} else if ((clan.getLevel() == 4) && !hasQuestItems(player, SEAL_OF_ASPIRATION)) {
								htmltext = "30760-04.html";
							}
						}
					} else {
						htmltext = "30760-04t.html";
					}
				}
			}
		} else if (qs.isStarted()) {
			switch (npc.getId()) {
				case SIR_GUSTAV_ATHEBALDT: {
					if (lqs != null) {
						if (qs.getMemoState() == 1000) {
							htmltext = "30760-09.html";
						} else if (qs.getMemoState() == 2000) {
							htmltext = "30760-10.html";
						} else if (qs.getMemoState() == 3000) {
							if (!player.isClanLeader()) {
								htmltext = "30760-11t.html";
							} else {
								htmltext = "30760-11.html";
							}
						} else if (qs.getMemoState() == 4000) {
							htmltext = "30760-13.html";
						} else if (qs.getMemoState() == 5000) {
							htmltext = "30760-14.html";
						} else if (qs.getMemoState() == 6000) {
							if (!player.isClanLeader()) {
								htmltext = "30760-15t.html";
							} else {
								htmltext = "30760-15.html";
							}
						} else if (qs.getMemoState() == 7000) {
							htmltext = "30760-17.html";
						} else if ((qs.getMemoState() >= 8000) && (qs.getMemoState() < 8700)) {
							htmltext = "30760-18.html";
						} else if ((qs.getMemoState() >= 8700) && (qs.getMemoState() < 10000) && player.isClanLeader()) {
							htmltext = "30760-19.html";
						} else if ((qs.getMemoState() == 9000) && !player.isClanLeader()) {
							htmltext = "30760-19t.html";
						} else if (qs.getMemoState() == 10000) {
							if (!player.isClanLeader()) {
								htmltext = "30760-24t.html";
							} else {
								htmltext = "30760-24.html";
							}
						}
					}
					break;
				}
				case HEAD_BLACKSMITH_KUSTO: {
					if ((lqs != null) && !player.isClanLeader()) {
						htmltext = "30512-01a.html";
					} else if (!hasAtLeastOneQuestItem(player, BROOCH_OF_THE_MAGPIE, BLACK_ANVIL_COIN)) {
						htmltext = "30512-01.html";
					} else if (hasQuestItems(player, BROOCH_OF_THE_MAGPIE)) {
						htmltext = "30512-02.html";
					} else if ((lqs != null) && hasQuestItems(player, BLACK_ANVIL_COIN) && !hasQuestItems(player, BROOCH_OF_THE_MAGPIE)) {
						htmltext = "30512-04.html";
					}
					break;
				}
				case MARTIEN: {
					if (lqs != null) {
						if ((qs.getMemoState() == 1000)) {
							if (!player.isClanLeader()) {
								htmltext = "30645-01.html";
							} else {
								htmltext = "30645-02.html";
							}
						} else if ((qs.getMemoState() < 3000) && (qs.getMemoState() >= 2000)) {
							if (!hasItemsAtLimit(player, MIST_DRAKES_EGG, BLITZ_WYRM_EGG, THUNDER_WYRM_EGG, DRAKES_EGG)) {
								htmltext = "30645-04.html";
							} else {
								takeItems(player, MIST_DRAKES_EGG.getId(), -1);
								takeItems(player, BLITZ_WYRM_EGG.getId(), -1);
								takeItems(player, DRAKES_EGG.getId(), -1);
								takeItems(player, THUNDER_WYRM_EGG.getId(), -1);
								qs.setMemoState(3000);
								qs.setCond(3, true);
								htmltext = "30645-05.html";
							}
						} else if ((qs.getMemoState() == 3000)) {
							htmltext = "30645-07.html";
						} else if ((qs.getMemoState() > 3000)) {
							htmltext = "30645-08.html";
						}
					}
					break;
				}
				case WITCH_ATHREA: {
					if (lqs != null) {
						htmltext = "30758-01.html";
					}
					break;
				}
				case WITCH_KALIS: {
					if (lqs != null) {
						htmltext = "30759-01.html";
					}
					break;
				}
				case CORPSE_OF_FRITZ: {
					if ((qs.getMemoState() < 3000) && (qs.getMemoState() >= 2000)) {
						htmltext = "30761-01.html";
					}
					break;
				}
				case CORPSE_OF_LUTZ: {
					if ((qs.getMemoState() < 3000) && (qs.getMemoState() >= 2000)) {
						htmltext = "30762-01.html";
					}
					break;
				}
				case CORPSE_OF_KURTZ: {
					if (((qs.getMemoState() < 3000) && (qs.getMemoState() == 2000)) || (qs.getMemoState() == 2110) || (qs.getMemoState() == 2010) || (qs.getMemoState() == 2100)) {
						htmltext = "30763-01.html";
					} else if ((qs.getMemoState() == 2001) || (qs.getMemoState() == 2111) || (qs.getMemoState() == 2011) || (qs.getMemoState() == 2101)) {
						htmltext = "30763-03.html";
					}
					break;
				}
				case BALTHAZAR: {
					if (lqs != null) {
						if ((qs.getMemoState() == 4000)) {
							if (!player.isClanLeader()) {
								htmltext = "30764-01.html";
							} else if (!hasQuestItems(player, BLACK_ANVIL_COIN) && player.isClanLeader()) {
								htmltext = "30764-02.html";
							} else if (hasQuestItems(player, BLACK_ANVIL_COIN)) {
								htmltext = "30764-04.html";
							}
						} else if ((qs.getMemoState() == 5000)) {
							if (!hasItemsAtLimit(player, SPITEFUL_SOUL_ENERGY)) {
								htmltext = "30764-07a.html";
							} else {
								takeItems(player, SPITEFUL_SOUL_ENERGY.getId(), -1);
								qs.setMemoState(6000);
								qs.setCond(6, true);
								htmltext = "30764-08a.html";
							}
						} else if ((qs.getMemoState() >= 6000)) {
							htmltext = "30764-09.html";
						}
					}
					break;
				}
				case IMPERIAL_COFFER: {
					if (lqs != null) {
						if ((qs.getMemoState() >= 8500) && (qs.getMemoState() < 8700)) {
							if (hasItemsAtLimit(player, IMPERIAL_KEY)) {
								if (!player.isClanLeader()) {
									htmltext = "30765-01.html";
								} else {
									htmltext = "30765-03.html";
								}
							}
						} else if (qs.getMemoState() >= 8700) {
							htmltext = "30765-05.html";
						}
					}
					break;
				}
				case WITCH_CLEO: {
					if (lqs != null) {
						if (!player.isClanLeader()) {
							htmltext = "30766-01.html";
						} else if (qs.getMemoState() == 8000) {
							htmltext = "30766-02.html";
						} else if (qs.getMemoState() == 8100) {
							htmltext = "30766-05.html";
						} else if ((qs.getMemoState() > 8100) && (qs.getMemoState() < 10000)) {
							htmltext = "30766-06.html";
						} else if ((qs.getMemoState() == 10000) && player.isClanLeader()) {
							htmltext = "30766-07.html";
						}
					}
					break;
				}
				case SIR_ERIC_RODEMAI: {
					if (lqs != null) {
						if (qs.getMemoState() == 7000) {
							if (!player.isClanLeader()) {
								htmltext = "30868-01.html";
							} else {
								htmltext = "30868-02.html";
							}
						} else if (qs.getMemoState() == 8000) {
							htmltext = "30868-05.html";
						} else if (qs.getMemoState() == 8100) {
							if (player.isClanLeader()) {
								qs.setMemoState(8500);
								qs.setCond(10, true);
								htmltext = "30868-06.html";
							} else {
								htmltext = "30868-07.html";
							}
						} else if ((qs.getMemoState() < 8511) && (qs.getMemoState() >= 8500)) {
							htmltext = "30868-08.html";
						} else if (qs.getMemoState() == 8700) {
							htmltext = "30868-09.html";
						} else if (qs.getMemoState() >= 9000) {
							htmltext = "30868-11.html";
						}
						break;
					}
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		switch (npc.getId()) {
			case WITCH_ATHREA: {
				if (npc.isScriptValue(50301)) {
					startQuestTimer("DESPAWN_WITCH_ATHREA", 5000, npc, null);
					npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.WAR_AND_DEATH));
				}
				break;
			}
			case WITCH_KALIS: {
				if (npc.isScriptValue(50302)) {
					startQuestTimer("DESPAWN_WITCH_KALIS", 5000, npc, null);
					npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.AMBITION_AND_POWER));
				}
				break;
			}
			case IMPERIAL_COFFER: {
				startQuestTimer("DESPAWN_IMPERIAL_COFFER", 180000, npc, null);
				npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.CURSE_OF_THE_GODS_ON_THE_ONE_THAT_DEFILES_THE_PROPERTY_OF_THE_EMPIRE));
				break;
			}
			case BLITZ_WYRM: {
				startQuestTimer("DESPAWN_BLITZ_WYRM", 180000, npc, null);
				break;
			}
		}
	}
	
	private static QuestState getLeaderQuestState(L2PcInstance player, String quest) {
		if (player.getClan() != null) {
			final L2PcInstance leader = player.getClan().getLeader().getPlayerInstance();
			if (leader != null) {
				return leader.getQuestState(quest);
			}
		}
		return null;
	}
}
