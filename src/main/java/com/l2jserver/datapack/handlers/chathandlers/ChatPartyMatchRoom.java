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
import com.l2jserver.gameserver.model.PartyMatchRoom;
import com.l2jserver.gameserver.model.PartyMatchRoomList;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * A chat handler
 * @author Gnacik
 */
public class ChatPartyMatchRoom implements IChatHandler {
	private static final int[] COMMAND_IDS = {
		14
	};
	
	@Override
	public void handleChat(int type, L2PcInstance activeChar, String target, String text) {
		if (activeChar.isInPartyMatchRoom()) {
			PartyMatchRoom _room = PartyMatchRoomList.getInstance().getPlayerRoom(activeChar);
			if (_room != null) {
				if (activeChar.isChatBanned() && general().getBanChatChannels().contains(type)) {
					activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
					return;
				}
				
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), type, activeChar.getName(), text);
				for (L2PcInstance _member : _room.getPartyMembers()) {
					_member.sendPacket(cs);
				}
			}
		}
	}
	
	@Override
	public int[] getChatTypeList() {
		return COMMAND_IDS;
	}
}