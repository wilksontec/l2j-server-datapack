/*
 * Copyright © 2004-2024 L2J DataPack
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
package com.l2jserver.datapack.handlers.custom;

import static com.l2jserver.gameserver.config.Configuration.customs;
import static com.l2jserver.gameserver.model.events.EventType.PLAYER_PVP_KILL;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.impl.character.player.PlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Custom Announce PK / PvP.
 * @author Zealar
 */
public class CustomAnnouncePkPvP {
	
	public CustomAnnouncePkPvP() {
		if (customs().announcePkPvP()) {
			Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), PLAYER_PVP_KILL, (PlayerPvPKill event) -> onPlayerPvPKill(event), this));
		}
	}
	
	private Object onPlayerPvPKill(PlayerPvPKill event) {
		L2PcInstance pk = event.player();
		if (pk.isGM()) {
			return null;
		}
		L2PcInstance player = event.target();
		
		String msg = customs().getAnnouncePvpMsg();
		if (player.getPvpFlag() == 0) {
			msg = customs().getAnnouncePkMsg();
		}
		msg = msg.replace("$killer", pk.getName()).replace("$target", player.getName());
		if (customs().announcePkPvPNormalMessage()) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1);
			sm.addString(msg);
			Broadcast.toAllOnlinePlayers(sm);
		} else {
			Broadcast.toAllOnlinePlayers(msg, false);
		}
		return null;
	}
}
