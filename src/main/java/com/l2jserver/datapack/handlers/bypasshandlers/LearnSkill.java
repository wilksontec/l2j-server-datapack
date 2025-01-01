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
package com.l2jserver.datapack.handlers.bypasshandlers;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.PlayerLearnSkillRequested;

/**
 * Learn Skill.
 * @author Zoey76
 * @version 2.6.3.0
 */
public class LearnSkill implements IBypassHandler {
	
	private static final String[] COMMANDS = {
		"learn_skill"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance player, L2Character target) {
		if (!(target instanceof L2NpcInstance npc)) {
			return false;
		}
		
		EventDispatcher.getInstance().notifyEventAsync(new PlayerLearnSkillRequested(npc, player, parseClassId(command)), target);
		return true;
	}
	
	private static ClassId parseClassId(String command) {
		try {
			final var classId = command.replace(COMMANDS[0], "").trim();
			if (classId.isBlank()) {
				return null;
			}
			return ClassId.getClassId(Integer.parseInt(classId));
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
