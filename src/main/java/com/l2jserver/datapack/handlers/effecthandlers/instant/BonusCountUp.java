/*
 * Copyright © 2004-2021 L2J DataPack
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

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * This effect instantly raises recommendations to give out by the specified count.
 * @author HorridoJoho
 * @since 2.6.3.0
 */
public class BonusCountUp extends AbstractEffect {
	private final int _count;
	
	protected BonusCountUp(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		_count = set.getInt("count", 0);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		super.onStart(info);
		
		L2PcInstance player = info.getEffected().getActingPlayer();
		if (player != null)
		{
			player.setRecomLeft(player.getRecomLeft() + _count);
		}
	}

	@Override
	public boolean isInstant() {
		return true;
	}
}
