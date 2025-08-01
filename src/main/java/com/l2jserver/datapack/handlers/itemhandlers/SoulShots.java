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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ActionType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

public class SoulShots implements IItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(SoulShots.class);
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		final SkillHolder[] skills = item.getItem().getSkills();
		
		int itemId = item.getId();
		
		if (skills == null) {
			LOG.warn("Item {} is missing skills!", itemId);
			return false;
		}
		
		// Check if Soul shot can be used
		if ((weaponInst == null) || (weaponItem.getSoulShotCount() == 0)) {
			if (!activeChar.getAutoSoulShot().contains(itemId)) {
				activeChar.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
			}
			return false;
		}
		
		boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT) && (weaponInst.getItem().getItemGradeSPlus() == item.getItem().getItemGradeSPlus());
		
		if (!gradeCheck) {
			if (!activeChar.getAutoSoulShot().contains(itemId)) {
				activeChar.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
			}
			return false;
		}
		
		activeChar.soulShotLock.lock();
		try {
			// Check if Soul shot is already active
			if (activeChar.isChargedShot(ShotType.SOULSHOTS)) {
				return false;
			}
			
			// Consume Soul shots if player has enough of them
			int SSCount = weaponItem.getSoulShotCount();
			if ((weaponItem.getReducedSoulShot() > 0) && (Rnd.get(100) < weaponItem.getReducedSoulShotChance())) {
				SSCount = weaponItem.getReducedSoulShot();
			}
			
			if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false)) {
				if (!activeChar.disableAutoShot(itemId)) {
					activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
				}
				return false;
			}
			// Charge soul shot
			weaponInst.setChargedShot(ShotType.SOULSHOTS, true);
		} finally {
			activeChar.soulShotLock.unlock();
		}
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_S1_);
		sm.addItemName(itemId);
		activeChar.sendPacket(sm);
		
		activeChar.sendPacket(SystemMessageId.ENABLED_SOULSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, skills[0].getSkillId(), skills[0].getSkillLvl(), 0, 0), 600);
		return true;
	}
}
