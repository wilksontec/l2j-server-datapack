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
package com.l2jserver.datapack.ai.npc.VarkaSilenosSupport;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.Util;

/**
 * Varka Silenos Support AI.<br>
 * Original Jython script by Emperorc and Kerberos_20.
 * @author Nyaran
 */
public final class VarkaSilenosSupport extends AbstractNpcAI {
	private static class BuffsData {
		private final int _skill;
		private final int _cost;
		
		public BuffsData(int skill, int cost) {
			_skill = skill;
			_cost = cost;
		}
		
		public Skill getSkill() {
			return SkillData.getInstance().getSkill(_skill, 1);
		}
		
		public int getCost() {
			return _cost;
		}
	}
	
	// NPCs
	private static final int ASHAS = 31377; // Hierarch
	private static final int NARAN = 31378; // Messenger
	private static final int UDAN = 31379; // Buffer
	private static final int DIYABU = 31380; // Grocer
	private static final int HAGOS = 31381; // Warehouse Keeper
	private static final int SHIKON = 31382; // Trader
	
	// Items
	private static final int SEED = 7187;
	private static final int[] VARKA_MARKS = {
		7221, // Mark of Varka's Alliance - Level 1
		7222, // Mark of Varka's Alliance - Level 2
		7223, // Mark of Varka's Alliance - Level 3
		7224, // Mark of Varka's Alliance - Level 4
		7225, // Mark of Varka's Alliance - Level 5
	};
	// Misc
	private static final Map<Integer, BuffsData> BUFF = new HashMap<>();
	static {
		BUFF.put(1, new BuffsData(4359, 2)); // Focus: Requires 2 Nepenthese Seeds
		BUFF.put(2, new BuffsData(4360, 2)); // Death Whisper: Requires 2 Nepenthese Seeds
		BUFF.put(3, new BuffsData(4345, 3)); // Might: Requires 3 Nepenthese Seeds
		BUFF.put(4, new BuffsData(4355, 3)); // Acumen: Requires 3 Nepenthese Seeds
		BUFF.put(5, new BuffsData(4352, 3)); // Berserker: Requires 3 Nepenthese Seeds
		BUFF.put(6, new BuffsData(4354, 3)); // Vampiric Rage: Requires 3 Nepenthese Seeds
		BUFF.put(7, new BuffsData(4356, 6)); // Empower: Requires 6 Nepenthese Seeds
		BUFF.put(8, new BuffsData(4357, 6)); // Haste: Requires 6 Nepenthese Seeds
	}
	
	public VarkaSilenosSupport() {
		bindFirstTalk(ASHAS, NARAN, UDAN, DIYABU, HAGOS, SHIKON);
		bindTalk(UDAN, HAGOS);
		bindStartNpc(HAGOS);
	}
	
	private int getAllianceLevel(L2PcInstance player) {
		for (int i = 0; i < VARKA_MARKS.length; i++) {
			if (hasQuestItems(player, VARKA_MARKS[i])) {
				return -(i + 1);
			}
		}
		return 0;
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (Util.isDigit(event) && BUFF.containsKey(Integer.parseInt(event))) {
			final BuffsData buff = BUFF.get(Integer.parseInt(event));
			if (getQuestItemsCount(player, SEED) >= buff.getCost()) {
				takeItems(player, SEED, buff.getCost());
				npc.setTarget(player);
				npc.doCast(buff.getSkill());
				npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());
			} else {
				htmltext = "31379-02.html";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final int allianceLevel = getAllianceLevel(player);
		switch (npc.getId()) {
			case ASHAS:
				htmltext = (allianceLevel < 0) ? "31377-friend.html" : "31377-no.html";
				break;
			case NARAN:
				htmltext = (allianceLevel < 0) ? "31378-friend.html" : "31378-no.html";
				break;
			case UDAN:
				htmltext = (allianceLevel < 0) ? (allianceLevel > -3) ? "31379-01.html" : "31379-04.html" : "31379-03.html";
				break;
			case DIYABU:
				htmltext = (allianceLevel < 0) ? "31380-friend.html" : "31380-no.html";
				break;
			case HAGOS:
				htmltext = (allianceLevel < 0) ? (allianceLevel == -1) ? "31381-01.html" : "31381-02.html" : "31381-no.html";
				break;
			case SHIKON:
				htmltext = switch (allianceLevel) {
					case -1, -2 -> "31382-01.html";
					case -3, -4 -> "31382-02.html";
					case -5 -> "31382-03.html";
					default -> "31382-no.html";
				};
				break;
		}
		return htmltext;
	}
}