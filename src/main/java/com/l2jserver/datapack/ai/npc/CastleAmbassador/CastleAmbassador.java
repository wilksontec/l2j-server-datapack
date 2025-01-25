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
package com.l2jserver.datapack.ai.npc.CastleAmbassador;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcEventReceived;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * Castle Ambassador AI.
 * @author St3eT
 */
public final class CastleAmbassador extends AbstractNpcAI {
	// NPCs
	// @formatter:off
	private static final int[] CASTLE_AMBASSADOR =
	{
		36393, 36394, 36437, 36435, // Gludio
		36395, 36436, 36439, 36441, // Dion
		36396, 36440, 36444, 36449, 36451, // Giran
		36397, 36438, 36442, 36443, 36446, // Oren
		36398, 36399, 36445, 36448, // Aden
		36400, 36450, // Innadril
		36401, 36447, 36453, // Goddard
		36433, 36452, 36454, // Rune
		36434, 36455, // Schuttgart
	};
	// @formatter:on
	
	public CastleAmbassador() {
		super(CastleAmbassador.class.getSimpleName(), "ai/npc");
		bindStartNpc(CASTLE_AMBASSADOR);
		bindTalk(CASTLE_AMBASSADOR);
		bindFirstTalk(CASTLE_AMBASSADOR);
		bindEventReceived(CASTLE_AMBASSADOR);
		bindSpawn(CASTLE_AMBASSADOR);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (npc != null) {
			final Fort fortresss = npc.getFort();
			String htmltext = null;
			
			switch (event) {
				case "signed": {
					if (fortresss.getFortState() == 0) {
						fortresss.setFortState(2, fortresss.getCastleIdByAmbassador(npc.getId()));
						cancelQuestTimer("DESPAWN", npc, null);
						startQuestTimer("DESPAWN", 3000, npc, null);
						htmltext = "ambassador-05.html";
					} else if (fortresss.getFortState() == 1) {
						htmltext = "ambassador-04.html";
					}
					break;
				}
				case "rejected": {
					if (fortresss.getFortState() == 0) {
						fortresss.setFortState(1, fortresss.getCastleIdByAmbassador(npc.getId()));
						cancelQuestTimer("DESPAWN", npc, null);
						startQuestTimer("DESPAWN", 3000, npc, null);
						htmltext = "ambassador-02.html";
					} else if (fortresss.getFortState() == 2) {
						htmltext = "ambassador-02.html";
					}
					break;
				}
				case "DESPAWN": {
					if (fortresss.getFortState() == 0) {
						fortresss.setFortState(1, fortresss.getCastleIdByAmbassador(npc.getId()));
					}
					cancelQuestTimer("DESPAWN", npc, null);
					npc.broadcastScriptEvent("DESPAWN", 1000);
					npc.deleteMe();
					break;
				}
			}
			
			if (htmltext != null) {
				final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
				packet.setHtml(getHtm(player.getHtmlPrefix(), htmltext));
				packet.replace("%castleName%", String.valueOf(fortresss.getCastleByAmbassador(npc.getId()).getName()));
				player.sendPacket(packet);
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onEventReceived(NpcEventReceived event) {
		if (event.receiver() != null) {
			event.receiver().deleteMe();
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final Fort fortresss = npc.getFort();
		final int fortOwner = fortresss.getOwnerClan() == null ? 0 : fortresss.getOwnerClan().getId();
		String htmltext = null;
		
		if (player.isClanLeader() && (player.getClanId() == fortOwner)) {
			htmltext = (fortresss.isBorderFortress()) ? "ambassador-01.html" : "ambassador.html";
		} else {
			htmltext = "ambassador-03.html";
		}
		
		final NpcHtmlMessage packet = new NpcHtmlMessage(npc.getObjectId());
		packet.setHtml(getHtm(player.getHtmlPrefix(), htmltext));
		packet.replace("%castleName%", String.valueOf(fortresss.getCastleByAmbassador(npc.getId()).getName()));
		player.sendPacket(packet);
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc) {
		final Castle castle = npc.getFort().getCastleByAmbassador(npc.getId());
		if (castle.getOwnerId() == 0) {
			npc.deleteMe();
		} else {
			startQuestTimer("DESPAWN", 3600000, npc, null);
		}
		return super.onSpawn(npc);
	}
}