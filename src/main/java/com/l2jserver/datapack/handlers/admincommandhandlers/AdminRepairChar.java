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

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - delete = deletes target
 * @version $Revision: 1.1.2.6.2.3 $ $Date: 2005/04/11 10:05:59 $
 */
public class AdminRepairChar implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminRepairChar.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_restore",
		"admin_repair"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		handleRepair(command);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleRepair(String command) {
		String[] parts = command.split(" ");
		if (parts.length != 2) {
			return;
		}
		
		final String playerName = parts[1];
		String cmd = "UPDATE characters SET x=-84318, y=244579, z=-3730 WHERE char_name=?";
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {
			try (PreparedStatement ps = con.prepareStatement(cmd)) {
				ps.setString(1, playerName);
				ps.execute();
			}
			
			final int objId = CharNameTable.getInstance().getIdByName(playerName);
			if (objId != 0) {
				// Delete player's shortcuts.
				try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?")) {
					ps.setInt(1, objId);
					ps.execute();
				}
				// Move all items to the inventory.
				try (PreparedStatement ps = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=?")) {
					ps.setInt(1, objId);
					ps.execute();
				}
			}
		} catch (Exception e) {
			LOG.warn("Could not repair char:", e);
		}
	}
}
