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

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.handler.IActionHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.SiegableHall;
import com.l2jserver.gameserver.model.holders.DoorRequestHolder;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;

public class L2DoorInstanceAction implements IActionHandler {
	@Override
	public boolean action(L2PcInstance activeChar, L2Object target, boolean interact) {
		// Check if the L2PcInstance already target the L2NpcInstance
		if (activeChar.getTarget() != target) {
			activeChar.setTarget(target);
		} else if (interact) {
			L2DoorInstance door = (L2DoorInstance) target;
			// MyTargetSelected my = new MyTargetSelected(getObjectId(), activeChar.getLevel());
			// activeChar.sendPacket(my);
			if (target.isAutoAttackable(activeChar)) {
				if (Math.abs(activeChar.getZ() - target.getZ()) < 400) {
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
			} else if ((activeChar.getClan() != null) && (door.getClanHall() != null) && (activeChar.getClanId() == door.getClanHall().getOwnerId())) {
				if (!door.isInsideRadius(activeChar, L2Npc.INTERACTION_DISTANCE, false, false)) {
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
				} else if (!door.getClanHall().isSiegableHall() || !((SiegableHall) door.getClanHall()).isInSiege()) {
					activeChar.addScript(new DoorRequestHolder(door));
					if (!door.getOpen()) {
						activeChar.sendPacket(new ConfirmDlg(1140));
					} else {
						activeChar.sendPacket(new ConfirmDlg(1141));
					}
				}
			} else if ((activeChar.getClan() != null) && (((L2DoorInstance) target).getFort() != null) && (activeChar.getClan() == ((L2DoorInstance) target).getFort().getOwnerClan()) && ((L2DoorInstance) target).isOpenableBySkill() && !((L2DoorInstance) target).getFort().getSiege().isInProgress()) {
				if (!((L2Character) target).isInsideRadius(activeChar, L2Npc.INTERACTION_DISTANCE, false, false)) {
					activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, target);
				} else {
					activeChar.addScript(new DoorRequestHolder((L2DoorInstance) target));
					if (!((L2DoorInstance) target).getOpen()) {
						activeChar.sendPacket(new ConfirmDlg(1140));
					} else {
						activeChar.sendPacket(new ConfirmDlg(1141));
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType() {
		return InstanceType.L2DoorInstance;
	}
}
