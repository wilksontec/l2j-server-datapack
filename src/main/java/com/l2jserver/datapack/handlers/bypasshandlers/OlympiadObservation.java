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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameManager;
import com.l2jserver.gameserver.model.olympiad.OlympiadGameTask;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExOlympiadMatchList;

/**
 * Olympiad Observation bypass handler.
 * @author DS
 */
public class OlympiadObservation implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(OlympiadObservation.class);
	
	private static final String[] COMMANDS = {
		"watchmatch",
		"arenachange"
	};
	
	@Override
	public final boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		try {
			final L2Npc olymanager = activeChar.getLastFolkNPC();
			
			if (command.startsWith(COMMANDS[0])) // list
			{
				if (!Olympiad.getInstance().inCompPeriod()) {
					activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				activeChar.sendPacket(new ExOlympiadMatchList());
			} else {
				if ((olymanager == null) || !(olymanager instanceof L2OlympiadManagerInstance)) {
					return false;
				}
				
				if (!activeChar.inObserverMode() && !activeChar.isInsideRadius(olymanager, 300, false, false)) {
					return false;
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(activeChar)) {
					activeChar.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
					return false;
				}
				
				if (!Olympiad.getInstance().inCompPeriod()) {
					activeChar.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
					return false;
				}
				
				if (activeChar.isOnEvent()) {
					activeChar.sendMessage("You can not observe games while registered on an event");
					return false;
				}
				
				final int arenaId = Integer.parseInt(command.substring(12).trim());
				final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(arenaId);
				if (nextArena != null) {
					activeChar.enterOlympiadObserverMode(nextArena.getZone().getSpectatorSpawns().get(0), arenaId);
					activeChar.setInstanceId(OlympiadGameManager.getInstance().getOlympiadTask(arenaId).getZone().getInstanceId());
				}
			}
			return true;
			
		} catch (Exception ex) {
			LOG.warn("Exception using bypass!", ex);
		}
		return false;
	}
	
	@Override
	public final String[] getBypassList() {
		return COMMANDS;
	}
}
