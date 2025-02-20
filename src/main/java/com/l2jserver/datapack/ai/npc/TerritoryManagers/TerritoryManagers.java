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
package com.l2jserver.datapack.ai.npc.TerritoryManagers;

import static com.l2jserver.gameserver.config.Configuration.territoryWar;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.TerritoryWarManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * Retail AI for Territory Managers.
 * @author Zoey76
 * @version 2.6.1.0
 */
public final class TerritoryManagers extends AbstractNpcAI {
	
	private static final int[] PRECIOUS_SOUL_1_ITEM_IDS = {
		7587,
		7588,
		7589,
		7597,
		7598,
		7599
	};
	
	private static final int[] PRECIOUS_SOUL_2_ITEM_IDS = {
		7595
	};
	
	private static final int[] PRECIOUS_SOUL_3_ITEM_IDS = {
		7678,
		7591,
		7592,
		7593
	};
	
	public TerritoryManagers() {
		for (int i = 0; i < 9; i++) {
			bindFirstTalk(36490 + i);
			bindTalk(36490 + i);
			bindStartNpc(36490 + i);
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		// If the player does not have the second class transfer or is under level 40, it cannot continue.
		if ((player.getClassId().level() < 2) || (player.getLevel() < 40)) {
			return "36490-08.html";
		}
		return npc.getId() + ".html";
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = null;
		final int npcId = npc.getId();
		final int itemId = 13757 + (npcId - 36490);
		final int territoryId = 81 + (npcId - 36490);
		switch (event) {
			case "36490-04.html": {
				// L2J Custom for minimum badges required.
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setFile(player.getHtmlPrefix(), "com/l2jserver/datapack/ai/npc/TerritoryManagers/36490-04.html");
				html.replace("%badge%", String.valueOf(territoryWar().getMinTerritoryBadgeForNobless()));
				player.sendPacket(html);
				break;
			}
			case "BuyProducts": {
				if (player.getInventory().getItemByItemId(itemId) != null) {
					// If the player has at least one Territory Badges then show the multisell.
					final int multiSellId = 364900001 + ((npcId - 36490) * 10000);
					MultisellData.getInstance().separateAndSend(multiSellId, player, npc, false);
				} else {
					// If the player does not have Territory Badges, it cannot continue.
					htmltext = "36490-02.html";
				}
				break;
			}
			case "MakeMeNoble": {
				if (player.getInventory().getInventoryItemCount(itemId, -1) < territoryWar().getMinTerritoryBadgeForNobless()) {
					// If the player does not have enough Territory Badges, it cannot continue.
					htmltext = "36490-02.html";
				} else if (player.isNoble()) {
					// If the player is already Noblesse, it cannot continue.
					htmltext = "36490-05.html";
				} else if (player.getLevel() < 75) {
					// If the player is not level 75 or greater, it cannot continue.
					htmltext = "36490-06.html";
				} else {
					// Complete the Noblesse related quests.
					// Possessor of a Precious Soul - 1 (241)
					processNoblesseQuest(player, 241, PRECIOUS_SOUL_1_ITEM_IDS);
					// Possessor of a Precious Soul - 2 (242)
					processNoblesseQuest(player, 242, PRECIOUS_SOUL_2_ITEM_IDS);
					// Possessor of a Precious Soul - 3 (246)
					processNoblesseQuest(player, 246, PRECIOUS_SOUL_3_ITEM_IDS);
					// Possessor of a Precious Soul - 4 (247)
					processNoblesseQuest(player, 247, null);
					
					// Take the Territory Badges.
					player.destroyItemByItemId(event, itemId, territoryWar().getMinTerritoryBadgeForNobless(), npc, true);
					// Give Noblesse Tiara to the player.
					player.addItem(event, 7694, 1, npc, true);
					// Set Noblesse status to the player.
					player.setNoble(true);
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new ExBrExtraUserInfo(player));
					// Complete the sub-class related quest.
					// Complete quest Seeds of Chaos (236) for Kamael characters.
					// Complete quest Mimir's Elixir (235) for other races characters.
					final Quest q = QuestManager.getInstance().getQuest((player.getRace() == Race.KAMAEL) ? 236 : 235);
					if (q != null) {
						QuestState qs = player.getQuestState(q.getName());
						if (qs == null) {
							qs = q.newQuestState(player);
							qs.setState(State.STARTED);
						}
						// Completes the quest.
						qs.exitQuest(false);
					}
					// Remove the following items
					// Caradine's Letter
					deleteIfExist(player, 7678, event, npc);
					// Caradine's Letter
					deleteIfExist(player, 7679, event, npc);
					// Star of Destiny
					deleteIfExist(player, 5011, event, npc);
					// Virgil's Letter
					deleteIfExist(player, 1239, event, npc);
					// Arkenia's Letter
					deleteIfExist(player, 1246, event, npc);
				}
				break;
			}
			case "CalcRewards": {
				final int[] reward = TerritoryWarManager.getInstance().calcReward(player);
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				final String prefix = player.getHtmlPrefix();
				if (TerritoryWarManager.getInstance().isTWInProgress() || (reward[0] == 0)) {
					html.setFile(prefix, "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0a.html");
				} else if (reward[0] != territoryId) {
					html.setFile(prefix, "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0b.html");
					html.replace("%castle%", CastleManager.getInstance().getCastleById(reward[0] - 80).getName());
				} else if (reward[1] == 0) {
					html.setFile(prefix, "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0a.html");
				} else {
					html.setFile(prefix, "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-1.html");
					html.replace("%castle%", CastleManager.getInstance().getCastleById(reward[0] - 80).getName());
					html.replace("%badge%", String.valueOf(reward[1]));
					html.replace("%adena%", String.valueOf(reward[1] * 5000));
				}
				html.replace("%territoryId%", String.valueOf(territoryId));
				html.replace("%objectId%", String.valueOf(npc.getObjectId()));
				player.sendPacket(html);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			case "ReceiveRewards": {
				int badgeId = 57;
				if (TerritoryWarManager.TERRITORY_ITEM_IDS.containsKey(territoryId)) {
					badgeId = TerritoryWarManager.TERRITORY_ITEM_IDS.get(territoryId);
				}
				int[] reward = TerritoryWarManager.getInstance().calcReward(player);
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				if (TerritoryWarManager.getInstance().isTWInProgress() || (reward[0] == 0)) {
					html.setFile(player.getHtmlPrefix(), "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0a.html");
				} else if (reward[0] != territoryId) {
					html.setFile(player.getHtmlPrefix(), "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0b.html");
					html.replace("%castle%", CastleManager.getInstance().getCastleById(reward[0] - 80).getName());
				} else if (reward[1] == 0) {
					html.setFile(player.getHtmlPrefix(), "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-0a.html");
				} else {
					html.setFile(player.getHtmlPrefix(), "com/l2jserver/datapack/ai/npc/TerritoryManagers/reward-2.html");
					player.addItem("ReceiveRewards", badgeId, reward[1], npc, true);
					player.addAdena("ReceiveRewards", reward[1] * 5000, npc, true);
					TerritoryWarManager.getInstance().resetReward(player);
				}
				
				html.replace("%objectId%", String.valueOf(npc.getObjectId()));
				player.sendPacket(html);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				break;
			}
			default: {
				htmltext = event;
				break;
			}
		}
		return htmltext;
	}
	
	/**
	 * Complete the following quests and delete its quest specific items.
	 * @param player the active player that will be processed
	 * @param questId the quest Id of the quest that will be processed
	 * @param itemIds the item Ids should be deleted
	 */
	private static void processNoblesseQuest(L2PcInstance player, int questId, int[] itemIds) {
		final Quest q = QuestManager.getInstance().getQuest(questId);
		if (q == null) {
			return;
		}
		
		QuestState qs = player.getQuestState(q.getName());
		if (qs == null) {
			qs = q.newQuestState(player);
			qs.setState(State.STARTED);
		}
		
		if (!qs.isCompleted()) {
			// Take the quest specific items.
			if (itemIds != null) {
				for (int itemId : itemIds) {
					takeItems(player, itemId, -1);
				}
			}
			// Completes the quest.
			qs.exitQuest(false);
		}
	}
	
	/**
	 * Deletes the item if exists.
	 * @param player the player owner of the item that must be deleted
	 * @param itemId the item Id of the item that must be deleted
	 * @param event the event leading to this deletion
	 * @param npc the npc referencing this deletion
	 */
	private static void deleteIfExist(L2PcInstance player, int itemId, String event, L2Npc npc) {
		final L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
		if (item != null) {
			player.destroyItem(event, item, npc, true);
		}
	}
}
