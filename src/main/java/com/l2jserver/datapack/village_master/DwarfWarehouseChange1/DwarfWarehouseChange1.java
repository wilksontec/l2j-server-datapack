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
package com.l2jserver.datapack.village_master.DwarfWarehouseChange1;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

/**
 * Dwarf class transfer AI.
 * @author Adry_85
 */
public final class DwarfWarehouseChange1 extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		30498, // Moke
		30503, // Rikadio
		30594, // Ranspo
		32092, // Alder
	};
	
	// Items
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
	private static final int RING_OF_RAVEN = 1642;
	// Class
	private static final int SCAVENGER = 54;
	
	public DwarfWarehouseChange1() {
		bindStartNpc(NPCS);
		bindTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "30498-01.htm": // warehouse_chief_moke003f
			case "30498-02.htm": // warehouse_chief_moke006fa
			case "30498-03.htm": // warehouse_chief_moke007fa
			case "30498-04.htm": // warehouse_chief_moke006fb
			case "30503-01.htm": // warehouse_chief_rikadio003f
			case "30503-02.htm": // warehouse_chief_rikadio006fa
			case "30503-03.htm": // warehouse_chief_rikadio007fa
			case "30503-04.htm": // warehouse_chief_rikadio006fb
			case "30594-01.htm": // warehouse_chief_ranspo003f
			case "30594-02.htm": // warehouse_chief_ranspo006fa
			case "30594-03.htm": // warehouse_chief_ranspo007fa
			case "30594-04.htm": // warehouse_chief_ranspo006fb
			case "32092-01.htm": // warehouse_chief_older003f
			case "32092-02.htm": // warehouse_chief_older006fa
			case "32092-03.htm": // warehouse_chief_older007fa
			case "32092-04.htm": // warehouse_chief_older006fb
			{
				htmltext = event;
				break;
			}
			case "54": {
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
			htmltext = "30498-12.htm"; // fnYouAreFourthClass
		} else if ((classId == SCAVENGER) && (player.getClassId() == ClassId.dwarvenFighter)) {
			if (player.getLevel() < 20) {
				if (hasQuestItems(player, RING_OF_RAVEN)) {
					htmltext = npc.getId() + "-08.htm"; // fnLowLevel11
				} else {
					htmltext = npc.getId() + "-09.htm"; // fnLowLevelNoProof11
				}
			} else if (hasQuestItems(player, RING_OF_RAVEN)) {
				takeItems(player, RING_OF_RAVEN, -1);
				player.setClassId(SCAVENGER);
				player.setBaseClass(SCAVENGER);
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
		if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
			htmltext = npc.getId() + "-01.htm"; // fnClassList1
		} else {
			htmltext = npc.getId() + "-05.htm"; // fnClassMismatch
		}
		return htmltext;
	}
}
