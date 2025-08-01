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

import static com.l2jserver.gameserver.config.Configuration.customs;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Time user command.
 */
public class Time implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = {
		77
	};
	
	private static final SimpleDateFormat fmt = new SimpleDateFormat("H:mm.");
	
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar) {
		if (COMMAND_IDS[0] != id) {
			return false;
		}
		
		int t = GameTimeController.getInstance().getGameTime();
		String h = "" + ((t / 60) % 24);
		String m;
		if ((t % 60) < 10) {
			m = "0" + (t % 60);
		} else {
			m = "" + (t % 60);
		}
		
		SystemMessage sm;
		if (GameTimeController.getInstance().isNight()) {
			sm = SystemMessage.getSystemMessage(SystemMessageId.TIME_S1_S2_IN_THE_NIGHT);
			sm.addString(h);
			sm.addString(m);
		} else {
			sm = SystemMessage.getSystemMessage(SystemMessageId.TIME_S1_S2_IN_THE_DAY);
			sm.addString(h);
			sm.addString(m);
		}
		activeChar.sendPacket(sm);
		if (customs().displayServerTime()) {
			activeChar.sendMessage("Server time is " + fmt.format(new Date(System.currentTimeMillis())));
		}
		return true;
	}
	
	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
