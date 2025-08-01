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
package com.l2jserver.datapack.handlers.effecthandlers.pump;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Talisman Slot effect implementation.
 * @author Adry_85
 */
public final class TalismanSlot extends AbstractEffect {
	private final int _slots;
	
	public TalismanSlot(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_slots = params.getInt("slots", 0);
	}
	
	@Override
	public boolean canStart(BuffInfo info) {
		return (info.getEffector() != null) && (info.getEffected() != null) && info.getEffected().isPlayer();
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().addTalismanSlots(_slots);
	}
	
	@Override
	public boolean onActionTime(BuffInfo info) {
		return info.getSkill().isPassive();
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().getActingPlayer().getStat().addTalismanSlots(-_slots);
	}
}
