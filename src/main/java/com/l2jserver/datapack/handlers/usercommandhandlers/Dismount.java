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
package com.l2jserver.datapack.handlers.usercommandhandlers;

import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Dismount user command.
 * @author Micht
 */
public class Dismount implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		62
	};
	
	@Override
	public synchronized boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0]) {
			return false;
		}
		
		if (activeChar.isRentedPet()) {
			activeChar.stopRentPet();
		} else if (activeChar.isMounted()) {
			activeChar.dismount();
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
