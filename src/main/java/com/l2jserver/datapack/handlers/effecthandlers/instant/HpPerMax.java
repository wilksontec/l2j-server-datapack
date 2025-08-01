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

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Hp Per Max effect implementation.
 * @author UnAfraid
 */
public final class HpPerMax extends AbstractEffect {
	private final int _power;
	
	public HpPerMax(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_power = params.getInt("power", 0);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.HP;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		L2Character target = info.getEffected();
		if ((target == null) || target.isDead() || target.isDoor()) {
			return;
		}
		
		double amount = 0;
		double power = _power;
		boolean full = (power == 100.0);
		
		amount = full ? target.getMaxHp() : (target.getMaxHp() * power) / 100.0;
		// Prevents overheal and negative amount
		amount = Math.max(Math.min(amount, target.getMaxRecoverableHp() - target.getCurrentHp()), 0);
		if (amount != 0) {
			target.setCurrentHp(amount + target.getCurrentHp());
		}
		SystemMessage sm;
		if (info.getEffector().getObjectId() != target.getObjectId()) {
			sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1);
			sm.addCharName(info.getEffector());
		} else {
			sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
		}
		sm.addInt((int) amount);
		target.sendPacket(sm);
	}
}
