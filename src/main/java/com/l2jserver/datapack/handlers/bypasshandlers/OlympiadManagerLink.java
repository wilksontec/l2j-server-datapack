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

import static com.l2jserver.gameserver.config.Configuration.olympiad;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.sql.impl.NpcBufferTable;
import com.l2jserver.gameserver.data.sql.impl.NpcBufferTable.NpcBufferData;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.olympiad.CompetitionType;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExHeroList;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Olympiad Manager Link bypass handler.
 * @author DS
 */
public class OlympiadManagerLink implements IBypassHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(OlympiadManagerLink.class);
	
	private static final String[] COMMANDS = {
		"olympiaddesc",
		"olympiadnoble",
		"olybuff",
		"olympiad"
	};
	
	private static final String FEWER_THAN = "Fewer than " + String.valueOf(olympiad().getRegistrationDisplayNumber());
	private static final String MORE_THAN = "More than " + String.valueOf(olympiad().getRegistrationDisplayNumber());
	
	private static final int[] BUFFS = {
		4357, // Haste Lv2
		4342, // Wind Walk Lv2
		4356, // Empower Lv3
		4355, // Acumen Lv3
		4351, // Concentration Lv6
		4345, // Might Lv3
		4358, // Guidance Lv3
		4359, // Focus Lv3
		4360, // Death Whisper Lv3
		4352, // Berserker Spirit Lv2
	};
	
	@Override
	public final boolean useBypass(String command, L2PcInstance activeChar, L2Character target) {
		if (!(target instanceof L2OlympiadManagerInstance)) {
			return false;
		}
		
		try {
			if (command.toLowerCase().startsWith("olympiaddesc")) {
				int val = Integer.parseInt(command.substring(13, 14));
				String suffix = command.substring(14);
				((L2OlympiadManagerInstance) target).showChatWindow(activeChar, val, suffix);
			} else if (command.toLowerCase().startsWith("olympiadnoble")) {
				final NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
				if (activeChar.isCursedWeaponEquipped()) {
					html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_cursed_weapon.htm");
					activeChar.sendPacket(html);
					return false;
				}
				if (activeChar.getClassIndex() != 0) {
					html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_sub.htm");
					html.replace("%objectId%", String.valueOf(target.getObjectId()));
					activeChar.sendPacket(html);
					return false;
				}
				if (!activeChar.isNoble() || (activeChar.getClassId().level() < 3)) {
					html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_thirdclass.htm");
					html.replace("%objectId%", String.valueOf(target.getObjectId()));
					activeChar.sendPacket(html);
					return false;
				}
				
				int passes;
				int val = Integer.parseInt(command.substring(14));
				switch (val) {
					case 0: // H5 match selection
						if (!OlympiadManager.getInstance().isRegistered(activeChar)) {
							html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_desc2a.htm");
							html.replace("%objectId%", String.valueOf(target.getObjectId()));
							html.replace("%olympiad_period%", String.valueOf(Olympiad.getInstance().getPeriod()));
							html.replace("%olympiad_cycle%", String.valueOf(Olympiad.getInstance().getCurrentCycle()));
							html.replace("%olympiad_opponent%", String.valueOf(OlympiadManager.getInstance().getCountOpponents()));
							activeChar.sendPacket(html);
						} else {
							html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_unregister.htm");
							html.replace("%objectId%", String.valueOf(target.getObjectId()));
							activeChar.sendPacket(html);
						}
						break;
					case 1: // unregister
						OlympiadManager.getInstance().unRegisterNoble(activeChar);
						break;
					case 2: // show waiting list | TODO: cleanup (not used anymore)
						final int nonClassed = OlympiadManager.getInstance().getRegisteredNonClassBased().size();
						final int teams = OlympiadManager.getInstance().getRegisteredTeamsBased().size();
						final Collection<List<Integer>> allClassed = OlympiadManager.getInstance().getRegisteredClassBased().values();
						int classed = 0;
						if (!allClassed.isEmpty()) {
							for (List<Integer> cls : allClassed) {
								if (cls != null) {
									classed += cls.size();
								}
							}
						}
						html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_registered.htm");
						if (olympiad().getRegistrationDisplayNumber() > 0) {
							html.replace("%listClassed%", classed < olympiad().getRegistrationDisplayNumber() ? FEWER_THAN : MORE_THAN);
							html.replace("%listNonClassedTeam%", teams < olympiad().getRegistrationDisplayNumber() ? FEWER_THAN : MORE_THAN);
							html.replace("%listNonClassed%", nonClassed < olympiad().getRegistrationDisplayNumber() ? FEWER_THAN : MORE_THAN);
						} else {
							html.replace("%listClassed%", String.valueOf(classed));
							html.replace("%listNonClassedTeam%", String.valueOf(teams));
							html.replace("%listNonClassed%", String.valueOf(nonClassed));
						}
						html.replace("%objectId%", String.valueOf(target.getObjectId()));
						activeChar.sendPacket(html);
						break;
					case 3: // There are %points% Grand Olympiad points granted for this event. | TODO: cleanup (not used anymore)
						int points = Olympiad.getInstance().getNoblePoints(activeChar.getObjectId());
						html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_points1.htm");
						html.replace("%points%", String.valueOf(points));
						html.replace("%objectId%", String.valueOf(target.getObjectId()));
						activeChar.sendPacket(html);
						break;
					case 4: // register non classed
						OlympiadManager.getInstance().registerNoble(activeChar, CompetitionType.NON_CLASSED);
						break;
					case 5: // register classed
						OlympiadManager.getInstance().registerNoble(activeChar, CompetitionType.CLASSED);
						break;
					case 6: // request tokens reward
						passes = Olympiad.getInstance().getNoblessePasses(activeChar, false);
						if (passes > 0) {
							html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_settle.htm");
							html.replace("%objectId%", String.valueOf(target.getObjectId()));
							activeChar.sendPacket(html);
						} else {
							html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_nopoints2.htm");
							html.replace("%objectId%", String.valueOf(target.getObjectId()));
							activeChar.sendPacket(html);
						}
						break;
					case 7: // Equipment Rewards
						MultisellData.getInstance().separateAndSend(102, activeChar, (L2Npc) target, false);
						break;
					case 8: // Misc. Rewards
						MultisellData.getInstance().separateAndSend(103, activeChar, (L2Npc) target, false);
						break;
					case 9: // Your Grand Olympiad Score from the previous period is %points% point(s) | TODO: cleanup (not used anymore)
						int point = Olympiad.getInstance().getLastNobleOlympiadPoints(activeChar.getObjectId());
						html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "noble_points2.htm");
						html.replace("%points%", String.valueOf(point));
						html.replace("%objectId%", String.valueOf(target.getObjectId()));
						activeChar.sendPacket(html);
						break;
					case 10: // give tokens to player
						passes = Olympiad.getInstance().getNoblessePasses(activeChar, true);
						if (passes > 0) {
							L2ItemInstance item = activeChar.getInventory().addItem("Olympiad", olympiad().getCompetitionRewardItem(), passes, activeChar, target);
							
							InventoryUpdate iu = new InventoryUpdate();
							iu.addModifiedItem(item);
							activeChar.sendPacket(iu);
							
							final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
							sm.addLong(passes);
							sm.addItemName(item);
							activeChar.sendPacket(sm);
						}
						break;
					case 11: // register team
						OlympiadManager.getInstance().registerNoble(activeChar, CompetitionType.TEAMS);
						break;
					default:
						LOG.warn("Olympiad System: Couldn't send packet for request {}", val);
						break;
				}
			} else if (command.toLowerCase().startsWith("olybuff")) {
				int buffCount = activeChar.getOlympiadBuffCount();
				if (buffCount <= 0) {
					return false;
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
				String[] params = command.split(" ");
				
				if (!Util.isDigit(params[1])) {
					LOG.warn("Olympiad Buffer Warning: npcId: {} has invalid buffGroup set in the bypass for the buff selected: {}", target.getId(), params[1]);
					return false;
				}
				
				final int index = Integer.parseInt(params[1]);
				if ((index < 0) || (index > BUFFS.length)) {
					LOG.warn("Olympiad Buffer Warning: npcId: {} has invalid index sent in the bypass: {}", target.getId(), index);
					return false;
				}
				
				final NpcBufferData npcBuffGroupInfo = NpcBufferTable.getInstance().getSkillInfo(target.getId(), BUFFS[index]);
				if (npcBuffGroupInfo == null) {
					LOG.warn("Olympiad Buffer Warning: npcId: {} {} Player: {} has tried to use skill group ({}) not assigned to the NPC Buffer!", target.getId(), target.getLocation(), activeChar, params[1]);
					return false;
				}
				
				if (buffCount > 0) {
					final Skill skill = npcBuffGroupInfo.getSkill().getSkill();
					if (skill != null) {
						target.setTarget(activeChar);
						
						activeChar.setOlympiadBuffCount(--buffCount);
						
						target.broadcastPacket(new MagicSkillUse(target, activeChar, skill.getId(), skill.getLevel(), 0, 0));
						skill.applyEffects(activeChar, activeChar);
						final L2Summon summon = activeChar.getSummon();
						if (summon != null) {
							target.broadcastPacket(new MagicSkillUse(target, summon, skill.getId(), skill.getLevel(), 0, 0));
							skill.applyEffects(summon, summon);
						}
					}
				}
				
				if (buffCount > 0) {
					html.setFile(activeChar.getHtmlPrefix(), buffCount == olympiad().getMaxBuffs() ? Olympiad.OLYMPIAD_HTML_PATH + "olympiad_buffs.htm" : Olympiad.OLYMPIAD_HTML_PATH + "olympiad_5buffs.htm");
					html.replace("%objectId%", String.valueOf(target.getObjectId()));
					activeChar.sendPacket(html);
				} else {
					html.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "olympiad_nobuffs.htm");
					html.replace("%objectId%", String.valueOf(target.getObjectId()));
					activeChar.sendPacket(html);
					target.decayMe();
				}
			} else if (command.toLowerCase().startsWith("olympiad")) {
				int val = Integer.parseInt(command.substring(9, 10));
				
				final NpcHtmlMessage reply = new NpcHtmlMessage(target.getObjectId());
				
				switch (val) {
					case 2: // show rank for a specific class
						// for example >> Olympiad 1_88
						int classId = Integer.parseInt(command.substring(11));
						if (((classId >= 88) && (classId <= 118)) || ((classId >= 131) && (classId <= 134)) || (classId == 136)) {
							List<String> names = Olympiad.getInstance().getClassLeaderBoard(classId);
							reply.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "olympiad_ranking.htm");
							
							int index = 1;
							for (String name : names) {
								reply.replace("%place" + index + "%", String.valueOf(index));
								reply.replace("%rank" + index + "%", name);
								index++;
								if (index > 10) {
									break;
								}
							}
							for (; index <= 10; index++) {
								reply.replace("%place" + index + "%", "");
								reply.replace("%rank" + index + "%", "");
							}
							
							reply.replace("%objectId%", String.valueOf(target.getObjectId()));
							activeChar.sendPacket(reply);
						}
						break;
					case 4: // hero list
						activeChar.sendPacket(new ExHeroList());
						break;
					case 5: // Hero Certification
						if (Hero.getInstance().isUnclaimedHero(activeChar.getObjectId())) {
							Hero.getInstance().claimHero(activeChar);
							reply.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "hero_receive.htm");
						} else {
							reply.setFile(activeChar.getHtmlPrefix(), Olympiad.OLYMPIAD_HTML_PATH + "hero_notreceive.htm");
						}
						reply.replace("%objectId%", String.valueOf(target.getObjectId()));
						activeChar.sendPacket(reply);
						break;
					default:
						LOG.warn("Olympiad System: Couldnt send packet for request {}!", val);
						break;
				}
			}
		} catch (Exception ex) {
			LOG.warn("Exception in processing by pass!", ex);
		}
		
		return true;
	}
	
	@Override
	public final String[] getBypassList() {
		return COMMANDS;
	}
}
