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
package com.l2jserver.datapack.village_master.DwarfBlacksmithChange1;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfBlacksmithChange1 extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		30499, // Tapoy
		30504, // Mendio
		30595, // Opix
		32093, // Bolin
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int FINAL_PASS_CERTIFICATE = 1635;
	// Class
	private static final int ARTISAN = 56;
	
	public DwarfBlacksmithChange1() {
		bindStartNpc(NPCS);
		bindTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "30499-01.htm": // head_blacksmith_tapoy003f
			case "30499-02.htm": // head_blacksmith_tapoy006fa
			case "30499-03.htm": // head_blacksmith_tapoy007fa
			case "30499-04.htm": // head_blacksmith_tapoy006fb
			case "30504-01.htm": // head_blacksmith_mendio003f
			case "30504-02.htm": // head_blacksmith_mendio006fa
			case "30504-03.htm": // head_blacksmith_mendio007fa
			case "30504-04.htm": // head_blacksmith_mendio006fb
			case "30595-01.htm": // head_blacksmith_opix003f
			case "30595-02.htm": // head_blacksmith_opix006fa
			case "30595-03.htm": // head_blacksmith_opix007fa
			case "30595-04.htm": // head_blacksmith_opix006fb
			case "32093-01.htm": // head_blacksmith_boillin003f
			case "32093-02.htm": // head_blacksmith_boillin006fa
			case "32093-03.htm": // head_blacksmith_boillin007fa
			case "32093-04.htm": // head_blacksmith_boillin006fb
			{
				htmltext = event;
				break;
			}
			case "56": {
				htmltext = ClassChangeRequested(player, npc, Integer.parseInt(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, L2Npc npc, int classId) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
			htmltext = npc.getId() + "-06.htm"; // fnYouAreSecondClass
		} else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
			htmltext = npc.getId() + "-07.htm"; // fnYouAreThirdClass
		} else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
			htmltext = "30499-12.htm"; // fnYouAreFourthClass
		} else if ((classId == ARTISAN) && (player.getClassId() == ClassId.dwarvenFighter)) {
			if (player.getLevel() < 20) {
				if (hasQuestItems(player, FINAL_PASS_CERTIFICATE)) {
					htmltext = npc.getId() + "-08.htm"; // fnLowLevel11
				} else {
					htmltext = npc.getId() + "-09.htm"; // fnLowLevelNoProof11
				}
			} else if (hasQuestItems(player, FINAL_PASS_CERTIFICATE)) {
				takeItems(player, FINAL_PASS_CERTIFICATE, -1);
				player.setClassId(ARTISAN);
				player.setBaseClass(ARTISAN);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE, 15);
				htmltext = npc.getId() + "-10.htm"; // fnAfterClassChange11
			} else {
				htmltext = npc.getId() + "-11.htm"; // fnNoProof11
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (player.isInCategory(CategoryType.WARSMITH_GROUP)) {
			htmltext = npc.getId() + "-01.htm"; // fnClassList1
		} else {
			htmltext = npc.getId() + "-05.htm"; // fnClassMismatch
		}
		return htmltext;
	}
}
