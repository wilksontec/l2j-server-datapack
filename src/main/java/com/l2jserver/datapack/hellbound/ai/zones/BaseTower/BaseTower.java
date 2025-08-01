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
package com.l2jserver.datapack.hellbound.ai.zones.BaseTower;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.AttackableAggroRangeEnter;
import com.l2jserver.gameserver.model.holders.SkillHolder;

/**
 * Base Tower.
 * @author GKR
 */
public final class BaseTower extends AbstractNpcAI {
	// NPCs
	private static final int GUZEN = 22362;
	private static final int KENDAL = 32301;
	private static final int BODY_DESTROYER = 22363;
	// Skills
	private static final SkillHolder DEATH_WORD = new SkillHolder(5256, 1);
	// Misc
	private static final Map<Integer, L2PcInstance> BODY_DESTROYER_TARGET_LIST = new ConcurrentHashMap<>();
	
	public BaseTower() {
		bindKill(GUZEN);
		bindKill(BODY_DESTROYER);
		bindFirstTalk(KENDAL);
		bindAggroRangeEnter(BODY_DESTROYER);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final ClassId classId = player.getClassId();
		if (classId.equalsOrChildOf(ClassId.hellKnight) || classId.equalsOrChildOf(ClassId.soultaker)) {
			return "32301-02.htm";
		}
		return "32301-01.htm";
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("CLOSE")) {
			DoorData.getInstance().getDoor(20260004).closeMe();
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAggroRangeEnter(AttackableAggroRangeEnter event) {
		final var npc = event.npc();
		if (!BODY_DESTROYER_TARGET_LIST.containsKey(npc.getObjectId())) {
			BODY_DESTROYER_TARGET_LIST.put(npc.getObjectId(), event.player());
			npc.setTarget(event.player());
			npc.doSimultaneousCast(DEATH_WORD);
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		switch (npc.getId()) {
			case GUZEN: {
				// Should Kendal be despawned before Guzen's spawn? Or it will be crowd of Kendal's
				addSpawn(KENDAL, npc.getSpawn().getLocation(), false, npc.getSpawn().getRespawnDelay(), false);
				DoorData.getInstance().getDoor(20260003).openMe();
				DoorData.getInstance().getDoor(20260004).openMe();
				startQuestTimer("CLOSE", 60000, npc, null, false);
				break;
			}
			case BODY_DESTROYER: {
				if (BODY_DESTROYER_TARGET_LIST.containsKey(npc.getObjectId())) {
					final L2PcInstance pl = BODY_DESTROYER_TARGET_LIST.get(npc.getObjectId());
					if ((pl != null) && pl.isOnline() && !pl.isDead()) {
						pl.stopSkillEffects(true, DEATH_WORD.getSkillId());
					}
					BODY_DESTROYER_TARGET_LIST.remove(npc.getObjectId());
				}
				break;
			}
		}
	}
}