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
package com.l2jserver.datapack.gracia.ai.npc.Nottingale;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;
import com.l2jserver.gameserver.instancemanager.AirShipManager;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Nottingale AI
 * @author xban1x
 */
public final class Nottingale extends AbstractNpcAI {
	// NPC
	private static final int NOTTINGALE = 32627;
	// Misc
	private static final Map<Integer, RadarInfo> RADARS = new HashMap<>();
	static {
		RADARS.put(2, new RadarInfo(-184545, 243120, 1581, 2));
		RADARS.put(5, new RadarInfo(-192361, 254528, 3598, 1));
		RADARS.put(6, new RadarInfo(-174600, 219711, 4424, 1));
		RADARS.put(7, new RadarInfo(-181989, 208968, 4424, 1));
		RADARS.put(8, new RadarInfo(-252898, 235845, 5343, 1));
		RADARS.put(9, new RadarInfo(-212819, 209813, 4288, 1));
		RADARS.put(10, new RadarInfo(-246899, 251918, 4352, 1));
	}
	
	record RadarInfo(int x, int y, int z, int type) {
		
	}
	
	public Nottingale() {
		bindStartNpc(NOTTINGALE);
		bindTalk(NOTTINGALE);
		bindFirstTalk(NOTTINGALE);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "32627-02.html":
			case "32627-03.html":
			case "32627-04.html": {
				if (player.getClan() != null) {
					if (player.hasClanPrivilege(ClanPrivilege.CL_SUMMON_AIRSHIP) && AirShipManager.getInstance().hasAirShipLicense(player.getClanId()) && !AirShipManager.getInstance().hasAirShip(player.getClanId())) {
						htmltext = event;
					} else {
						final var st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
						if ((st != null) && st.isCompleted()) {
							htmltext = event;
						} else {
							final var radarInfo = RADARS.get(2);
							showRadar(player, radarInfo.x(), radarInfo.y(), radarInfo.z(), radarInfo.type());
							htmltext = "32627-01.html";
						}
					}
				} else {
					final var st = player.getQuestState(Q10273_GoodDayToFly.class.getSimpleName());
					if ((st != null) && st.isCompleted()) {
						htmltext = event;
					} else {
						final var radarInfo = RADARS.get(2);
						showRadar(player, radarInfo.x(), radarInfo.y(), radarInfo.z(), radarInfo.type());
						htmltext = "32627-01.html";
					}
				}
				break;
			}
			case "32627-05.html":
			case "32627-06.html":
			case "32627-07.html":
			case "32627-08.html":
			case "32627-09.html":
			case "32627-10.html": {
				final var radarInfo = RADARS.get(Integer.valueOf(event.substring(6, 8)));
				showRadar(player, radarInfo.x(), radarInfo.y(), radarInfo.z(), radarInfo.type());
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
}
