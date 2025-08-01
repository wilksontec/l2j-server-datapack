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
package com.l2jserver.datapack.quests.Q00212_TrialOfDuty;

import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.enums.audio.Sound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.variables.PlayerVariables;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Util;

/**
 * Trial of Duty (212)
 * @author jurchiks
 */
public final class Q00212_TrialOfDuty extends Quest {
	// NPCs
	private static final int HANNAVALT = 30109;
	private static final int DUSTIN = 30116;
	private static final int SIR_COLLIN_WINDAWOOD = 30311;
	private static final int SIR_ARON_TANFORD = 30653;
	private static final int SIR_KIEL_NIGHTHAWK = 30654;
	private static final int ISAEL_SILVERSHADOW = 30655;
	private static final int SPIRIT_OF_SIR_TALIANUS = 30656;
	// Items
	private static final int LETTER_OF_DUSTIN = 2634;
	private static final int KNIGHTS_TEAR = 2635;
	private static final int MIRROR_OF_ORPIC = 2636;
	private static final int TEAR_OF_CONFESSION = 2637;
	private static final int TALIANUSS_REPORT = 2639;
	private static final int TEAR_OF_LOYALTY = 2640;
	private static final int SAINTS_ASHES_URN = 2641;
	private static final int ATHEBALDTS_SKULL = 2643;
	private static final int ATHEBALDTS_RIBS = 2644;
	private static final int ATHEBALDTS_SHIN = 2645;
	private static final int LETTER_OF_WINDAWOOD = 2646;
	private static final int OLD_KNIGHTS_SWORD = 3027;
	private static final QuestItemChanceHolder REPORT_PIECE = new QuestItemChanceHolder(2638, 10L);
	private static final QuestItemChanceHolder MILITAS_ARTICLE = new QuestItemChanceHolder(2641, 20L);
	// Monsters
	private static final int HANGMAN_TREE = 20144;
	private static final int SKELETON_MARAUDER = 20190;
	private static final int SKELETON_RAIDER = 20191;
	private static final int STRAIN = 20200;
	private static final int GHOUL = 20201;
	private static final int BREKA_ORC_OVERLORD = 20270;
	private static final int LETO_LIZARDMAN = 20577;
	private static final int LETO_LIZARDMAN_ARCHER = 20578;
	private static final int LETO_LIZARDMAN_SOLDIER = 20579;
	private static final int LETO_LIZARDMAN_WARRIOR = 20580;
	private static final int LETO_LIZARDMAN_SHAMAN = 20581;
	private static final int LETO_LIZARDMAN_OVERLORD = 20582;
	private static final int SPIRIT_OF_SIR_HEROD = 27119;
	// Rewards
	private static final int MARK_OF_DUTY = 2633;
	private static final int DIMENSIONAL_DIAMOND = 7562;
	// Misc
	private static final int MIN_LEVEL = 35;
	
	public Q00212_TrialOfDuty() {
		super(212);
		bindStartNpc(HANNAVALT);
		bindTalk(HANNAVALT, DUSTIN, SIR_COLLIN_WINDAWOOD, SIR_ARON_TANFORD, SIR_KIEL_NIGHTHAWK, ISAEL_SILVERSHADOW, SPIRIT_OF_SIR_TALIANUS);
		bindKill(HANGMAN_TREE, SKELETON_MARAUDER, SKELETON_RAIDER, STRAIN, GHOUL, BREKA_ORC_OVERLORD, LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD, SPIRIT_OF_SIR_HEROD);
		registerQuestItems(LETTER_OF_DUSTIN, KNIGHTS_TEAR, MIRROR_OF_ORPIC, TEAR_OF_CONFESSION, REPORT_PIECE.getId(), TALIANUSS_REPORT, TEAR_OF_LOYALTY, MILITAS_ARTICLE.getId(), SAINTS_ASHES_URN, ATHEBALDTS_SKULL, ATHEBALDTS_RIBS, ATHEBALDTS_SHIN, LETTER_OF_WINDAWOOD, OLD_KNIGHTS_SWORD);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		final QuestState qs = getQuestState(player, false);
		String html = null;
		
		if (qs == null) {
			return html;
		}
		
		switch (event) {
			case "quest_accept": {
				if (qs.isCreated() && (player.getLevel() >= MIN_LEVEL) && player.isInCategory(CategoryType.KNIGHT_GROUP)) {
					qs.startQuest();
					qs.setMemoState(1);
					qs.set("flag", 0);
					
					if (rewardDimensionalDiamonds(player)) {
						html = "30109-04a.htm";
					} else {
						html = "30109-04.htm";
					}
				}
				break;
			}
			case "30116-02.html":
			case "30116-03.html":
			case "30116-04.html": {
				if (qs.isMemoState(10) && hasQuestItems(player, TEAR_OF_LOYALTY)) {
					html = event;
				}
				break;
			}
			case "30116-05.html": {
				if (qs.isMemoState(10) && hasQuestItems(player, TEAR_OF_LOYALTY)) {
					html = event;
					takeItems(player, TEAR_OF_LOYALTY, -1);
					qs.setMemoState(11);
					qs.setCond(14, true);
				}
				break;
			}
		}
		return html;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		final QuestState qs = getQuestState(killer, false);
		if ((qs == null) || !Util.checkIfInRange(1500, killer, npc, true)) {
			return;
		}
		
		switch (npc.getId()) {
			case SKELETON_MARAUDER, SKELETON_RAIDER -> {
				if (qs.isMemoState(2)) {
					final int flag = qs.getInt("flag");
					
					if (getRandom(100) < (flag * 10)) {
						addSpawn(SPIRIT_OF_SIR_HEROD, npc);
						qs.set("flag", 0);
					} else {
						qs.set("flag", flag + 1);
					}
				}
			}
			case SPIRIT_OF_SIR_HEROD -> {
				if (qs.isMemoState(2)) {
					final L2Weapon weapon = killer.getActiveWeaponItem();
					
					if ((weapon != null) && (weapon.getId() == OLD_KNIGHTS_SWORD)) {
						giveItems(killer, KNIGHTS_TEAR, 1);
						qs.setMemoState(3);
						qs.setCond(3, true);
					}
				}
			}
			case STRAIN, GHOUL -> {
				if (qs.isMemoState(5) && !hasQuestItems(killer, TALIANUSS_REPORT) && giveItemRandomly(qs.getPlayer(), npc, REPORT_PIECE, true)) {
					takeItem(killer, REPORT_PIECE);
					giveItems(killer, TALIANUSS_REPORT, 1);
					qs.setCond(6);
				}
			}
			case HANGMAN_TREE -> {
				if (qs.isMemoState(6)) {
					final int flag = qs.getInt("flag");
					
					if (getRandom(100) < ((flag - 3) * 33)) {
						addSpawn(SPIRIT_OF_SIR_TALIANUS, npc);
						qs.set("flag", 0);
						qs.setCond(8, true);
					} else {
						qs.set("flag", flag + 1);
					}
				}
			}
			case LETO_LIZARDMAN, LETO_LIZARDMAN_ARCHER, LETO_LIZARDMAN_SOLDIER, LETO_LIZARDMAN_WARRIOR, LETO_LIZARDMAN_SHAMAN, LETO_LIZARDMAN_OVERLORD -> {
				if (qs.isMemoState(9) && giveItemRandomly(qs.getPlayer(), npc, MILITAS_ARTICLE, true)) {
					qs.setCond(12);
				}
			}
			case BREKA_ORC_OVERLORD -> {
				if (qs.isMemoState(11)) {
					if (!hasQuestItems(killer, ATHEBALDTS_SKULL)) {
						giveItems(killer, ATHEBALDTS_SKULL, 1);
						playSound(killer, Sound.ITEMSOUND_QUEST_ITEMGET);
					} else if (!hasQuestItems(killer, ATHEBALDTS_RIBS)) {
						giveItems(killer, ATHEBALDTS_RIBS, 1);
						playSound(killer, Sound.ITEMSOUND_QUEST_ITEMGET);
					} else if (!hasQuestItems(killer, ATHEBALDTS_SHIN)) {
						giveItems(killer, ATHEBALDTS_SHIN, 1);
						qs.setCond(15, true);
					}
				}
			}
		}
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker) {
		final QuestState qs = getQuestState(talker, true);
		String html = getNoQuestMsg(talker);
		
		switch (npc.getId()) {
			case HANNAVALT: {
				if (qs.isCreated()) {
					if (!talker.isInCategory(CategoryType.KNIGHT_GROUP)) {
						html = "30109-02.html";
					} else if (talker.getLevel() < MIN_LEVEL) {
						html = "30109-01.html";
					} else {
						html = "30109-03.htm";
					}
				} else if (qs.isStarted()) {
					switch (qs.getMemoState()) {
						case 1: {
							html = "30109-04.htm";
							break;
						}
						case 14: {
							if (hasQuestItems(talker, LETTER_OF_DUSTIN)) {
								html = "30109-05.html";
								takeItems(talker, LETTER_OF_DUSTIN, -1);
								addExpAndSp(talker, 762576, 49458);
								giveAdena(talker, 138968, true);
								giveItems(talker, MARK_OF_DUTY, 1);
								qs.exitQuest(false, true);
								talker.sendPacket(new SocialAction(talker.getObjectId(), 3));
								rewardDimensionalDiamonds(talker);
							}
							break;
						}
					}
				} else {
					html = getAlreadyCompletedMsg(talker);
				}
				break;
			}
			case SIR_ARON_TANFORD: {
				switch (qs.getMemoState()) {
					case 1: {
						html = "30653-01.html";
						
						if (!hasQuestItems(talker, OLD_KNIGHTS_SWORD)) {
							giveItems(talker, OLD_KNIGHTS_SWORD, 1);
						}
						
						qs.setMemoState(2);
						qs.setCond(2, true);
						break;
					}
					case 2: {
						if (hasQuestItems(talker, OLD_KNIGHTS_SWORD)) {
							html = "30653-02.html";
						}
						break;
					}
					case 3: {
						if (hasQuestItems(talker, KNIGHTS_TEAR)) {
							html = "30653-03.html";
							takeItems(talker, -1, KNIGHTS_TEAR, OLD_KNIGHTS_SWORD);
							qs.setMemoState(4);
							qs.setCond(4, true);
						}
						break;
					}
					case 4: {
						html = "30653-04.html";
						break;
					}
				}
				break;
			}
			case SIR_KIEL_NIGHTHAWK: {
				switch (qs.getMemoState()) {
					case 4: {
						html = "30654-01.html";
						qs.setMemoState(5);
						qs.setCond(5, true);
						break;
					}
					case 5: {
						if (!hasQuestItems(talker, TALIANUSS_REPORT)) {
							html = "30654-02.html";
						} else {
							html = "30654-03.html";
							qs.setMemoState(6);
							qs.setCond(7, true);
							giveItems(talker, MIRROR_OF_ORPIC, 1);
						}
						break;
					}
					case 6: {
						if (hasQuestItems(talker, MIRROR_OF_ORPIC)) {
							html = "30654-04.html";
						}
						break;
					}
					case 7: {
						if (hasQuestItems(talker, TEAR_OF_CONFESSION)) {
							html = "30654-05.html";
							takeItems(talker, TEAR_OF_CONFESSION, -1);
							qs.setMemoState(8);
							qs.setCond(10, true);
						}
						break;
					}
					case 8: {
						html = "30654-06.html";
						break;
					}
				}
				break;
			}
			case SPIRIT_OF_SIR_TALIANUS: {
				if (qs.isMemoState(6) && hasQuestItems(talker, MIRROR_OF_ORPIC, TALIANUSS_REPORT)) {
					html = "30656-01.html";
					takeItems(talker, -1, MIRROR_OF_ORPIC, TALIANUSS_REPORT);
					giveItems(talker, TEAR_OF_CONFESSION, 1);
					qs.setMemoState(7);
					qs.setCond(9, true);
					npc.deleteMe();
				}
				break;
			}
			case ISAEL_SILVERSHADOW: {
				switch (qs.getMemoState()) {
					case 8: {
						if (talker.getLevel() < MIN_LEVEL) {
							html = "30655-01.html";
						} else {
							html = "30655-02.html";
							qs.setMemoState(9);
							qs.setCond(11, true);
						}
						break;
					}
					case 9: {
						if (!hasItem(talker, MILITAS_ARTICLE)) {
							html = "30655-03.html";
						} else {
							html = "30655-04.html";
							giveItems(talker, TEAR_OF_LOYALTY, 1);
							takeItem(talker, MILITAS_ARTICLE);
							qs.setMemoState(10);
							qs.setCond(13, true);
						}
						break;
					}
					case 10: {
						if (hasQuestItems(talker, TEAR_OF_LOYALTY)) {
							html = "30655-05.html";
						}
						break;
					}
				}
				break;
			}
			case DUSTIN: {
				switch (qs.getMemoState()) {
					case 10: {
						if (hasQuestItems(talker, TEAR_OF_LOYALTY)) {
							html = "30116-01.html";
						}
						break;
					}
					case 11: {
						if (!hasQuestItems(talker, ATHEBALDTS_SKULL, ATHEBALDTS_RIBS, ATHEBALDTS_SHIN)) {
							html = "30116-06.html";
						} else {
							html = "30116-07.html";
							takeItems(talker, -1, ATHEBALDTS_SKULL, ATHEBALDTS_RIBS, ATHEBALDTS_SHIN);
							giveItems(talker, SAINTS_ASHES_URN, 1);
							qs.setMemoState(12);
							qs.setCond(16, true);
						}
						break;
					}
					case 12: {
						if (hasQuestItems(talker, SAINTS_ASHES_URN)) {
							html = "30116-09.html";
						}
						break;
					}
					case 13: {
						if (hasQuestItems(talker, LETTER_OF_WINDAWOOD)) {
							html = "30116-08.html";
							takeItems(talker, LETTER_OF_WINDAWOOD, -1);
							giveItems(talker, LETTER_OF_DUSTIN, 1);
							qs.setMemoState(14);
							qs.setCond(18, true);
						}
						break;
					}
					case 14: {
						if (hasQuestItems(talker, LETTER_OF_DUSTIN)) {
							html = "30116-10.html";
						}
						break;
					}
				}
				break;
			}
			case SIR_COLLIN_WINDAWOOD: {
				switch (qs.getMemoState()) {
					case 12: {
						if (hasQuestItems(talker, SAINTS_ASHES_URN)) {
							html = "30311-01.html";
							takeItems(talker, SAINTS_ASHES_URN, -1);
							giveItems(talker, LETTER_OF_WINDAWOOD, 1);
							qs.setMemoState(13);
							qs.setCond(17, true);
						}
						break;
					}
					case 13: {
						if (hasQuestItems(talker, LETTER_OF_WINDAWOOD)) {
							html = "30311-02.html";
						}
						break;
					}
				}
				break;
			}
		}
		return html;
	}
	
	private static boolean rewardDimensionalDiamonds(L2PcInstance player) {
		final PlayerVariables vars = player.getVariables();
		
		if (vars.getInt("2ND_CLASS_DIAMOND_REWARD", 0) == 0) {
			if (player.getClassId() == ClassId.knight) {
				rewardItems(player, DIMENSIONAL_DIAMOND, 45);
			} else {
				rewardItems(player, DIMENSIONAL_DIAMOND, 61);
			}
			
			vars.set("2ND_CLASS_DIAMOND_REWARD", 1);
			return true;
		}
		return false;
	}
}
