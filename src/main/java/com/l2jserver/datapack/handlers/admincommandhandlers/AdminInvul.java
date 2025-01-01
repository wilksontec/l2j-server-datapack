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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * This class handles following admin commands: - invul = turns invulnerability on/off
 * @version $Revision: 1.2.4.4 $ $Date: 2007/07/31 10:06:02 $
 */
public class AdminInvul implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminInvul.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_invul",
		"admin_setinvul"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		
		if (command.equals("admin_invul")) {
			handleInvul(activeChar);
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		}
		if (command.equals("admin_setinvul")) {
			L2Object target = activeChar.getTarget();
			if (target instanceof L2PcInstance) {
				handleInvul((L2PcInstance) target);
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleInvul(L2PcInstance activeChar) {
		String text;
		if (activeChar.isInvul()) {
			activeChar.setIsInvul(false);
			text = activeChar.getName() + " is now mortal";
			if (general().debug()) {
				LOG.debug("GM: Gm removed invul mode from character {}({})", activeChar.getName(), activeChar.getObjectId());
			}
		} else {
			activeChar.setIsInvul(true);
			text = activeChar.getName() + " is now invulnerable";
			if (general().debug()) {
				LOG.debug("GM: Gm activated invul mode for character {}({})", activeChar.getName(), activeChar.getObjectId());
			}
		}
		activeChar.sendMessage(text);
	}
}
