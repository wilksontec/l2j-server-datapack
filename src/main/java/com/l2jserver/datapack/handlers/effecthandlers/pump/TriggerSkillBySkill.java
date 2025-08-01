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

import static com.l2jserver.gameserver.enums.TriggerTargetType.SELF;
import static com.l2jserver.gameserver.model.events.EventType.CREATURE_SKILL_USE;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.enums.TriggerTargetType;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.impl.character.CreatureSkillUse;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Trigger Skill By Skill effect implementation.
 * @author Zealar
 * @author Zoey76
 */
public final class TriggerSkillBySkill extends AbstractEffect {
	private final int _castSkillId;
	private final int _chance;
	private final SkillHolder _skill;
	private final TriggerTargetType _targetType;
	
	public TriggerSkillBySkill(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_castSkillId = params.getInt("castSkillId", 0);
		_chance = params.getInt("chance", 100);
		_skill = new SkillHolder(params.getInt("skillId", 0), params.getInt("skillLevel", 0));
		_targetType = params.getEnum("targetType", TriggerTargetType.class, SELF);
	}
	
	public void onSkillUseEvent(CreatureSkillUse event) {
		if ((_chance == 0) || ((_skill.getSkillId() == 0) || (_skill.getSkillLvl() == 0) || (_castSkillId == 0))) {
			return;
		}
		
		if (_castSkillId != event.skill().getId()) {
			return;
		}
		
		if (Rnd.get(100) > _chance) {
			return;
		}
		
		final var triggerSkill = _skill.getSkill();
		final var targets = _targetType.getTargets(event.caster(), event.target());
		for (var target : targets) {
			if (!target.isInvul()) {
				event.caster().makeTriggerCast(triggerSkill, target);
			}
		}
	}
	
	@Override
	public void onExit(BuffInfo info) {
		info.getEffected().removeListenerIf(CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
	}
	
	@Override
	public void onStart(BuffInfo info) {
		info.getEffected().addListener(new ConsumerEventListener(info.getEffected(), CREATURE_SKILL_USE, (CreatureSkillUse event) -> onSkillUseEvent(event), this));
	}
}
