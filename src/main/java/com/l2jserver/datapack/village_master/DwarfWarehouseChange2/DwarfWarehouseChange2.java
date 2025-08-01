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
package com.l2jserver.datapack.village_master.DwarfWarehouseChange2;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfWarehouseChange2 extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		30511, // Gesto
		30676, // Croop
		30685, // Baxt
		30845, // Klump
		30894, // Natools
		31269, // Mona
		31314, // Donal
		31958, // Yasheni
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	private static final int MARK_OF_SEARCHER = 2809; // proof11z
	private static final int MARK_OF_GUILDSMAN = 3119; // proof11x
	private static final int MARK_OF_PROSPERITY = 3238; // proof11y
	// Class
	private static final int BOUNTY_HUNTER = 55;
	
	public DwarfWarehouseChange2() {
		bindStartNpc(NPCS);
		bindTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "30511-03.htm": // master_lv3_ware006fa
			case "30511-04.htm": // master_lv3_ware007fa
			case "30511-05.htm": // master_lv3_ware007fat
			{
				htmltext = event;
				break;
			}
			case "55": {
				htmltext = ClassChangeRequested(player, Integer.parseInt(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = "30511-08.htm"; // fnYouAreThirdClass
		} else if ((classId == BOUNTY_HUNTER) && (player.getClassId() == ClassId.scavenger)) {
			if (player.getLevel() < 40) {
				if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER)) {
					htmltext = "30511-09.htm"; // fnLowLevel11
				} else {
					htmltext = "30511-10.htm"; // fnLowLevelNoProof11
				}
			} else if (hasQuestItems(player, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER)) {
				takeItems(player, -1, MARK_OF_GUILDSMAN, MARK_OF_PROSPERITY, MARK_OF_SEARCHER);
				player.setClassId(BOUNTY_HUNTER);
				player.setBaseClass(BOUNTY_HUNTER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30511-11.htm"; // fnAfterClassChange11
			} else {
				htmltext = "30511-12.htm"; // fnNoProof11
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
			htmltext = "30511-01.htm"; // fnYouAreFourthClass
		} else if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.scavenger) || (classId == ClassId.bountyHunter)) {
				htmltext = "30511-02.htm"; // fnClassList1
			} else {
				htmltext = "30511-06.htm"; // fnYouAreFirstClass
			}
		} else {
			htmltext = "30511-07.htm"; // fnClassMismatch
		}
		return htmltext;
	}
}
