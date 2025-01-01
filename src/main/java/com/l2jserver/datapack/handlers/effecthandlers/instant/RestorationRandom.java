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

import static com.l2jserver.gameserver.config.Configuration.rates;
import static com.l2jserver.gameserver.network.SystemMessageId.NOTHING_INSIDE_THAT;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Restoration Random effect implementation.<br>
 * This effect is present in item skills that "extract" new items upon usage.<br>
 * This effect has been unhardcoded in order to work on targets as well.
 * @author Zoey76
 */
public final class RestorationRandom extends AbstractEffect {
	
	private static final Logger LOG = LoggerFactory.getLogger(RestorationRandom.class);
	
	public RestorationRandom(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer()) {
			return;
		}
		
		final var exSkill = info.getSkill().getExtractableSkill();
		if (exSkill == null) {
			return;
		}
		
		if (exSkill.getProductItems().isEmpty()) {
			LOG.warn("Extractable Skill with no data, probably wrong/empty table in Skill Id {}!", info.getSkill().getId());
			return;
		}
		
		final double rndNum = 100 * Rnd.nextDouble();
		double chanceFrom = 0;
		final var creationList = new LinkedList<ItemHolder>();
		
		// Explanation for future changes:
		// You get one chance for the current skill, then you can fall into
		// one of the "areas" like in a roulette.
		// Example: for an item like Id1,A1,30;Id2,A2,50;Id3,A3,20;
		// #---#-----#--#
		// 0--30----80-100
		// If you get chance equal 45% you fall into the second zone 30-80.
		// Meaning you get the second production list.
		// Calculate extraction
		for (var expi : exSkill.getProductItems()) {
			final var chance = expi.getChance();
			if ((rndNum >= chanceFrom) && (rndNum <= (chance + chanceFrom))) {
				creationList.addAll(expi.getItems());
				break;
			}
			chanceFrom += chance;
		}
		
		final var player = info.getEffected().getActingPlayer();
		if (creationList.isEmpty()) {
			player.sendPacket(NOTHING_INSIDE_THAT);
			return;
		}
		
		for (var item : creationList) {
			if ((item.getId() <= 0) || (item.getCount() <= 0)) {
				continue;
			}
			player.addItem("Extract", item.getId(), (long) (item.getCount() * rates().getRateExtractable()), info.getEffector(), true);
		}
	}
}
