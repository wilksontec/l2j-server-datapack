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
package com.l2jserver.datapack.handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Allows Game Masters to test System Messages.<br>
 * admin_msg display the raw message.<br>
 * admin_msgx is an extended version that allows to set parameters.
 * @author Zoey76
 */
public class AdminMessages implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_msg",
		"admin_msgx"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_msg ")) {
			try {
				activeChar.sendPacket(SystemMessage.getSystemMessage(Integer.parseInt(command.substring(10).trim())));
				return true;
			} catch (Exception e) {
				activeChar.sendMessage("Command format: //msg <SYSTEM_MSG_ID>");
			}
		} else if (command.startsWith("admin_msgx ")) {
			String[] tokens = command.split(" ");
			if ((tokens.length <= 2) || !Util.isDigit(tokens[1])) {
				activeChar.sendMessage("Command format: //msgx <SYSTEM_MSG_ID> [item:Id] [skill:Id] [npc:Id] [zone:x,y,x] [castle:Id] [str:'text']");
				return false;
			}
			
			SystemMessage sm = SystemMessage.getSystemMessage(Integer.parseInt(tokens[1]));
			String val;
			int lastPos = 0;
			for (int i = 2; i < tokens.length; i++) {
				try {
					val = tokens[i];
					if (val.startsWith("item:")) {
						sm.addItemName(Integer.parseInt(val.substring(5)));
					} else if (val.startsWith("skill:")) {
						sm.addSkillName(Integer.parseInt(val.substring(6)));
					} else if (val.startsWith("npc:")) {
						sm.addNpcName(Integer.parseInt(val.substring(4)));
					} else if (val.startsWith("zone:")) {
						int x = Integer.parseInt(val.substring(5, val.indexOf(",")));
						int y = Integer.parseInt(val.substring(val.indexOf(",") + 1, val.lastIndexOf(",")));
						int z = Integer.parseInt(val.substring(val.lastIndexOf(",") + 1));
						sm.addZoneName(x, y, z);
					} else if (val.startsWith("castle:")) {
						sm.addCastleId(Integer.parseInt(val.substring(7)));
					} else if (val.startsWith("str:")) {
						final int pos = command.indexOf("'", lastPos + 1);
						lastPos = command.indexOf("'", pos + 1);
						sm.addString(command.substring(pos + 1, lastPos));
					}
				} catch (Exception e) {
					activeChar.sendMessage("Exception: " + e.getMessage());
					continue;
				}
			}
			activeChar.sendPacket(sm);
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}