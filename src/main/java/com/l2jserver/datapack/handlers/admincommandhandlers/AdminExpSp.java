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

import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands:
 * <li>add_exp_sp_to_character <i>shows menu for add or remove</i>
 * <li>add_exp_sp exp sp <i>Adds exp & sp to target, displays menu if a parameter is missing</i>
 * <li>remove_exp_sp exp sp <i>Removes exp & sp from target, displays menu if a parameter is missing</i>
 * @version $Revision: 1.2.4.6 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminExpSp implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminExpSp.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_add_exp_sp_to_character",
		"admin_add_exp_sp",
		"admin_remove_exp_sp"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_add_exp_sp")) {
			try {
				String val = command.substring(16);
				if (!adminAddExpSp(activeChar, val)) {
					activeChar.sendMessage("Usage: //add_exp_sp exp sp");
				}
			} catch (StringIndexOutOfBoundsException e) { // Case of missing parameter
				activeChar.sendMessage("Usage: //add_exp_sp exp sp");
			}
		} else if (command.startsWith("admin_remove_exp_sp")) {
			try {
				String val = command.substring(19);
				if (!adminRemoveExpSP(activeChar, val)) {
					activeChar.sendMessage("Usage: //remove_exp_sp exp sp");
				}
			} catch (StringIndexOutOfBoundsException e) { // Case of missing parameter
				activeChar.sendMessage("Usage: //remove_exp_sp exp sp");
			}
		}
		addExpSp(activeChar);
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void addExpSp(L2PcInstance activeChar) {
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) {
			player = (L2PcInstance) target;
		} else {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar.getHtmlPrefix(), "data/html/admin/expsp.htm");
		adminReply.replace("%name%", player.getName());
		adminReply.replace("%level%", String.valueOf(player.getLevel()));
		adminReply.replace("%xp%", String.valueOf(player.getExp()));
		adminReply.replace("%sp%", String.valueOf(player.getSp()));
		adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
		activeChar.sendPacket(adminReply);
	}
	
	private boolean adminAddExpSp(L2PcInstance activeChar, String ExpSp) {
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) {
			player = (L2PcInstance) target;
		} else {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens() != 2) {
			return false;
		}
		
		String exp = st.nextToken();
		String sp = st.nextToken();
		long expval = 0;
		int spval = 0;
		try {
			expval = Long.parseLong(exp);
			spval = Integer.parseInt(sp);
		} catch (Exception e) {
			return false;
		}
		if ((expval != 0) || (spval != 0)) {
			// Common character information
			player.sendMessage("Admin is adding you " + expval + " xp and " + spval + " sp.");
			player.addExpAndSp(expval, spval);
			player.broadcastUserInfo();
			// Admin information
			activeChar.sendMessage("Added " + expval + " xp and " + spval + " sp to " + player.getName() + ".");
			if (general().debug()) {
				LOG.debug("GM: {}({}) added {} xp and {} sp to {}.", activeChar.getName(), activeChar.getObjectId(), expval, spval, player.getObjectId());
			}
		}
		return true;
	}
	
	private boolean adminRemoveExpSP(L2PcInstance activeChar, String ExpSp) {
		L2Object target = activeChar.getTarget();
		L2PcInstance player = null;
		if (target instanceof L2PcInstance) {
			player = (L2PcInstance) target;
		} else {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		}
		StringTokenizer st = new StringTokenizer(ExpSp);
		if (st.countTokens() != 2) {
			return false;
		}
		
		String exp = st.nextToken();
		String sp = st.nextToken();
		long expval = 0;
		int spval = 0;
		try {
			expval = Long.parseLong(exp);
			spval = Integer.parseInt(sp);
		} catch (Exception e) {
			return false;
		}
		if ((expval != 0) || (spval != 0)) {
			// Common character information
			player.sendMessage("Admin is removing you " + expval + " xp and " + spval + " sp.");
			player.removeExpAndSp(expval, spval);
			player.broadcastUserInfo();
			// Admin information
			activeChar.sendMessage("Removed " + expval + " xp and " + spval + " sp from " + player.getName() + ".");
			if (general().debug()) {
				LOG.debug("GM: {}({}) removed {} xp and {} sp from {}.", activeChar.getName(), activeChar.getObjectId(), expval, spval, player.getObjectId());
			}
		}
		return true;
	}
}
