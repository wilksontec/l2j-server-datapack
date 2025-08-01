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
package com.l2jserver.datapack.ai.npc.Teleports.TeleportToUndergroundColiseum;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;

/**
 * Underground Coliseum teleport AI
 * @author malyelfik
 */
public final class TeleportToUndergroundColiseum extends AbstractNpcAI {
	// NPCs
	private static final int COLISEUM_HELPER = 32491;
	private static final int PADDIES = 32378;
	private static final int[] MANAGERS = {
		32377,
		32513,
		32514,
		32515,
		32516
	};
	
	// Locations
	private static final Location[] COLISEUM_LOCS = {
		new Location(-81896, -49589, -10352),
		new Location(-82271, -49196, -10352),
		new Location(-81886, -48784, -10352),
		new Location(-81490, -49167, -10352)
	};
	
	private static final Location[] RETURN_LOCS = {
		new Location(-59161, -56954, -2036),
		new Location(-59155, -56831, -2036),
		new Location(-59299, -56955, -2036),
		new Location(-59224, -56837, -2036),
		new Location(-59134, -56899, -2036)
	};
	
	private static final Location[][] MANAGERS_LOCS = {
		{
			new Location(-84451, -45452, -10728),
			new Location(-84580, -45587, -10728)
		},
		{
			new Location(-86154, -50429, -10728),
			new Location(-86118, -50624, -10728)
		},
		{
			new Location(-82009, -53652, -10728),
			new Location(-81802, -53665, -10728)
		},
		{
			new Location(-77603, -50673, -10728),
			new Location(-77586, -50503, -10728)
		},
		{
			new Location(-79186, -45644, -10728),
			new Location(-79309, -45561, -10728)
		}
	};
	
	public TeleportToUndergroundColiseum() {
		bindStartNpc(MANAGERS);
		bindStartNpc(COLISEUM_HELPER, PADDIES);
		bindFirstTalk(COLISEUM_HELPER);
		bindTalk(MANAGERS);
		bindTalk(COLISEUM_HELPER, PADDIES);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.endsWith(".htm")) {
			return event;
		} else if (event.equals("return")) {
			player.teleToLocation(RETURN_LOCS[getRandom(RETURN_LOCS.length)], false);
		} else if (Util.isDigit(event)) {
			int val = Integer.parseInt(event) - 1;
			player.teleToLocation(MANAGERS_LOCS[val][getRandom(MANAGERS_LOCS[val].length)], false);
		}
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (Util.contains(MANAGERS, npc.getId())) {
			player.teleToLocation(RETURN_LOCS[getRandom(RETURN_LOCS.length)], false);
		} else {
			player.teleToLocation(COLISEUM_LOCS[getRandom(COLISEUM_LOCS.length)], false);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "32491.htm";
	}
}