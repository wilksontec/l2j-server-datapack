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
package com.l2jserver.datapack.custom.events.Wedding;

import static com.l2jserver.gameserver.config.Configuration.customs;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.CoupleManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Couple;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Wedding AI.
 * @author Zoey76
 */
public final class Wedding extends AbstractNpcAI {
	// NPC
	private static final int MANAGER_ID = 50007;
	// Item
	private static final int FORMAL_WEAR = 6408;
	
	public Wedding() {
		bindFirstTalk(MANAGER_ID);
		bindTalk(MANAGER_ID);
		bindStartNpc(MANAGER_ID);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (player.getPartnerId() == 0) {
			return "NoPartner.html";
		}
		
		final L2PcInstance partner = L2World.getInstance().getPlayer(player.getPartnerId());
		if ((partner == null) || !partner.isOnline()) {
			return "NotFound.html";
		}
		
		if (player.isMarried()) {
			return "Already.html";
		}
		
		if (player.isMarryAccepted()) {
			return "WaitForPartner.html";
		}
		
		String htmltext = null;
		if (player.isMarryRequest()) {
			if (!isWearingFormalWear(player) || !isWearingFormalWear(partner)) {
				htmltext = sendHtml(partner, "NoFormal.html", null, null);
			} else {
				player.setMarryRequest(false);
				partner.setMarryRequest(false);
				htmltext = getHtm(player.getHtmlPrefix(), "Ask.html");
				htmltext = htmltext.replaceAll("%player%", partner.getName());
			}
			return htmltext;
		}
		
		switch (event) {
			case "ask": {
				if (!isWearingFormalWear(player) || !isWearingFormalWear(partner)) {
					htmltext = sendHtml(partner, "NoFormal.html", null, null);
				} else {
					player.setMarryAccepted(true);
					partner.setMarryRequest(true);
					
					sendHtml(partner, "Ask.html", "%player%", player.getName());
					
					htmltext = getHtm(player.getHtmlPrefix(), "Requested.html");
					htmltext = htmltext.replaceAll("%player%", partner.getName());
				}
				break;
			}
			case "accept": {
				if (!isWearingFormalWear(player) || !isWearingFormalWear(partner)) {
					htmltext = sendHtml(partner, "NoFormal.html", null, null);
				} else if ((player.getAdena() < customs().getWeddingPrice()) || (partner.getAdena() < customs().getWeddingPrice())) {
					htmltext = sendHtml(partner, "Adena.html", "%fee%", String.valueOf(customs().getWeddingPrice()));
				} else {
					player.reduceAdena("Wedding", customs().getWeddingPrice(), player.getLastFolkNPC(), true);
					partner.reduceAdena("Wedding", customs().getWeddingPrice(), player.getLastFolkNPC(), true);
					
					// Accept the wedding request
					player.setMarryAccepted(true);
					Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
					couple.marry();
					
					// Messages to the couple
					player.sendMessage("Congratulations you are married!");
					player.setMarried(true);
					player.setMarryRequest(false);
					partner.sendMessage("Congratulations you are married!");
					partner.setMarried(true);
					partner.setMarryRequest(false);
					
					// Wedding march
					player.broadcastPacket(new MagicSkillUse(player, player, 2230, 1, 1, 0));
					partner.broadcastPacket(new MagicSkillUse(partner, partner, 2230, 1, 1, 0));
					
					// Fireworks
					Skill skill = CommonSkill.LARGE_FIREWORK.getSkill();
					if (skill != null) {
						player.doCast(skill);
						partner.doCast(skill);
					}
					
					Broadcast.toAllOnlinePlayers("Congratulations to " + player.getName() + " and " + partner.getName() + "! They have been married.");
					
					htmltext = sendHtml(partner, "Accepted.html", null, null);
				}
				break;
			}
			case "decline": {
				player.setMarryRequest(false);
				partner.setMarryRequest(false);
				player.setMarryAccepted(false);
				partner.setMarryAccepted(false);
				
				player.sendMessage("You declined your partner's marriage request.");
				partner.sendMessage("Your partner declined your marriage request.");
				
				htmltext = sendHtml(partner, "Declined.html", null, null);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		final String htmltext = getHtm(player.getHtmlPrefix(), "Start.html");
		return htmltext.replaceAll("%fee%", String.valueOf(customs().getWeddingPrice()));
	}
	
	private String sendHtml(L2PcInstance player, String fileName, String regex, String replacement) {
		String html = getHtm(player.getHtmlPrefix(), fileName);
		if ((regex != null) && (replacement != null)) {
			html = html.replaceAll(regex, replacement);
		}
		player.sendPacket(new NpcHtmlMessage(html));
		return html;
	}
	
	private static boolean isWearingFormalWear(L2PcInstance player) {
		if (customs().weddingFormalWear()) {
			final L2ItemInstance formalWear = player.getChestArmorInstance();
			return (formalWear != null) && (formalWear.getId() == FORMAL_WEAR);
		}
		return true;
	}
	
	public static void main(String[] args) {
		new Wedding();
	}
}
