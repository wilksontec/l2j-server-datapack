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
package com.l2jserver.datapack.ai.npc.ArenaManager;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;

/**
 * Arena Manager AI.
 * @author St3eT
 */
public class ArenaManager extends AbstractNpcAI {
	// NPCs
	private static final int[] ARENA_MANAGER = {
		31226, // Arena Director (MDT)
		31225, // Arena Manager (Coliseum)
	};
	// Skill
	private static final SkillHolder[] BUFFS = {
		new SkillHolder(6805), // Arena Empower
		new SkillHolder(6806), // Arena Acumen
		new SkillHolder(6807), // Arena Concentration
		new SkillHolder(6808), // Arena Might
		new SkillHolder(6804), // Arena Wind Walk
		new SkillHolder(6812), // Arena Berserker Spirit
	};
	private static final SkillHolder CP_RECOVERY = new SkillHolder(4380); // Arena: CP Recovery
	private static final SkillHolder HP_RECOVERY = new SkillHolder(6817); // Arena HP Recovery
	// Misc
	private static final int CP_COST = 1000;
	private static final int HP_COST = 1000;
	private static final int BUFF_COST = 2000;
	
	public ArenaManager() {
		bindStartNpc(ARENA_MANAGER);
		bindTalk(ARENA_MANAGER);
		bindFirstTalk(ARENA_MANAGER);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "CPrecovery": {
				if (player.getAdena() >= CP_COST) {
					takeItems(player, Inventory.ADENA_ID, CP_COST);
					startQuestTimer("CPrecovery_delay", 2000, npc, player);
				} else {
					player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				}
				break;
			}
			case "CPrecovery_delay": {
				if ((player != null) && !player.isInsideZone(ZoneId.PVP)) {
					npc.setTarget(player);
					npc.doCast(CP_RECOVERY);
				}
				break;
			}
			case "HPrecovery": {
				if (player.getAdena() >= HP_COST) {
					takeItems(player, Inventory.ADENA_ID, HP_COST);
					startQuestTimer("HPrecovery_delay", 2000, npc, player);
				} else {
					player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				}
				break;
			}
			case "HPrecovery_delay": {
				if ((player != null) && !player.isInsideZone(ZoneId.PVP)) {
					npc.setTarget(player);
					npc.doCast(HP_RECOVERY);
				}
				break;
			}
			case "Buff": {
				if (player.getAdena() >= BUFF_COST) {
					takeItems(player, Inventory.ADENA_ID, BUFF_COST);
					npc.setTarget(player);
					for (SkillHolder skill : BUFFS) {
						npc.doCast(skill);
					}
				} else {
					player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
				}
				break;
			}
		}
		return null;
	}
}