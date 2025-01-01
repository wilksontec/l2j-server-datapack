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

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.PetDataTable;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.holders.PetItemHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.PetItemList;

/**
 * Summon Pet effect implementation.
 * @author UnAfraid
 */
public final class SummonPet extends AbstractEffect {
	
	private static final Logger LOG = LoggerFactory.getLogger(SummonPet.class);
	
	public SummonPet(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
		super(attachCond, applyCond, set, params);
	}
	
	@Override
	public boolean isInstant() {
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info) {
		if ((info.getEffector() == null) || (info.getEffected() == null) || !info.getEffector().isPlayer() || !info.getEffected().isPlayer() || info.getEffected().isAlikeDead()) {
			return;
		}
		
		final var player = info.getEffector().getActingPlayer();
		if ((player.hasSummon() || player.isMounted())) {
			player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
			return;
		}
		
		final var holder = player.removeScript(PetItemHolder.class);
		if (holder == null) {
			LOG.warn("Summoning pet without attaching PetItemHandler!");
			return;
		}
		
		final var item = holder.getItem();
		if (player.getInventory().getItemByObjectId(item.getObjectId()) != item) {
			LOG.warn("Player {} is trying to summon pet from item that he doesn't owns.", player);
			return;
		}
		
		final var petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
		if ((petData == null) || (petData.getNpcId() == -1)) {
			return;
		}
		
		final var npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
		final var pet = L2PetInstance.spawnPet(npcTemplate, player, item);
		pet.setShowSummonAnimation(true);
		if (!pet.isRespawned()) {
			pet.setCurrentHp(pet.getMaxHp());
			pet.setCurrentMp(pet.getMaxMp());
			pet.getStat().setExp(pet.getExpForThisLevel());
			pet.setCurrentFed(pet.getMaxFed());
		}
		
		pet.setRunning();
		
		if (!pet.isRespawned()) {
			pet.storeMe();
		}
		
		item.setEnchantLevel(pet.getLevel());
		player.setPet(pet);
		pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
		pet.startFeed();
		pet.setFollowStatus(true);
		pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
		pet.broadcastStatusUpdate();
	}
}
