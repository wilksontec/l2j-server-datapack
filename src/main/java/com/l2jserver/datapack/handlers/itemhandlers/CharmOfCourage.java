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

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.EtcStatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Charm Of Courage Handler
 * @author Zealar
 */
public class CharmOfCourage implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		
		if (!playable.isPlayer()) {
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		
		int level = activeChar.getLevel();
		final int itemLevel = item.getItem().getItemGrade().getId();
		
		if (level < 20) {
			level = 0;
		} else if (level < 40) {
			level = 1;
		} else if (level < 52) {
			level = 2;
		} else if (level < 61) {
			level = 3;
		} else if (level < 76) {
			level = 4;
		} else {
			level = 5;
		}
		
		if (itemLevel < level) {
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED);
			sm.addItemName(item.getId());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false)) {
			activeChar.setCharmOfCourage(true);
			activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			return true;
		}
		return false;
	}
}