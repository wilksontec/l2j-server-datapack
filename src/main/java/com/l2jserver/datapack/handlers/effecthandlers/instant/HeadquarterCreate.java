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

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.ClanHallSiegeManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2SiegeFlagInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;

/**
 * Headquarter Create effect implementation.
 * @author Adry_85
 */
public final class HeadquarterCreate extends AbstractEffect {
	private static final int HQ_NPC_ID = 35062;
	private final boolean _isAdvanced;
	
	public HeadquarterCreate(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_isAdvanced = params.getBoolean("isAdvanced", false);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		final var player = info.getEffector().getActingPlayer();
		if (!player.isClanLeader()) {
			return;
		}

		final var objectId = IdFactory.getInstance().getNextId();
		final var template = NpcData.getInstance().getTemplate(HQ_NPC_ID);
		final var flag = new L2SiegeFlagInstance(objectId, player, template, _isAdvanced, false);
		flag.setTitle(player.getClan().getName());
		flag.setCurrentHpMp(flag.getMaxHp(), flag.getMaxMp());
		flag.setHeading(player.getHeading());
		flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
		final var castle = CastleManager.getInstance().getCastle(player);
		final var fort = FortManager.getInstance().getFort(player);
		final var hall = ClanHallSiegeManager.getInstance().getNearbyClanHall(player);
		if (castle != null) {
			castle.getSiege().getFlag(player.getClan()).add(flag);
		} else if (fort != null) {
			fort.getSiege().getFlag(player.getClan()).add(flag);
		} else {
			hall.getSiege().getFlag(player.getClan()).add(flag);
		}
	}
}
