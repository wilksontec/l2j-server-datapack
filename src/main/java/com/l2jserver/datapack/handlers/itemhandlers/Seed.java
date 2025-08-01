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

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.instancemanager.CastleManorManager;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Seed;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2ChestInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;

/**
 * @author l3x
 */
public class Seed implements IItemHandler {
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
		if (!general().allowManor()) {
			return false;
		} else if (!playable.isPlayer()) {
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2Object tgt = playable.getTarget();
		if (tgt == null) {
			return false;
		}
		if (!tgt.isNpc()) {
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		} else if (!tgt.isMonster() || ((L2MonsterInstance) tgt).isRaid() || (tgt instanceof L2ChestInstance)) {
			playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
			return false;
		}
		
		final L2MonsterInstance target = (L2MonsterInstance) tgt;
		if (target.isDead()) {
			playable.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return false;
		} else if (target.isSeeded()) {
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final L2Seed seed = CastleManorManager.getInstance().getSeed(item.getId());
		if (seed == null) {
			return false;
		} else if (seed.getCastleId() != MapRegionManager.getInstance().getAreaCastle(playable)) // TODO: replace me with tax zone
		{
			playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		target.setSeeded(seed, activeChar);
		
		final SkillHolder[] skills = item.getItem().getSkills();
		if (skills != null) {
			for (SkillHolder sk : skills) {
				activeChar.useMagic(sk.getSkill(), false, false);
			}
		}
		return true;
	}
}