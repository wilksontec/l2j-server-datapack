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
package com.l2jserver.datapack.handlers.bypasshandlers;

import static com.l2jserver.gameserver.config.Configuration.customs;
import static com.l2jserver.gameserver.config.Configuration.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SortedWareHouseWithdrawalList;
import com.l2jserver.gameserver.network.serverpackets.SortedWareHouseWithdrawalList.WarehouseListType;
import com.l2jserver.gameserver.network.serverpackets.WareHouseDepositList;
import com.l2jserver.gameserver.network.serverpackets.WareHouseWithdrawalList;

public class PrivateWarehouse implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(PrivateWarehouse.class);
	
	private static final String[] COMMANDS = {
		"withdrawp",
		"withdrawsortedp",
		"depositp"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		if (activeChar.isEnchanting()) {
			return false;
		}
		
		try {
			if (command.toLowerCase().startsWith(COMMANDS[0])) // WithdrawP
			{
				if (customs().enableWarehouseSortingPrivate()) {
					final NpcHtmlMessage msg = new NpcHtmlMessage(target.getObjectId());
					msg.setFile(activeChar.getHtmlPrefix(), "data/html/mods/WhSortedP.htm");
					msg.replace("%objectId%", String.valueOf(target.getObjectId()));
					activeChar.sendPacket(msg);
				} else {
					showWithdrawWindow(activeChar, null, (byte) 0);
				}
				return true;
			} else if (command.toLowerCase().startsWith(COMMANDS[1])) // WithdrawSortedP
			{
				final String param[] = command.split(" ");
				
				if (param.length > 2) {
					showWithdrawWindow(activeChar, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.getOrder(param[2]));
				} else if (param.length > 1) {
					showWithdrawWindow(activeChar, WarehouseListType.valueOf(param[1]), SortedWareHouseWithdrawalList.A2Z);
				} else {
					showWithdrawWindow(activeChar, WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z);
				}
				return true;
			} else if (command.toLowerCase().startsWith(COMMANDS[2])) // DepositP
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				activeChar.setActiveWarehouse(activeChar.getWarehouse());
				activeChar.setInventoryBlockingStatus(true);
				activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.PRIVATE));
				return true;
			}
			
			return false;
		} catch (Exception ex) {
			LOG.warn("Exception using bypass!", ex);
		}
		return false;
	}
	
	private static final void showWithdrawWindow(L2PcInstance player, WarehouseListType itemtype, byte sortorder) {
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0) {
			player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
		
		if (itemtype != null) {
			player.sendPacket(new SortedWareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE, itemtype, sortorder));
		} else {
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE));
		}
		
		if (general().debug()) {
			LOG.info("Player: {}; Command: showRetrieveWindow; Message: Showing stored items.", player);
		}
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
