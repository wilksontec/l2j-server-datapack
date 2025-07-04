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
package com.l2jserver.datapack.ai.npc.Teleporter.TeleportCubeFrintezza;

import com.l2jserver.datapack.ai.npc.Teleporter.Teleporter;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.player.PlayerTeleportRequest;

/**
 * @author Charus
 * @version 2.6.3.0
 */
public class TeleportCubeFrintezza extends Teleporter {
	
	private static final int npcId = 29061;
	
	public TeleportCubeFrintezza() {
		super(npcId);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance talker) {
		showPage(talker, fnPath + "teleport_cube_frintessa001.htm");
		
		return null;
	}
	
	@Override
	public void onTeleportRequest(PlayerTeleportRequest event) {
		final var talker = event.player();
		
		final var i1 = -87760 + getRandom(50);
		final var i2 = -151833 + getRandom(50);
		
		talker.teleToLocation(i1, i2, -9152);
	}
}