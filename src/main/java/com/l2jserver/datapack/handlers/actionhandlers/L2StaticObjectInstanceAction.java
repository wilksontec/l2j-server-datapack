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
package com.l2jserver.datapack.handlers.actionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2StaticObjectInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

public class L2StaticObjectInstanceAction implements IActionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(L2StaticObjectInstanceAction.class);
	
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		final L2StaticObjectInstance staticObject = (L2StaticObjectInstance) target;
		if (staticObject.getType() < 0) {
			LOG.info("StaticObject with invalid type! StaticObjectId {}!", staticObject.getId());
		}
		
		// Check if the L2PcInstance already target the L2NpcInstance
		if (activeChar.getTarget() != staticObject) {
			// Set the target of the L2PcInstance activeChar
			activeChar.setTarget(staticObject);
		} else if (interact) {
			// Calculate the distance between the L2PcInstance and the L2NpcInstance
			if (!activeChar.isInsideRadius(staticObject, L2Npc.INTERACTION_DISTANCE, false, false)) {
				// Notify the L2PcInstance AI with AI_INTENTION_INTERACT
				activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, staticObject);
			} else {
				if (staticObject.getType() == 2) {
					final String filename = (staticObject.getId() == 24230101) ? "data/html/signboards/tomb_of_crystalgolem.htm" : "data/html/signboards/pvp_signboard.htm";
					final String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), filename);
					final NpcHtmlMessage html = new NpcHtmlMessage(staticObject.getObjectId());
					
					if (content == null) {
						html.setHtml("<html><body>Signboard is missing:<br>" + filename + "</body></html>");
					} else {
						html.setHtml(content);
					}
					
					activeChar.sendPacket(html);
				} else if (staticObject.getType() == 0) {
					activeChar.sendPacket(staticObject.getMap());
				}
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2StaticObjectInstance;
	}
}
