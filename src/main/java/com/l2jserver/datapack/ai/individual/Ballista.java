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
package com.l2jserver.datapack.ai.individual;

import static com.l2jserver.gameserver.config.Configuration.clan;

import java.util.List;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Ballista AI.
 * @author St3eT
 */
public final class Ballista extends AbstractNpcAI {
	// NPCs
	private static final int[] BALLISTA = {
		35685, // Shanty Fortress
		35723, // Southern Fortress
		35754, // Hive Fortress
		35792, // Valley Fortress
		35823, // Ivory Fortress
		35854, // Narsell Fortress
		35892, // Bayou Fortress
		35923, // White Sands Fortress
		35961, // Borderland Fortress
		35999, // Swamp Fortress
		36030, // Archaic Fortress
		36068, // Floran Fortress
		36106, // Cloud Mountain)
		36137, // Tanor Fortress
		36168, // Dragonspine Fortress
		36206, // Antharas's Fortress
		36244, // Western Fortress
		36282, // Hunter's Fortress
		36313, // Aaru Fortress
		36351, // Demon Fortress
		36389, // Monastic Fortress
	};
	// Skill
	private static final SkillHolder BOMB = new SkillHolder(2342, 1); // Ballista Bomb
	// Misc
	private static final int MIN_CLAN_LV = 5;
	
	public Ballista() {
		bindSkillSee(BALLISTA);
		bindSpawn(BALLISTA);
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if ((skill != null) && (caster.getTarget() == npc) && (getRandom(100) < 40) && (skill == BOMB.getSkill())) {
			if (npc.getFort().getSiege().isInProgress()) {
				if ((caster.getClan() != null) && (caster.getClan().getLevel() >= MIN_CLAN_LV)) {
					caster.getClan().addReputationScore(clan().getKillBallistaPoints(), true);
					caster.sendPacket(SystemMessageId.BALLISTA_DESTROYED_CLAN_REPU_INCREASED);
				}
			}
			npc.doDie(caster);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		npc.disableCoreAI(true);
		npc.setIsMortal(false);
	}
}