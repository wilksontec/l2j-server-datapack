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
package com.l2jserver.datapack.quests.Q00103_SpiritOfCraftsman;

import com.l2jserver.datapack.ai.npc.Teleports.NewbieGuide.NewbieGuide;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.audio.Voice;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Util;

/**
 * Spirit of Craftsman (103)
 * @author janiko
 */
public final class Q00103_SpiritOfCraftsman extends Quest {
	// NPCs
	private static final int BLACKSMITH_KAROYD = 30307;
	private static final int CECON = 30132;
	private static final int HARNE = 30144;
	// Items
	private static final int KAROYDS_LETTER = 968;
	private static final int CECKTINONS_VOUCHER1 = 969;
	private static final int CECKTINONS_VOUCHER2 = 970;
	private static final int SOUL_CATCHER = 971;
	private static final int PRESERVE_OIL = 972;
	private static final int ZOMBIE_HEAD = 973;
	private static final int STEELBENDERS_HEAD = 974;
	private static final QuestItemChanceHolder BONE_FRAGMENT = new QuestItemChanceHolder(1107, 10L);
	// Monsters
	private static final int MARSH_ZOMBIE = 20015;
	private static final int DOOM_SOLDIER = 20455;
	private static final int SKELETON_HUNTER = 20517;
	private static final int SKELETON_HUNTER_ARCHER = 20518;
	// Rewards
	private static final int BLOODSABER = 975;
	private static final ItemHolder[] REWARDS = {
		new ItemHolder(1060, 100), // Lesser Healing Potion
		new ItemHolder(4412, 10), // Echo Crystal - Theme of Battle
		new ItemHolder(4413, 10), // Echo Crystal - Theme of Love
		new ItemHolder(4414, 10), // Echo Crystal - Theme of Solitude
		new ItemHolder(4415, 10), // Echo Crystal - Theme of Feast
		new ItemHolder(4416, 10), // Echo Crystal - Theme of Celebration
	};
	// Misc
	private static final int MIN_LVL = 10;
	
	private static final int GUIDE_MISSION = 41;
	
	private static final ItemHolder SOULSHOTS_NO_GRADE_FOR_ROOKIES = new ItemHolder(5789, 7000);
	private static final ItemHolder SOULSHOTS_NO_GRADE = new ItemHolder(1835, 1000);
	private static final ItemHolder SPIRITSHOTS_NO_GRADE = new ItemHolder(2509, 500);
	
	public Q00103_SpiritOfCraftsman() {
		super(103);
		bindStartNpc(BLACKSMITH_KAROYD);
		bindTalk(BLACKSMITH_KAROYD, CECON, HARNE);
		bindKill(MARSH_ZOMBIE, DOOM_SOLDIER, SKELETON_HUNTER, SKELETON_HUNTER_ARCHER);
		registerQuestItems(KAROYDS_LETTER, CECKTINONS_VOUCHER1, CECKTINONS_VOUCHER2, SOUL_CATCHER, PRESERVE_OIL, ZOMBIE_HEAD, STEELBENDERS_HEAD, BONE_FRAGMENT.getId());
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String htmltext = null;
		if (qs == null) {
			return htmltext;
		}
		switch (event) {
			case "30307-04.htm": {
				htmltext = event;
				break;
			}
			case "30307-05.htm": {
				if (qs.isCreated()) {
					qs.startQuest();
					giveItems(player, KAROYDS_LETTER, 1);
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String htmltext = getNoQuestMsg(talker);
		switch (npc.getId()) {
			case BLACKSMITH_KAROYD: {
				if (qs.isCreated()) {
					if (talker.getRace() != Race.DARK_ELF) {
						htmltext = "30307-01.htm";
					} else if (talker.getLevel() < MIN_LVL) {
						htmltext = "30307-02.htm";
					} else {
						htmltext = "30307-03.htm";
					}
				} else if (qs.isStarted()) {
					if (hasAtLeastOneQuestItem(talker, KAROYDS_LETTER, CECKTINONS_VOUCHER1, CECKTINONS_VOUCHER2)) {
						htmltext = "30307-06.html";
					} else if (hasQuestItems(talker, STEELBENDERS_HEAD)) {
						giveNewbieReward(talker);
						
						for (ItemHolder reward : REWARDS) {
							rewardItems(talker, reward);
						}
						
						// Newbie Guide
						final var newbieGuide = QuestManager.getInstance().getQuest(NewbieGuide.class.getSimpleName());
						if (newbieGuide != null) {
							final var newbieGuideQs = newbieGuide.getQuestState(talker, true);
							if (!newbieGuideQs.haveNRMemo(talker, GUIDE_MISSION)) {
								newbieGuideQs.setNRMemo(talker, GUIDE_MISSION);
								newbieGuideQs.setNRMemoState(talker, GUIDE_MISSION, 100000);
								
								showOnScreenMsg(talker, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
							} else {
								if (((newbieGuideQs.getNRMemoState(talker, GUIDE_MISSION) % 1000000) / 100000) != 1) {
									newbieGuideQs.setNRMemo(talker, GUIDE_MISSION);
									newbieGuideQs.setNRMemoState(talker, GUIDE_MISSION, newbieGuideQs.getNRMemoState(talker, GUIDE_MISSION) + 100000);
									
									showOnScreenMsg(talker, NpcStringId.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_N_GO_FIND_THE_NEWBIE_GUIDE, 2, 5000);
								}
							}
						}
						
						addExpAndSp(talker, 46663, 3999);
						giveAdena(talker, 19799, true);
						rewardItems(talker, BLOODSABER, 1);
						qs.exitQuest(false, true);
						talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
						htmltext = "30307-07.html";
					}
				} else if (qs.isCompleted()) {
					htmltext = getAlreadyCompletedMsg(talker);
					break;
				}
			}
			case CECON: {
				if (qs.isStarted()) {
					if (hasQuestItems(talker, KAROYDS_LETTER)) {
						qs.setCond(2, true);
						takeItems(talker, KAROYDS_LETTER, 1);
						giveItems(talker, CECKTINONS_VOUCHER1, 1);
						htmltext = "30132-01.html";
					} else if (hasAtLeastOneQuestItem(talker, CECKTINONS_VOUCHER1, CECKTINONS_VOUCHER2)) {
						htmltext = "30132-02.html";
					} else if (hasQuestItems(talker, SOUL_CATCHER)) {
						qs.setCond(6, true);
						takeItems(talker, SOUL_CATCHER, 1);
						giveItems(talker, PRESERVE_OIL, 1);
						htmltext = "30132-03.html";
					} else if (hasQuestItems(talker, PRESERVE_OIL) && !hasQuestItems(talker, ZOMBIE_HEAD, STEELBENDERS_HEAD)) {
						htmltext = "30132-04.html";
					} else if (hasQuestItems(talker, ZOMBIE_HEAD)) {
						qs.setCond(8, true);
						takeItems(talker, ZOMBIE_HEAD, 1);
						giveItems(talker, STEELBENDERS_HEAD, 1);
						htmltext = "30132-05.html";
					} else if (hasQuestItems(talker, STEELBENDERS_HEAD)) {
						htmltext = "30132-06.html";
					}
				}
				break;
			}
			case HARNE: {
				if (qs.isStarted()) {
					if (hasQuestItems(talker, CECKTINONS_VOUCHER1)) {
						qs.setCond(3, true);
						takeItems(talker, CECKTINONS_VOUCHER1, 1);
						giveItems(talker, CECKTINONS_VOUCHER2, 1);
						htmltext = "30144-01.html";
					} else if (hasQuestItems(talker, CECKTINONS_VOUCHER2)) {
						if (hasItemsAtLimit(talker, BONE_FRAGMENT)) {
							qs.setCond(5, true);
							takeItems(talker, CECKTINONS_VOUCHER2, 1);
							takeItems(talker, BONE_FRAGMENT.getId(), -1);
							giveItems(talker, SOUL_CATCHER, 1);
							htmltext = "30144-03.html";
						} else {
							htmltext = "30144-02.html";
						}
					} else if (hasQuestItems(talker, SOUL_CATCHER)) {
						htmltext = "30144-04.html";
					}
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
		
		switch (npc.getId()) {
			case MARSH_ZOMBIE -> {
				if (hasQuestItems(qs.getPlayer(), PRESERVE_OIL) && (getRandom(10) < 5) && Util.checkIfInRange(1500, npc, qs.getPlayer(), true)) {
					giveItems(qs.getPlayer(), ZOMBIE_HEAD, 1);
					takeItems(qs.getPlayer(), PRESERVE_OIL, -1);
					qs.setCond(7, true);
				}
			}
			case DOOM_SOLDIER, SKELETON_HUNTER, SKELETON_HUNTER_ARCHER -> {
				if (hasQuestItems(qs.getPlayer(), CECKTINONS_VOUCHER2) && giveItemRandomly(qs.getPlayer(), npc, BONE_FRAGMENT, true)) {
					qs.setCond(4);
				}
			}
		}
	}
	
	/**
	 * Give basic newbie reward.
	 * @param player the player to reward
	 */
	private static void giveNewbieReward(L2PcInstance player) {
		if (player.getLevel() < 25) {
			if (!player.isMageClass()) {
				giveItems(player, SOULSHOTS_NO_GRADE_FOR_ROOKIES);
				playSound(player, Voice.TUTORIAL_VOICE_026_1000);
			}
		}
		if (!player.isMageClass()) {
			giveItems(player, SOULSHOTS_NO_GRADE);
		} else {
			giveItems(player, SPIRITSHOTS_NO_GRADE);
		}
	}
}
