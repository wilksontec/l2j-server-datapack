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
package com.l2jserver.datapack.handlers.itemhandlers;

import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.L2ExtractableProduct;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Extractable Items handler.
 * @author HorridoJoho
 */
public class ExtractableItems implements IItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExtractableItems.class);
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final L2EtcItem etcItem = (L2EtcItem) item.getItem();
		final List<L2ExtractableProduct> exitem = etcItem.getExtractableItems();
		if (exitem == null) {
			LOG.warn("No extractable data defined for {}!", etcItem);
			return false;
		}
		
		// destroy item
		if (!activeChar.destroyItem("Extract", item.getObjectId(), 1, activeChar, true)) {
			return false;
		}
		
		boolean created = false;
		for (L2ExtractableProduct expi : exitem) {
			if (Rnd.get(100000) <= expi.getChance()) {
				final int min = (int) (expi.getMin() * rates().getRateExtractable());
				final int max = (int) (expi.getMax() * rates().getRateExtractable());
				
				int createItemAmount = (max == min) ? min : (Rnd.get((max - min) + 1) + min);
				if (createItemAmount == 0) {
					continue;
				}
				
				if (item.isStackable() || (createItemAmount == 1)) {
					activeChar.addItem("Extract", expi.getId(), createItemAmount, activeChar, true);
				} else {
					while (createItemAmount > 0) {
						activeChar.addItem("Extract", expi.getId(), 1, activeChar, true);
						createItemAmount--;
					}
				}
				created = true;
			}
		}
		
		if (!created) {
			activeChar.sendPacket(SystemMessageId.NOTHING_INSIDE_THAT);
		}
		return true;
	}
}
