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
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Beast SoulShot item handler.
 * @author Tempy
 */
public class BeastSoulShot implements IItemHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(BeastSoulShot.class);
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeOwner = playable.getActingPlayer();
		if (!activeOwner.hasSummon()) {
			activeOwner.sendPacket(SystemMessageId.PETS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}
		
		if (activeOwner.getSummon().isDead()) {
			activeOwner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET);
			return false;
		}
		
		final int itemId = item.getId();
		final short shotConsumption = activeOwner.getSummon().getSoulShotsPerHit();
		final long shotCount = item.getCount();
		final SkillHolder[] skills = item.getItem().getSkills();
		
		if (skills == null) {
			LOG.warn("Item {} is missing skills!", itemId);
			return false;
		}
		
		if (shotCount < shotConsumption) {
			// Not enough Soulshots to use.
			if (!activeOwner.disableAutoShot(itemId)) {
				activeOwner.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
			}
			return false;
		}
		
		if (activeOwner.getSummon().isChargedShot(ShotType.SOULSHOTS)) {
			// SoulShots are already active.
			return false;
		}
		
		// If the player doesn't have enough beast soulshot remaining, remove any auto soulshot task.
		if (!activeOwner.destroyItemWithoutTrace("Consume", item.getObjectId(), shotConsumption, null, false)) {
			if (!activeOwner.disableAutoShot(itemId)) {
				activeOwner.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS_FOR_PET);
			}
			return false;
		}
		
		activeOwner.getSummon().setChargedShot(ShotType.SOULSHOTS, true);
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_S1_);
		sm.addItemName(itemId);
		activeOwner.sendPacket(sm);
		
		activeOwner.sendPacket(SystemMessageId.PET_USE_SPIRITSHOT);
		Broadcast.toSelfAndKnownPlayersInRadius(activeOwner, new MagicSkillUse(activeOwner.getSummon(), activeOwner.getSummon(), skills[0].getSkillId(), skills[0].getSkillLvl(), 0, 0), 600);
		return true;
	}
}
