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
package com.l2jserver.datapack.ai.npc.SymbolMaker;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.HennaEquipList;
import com.l2jserver.gameserver.network.serverpackets.HennaRemoveList;

/**
 * Symbol Maker AI.
 * @author Adry_85
 */
public final class SymbolMaker extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		31046, // Marsden
		31047, // Kell
		31048, // McDermott
		31049, // Pepper
		31050, // Thora
		31051, // Keach
		31052, // Heid
		31053, // Kidder
		31264, // Olsun
		31308, // Achim
		31953, // Rankar
	};
	
	public SymbolMaker() {
		bindFirstTalk(NPCS);
		bindStartNpc(NPCS);
		bindTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "symbol_maker.htm":
			case "symbol_maker-1.htm":
			case "symbol_maker-2.htm":
			case "symbol_maker-3.htm": {
				htmltext = event;
				break;
			}
			case "Draw": {
				player.sendPacket(new HennaEquipList(player));
				break;
			}
			case "Remove": {
				player.sendPacket(new HennaRemoveList(player));
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "symbol_maker.htm";
	}
}