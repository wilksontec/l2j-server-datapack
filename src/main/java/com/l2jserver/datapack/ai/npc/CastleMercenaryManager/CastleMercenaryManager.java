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
package com.l2jserver.datapack.ai.npc.CastleMercenaryManager;

import java.util.StringTokenizer;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Castle Mercenary Manager AI.
 * @author malyelfik
 */
public final class CastleMercenaryManager extends AbstractNpcAI {
	// NPCs
	private static final int[] NPCS = {
		35102, // Greenspan
		35144, // Sanford
		35186, // Arvid
		35228, // Morrison
		35276, // Eldon
		35318, // Solinus
		35365, // Rowell
		35511, // Gompus
		35557, // Kendrew
	};
	
	public CastleMercenaryManager() {
		bindStartNpc(NPCS);
		bindTalk(NPCS);
		bindFirstTalk(NPCS);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		final StringTokenizer st = new StringTokenizer(event, " ");
		switch (st.nextToken()) {
			case "limit": {
				final Castle castle = npc.getCastle();
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				if (castle.getName().equals("aden")) {
					html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-aden-limit.html"));
				} else if (castle.getName().equals("rune")) {
					html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-rune-limit.html"));
				} else {
					html.setHtml(getHtm(player.getHtmlPrefix(), "mercmanager-limit.html"));
				}
				html.replace("%feud_name%", String.valueOf(1001000 + castle.getResidenceId()));
				player.sendPacket(html);
				break;
			}
			case "buy": {
				if (SevenSigns.getInstance().isSealValidationPeriod()) {
					final int listId = Integer.parseInt(npc.getId() + st.nextToken());
					((L2MerchantInstance) npc).showBuyWindow(player, listId, false); // NOTE: Not affected by Castle Taxes, baseTax is 20% (done in merchant buylists)
				} else {
					htmltext = "mercmanager-ssq.html";
				}
				break;
			}
			case "main": {
				htmltext = onFirstTalk(npc, player);
				break;
			}
			case "mercmanager-01.html": {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final String htmltext;
		if (player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || ((player.getClanId() == npc.getCastle().getOwnerId()) && player.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))) {
			if (npc.getCastle().getSiege().isInProgress()) {
				htmltext = "mercmanager-siege.html";
			} else {
				htmltext = switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)) {
					case SevenSigns.CABAL_DUSK -> "mercmanager-dusk.html";
					case SevenSigns.CABAL_DAWN -> "mercmanager-dawn.html";
					default -> "mercmanager.html";
				};
			}
		} else {
			htmltext = "mercmanager-no.html";
		}
		return htmltext;
	}
}