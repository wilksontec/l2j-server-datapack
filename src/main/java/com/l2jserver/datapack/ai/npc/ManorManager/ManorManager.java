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
package com.l2jserver.datapack.ai.npc.ManorManager;

import static com.l2jserver.gameserver.config.Configuration.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.instancemanager.CastleManorManager;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MerchantInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcManorBypass;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.BuyListSeed;
import com.l2jserver.gameserver.network.serverpackets.ExShowCropInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowProcureCropDetail;
import com.l2jserver.gameserver.network.serverpackets.ExShowSeedInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowSellCropList;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Manor Manager AI.
 * @author malyelfik
 */
public final class ManorManager extends AbstractNpcAI {
	
	private static final Logger LOG = LoggerFactory.getLogger(ManorManager.class);
	
	private static final int[] NPC = {
		35644,
		35645,
		35319,
		35366,
		36456,
		35512,
		35558,
		35229,
		35230,
		35231,
		35277,
		35103,
		35145,
		35187
	};
	
	public ManorManager() {
		bindStartNpc(NPC);
		bindFirstTalk(NPC);
		bindTalk(NPC);
		bindManorMenuSelected(NPC);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		switch (event) {
			case "manager-help-01.htm":
			case "manager-help-02.htm":
			case "manager-help-03.htm":
				htmltext = event;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		if (general().allowManor()) {
			final int castleId = npc.getTemplate().getParameters().getInt("manor_id", -1);
			if (!player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) && player.isClanLeader() && (castleId == player.getClan().getCastleId())) {
				return "manager-lord.htm";
			}
			return "manager.htm";
		}
		return getHtm(player.getHtmlPrefix(), "data/html/npcdefault.htm");
	}
	
	
	@Override
	public void onManorMenuSelected(NpcManorBypass evt) {
		final L2PcInstance player = evt.player();
		if (CastleManorManager.getInstance().isUnderMaintenance()) {
			player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
			return;
		}
		
		final L2Npc npc = (L2Npc) evt.target();
		final int templateId = npc.getTemplate().getParameters().getInt("manor_id", -1);
		final int castleId = (evt.manorId() == -1) ? templateId : evt.manorId();
		switch (evt.request()) {
			case 1: // Seed purchase
			{
				if (templateId != castleId) {
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR).addCastleId(templateId));
					return;
				}
				player.sendPacket(new BuyListSeed(player.getAdena(), castleId));
				break;
			}
			case 2: // Crop sales
				player.sendPacket(new ExShowSellCropList(player.getInventory(), castleId));
				break;
			case 3: // Seed info
				player.sendPacket(new ExShowSeedInfo(castleId, evt.nextPeriod(), false));
				break;
			case 4: // Crop info
				player.sendPacket(new ExShowCropInfo(castleId, evt.nextPeriod(), false));
				break;
			case 5: // Basic info
				player.sendPacket(new ExShowManorDefaultInfo(false));
				break;
			case 6: // Buy harvester
				((L2MerchantInstance) npc).showBuyWindow(player, 300000 + npc.getId());
				break;
			case 9: // Edit sales (Crop sales)
				player.sendPacket(new ExShowProcureCropDetail(evt.manorId()));
				break;
			default:
				LOG.warn("Player {} send unknown request Id {}!", player, evt.request());
		}
	}
}