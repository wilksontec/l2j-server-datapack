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

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class PlayerHelp implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(PlayerHelp.class);
	
	private static final String[] COMMANDS = {
		"player_help"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		try {
			if (command.length() < 13) {
				return false;
			}
			
			final String path = command.substring(12);
			if (path.indexOf("..") != -1) {
				return false;
			}
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html;
			if (cmd.length > 1) {
				final int itemId = Integer.parseInt(cmd[1]);
				html = new NpcHtmlMessage(0, itemId);
			} else {
				html = new NpcHtmlMessage();
			}
			
			html.setFile(activeChar.getHtmlPrefix(), "data/html/help/" + cmd[0]);
			activeChar.sendPacket(html);
		} catch (Exception ex) {
			LOG.warn("Exception using bypass!", ex);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
