/*
 * Copyright © 2004-2024 L2J DataPack
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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * This class handles following admin commands: - heal = restores HP/MP/CP on target, name or radius
 * @version $Revision: 1.2.4.5 $ $Date: 2005/04/11 10:06:06 $ Small typo fix by Zoey76 24/02/2011
 */
public class AdminHeal implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminHeal.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_heal"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_heal")) {
			handleHeal(activeChar);
		} else if (command.startsWith("admin_heal")) {
			try {
				String healTarget = command.substring(11);
				handleHeal(activeChar, healTarget);
			} catch (StringIndexOutOfBoundsException e) {
				if (general().developer()) {
					LOG.warn("Heal error: {}", e.getMessage(), e);
				}
				activeChar.sendMessage("Incorrect target/radius specified.");
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleHeal(L2PcInstance activeChar) {
		handleHeal(activeChar, null);
	}
	
	private void handleHeal(L2PcInstance activeChar, String player) {
		
		L2Object obj = activeChar.getTarget();
		if (player != null) {
			L2PcInstance plyr = L2World.getInstance().getPlayer(player);
			
			if (plyr != null) {
				obj = plyr;
			} else {
				try {
					int radius = Integer.parseInt(player);
					Collection<L2Object> objs = activeChar.getKnownList().getKnownObjects().values();
					for (L2Object object : objs) {
						if (object instanceof L2Character character) {
							character.setCurrentHpMp(character.getMaxHp(), character.getMaxMp());
							if (object instanceof L2PcInstance) {
								character.setCurrentCp(character.getMaxCp());
							}
						}
					}
					
					activeChar.sendMessage("Healed within " + radius + " unit radius.");
					return;
				} catch (NumberFormatException nbe) {
				}
			}
		}
		if (obj == null) {
			obj = activeChar;
		}
		if (obj instanceof L2Character target) {
			target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp());
			if (target instanceof L2PcInstance) {
				target.setCurrentCp(target.getMaxCp());
			}
			if (general().debug()) {
				LOG.debug("GM: {}({}) healed character {}", activeChar.getName(), activeChar.getObjectId(), target.getName());
			}
		} else {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
		}
	}
}
