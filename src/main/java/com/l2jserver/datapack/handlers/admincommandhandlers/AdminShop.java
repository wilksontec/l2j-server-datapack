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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.BuyList;
import com.l2jserver.gameserver.network.serverpackets.ExBuySellList;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>gmshop = shows menu</li>
 * <li>buy id = shows shop with respective id</li>
 * </ul>
 */
public class AdminShop implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminShop.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_buy",
		"admin_gmshop"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_buy")) {
			try {
				handleBuyRequest(activeChar, command.substring(10));
			} catch (IndexOutOfBoundsException e) {
				activeChar.sendMessage("Please specify buylist.");
			}
		} else if (command.equals("admin_gmshop")) {
			AdminHtml.showAdminHtml(activeChar, "gmshops.htm");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void handleBuyRequest(L2PcInstance activeChar, String command) {
		int val = -1;
		try {
			val = Integer.parseInt(command);
		} catch (Exception e) {
			LOG.warn("Admin buylist failed: {}", command);
		}
		
		L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
		
		if (buyList != null) {
			activeChar.sendPacket(new BuyList(buyList, activeChar.getAdena(), 0));
			activeChar.sendPacket(new ExBuySellList(activeChar, false));
		} else {
			LOG.warn("No buylist with id: {}", val);
		}
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
