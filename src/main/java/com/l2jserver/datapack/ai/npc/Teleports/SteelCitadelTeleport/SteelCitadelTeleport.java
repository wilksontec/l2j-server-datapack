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
package com.l2jserver.datapack.ai.npc.Teleports.SteelCitadelTeleport;

import static com.l2jserver.gameserver.config.Configuration.grandBoss;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;

/**
 * Steel Citadel teleport AI.
 * @author GKR
 */
public final class SteelCitadelTeleport extends AbstractNpcAI {
	// NPCs
	private static final int BELETH = 29118;
	private static final int NAIA_CUBE = 32376;
	// Location
	private static final Location TELEPORT_CITADEL = new Location(16342, 209557, -9352);
	
	public SteelCitadelTeleport() {
		bindStartNpc(NAIA_CUBE);
		bindTalk(NAIA_CUBE);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		final int belethStatus = GrandBossManager.getInstance().getBossStatus(BELETH);
		if (belethStatus == 3) {
			return "32376-02.htm";
		}
		
		if (belethStatus > 0) {
			return "32376-03.htm";
		}
		
		final L2CommandChannel channel = player.getParty() == null ? null : player.getParty().getCommandChannel();
		if ((channel == null) || (channel.getLeader().getObjectId() != player.getObjectId()) || (channel.getMemberCount() < grandBoss().getBelethMinPlayers())) {
			return "32376-02a.htm";
		}
		
		final L2BossZone zone = (L2BossZone) ZoneManager.getInstance().getZoneById(12018);
		if (zone != null) {
			GrandBossManager.getInstance().setBossStatus(BELETH, 1);
			
			for (L2Party party : channel.getParties()) {
				if (party == null) {
					continue;
				}
				
				for (L2PcInstance pl : party.getMembers()) {
					if (pl.isInsideRadius(npc.getX(), npc.getY(), npc.getZ(), 3000, true, false)) {
						zone.allowPlayerEntry(pl, 30);
						pl.teleToLocation(TELEPORT_CITADEL, true);
					}
				}
			}
		}
		return null;
	}
}
