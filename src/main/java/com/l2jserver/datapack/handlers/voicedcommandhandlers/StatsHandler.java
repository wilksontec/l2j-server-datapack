/*
 * Copyright © 2004-2024 L2J DataPack
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
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.network.SystemMessageId.S1_OFFLINE;
import static com.l2jserver.gameserver.network.SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.StringUtil;

/**
 * Stats voiced handler implementation.
 * @author Zoey76.
 */
public class StatsHandler implements IVoicedCommandHandler {
	private static final String[] COMMANDS = {
		"stats"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String params) {
		if (!command.equals("stats") || (params == null) || params.isEmpty()) {
			player.sendMessage("Usage: .stats <player name>");
			return false;
		}
		
		final var pc = L2World.getInstance().getPlayer(params);
		if ((pc == null)) {
			player.sendPacket(TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return false;
		}
		
		if (pc.getClient().isDetached()) {
			final var sm = SystemMessage.getSystemMessage(S1_OFFLINE);
			sm.addPcName(pc);
			player.sendPacket(sm);
			return false;
		}
		
		if (!L2Event.isParticipant(pc) || (pc.getEventStatus() == null)) {
			player.sendMessage("That player is not an event participant.");
			return false;
		}
		
		final var replyMSG = StringUtil.startAppend(300 + (pc.getEventStatus().getKills().size() * 50), "<html><body>"
			+ "<center><font color=\"LEVEL\">[ L2J EVENT ENGINE ]</font></center><br><br>Statistics for player <font color=\"LEVEL\">", pc.getName(), "</font><br>Total kills <font color=\"FF0000\">", String.valueOf(pc.getEventStatus().getKills().size()), "</font><br><br>Detailed list: <br>");
		for (var plr : pc.getEventStatus().getKills()) {
			StringUtil.append(replyMSG, "<font color=\"FF0000\">", plr.getName(), "</font><br>");
		}
		replyMSG.append("</body></html>");
		final var adminReply = new NpcHtmlMessage();
		adminReply.setHtml(replyMSG.toString());
		player.sendPacket(adminReply);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return COMMANDS;
	}
}
