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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author poltomb
 */
public class AdminSummon implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminSummon.class);
	
	public static final String[] ADMIN_COMMANDS = {
		"admin_summon"
	};
	
	@Override
	public String[] getAdminCommandList() {
		
		return ADMIN_COMMANDS;
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		int id;
		int count = 1;
		String[] data = command.split(" ");
		try {
			id = Integer.parseInt(data[1]);
			if (data.length > 2) {
				count = Integer.parseInt(data[2]);
			}
		} catch (NumberFormatException nfe) {
			activeChar.sendMessage("Incorrect format for command 'summon'");
			return false;
		}
		
		String subCommand;
		if (id < 1000000) {
			subCommand = "admin_create_item";
			if (!AdminData.getInstance().hasAccess(subCommand, activeChar.getAccessLevel())) {
				activeChar.sendMessage("You don't have the access right to use this command!");
				LOG.warn("Character {} tried to use admin command {}, but have no access to it!", activeChar.getName(), subCommand);
				return false;
			}
			IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(subCommand);
			ach.useAdminCommand(subCommand + " " + id + " " + count, activeChar);
		} else {
			subCommand = "admin_spawn_once";
			if (!AdminData.getInstance().hasAccess(subCommand, activeChar.getAccessLevel())) {
				activeChar.sendMessage("You don't have the access right to use this command!");
				LOG.warn("Character {} tried to use admin command {}, but have no access to it!", activeChar.getName(), subCommand);
				return false;
			}
			IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(subCommand);
			
			activeChar.sendMessage("This is only a temporary spawn.  The mob(s) will NOT respawn.");
			id -= 1000000;
			ach.useAdminCommand(subCommand + " " + id + " " + count, activeChar);
		}
		return true;
	}
}