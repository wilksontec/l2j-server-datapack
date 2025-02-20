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
package com.l2jserver.datapack.features.SkillTransfer;

import static com.l2jserver.gameserver.config.Configuration.general;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.data.xml.impl.ClassListData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.player.PlayerProfessionCancel;
import com.l2jserver.gameserver.model.events.impl.character.player.PlayerProfessionChange;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.Util;

/**
 * Skill Transfer feature.
 * @author Zoey76
 */
public final class SkillTransfer extends AbstractNpcAI {
	private static final String HOLY_POMANDER = "HOLY_POMANDER_";
	private static final ItemHolder[] PORMANDERS = {
		// Cardinal (97)
		new ItemHolder(15307, 1),
		// Eva's Saint (105)
		new ItemHolder(15308, 1),
		// Shillen Saint (112)
		new ItemHolder(15309, 4)
	};
	
	private SkillTransfer() {
		setPlayerProfessionChangeId(this::onProfessionChange);
		setPlayerProfessionCancelId(this::onProfessionCancel);
		setOnEnterWorld(general().skillCheckEnable());
	}
	
	public void onProfessionChange(PlayerProfessionChange event) {
		final L2PcInstance player = event.player();
		final int index = getTransferClassIndex(player);
		if (index < 0) {
			return;
		}
		
		final String name = HOLY_POMANDER + player.getClassId().getId();
		if (!player.getVariables().getBoolean(name, false)) {
			player.getVariables().set(name, true);
			giveItems(player, PORMANDERS[index]);
		}
	}
	
	public void onProfessionCancel(PlayerProfessionCancel event) {
		final L2PcInstance player = event.player();
		final int index = getTransferClassIndex(player);
		
		// is a transfer class
		if (index < 0) {
			return;
		}
		
		int pomanderId = PORMANDERS[index].getId();
		// remove unsused HolyPomander
		PcInventory inv = player.getInventory();
		for (L2ItemInstance itemI : inv.getAllItemsByItemId(pomanderId)) {
			inv.destroyItem("[HolyPomander - remove]", itemI, player, null);
		}
		// remove holy pomander variable
		final String name = HOLY_POMANDER + event.classId();
		player.getVariables().remove(name);
	}
	
	@Override
	public void onEnterWorld(L2PcInstance player) {
		if (!player.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) || general().skillCheckGM()) {
			final int index = getTransferClassIndex(player);
			if (index < 0) {
				return;
			}
			long count = PORMANDERS[index].getCount() - player.getInventory().getInventoryItemCount(PORMANDERS[index].getId(), -1, false);
			for (Skill sk : player.getAllSkills()) {
				for (L2SkillLearn s : SkillTreesData.getInstance().getTransferSkillTree(player.getClassId()).values()) {
					if (s.getSkillId() == sk.getId()) {
						// Holy Weapon allowed for Shilien Saint/Inquisitor stance
						if ((sk.getId() == 1043) && (index == 2) && player.isInStance()) {
							continue;
						}
						
						count--;
						if (count < 0) {
							final String className = ClassListData.getInstance().getClass(player.getClassId()).getClassName();
							Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " has too many transfered skills or items, skill:" + s.getName() + " (" + sk.getId() + "/" + sk.getLevel() + "), class:" + className, IllegalActionPunishmentType.BROADCAST);
							if (general().skillCheckRemove()) {
								player.removeSkill(sk);
							}
						}
					}
				}
			}
			// SkillTransfer or HolyPomander missing
			if (count > 0) {
				player.getInventory().addItem("[HolyPomander- missing]", PORMANDERS[index].getId(), count, player, null);
			}
		}
	}
	
	private static int getTransferClassIndex(L2PcInstance player) {
		return switch (player.getClassId()) {
			case cardinal -> 0;
			case evaSaint -> 1;
			case shillienSaint -> 2;
			default -> -1;
		};
	}
	
	public static void main(String[] args) {
		new SkillTransfer();
	}
}
