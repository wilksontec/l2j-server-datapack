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
package com.l2jserver.datapack.handlers.chathandlers;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.BlockList;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * Shout chat handler.
 * @author durgus
 */
public class ChatShout implements IChatHandler {
	private static final int[] COMMAND_IDS = {
		1
	};
	
	/**
	 * Handle chat type 'shout'
	 */
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text) {
		if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
			return;
		}
		
		final CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
		if (general().getGlobalChat().equalsIgnoreCase("on") || (general().getGlobalChat().equalsIgnoreCase("gm") && activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS))) {
			int region = MapRegionManager.getInstance().getMapRegionLocId(activeChar);
			for (L2PcInstance player : L2World.getInstance().getPlayers()) {
				if ((region == MapRegionManager.getInstance().getMapRegionLocId(player)) && !BlockList.isBlocked(player, activeChar) && (player.getInstanceId() == activeChar.getInstanceId())) {
					player.sendPacket(cs);
				}
			}
		} else if (general().getGlobalChat().equalsIgnoreCase("global")) {
			if (!activeChar.canOverrideCond(PcCondOverride.CHAT_CONDITIONS) && !activeChar.getFloodProtectors().getGlobalChat().tryPerformAction("global chat")) {
				activeChar.sendMessage("Do not spam shout channel.");
				return;
			}
			
			for (L2PcInstance player : L2World.getInstance().getPlayers()) {
				if (!BlockList.isBlocked(player, activeChar)) {
					player.sendPacket(cs);
				}
			}
		}
	}
	
	/**
	 * Returns the chat types registered to this handler.
	 */
	@Override
	public int[] getChatTypeList() {
		return COMMAND_IDS;
	}
}
