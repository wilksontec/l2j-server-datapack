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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2DecoyInstance;
import com.l2jserver.gameserver.model.actor.instance.L2EffectPointInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

/**
 * Summon Npc effect implementation.
 * @author Zoey76
 */
public final class SummonNpc extends AbstractEffect {
	
	private static final Logger LOG = LoggerFactory.getLogger(SummonNpc.class);
	
	private int _despawnDelay;
	private final int _npcId;
	private final int _npcCount;
	private final boolean _randomOffset;
	private final boolean _isSummonSpawn;
	
	public SummonNpc(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
		
		_despawnDelay = params.getInt("despawnDelay", 20000);
		_npcId = params.getInt("npcId", 0);
		_npcCount = params.getInt("npcCount", 1);
		_randomOffset = params.getBoolean("randomOffset", false);
		_isSummonSpawn = params.getBoolean("isSummonSpawn", false);
	}
	
	@Override
	public L2EffectType getEffectType() {
		return L2EffectType.SUMMON_NPC;
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffected() == null) || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead() || info.getEffected().getActingPlayer().inObserverMode()) {
			return;
		}
		
		if ((_npcId <= 0) || (_npcCount <= 0)) {
			LOG.warn("Invalid NPC Id or count for skill: {}", info.getSkill());
			return;
		}
		
		final var player = info.getEffected().getActingPlayer();
		if (player.isMounted()) {
			return;
		}
		
		final var npcTemplate = NpcData.getInstance().getTemplate(_npcId);
		if (npcTemplate == null) {
			LOG.warn("Spawn of the nonexisting NPC Id: {}, skill: {}!", _npcId, info.getSkill());
			return;
		}
		
		switch (npcTemplate.getType()) {
			case "L2Decoy": {
				final var objectId = IdFactory.getInstance().getNextId();
				final var decoy = new L2DecoyInstance(objectId, npcTemplate, player, _despawnDelay);
				decoy.setCurrentHp(decoy.getMaxHp());
				decoy.setCurrentMp(decoy.getMaxMp());
				decoy.setHeading(player.getHeading());
				decoy.setInstanceId(player.getInstanceId());
				decoy.setSummoner(player);
				decoy.spawnMe(player.getX(), player.getY(), player.getZ());
				player.setDecoy(decoy);
				break;
			}
			// TODO: Implement proper signet skills.
			case "L2EffectPoint": {
				final var objectId = IdFactory.getInstance().getNextId();
				final var effectPoint = new L2EffectPointInstance(objectId, npcTemplate, player);
				effectPoint.setCurrentHp(effectPoint.getMaxHp());
				effectPoint.setCurrentMp(effectPoint.getMaxMp());
				int x = player.getX();
				int y = player.getY();
				int z = player.getZ();
				
				if (info.getSkill().getTargetType() == TargetType.GROUND) {
					final Location wordPosition = player.getActingPlayer().getCurrentSkillWorldPosition();
					if (wordPosition != null) {
						x = wordPosition.getX();
						y = wordPosition.getY();
						z = wordPosition.getZ();
					}
				}
				
				effectPoint.setIsInvul(true);
				effectPoint.setSummoner(player);
				effectPoint.spawnMe(x, y, z);
				_despawnDelay = NpcData.getInstance().getTemplate(_npcId).getParameters().getInt("despawn_time") * 1000;
				if (_despawnDelay > 0) {
					effectPoint.scheduleDespawn(_despawnDelay);
				}
				break;
			}
			default: {
				L2Spawn spawn;
				try {
					spawn = new L2Spawn(_npcId);
				} catch (Exception ex) {
					LOG.warn("Error summoning an NPC!", ex);
					return;
				}
				
				int x = player.getX();
				int y = player.getY();
				if (_randomOffset) {
					x += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
					y += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
				}
				
				spawn.setX(x);
				spawn.setY(y);
				spawn.setZ(player.getZ());
				spawn.setHeading(player.getHeading());
				spawn.stopRespawn();
				
				final var npc = spawn.doSpawn(_isSummonSpawn);
				npc.setSummoner(player);
				npc.setName(npcTemplate.getName());
				npc.setTitle(npcTemplate.getName());
				if (_despawnDelay > 0) {
					npc.scheduleDespawn(_despawnDelay);
				}
				npc.setIsRunning(false); // TODO: Fix broadcast info.
			}
		}
	}
}
