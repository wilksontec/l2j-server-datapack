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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.TraitType;

/**
 * Attack Trait effect implementation.
 * @author NosBit
 */
public final class AttackTrait extends AbstractEffect {
	
	private static final Logger LOG = LoggerFactory.getLogger(AttackTrait.class);
	
	private final Map<TraitType, Float> _attackTraits = new HashMap<>();
	
	public AttackTrait(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		if (params.isEmpty()) {
			LOG.warn("This effect must have parameters!");
			return;
		}
		
		for (var param : params.getSet().entrySet()) {
			_attackTraits.put(TraitType.valueOf(param.getKey()), (Float.parseFloat((String) param.getValue()) + 100) / 100);
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		final var charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits()) {
			for (var trait : _attackTraits.entrySet()) {
				charStat.getAttackTraits()[trait.getKey().getId()] /= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]--;
			}
		}
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final var charStat = info.getEffected().getStat();
		synchronized (charStat.getAttackTraits()) {
			for (var trait : _attackTraits.entrySet()) {
				charStat.getAttackTraits()[trait.getKey().getId()] *= trait.getValue();
				charStat.getAttackTraitsCount()[trait.getKey().getId()]++;
			}
		}
	}
}
