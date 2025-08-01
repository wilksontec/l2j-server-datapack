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
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcFirstTalk;

public class ChatLink implements IBypassHandler {
	private static final String[] COMMANDS = {
		"Chat"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		int val = 0;
		try {
			val = Integer.parseInt(command.substring(5));
		} catch (Exception ioobe) {
			
		}
		
		final L2Npc npc = (L2Npc) target;
		if ((val == 0) && npc.hasListener(EventType.NPC_FIRST_TALK)) {
			EventDispatcher.getInstance().notifyEventAsync(new NpcFirstTalk(npc, activeChar), npc);
		} else {
			npc.showChatWindow(activeChar, val);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
