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
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.network.SystemMessageId.NOTHING_INSIDE_THAT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.PetItemList;

/**
 * Restoration effect implementation.
 * @author Zoey76
 */
public final class Restoration extends AbstractEffect {
	
	private static final Logger LOG = LoggerFactory.getLogger(Restoration.class);
	
	private final int _itemId;
	private final int _itemCount;
	
	public Restoration(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_itemId = params.getInt("itemId", 0);
		_itemCount = params.getInt("itemCount", 0);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() == null) || !info.getEffected().isPlayable()) {
			return;
		}
		
		if ((_itemId <= 0) || (_itemCount <= 0)) {
			LOG.warn("Wrong item Id/count: {}/{}!", _itemId, _itemCount);
			info.getEffected().sendPacket(NOTHING_INSIDE_THAT);
			return;
		}
		
		if (info.getEffected().isPlayer()) {
			info.getEffected().getActingPlayer().addItem("Skill", _itemId, _itemCount, info.getEffector(), true);
		} else if (info.getEffected().isPet()) {
			info.getEffected().getInventory().addItem("Skill", _itemId, _itemCount, info.getEffected().getActingPlayer(), info.getEffector());
			info.getEffected().getActingPlayer().sendPacket(new PetItemList(info.getEffected().getInventory().getItems()));
		}
	}
}
