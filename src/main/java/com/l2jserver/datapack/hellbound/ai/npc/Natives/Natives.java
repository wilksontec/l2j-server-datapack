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
package com.l2jserver.datapack.hellbound.ai.npc.Natives;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Natives AI.
 * @author DS, GKR
 */
public final class Natives extends AbstractNpcAI {
	// NPCs
	private static final int NATIVE = 32362;
	private static final int INSURGENT = 32363;
	private static final int TRAITOR = 32364;
	private static final int INCASTLE = 32357;
	// Items
	private static final int MARK_OF_BETRAYAL = 9676; // Mark of Betrayal
	private static final int BADGES = 9674; // Darion's Badge
	// Misc
	private static final int[] DOORS = {
		19250003,
		19250004,
	};
	
	public Natives() {
		bindFirstTalk(NATIVE);
		bindFirstTalk(INSURGENT);
		bindFirstTalk(INCASTLE);
		bindStartNpc(TRAITOR);
		bindStartNpc(INCASTLE);
		bindTalk(TRAITOR);
		bindTalk(INCASTLE);
		bindSpawn(NATIVE);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		final int hellboundLevel = HellboundEngine.getInstance().getLevel();
		switch (npc.getId()) {
			case NATIVE: {
				htmltext = hellboundLevel > 5 ? "32362-01.htm" : "32362.htm";
				break;
			}
			case INSURGENT: {
				htmltext = hellboundLevel > 5 ? "32363-01.htm" : "32363.htm";
				break;
			}
			case INCASTLE: {
				if (hellboundLevel < 9) {
					htmltext = "32357-01a.htm";
				} else if (hellboundLevel == 9) {
					htmltext = npc.isBusy() ? "32357-02.htm" : "32357-01.htm";
				} else {
					htmltext = "32357-01b.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		if (npc.getId() == TRAITOR) {
			if (event.equalsIgnoreCase("open_door")) {
				if (getQuestItemsCount(player, MARK_OF_BETRAYAL) >= 10) {
					takeItems(player, MARK_OF_BETRAYAL, 10);
					broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.ALRIGHT_NOW_LEODAS_IS_YOURS);
					HellboundEngine.getInstance().updateTrust(-50, true);
					
					for (int doorId : DOORS) {
						L2DoorInstance door = DoorData.getInstance().getDoor(doorId);
						if (door != null) {
							door.openMe();
						}
					}
					
					cancelQuestTimers("close_doors");
					startQuestTimer("close_doors", 1800000, npc, player); // 30 min
				} else if (hasQuestItems(player, MARK_OF_BETRAYAL)) {
					htmltext = "32364-01.htm";
				} else {
					htmltext = "32364-02.htm";
				}
			} else if (event.equalsIgnoreCase("close_doors")) {
				for (int doorId : DOORS) {
					L2DoorInstance door = DoorData.getInstance().getDoor(doorId);
					if (door != null) {
						door.closeMe();
					}
				}
			}
		} else if ((npc.getId() == NATIVE) && event.equalsIgnoreCase("hungry_death")) {
			broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.HUN_HUNGRY);
			npc.doDie(null);
		} else if (npc.getId() == INCASTLE) {
			if (event.equalsIgnoreCase("FreeSlaves")) {
				if (getQuestItemsCount(player, BADGES) >= 5) {
					takeItems(player, BADGES, 5);
					npc.setBusy(true); // Prevent Native from take items more, than once
					HellboundEngine.getInstance().updateTrust(100, true);
					htmltext = "32357-02.htm";
					startQuestTimer("delete_me", 3000, npc, null);
				} else {
					htmltext = "32357-02a.htm";
				}
			} else if (event.equalsIgnoreCase("delete_me")) {
				npc.setBusy(false); // TODO: Does it really need?
				npc.deleteMe();
				npc.getSpawn().decreaseCount(npc);
			}
		}
		return htmltext;
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if ((npc.getId() == NATIVE) && (HellboundEngine.getInstance().getLevel() < 6)) {
			startQuestTimer("hungry_death", 600000, npc, null);
		}
	}
}