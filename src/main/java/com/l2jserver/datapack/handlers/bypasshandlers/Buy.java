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
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Buy implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(Buy.class);
	
	private static final String[] COMMANDS = {
		"Buy"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!(target instanceof L2MerchantInstance)) {
			return false;
		}
		
		try {
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (st.countTokens() < 1) {
				return false;
			}
			
			((L2MerchantInstance) target).showBuyWindow(activeChar, Integer.parseInt(st.nextToken()));
			return true;
		} catch (Exception ex) {
			LOG.warn("Unable to use bypass!", ex);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
