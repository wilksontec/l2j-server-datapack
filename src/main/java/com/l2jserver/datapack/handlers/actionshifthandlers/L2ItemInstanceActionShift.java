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
package com.l2jserver.datapack.handlers.actionshifthandlers;

import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionShiftHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;

public class L2ItemInstanceActionShift implements IActionShiftHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		if (activeChar.getAccessLevel().isGm()) {
			final NpcHtmlMessage html = new NpcHtmlMessage(StringUtil
				.concat("<html><body><center><font color=\"LEVEL\">Item Info</font></center><br><table border=0>", "<tr><td>Object ID: </td><td>", String.valueOf(target.getObjectId()), "</td></tr><tr><td>Item ID: </td><td>", String.valueOf(target.getId()), "</td></tr><tr><td>Owner ID: </td><td>", String
					.valueOf(((L2ItemInstance) target).getOwnerId()), "</td></tr><tr><td>Location: </td><td>", String.valueOf(target.getLocation()), "</td></tr><tr><td><br></td></tr><tr><td>Class: </td><td>", target.getClass().getSimpleName(), "</td></tr></table></body></html>"));
			activeChar.sendPacket(html);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2ItemInstance;
	}
}
