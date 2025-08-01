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
package com.l2jserver.datapack.quests.Q00350_EnhanceYourWeapon;

import static com.l2jserver.gameserver.config.Configuration.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.AbsorberInfo;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * Enhance Your Weapon (350)
 * @author Gigiikun
 */
public class Q00350_EnhanceYourWeapon extends Quest {
	
	private static final Logger LOG = LoggerFactory.getLogger(Q00350_EnhanceYourWeapon.class);
	
	private enum AbsorbCrystalType {
		LAST_HIT,
		FULL_PARTY,
		PARTY_ONE_RANDOM,
		PARTY_RANDOM
	}
	
	private static final class LevelingInfo {
		private final AbsorbCrystalType _absorbCrystalType;
		private final boolean _isSkillNeeded;
		private final int _chance;
		
		public LevelingInfo(AbsorbCrystalType absorbCrystalType, boolean isSkillNeeded, int chance) {
			_absorbCrystalType = absorbCrystalType;
			_isSkillNeeded = isSkillNeeded;
			_chance = chance;
		}
		
		public AbsorbCrystalType getAbsorbCrystalType() {
			return _absorbCrystalType;
		}
		
		public int getChance() {
			return _chance;
		}
		
		public boolean isSkillNeeded() {
			return _isSkillNeeded;
		}
	}
	
	private static final class SoulCrystal {
		private final int _level;
		private final int _itemId;
		private final int _leveledItemId;
		
		public SoulCrystal(int level, int itemId, int leveledItemId) {
			_level = level;
			_itemId = itemId;
			_leveledItemId = leveledItemId;
		}
		
		public int getItemId() {
			return _itemId;
		}
		
		public int getLevel() {
			return _level;
		}
		
		public int getLeveledItemId() {
			return _leveledItemId;
		}
	}
	
	// NPCs
	private static final int[] STARTING_NPCS = {
		30115,
		30856,
		30194
	};
	// Items
	private static final int RED_SOUL_CRYSTAL0_ID = 4629;
	private static final int GREEN_SOUL_CRYSTAL0_ID = 4640;
	private static final int BLUE_SOUL_CRYSTAL0_ID = 4651;
	
	private static final Map<Integer, SoulCrystal> SOUL_CRYSTALS = new HashMap<>();
	// <npcid, <level, LevelingInfo>>
	private static final Map<Integer, Map<Integer, LevelingInfo>> NPC_LEVELING_INFO = new HashMap<>();
	
	public Q00350_EnhanceYourWeapon() {
		super(350);
		bindStartNpc(STARTING_NPCS);
		bindTalk(STARTING_NPCS);
		load();
		for (int npcId : NPC_LEVELING_INFO.keySet()) {
			bindSkillSee(npcId);
			bindKill(npcId);
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		String htmltext = event;
		QuestState st = getQuestState(player, false);
		if (event.endsWith("-04.htm")) {
			st.startQuest();
		} else if (event.endsWith("-09.htm")) {
			st.giveItems(RED_SOUL_CRYSTAL0_ID, 1);
		} else if (event.endsWith("-10.htm")) {
			st.giveItems(GREEN_SOUL_CRYSTAL0_ID, 1);
		} else if (event.endsWith("-11.htm")) {
			st.giveItems(BLUE_SOUL_CRYSTAL0_ID, 1);
		} else if (event.equalsIgnoreCase("exit.htm")) {
			st.exitQuest(true);
		}
		return htmltext;
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.isAttackable() && NPC_LEVELING_INFO.containsKey(npc.getId())) {
			levelSoulCrystals((L2Attackable) npc, killer);
		}
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if ((skill == null) || (skill.getId() != 2096)) {
			return;
		}
		
		if ((caster == null) || caster.isDead()) {
			return;
		}
		
		if (!npc.isAttackable() || npc.isDead() || !NPC_LEVELING_INFO.containsKey(npc.getId())) {
			return;
		}
		((L2Attackable) npc).addAbsorber(caster);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		String htmltext = getNoQuestMsg(player);
		final QuestState st = getQuestState(player, true);
		if (st.getState() == State.CREATED) {
			st.set("cond", "0");
		}
		if (st.getInt("cond") == 0) {
			htmltext = npc.getId() + "-01.htm";
		} else if (check(st)) {
			htmltext = npc.getId() + "-03.htm";
		} else if (!st.hasQuestItems(RED_SOUL_CRYSTAL0_ID) && !st.hasQuestItems(GREEN_SOUL_CRYSTAL0_ID) && !st.hasQuestItems(BLUE_SOUL_CRYSTAL0_ID)) {
			htmltext = npc.getId() + "-21.htm";
		}
		return htmltext;
	}
	
	private static boolean check(QuestState st) {
		for (int i = 4629; i < 4665; i++) {
			if (st.hasQuestItems(i)) {
				return true;
			}
		}
		return false;
	}
	
	private static void exchangeCrystal(L2PcInstance player, L2Attackable mob, int takeid, int giveid, boolean broke) {
		L2ItemInstance Item = player.getInventory().destroyItemByItemId("SoulCrystal", takeid, 1, player, mob);
		if (Item != null) {
			// Prepare inventory update packet
			InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addRemovedItem(Item);
			
			// Add new crystal to the killer's inventory
			Item = player.getInventory().addItem("SoulCrystal", giveid, 1, player, mob);
			playerIU.addItem(Item);
			
			// Send a sound event and text message to the player
			if (broke) {
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_BROKE);
			} else {
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_SUCCEEDED);
			}
			
			// Send system message
			SystemMessage sms = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
			sms.addItemName(giveid);
			player.sendPacket(sms);
			
			// Send inventory update packet
			player.sendPacket(playerIU);
		}
	}
	
	private static SoulCrystal getSCForPlayer(L2PcInstance player) {
		final QuestState st = player.getQuestState(Q00350_EnhanceYourWeapon.class.getSimpleName());
		if ((st == null) || !st.isStarted()) {
			return null;
		}
		
		L2ItemInstance[] inv = player.getInventory().getItems();
		SoulCrystal ret = null;
		for (L2ItemInstance item : inv) {
			int itemId = item.getId();
			if (!SOUL_CRYSTALS.containsKey(itemId)) {
				continue;
			}
			
			if (ret != null) {
				return null;
			}
			ret = SOUL_CRYSTALS.get(itemId);
		}
		return ret;
	}
	
	private static boolean isPartyLevelingMonster(int npcId) {
		for (LevelingInfo li : NPC_LEVELING_INFO.get(npcId).values()) {
			if (li.getAbsorbCrystalType() != AbsorbCrystalType.LAST_HIT) {
				return true;
			}
		}
		return false;
	}
	
	private static void levelCrystal(L2PcInstance player, SoulCrystal sc, L2Attackable mob) {
		if ((sc == null) || !NPC_LEVELING_INFO.containsKey(mob.getId())) {
			return;
		}
		
		// If the crystal level is way too high for this mob, say that we can't increase it
		if (!NPC_LEVELING_INFO.get(mob.getId()).containsKey(sc.getLevel())) {
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		if (getRandom(100) <= NPC_LEVELING_INFO.get(mob.getId()).get(sc.getLevel()).getChance()) {
			exchangeCrystal(player, mob, sc.getItemId(), sc.getLeveledItemId(), false);
		} else {
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED);
		}
	}
	
	/**
	 * Calculate the leveling chance of Soul Crystals based on the attacker that killed this L2Attackable
	 * @param mob
	 * @param killer The player that last killed this L2Attackable $ Rewrite 06.12.06 - Yesod $ Rewrite 08.01.10 - Gigiikun
	 */
	public static void levelSoulCrystals(L2Attackable mob, L2PcInstance killer) {
		// Only L2PcInstance can absorb a soul
		if (killer == null) {
			mob.resetAbsorbList();
			return;
		}
		
		Map<L2PcInstance, SoulCrystal> players = new HashMap<>();
		int maxSCLevel = 0;
		
		// TODO: what if mob support last_hit + party?
		if (isPartyLevelingMonster(mob.getId()) && (killer.getParty() != null)) {
			// firts get the list of players who has one Soul Cry and the quest
			for (L2PcInstance pl : killer.getParty().getMembers()) {
				if (pl == null) {
					continue;
				}
				
				SoulCrystal sc = getSCForPlayer(pl);
				if (sc == null) {
					continue;
				}
				
				players.put(pl, sc);
				if ((maxSCLevel < sc.getLevel()) && NPC_LEVELING_INFO.get(mob.getId()).containsKey(sc.getLevel())) {
					maxSCLevel = sc.getLevel();
				}
			}
		} else {
			SoulCrystal sc = getSCForPlayer(killer);
			if (sc != null) {
				players.put(killer, sc);
				if ((maxSCLevel < sc.getLevel()) && NPC_LEVELING_INFO.get(mob.getId()).containsKey(sc.getLevel())) {
					maxSCLevel = sc.getLevel();
				}
			}
		}
		// Init some useful vars
		LevelingInfo mainlvlInfo = NPC_LEVELING_INFO.get(mob.getId()).get(maxSCLevel);
		
		if (mainlvlInfo == null) {
			/* throw new NullPointerException("Target: "+mob+ " player: "+killer+" level: "+maxSCLevel); */
			return;
		}
		
		// If this mob is not require skill, then skip some checkings
		if (mainlvlInfo.isSkillNeeded()) {
			// Fail if this L2Attackable isn't absorbed or there's no one in its _absorbersList
			if (!mob.isAbsorbed() /* || _absorbersList == null */) {
				mob.resetAbsorbList();
				return;
			}
			
			// Fail if the killer isn't in the _absorbersList of this L2Attackable and mob is not boss
			AbsorberInfo ai = mob.getAbsorbersList().get(killer.getObjectId());
			boolean isSuccess = true;
			if ((ai == null) || (ai.getObjectId() != killer.getObjectId())) {
				isSuccess = false;
			}
			
			// Check if the soul crystal was used when HP of this L2Attackable wasn't higher than half of it
			if ((ai != null) && (ai.getAbsorbedHp() > (mob.getMaxHp() / 2.0))) {
				isSuccess = false;
			}
			
			if (!isSuccess) {
				mob.resetAbsorbList();
				return;
			}
		}
		
		switch (mainlvlInfo.getAbsorbCrystalType()) {
			case PARTY_ONE_RANDOM:
				// This is a naive method for selecting a random member. It gets any random party member and
				// then checks if the member has a valid crystal. It does not select the random party member
				// among those who have crystals, only. However, this might actually be correct (same as retail).
				if (killer.getParty() != null) {
					L2PcInstance lucky = killer.getParty().getMembers().get(getRandom(killer.getParty().getMemberCount()));
					levelCrystal(lucky, players.get(lucky), mob);
				} else {
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			case PARTY_RANDOM:
				if (killer.getParty() != null) {
					List<L2PcInstance> luckyParty = new ArrayList<>();
					luckyParty.addAll(killer.getParty().getMembers());
					while ((getRandom(100) < 33) && !luckyParty.isEmpty()) {
						L2PcInstance lucky = luckyParty.remove(getRandom(luckyParty.size()));
						if (players.containsKey(lucky)) {
							levelCrystal(lucky, players.get(lucky), mob);
						}
					}
				} else if (getRandom(100) < 33) {
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			case FULL_PARTY:
				if (killer.getParty() != null) {
					for (L2PcInstance pl : killer.getParty().getMembers()) {
						levelCrystal(pl, players.get(pl), mob);
					}
				} else {
					levelCrystal(killer, players.get(killer), mob);
				}
				break;
			case LAST_HIT:
				levelCrystal(killer, players.get(killer), mob);
				break;
		}
	}
	
	/**
	 * TODO: Implement using DocumentParser.
	 */
	private static void load() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			
			File file = new File(server().getDatapackRoot(), "data/levelUpCrystalData.xml");
			if (!file.exists()) {
				LOG.error("Missing levelUpCrystalData.xml. The quest wont work without it!");
				return;
			}
			
			Document doc = factory.newDocumentBuilder().parse(file);
			Node first = doc.getFirstChild();
			if ((first != null) && "list".equalsIgnoreCase(first.getNodeName())) {
				for (Node n = first.getFirstChild(); n != null; n = n.getNextSibling()) {
					if ("crystal".equalsIgnoreCase(n.getNodeName())) {
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
							if ("item".equalsIgnoreCase(d.getNodeName())) {
								NamedNodeMap attrs = d.getAttributes();
								Node att = attrs.getNamedItem("itemId");
								if (att == null) {
									LOG.error("Missing itemId in Crystal List, skipping!");
									continue;
								}
								int itemId = Integer.parseInt(attrs.getNamedItem("itemId").getNodeValue());
								
								att = attrs.getNamedItem("level");
								if (att == null) {
									LOG.error("Missing level in Crystal List item Id {}, skipping!", itemId);
									continue;
								}
								int level = Integer.parseInt(attrs.getNamedItem("level").getNodeValue());
								
								att = attrs.getNamedItem("leveledItemId");
								if (att == null) {
									LOG.error("Missing leveledItemId in Crystal List item Id {}, skipping!", itemId);
									continue;
								}
								int leveledItemId = Integer.parseInt(attrs.getNamedItem("leveledItemId").getNodeValue());
								
								SOUL_CRYSTALS.put(itemId, new SoulCrystal(level, itemId, leveledItemId));
							}
						}
					} else if ("npc".equalsIgnoreCase(n.getNodeName())) {
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
							if ("item".equalsIgnoreCase(d.getNodeName())) {
								NamedNodeMap attrs = d.getAttributes();
								Node att = attrs.getNamedItem("npcId");
								if (att == null) {
									LOG.error("Missing npc Id in NPC List, skipping!");
									continue;
								}
								
								int npcId = Integer.parseInt(att.getNodeValue());
								Map<Integer, LevelingInfo> temp = new HashMap<>();
								for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
									boolean isSkillNeeded = false;
									int chance = 5;
									AbsorbCrystalType absorbType = AbsorbCrystalType.LAST_HIT;
									
									if ("detail".equalsIgnoreCase(cd.getNodeName())) {
										attrs = cd.getAttributes();
										
										att = attrs.getNamedItem("absorbType");
										if (att != null) {
											absorbType = Enum.valueOf(AbsorbCrystalType.class, att.getNodeValue());
										}
										
										att = attrs.getNamedItem("chance");
										if (att != null) {
											chance = Integer.parseInt(att.getNodeValue());
										}
										
										att = attrs.getNamedItem("skill");
										if (att != null) {
											isSkillNeeded = Boolean.parseBoolean(att.getNodeValue());
										}
										
										Node att1 = attrs.getNamedItem("maxLevel");
										Node att2 = attrs.getNamedItem("levelList");
										if ((att1 == null) && (att2 == null)) {
											LOG.error("Missing maxlevel/levelList in NPC List npc Id {}, skipping!", npcId);
											continue;
										}
										LevelingInfo info = new LevelingInfo(absorbType, isSkillNeeded, chance);
										if (att1 != null) {
											int maxLevel = Integer.parseInt(att1.getNodeValue());
											for (int i = 0; i <= maxLevel; i++) {
												temp.put(i, info);
											}
										} else if (att2 != null) {
											StringTokenizer st = new StringTokenizer(att2.getNodeValue(), ",");
											int tokenCount = st.countTokens();
											for (int i = 0; i < tokenCount; i++) {
												Integer value = Integer.decode(st.nextToken().trim());
												if (value == null) {
													LOG.error("Bad Level value!! npc Id {}, token {}!", npcId, i);
													value = 0;
												}
												temp.put(value, info);
											}
										}
									}
								}
								
								if (temp.isEmpty()) {
									LOG.error("No leveling info for npc Id {}, skipping!", npcId);
									continue;
								}
								NPC_LEVELING_INFO.put(npcId, temp);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			LOG.warn("Could not parse levelUpCrystalData.xml file!", ex);
		}
		LOG.info("Loaded {} soul crystal data.", SOUL_CRYSTALS.size());
		LOG.info("Loaded {} npc leveling data.", NPC_LEVELING_INFO.size());
	}
}
