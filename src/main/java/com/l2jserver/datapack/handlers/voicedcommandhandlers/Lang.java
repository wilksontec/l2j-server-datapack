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
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.customs;

import java.util.StringTokenizer;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.StringUtil;

public class Lang implements IVoicedCommandHandler {
	private static final String[] VOICED_COMMANDS = {
		"lang"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String params) {
		if (!customs().multiLangEnable() || !customs().multiLangHandler()) {
			return false;
		}
		
		final NpcHtmlMessage msg = new NpcHtmlMessage();
		if (params == null) {
			final StringBuilder html = StringUtil.startAppend(100);
			for (String lang : customs().getMultiLangAllowed()) {
				StringUtil.append(html, "<button value=\"", lang.toUpperCase(), "\" action=\"bypass -h voice .lang ", lang, "\" width=60 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>");
			}
			
			msg.setFile(player.getHtmlPrefix(), "data/html/mods/Lang/LanguageSelect.htm");
			msg.replace("%list%", html.toString());
			player.sendPacket(msg);
			return true;
		}
		
		final StringTokenizer st = new StringTokenizer(params);
		if (st.hasMoreTokens()) {
			final String lang = st.nextToken().trim();
			if (player.setLang(lang)) {
				msg.setFile(player.getHtmlPrefix(), "data/html/mods/Lang/Ok.htm");
				player.sendPacket(msg);
				return true;
			}
			msg.setFile(player.getHtmlPrefix(), "data/html/mods/Lang/Error.htm");
			player.sendPacket(msg);
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return VOICED_COMMANDS;
	}
}