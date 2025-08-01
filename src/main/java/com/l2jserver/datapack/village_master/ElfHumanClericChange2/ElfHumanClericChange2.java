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
package com.l2jserver.datapack.village_master.ElfHumanClericChange2;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

/**
 * Elf Human class transfer AI.
 * @author Adry_85
 */
public final class ElfHumanClericChange2 extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		30120, // Maximilian
		30191, // Hollint
		30857, // Orven
		30905, // Squillari
		31279, // Gregory
		31328, // Innocentin
		31968, // Baryl
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_PILGRIM = 2721; // proof11x, proof12x, proof21x
	private static final int MARK_OF_TRUST = 2734; // proof11y, proof12y
	private static final int MARK_OF_HEALER = 2820; // proof11z, proof21z
	private static final int MARK_OF_REFORMER = 2821; // proof12z
	private static final int MARK_OF_LIFE = 3140; // proof21y
	// Classes
	private static final int BISHOP = 16;
	private static final int PROPHET = 17;
	private static final int ELDER = 30;
	
	public ElfHumanClericChange2() {
		bindStartNpc(NPCS);
		bindTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "30120-02.htm": // master_lv3_hec003h
			case "30120-03.htm": // master_lv3_hec006ha
			case "30120-04.htm": // master_lv3_hec007ha
			case "30120-05.htm": // master_lv3_hec007hat
			case "30120-06.htm": // master_lv3_hec006hb
			case "30120-07.htm": // master_lv3_hec007hb
			case "30120-08.htm": // master_lv3_hec007hbt
			case "30120-10.htm": // master_lv3_hec006ea
			case "30120-11.htm": // master_lv3_hec007ea
			case "30120-12.htm": // master_lv3_hec007eat
			{
				htmltext = event;
				break;
			}
			case "16":
			case "17":
			case "30": {
				htmltext = ClassChangeRequested(player, Integer.parseInt(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = "30120-15.htm"; // fnYouAreThirdClass
		} else if ((classId == BISHOP) && (player.getClassId() == ClassId.cleric)) {
			if (player.getLevel() < 40) {
				if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER)) {
					htmltext = "30120-16.htm"; // fnLowLevel11
				} else {
					htmltext = "30120-17.htm"; // fnLowLevelNoProof11
				}
			} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER)) {
				takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_HEALER);
				player.setClassId(BISHOP);
				player.setBaseClass(BISHOP);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30120-18.htm"; // fnAfterClassChange11
			} else {
				htmltext = "30120-19.htm"; // fnNoProof11
			}
		} else if ((classId == PROPHET) && (player.getClassId() == ClassId.cleric)) {
			if (player.getLevel() < 40) {
				if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER)) {
					htmltext = "30120-20.htm"; // fnLowLevel12
				} else {
					htmltext = "30120-21.htm"; // fnLowLevelNoProof12
				}
			} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER)) {
				takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_TRUST, MARK_OF_REFORMER);
				player.setClassId(PROPHET);
				player.setBaseClass(PROPHET);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30120-22.htm"; // fnAfterClassChange12
			} else {
				htmltext = "30120-23.htm"; // fnNoProof12
			}
		} else if ((classId == ELDER) && (player.getClassId() == ClassId.oracle)) {
			if (player.getLevel() < 40) {
				if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER)) {
					htmltext = "30120-24.htm"; // fnLowLevel21
				} else {
					htmltext = "30120-25.htm"; // fnLowLevelNoProof21
				}
			} else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER)) {
				takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_LIFE, MARK_OF_HEALER);
				player.setClassId(ELDER);
				player.setBaseClass(ELDER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30120-26.htm"; // fnAfterClassChange21
			} else {
				htmltext = "30120-27.htm"; // fnNoProof21
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.CLERIC_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS))) {
			htmltext = "30120-01.htm"; // fnYouAreFourthClass
		} else if (player.isInCategory(CategoryType.CLERIC_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS))) {
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.cleric) || (classId == ClassId.bishop) || (classId == ClassId.prophet)) {
				htmltext = "30120-02.htm"; // fnClassList1
			} else if ((classId == ClassId.oracle) || (classId == ClassId.elder)) {
				htmltext = "30120-09.htm"; // fnClassList2
			} else {
				htmltext = "30120-13.htm"; // fnYouAreFirstClass
			}
		} else {
			htmltext = "30120-14.htm"; // fnClassMismatch
		}
		return htmltext;
	}
}
