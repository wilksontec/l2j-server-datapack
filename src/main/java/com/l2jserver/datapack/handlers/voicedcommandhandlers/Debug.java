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
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Debug implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"debug"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String params) {
		if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
			return false;
		}
		
		if (VOICED_COMMANDS[0].equalsIgnoreCase(command)) {
			if (player.isDebug()) {
				player.setDebug(null);
				player.sendMessage("Debugging disabled.");
			} else {
				player.setDebug(player);
				player.sendMessage("Debugging enabled.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}