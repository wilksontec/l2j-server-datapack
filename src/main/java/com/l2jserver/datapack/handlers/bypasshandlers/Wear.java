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

import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.BuyListData;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.buylist.L2BuyList;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ShopPreviewList;

public class Wear implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(Wear.class);
	
	private static final String[] COMMANDS = {
		"Wear"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!target.isNpc()) {
			return false;
		}
		
		if (!general().allowWear()) {
			return false;
		}
		
		try {
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (st.countTokens() < 1) {
				return false;
			}
			
			showWearWindow(activeChar, Integer.parseInt(st.nextToken()));
			return true;
		} catch (Exception ex) {
			LOG.warn("Exception using bypass!", ex);
		}
		return false;
	}
	
	private static final void showWearWindow(L2PcInstance player, int val) {
		final L2BuyList buyList = BuyListData.getInstance().getBuyList(val);
		if (buyList == null) {
			LOG.warn("BuyList not found! BuyListId: {}", val);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		player.setInventoryBlockingStatus(true);
		
		player.sendPacket(new ShopPreviewList(buyList, player.getAdena(), player.getExpertiseLevel()));
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
