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

import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.L2Weapon;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ActionType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Fish Shots item handler.
 * @author -Nemesiss-
 */
public class FishShots implements IItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(FishShots.class);
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		
		if ((weaponInst == null) || (weaponItem.getItemType() != WeaponType.FISHINGROD)) {
			return false;
		}
		
		if (activeChar.isChargedShot(ShotType.FISH_SOULSHOTS)) {
			return false;
		}
		
		final long count = item.getCount();
		final SkillHolder[] skills = item.getItem().getSkills();
		
		if (skills == null) {
			LOG.warn("Item {} is missing skills!", item.getId());
			return false;
		}
		
		boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.FISHINGSHOT) && (weaponInst.getItem().getItemGradeSPlus() == item.getItem().getItemGradeSPlus());
		
		if (!gradeCheck) {
			activeChar.sendPacket(SystemMessageId.WRONG_FISHINGSHOT_GRADE);
			return false;
		}
		
		if (count < 1) {
			return false;
		}
		
		activeChar.setChargedShot(ShotType.FISH_SOULSHOTS, true);
		activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
		L2Object oldTarget = activeChar.getTarget();
		activeChar.setTarget(activeChar);
		
		Broadcast.toSelfAndKnownPlayers(activeChar, new MagicSkillUse(activeChar, skills[0].getSkillId(), skills[0].getSkillLvl(), 0, 0));
		activeChar.setTarget(oldTarget);
		return true;
	}
}
