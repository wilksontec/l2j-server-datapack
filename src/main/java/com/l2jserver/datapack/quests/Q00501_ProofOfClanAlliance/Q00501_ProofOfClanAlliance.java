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
package com.l2jserver.datapack.quests.Q00501_ProofOfClanAlliance;

import java.util.Arrays;
import java.util.List;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestDroplist;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Proof of Clan Alliance (501)
 * @author Zoey76
 */
public final class Q00501_ProofOfClanAlliance extends Quest {
	// NPCs
	private static final int SIR_KRISTOF_RODEMAI = 30756;
	private static final int STATUE_OF_OFFERING = 30757;
	private static final int ATHREA = 30758;
	private static final int KALIS = 30759;
	// Monsters
	private static final int OEL_MAHUM_WITCH_DOCTOR = 20576;
	private static final int HARIT_LIZARDMAN_SHAMAN = 20644;
	private static final int VANOR_SILENOS_SHAMAN = 20685;
	private static final int BOX_OF_ATHREA_1 = 27173;
	private static final int BOX_OF_ATHREA_2 = 27174;
	private static final int BOX_OF_ATHREA_3 = 27175;
	private static final int BOX_OF_ATHREA_4 = 27176;
	private static final int BOX_OF_ATHREA_5 = 27177;
	// Items
	private static final int BLOOD_OF_EVA = 3835;
	private static final int ATHREAS_COIN = 3836;
	private static final int SYMBOL_OF_LOYALTY = 3837;
	private static final int ANTIDOTE_RECIPE_LIST = 3872;
	private static final int VOUCHER_OF_FAITH = 3873;
	private static final int ALLIANCE_MANIFESTO = 3874;
	private static final int POTION_OF_RECOVERY = 3889;
	private static final QuestItemChanceHolder HERB_OF_HARIT = new QuestItemChanceHolder(3832, 10.0);
	private static final QuestItemChanceHolder HERB_OF_VANOR = new QuestItemChanceHolder(3833, 10.0);
	private static final QuestItemChanceHolder HERB_OF_OEL_MAHUM = new QuestItemChanceHolder(3834, 10.0);
	// Droplist
	private static final QuestDroplist DROPLIST = QuestDroplist.builder()
		.addSingleDrop(OEL_MAHUM_WITCH_DOCTOR, HERB_OF_OEL_MAHUM)
		.addSingleDrop(HARIT_LIZARDMAN_SHAMAN, HERB_OF_HARIT)
		.addSingleDrop(VANOR_SILENOS_SHAMAN, HERB_OF_VANOR)
		.build();
	// Skills
	private static final SkillHolder POISON_OF_DEATH = new SkillHolder(4082);
	private static final SkillHolder DIE_YOU_FOOL = new SkillHolder(4083);
	// Locations
	// @formatter:off
	private static final List<Location> LOCS = Arrays.asList(
		new Location(102273, 103433, -3512),
		new Location(102190, 103379, -3524),
		new Location(102107, 103325, -3533),
		new Location(102024, 103271, -3500),
		new Location(102327, 103350, -3511),
		new Location(102244, 103296, -3518),
		new Location(102161, 103242, -3529),
		new Location(102078, 103188, -3500),
		new Location(102381, 103267, -3538),
		new Location(102298, 103213, -3532),
		new Location(102215, 103159, -3520),
		new Location(102132, 103105, -3513),
		new Location(102435, 103184, -3515),
		new Location(102352, 103130, -3522),
		new Location(102269, 103076, -3533),
		new Location(102186, 103022, -3541));
	// @formatter:on
	// Misc
	private static final int CLAN_MIN_LEVEL = 3;
	private static final int CLAN_MEMBER_MIN_LEVEL = 40;
	private static final int ADENA_TO_RESTART_GAME = 10000;
	
	public Q00501_ProofOfClanAlliance() {
		super(501);
		bindStartNpc(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING);
		bindTalk(SIR_KRISTOF_RODEMAI, STATUE_OF_OFFERING, ATHREA, KALIS);
		bindKill(OEL_MAHUM_WITCH_DOCTOR, HARIT_LIZARDMAN_SHAMAN, VANOR_SILENOS_SHAMAN, BOX_OF_ATHREA_1, BOX_OF_ATHREA_2, BOX_OF_ATHREA_3, BOX_OF_ATHREA_4, BOX_OF_ATHREA_5);
		registerQuestItems(ANTIDOTE_RECIPE_LIST, VOUCHER_OF_FAITH, HERB_OF_HARIT.getId(), HERB_OF_VANOR.getId(), HERB_OF_OEL_MAHUM.getId(), BLOOD_OF_EVA, ATHREAS_COIN, SYMBOL_OF_LOYALTY);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		if (qs == null) {
			return null;
		}
		
		String htmltext = null;
		switch (event) {
			case "30756-06.html":
			case "30756-08.html":
			case "30757-05.html":
			case "30758-02.html":
			case "30758-04.html":
			case "30759-02.html":
			case "30759-04.html": {
				htmltext = event;
				break;
			}
			case "30756-07.html": {
				if (qs.isCreated() && player.isClanLeader() && (player.getClan().getLevel() == CLAN_MIN_LEVEL)) {
					qs.startQuest();
					qs.setMemoState(1);
					htmltext = event;
				}
				break;
			}
			case "30757-04.html": {
				if (getRandom(10) > 5) {
					if (qs.getInt("flag") != 2501) {
						giveItems(player, SYMBOL_OF_LOYALTY, 1);
						qs.set("flag", 2501);
					}
					htmltext = event;
				} else {
					npc.setTarget(player);
					npc.doCast(DIE_YOU_FOOL);
					startQuestTimer("SYMBOL_OF_LOYALTY", 4000, npc, player);
					htmltext = "30757-03.html";
				}
				break;
			}
			case "30758-03.html": {
				final QuestState lqs = getLeaderQuestState(player, getName());
				if (lqs != null) {
					if (npc.getSummonedNpcCount() < 4) {
						lqs.setMemoState(4);
						lqs.set("flag", 0);
						npc.setScriptValue(0);
						for (Location loc : LOCS) {
							final L2Npc box = addSpawn(npc, getRandom(BOX_OF_ATHREA_1, BOX_OF_ATHREA_5), loc, false, 300000);
							box.disableCoreAI(true);
							box.setIsNoRndWalk(true);
						}
						htmltext = event;
					} else {
						htmltext = "30758-03a.html";
					}
				}
				break;
			}
			case "30758-07.html": {
				if (player.getAdena() >= ADENA_TO_RESTART_GAME) {
					if (npc.getSummonedNpcCount() < 4) {
						takeItems(player, Inventory.ADENA_ID, ADENA_TO_RESTART_GAME);
					}
					htmltext = event;
				} else {
					htmltext = "30758-06.html";
				}
				break;
			}
			case "30759-03.html": {
				if (qs.isMemoState(1)) {
					qs.setCond(2, true);
					qs.setMemoState(2);
					htmltext = event;
				}
				break;
			}
			case "30759-07.html": {
				if (qs.isMemoState(2) && (getQuestItemsCount(player, SYMBOL_OF_LOYALTY) >= 3)) {
					takeItems(player, SYMBOL_OF_LOYALTY, -1);
					giveItems(player, ANTIDOTE_RECIPE_LIST, 1);
					npc.setTarget(player);
					npc.doCast(POISON_OF_DEATH);
					qs.setCond(3, true);
					qs.setMemoState(3);
					htmltext = event;
				}
				break;
			}
			case "SYMBOL_OF_LOYALTY": {
				if (player.isDead() && (qs.getInt("flag") != 2501)) {
					giveItems(player, SYMBOL_OF_LOYALTY, 1);
					qs.set("flag", 2501);
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getRandomPartyMemberState(killer, -1, 3, npc);
		if (qs == null) {
			return;
		}
		
		final L2PcInstance player = qs.getPlayer();
		final QuestState lqs = getLeaderQuestState(player, getName());
		if (lqs != null) {
			switch (npc.getId()) {
				case OEL_MAHUM_WITCH_DOCTOR, HARIT_LIZARDMAN_SHAMAN, VANOR_SILENOS_SHAMAN -> {
					if ((lqs.getMemoState() >= 3) && (lqs.getMemoState() < 6)) {
						giveItemRandomly(player, npc, DROPLIST.get(npc), true);
					}
				}
				case BOX_OF_ATHREA_1, BOX_OF_ATHREA_2, BOX_OF_ATHREA_3, BOX_OF_ATHREA_4, BOX_OF_ATHREA_5 -> {
					final L2Character summoner = npc.getSummoner();
					if ((summoner != null) && summoner.isNpc() && lqs.isMemoState(4)) {
						final L2Npc arthea = (L2Npc) summoner;
						if ((lqs.getInt("flag") == 3) && arthea.isScriptValue(15)) {
							lqs.set("flag", lqs.getInt("flag") + 1);
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BINGO));
						} else if ((lqs.getInt("flag") == 2) && arthea.isScriptValue(14)) {
							lqs.set("flag", lqs.getInt("flag") + 1);
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BINGO));
						} else if ((lqs.getInt("flag") == 1) && arthea.isScriptValue(13)) {
							lqs.set("flag", lqs.getInt("flag") + 1);
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BINGO));
						} else if ((lqs.getInt("flag") == 0) && arthea.isScriptValue(12)) {
							lqs.set("flag", lqs.getInt("flag") + 1);
							npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BINGO));
						} else if (lqs.getInt("flag") < 4) {
							if (getRandom(4) == 0) {
								lqs.set("flag", lqs.getInt("flag") + 1);
								npc.broadcastPacket(new NpcSay(npc, Say2.NPC_ALL, NpcStringId.BINGO));
							}
						}
						arthea.setScriptValue(arthea.getScriptValue() + 1);
					}
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, true);
		final QuestState lqs = getLeaderQuestState(player, getName());
		String htmltext = getNoQuestMsg(player);
		
		switch (npc.getId()) {
			case SIR_KRISTOF_RODEMAI: {
				switch (qs.getState()) {
					case State.CREATED: {
						if (player.isClanLeader()) {
							final L2Clan clan = player.getClan();
							if (clan.getLevel() < CLAN_MIN_LEVEL) {
								htmltext = "30756-01.html";
							} else if (clan.getLevel() == CLAN_MIN_LEVEL) {
								if (hasQuestItems(player, ALLIANCE_MANIFESTO)) {
									htmltext = "30756-03.html";
								} else {
									htmltext = "30756-04.html";
								}
							} else {
								htmltext = "30756-02.html";
							}
						} else {
							htmltext = "30756-05.html";
						}
						break;
					}
					case State.STARTED: {
						if (qs.isMemoState(6) && hasQuestItems(player, VOUCHER_OF_FAITH)) {
							takeItems(player, VOUCHER_OF_FAITH, -1);
							giveItems(player, ALLIANCE_MANIFESTO, 1);
							addExpAndSp(player, 0, 120000);
							qs.exitQuest(false);
							htmltext = "30756-09.html";
						} else {
							htmltext = "30756-10.html";
						}
						break;
					}
				}
				break;
			}
			case STATUE_OF_OFFERING: {
				if ((lqs != null) && lqs.isMemoState(2)) {
					if (!player.isClanLeader()) {
						if (player.getLevel() >= CLAN_MEMBER_MIN_LEVEL) {
							htmltext = (qs.getInt("flag") != 2501) ? "30757-01.html" : "30757-01b.html";
						} else {
							htmltext = "30757-02.html";
						}
					} else {
						htmltext = "30757-01a.html";
					}
				} else {
					htmltext = "30757-06.html";
				}
				break;
			}
			case ATHREA: {
				if (lqs != null) {
					switch (lqs.getMemoState()) {
						case 3: {
							if (hasQuestItems(lqs.getPlayer(), ANTIDOTE_RECIPE_LIST) && !hasQuestItems(lqs.getPlayer(), BLOOD_OF_EVA)) {
								lqs.set("flag", 0);
								htmltext = "30758-01.html";
							}
							break;
						}
						case 4: {
							if (lqs.getInt("flag") < 4) {
								htmltext = "30758-05.html";
							} else {
								giveItems(player, BLOOD_OF_EVA, 1);
								lqs.setMemoState(5);
								htmltext = "30758-08.html";
							}
							break;
						}
						case 5: {
							htmltext = "30758-09.html";
							break;
						}
					}
				}
				break;
			}
			case KALIS: {
				if (qs.isMemoState(1) && !hasQuestItems(player, SYMBOL_OF_LOYALTY)) {
					htmltext = "30759-01.html";
				} else if (qs.isMemoState(2) && (getQuestItemsCount(player, SYMBOL_OF_LOYALTY) < 3)) {
					htmltext = "30759-05.html";
				} else if ((getQuestItemsCount(player, SYMBOL_OF_LOYALTY) >= 3) && !hasAbnormal(player)) {
					htmltext = "30759-06.html";
				} else if (qs.isMemoState(5) && hasQuestItems(player, BLOOD_OF_EVA) && hasQuestItems(player, HERB_OF_VANOR.getId()) && hasQuestItems(player, HERB_OF_HARIT.getId()) && hasQuestItems(player, HERB_OF_OEL_MAHUM.getId()) && hasAbnormal(player)) {
					giveItems(player, VOUCHER_OF_FAITH, 1);
					giveItems(player, POTION_OF_RECOVERY, 1);
					takeItems(player, BLOOD_OF_EVA, -1);
					takeItems(player, ANTIDOTE_RECIPE_LIST, -1);
					takeItems(player, HERB_OF_OEL_MAHUM.getId(), -1);
					takeItems(player, HERB_OF_HARIT.getId(), -1);
					takeItems(player, HERB_OF_VANOR.getId(), -1);
					qs.setCond(4, true);
					qs.setMemoState(6);
					htmltext = "30759-08.html";
				} else if ((qs.isMemoState(3) || qs.isMemoState(4) || qs.isMemoState(5)) && !hasAbnormal(player)) {
					takeItems(player, ANTIDOTE_RECIPE_LIST, -1);
					qs.setMemoState(1);
					htmltext = "30759-09.html";
				} else if ((qs.getMemoState() < 6) && (getQuestItemsCount(player, SYMBOL_OF_LOYALTY) >= 3) && !hasAtLeastOneQuestItem(player, BLOOD_OF_EVA, HERB_OF_VANOR.getId(), HERB_OF_HARIT.getId(), HERB_OF_OEL_MAHUM.getId()) && hasAbnormal(player)) {
					htmltext = "30759-10.html";
				} else if (qs.isMemoState(6)) {
					htmltext = "30759-11.html";
				} else if ((lqs != null) && !player.isClanLeader()) {
					htmltext = "30759-12.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	/**
	 * Verifies if the player has the poison.
	 * @param player the player to check
	 * @return {@code true} if the player has {@link AbnormalType#FATAL_POISON} abnormal
	 */
	private static boolean hasAbnormal(L2PcInstance player) {
		return player.getEffectList().getBuffInfoByAbnormalType(AbnormalType.FATAL_POISON) != null;
	}
	
	/**
	 * Gets the clan leader's quest state.
	 * @param player the player
	 * @param quest the quest name
	 * @return the clan leader's quest state
	 */
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
