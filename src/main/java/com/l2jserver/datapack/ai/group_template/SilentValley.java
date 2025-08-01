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
package com.l2jserver.datapack.ai.group_template;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcEventReceived;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Silent Valley AI
 * @author malyelfik
 */
public final class SilentValley extends AbstractNpcAI {
	// Skills
	private static final SkillHolder BETRAYAL = new SkillHolder(6033); // Treasure Seeker's Betrayal
	private static final SkillHolder BLAZE = new SkillHolder(4157, 10); // NPC Blaze - Magic
	// Item
	private static final int SACK = 13799; // Treasure Sack of the Ancient Giants
	// Chance
	private static final int SPAWN_CHANCE = 2;
	private static final int CHEST_DIE_CHANCE = 5;
	// Monsters
	private static final int CHEST = 18693; // Treasure Chest of the Ancient Giants
	private static final int GUARD1 = 18694; // Treasure Chest Guard
	private static final int GUARD2 = 18695; // Treasure Chest Guard
	private static final int[] MOBS = {
		20965, // Chimera Piece
		20966, // Changed Creation
		20967, // Past Creature
		20968, // Nonexistent Man
		20969, // Giant's Shadow
		20970, // Soldier of Ancient Times
		20971, // Warrior of Ancient Times
		20972, // Shaman of Ancient Times
		20973, // Forgotten Ancient People
	};
	
	public SilentValley() {
		bindAttack(MOBS);
		bindAttack(CHEST, GUARD1, GUARD2);
		bindEventReceived(GUARD1, GUARD2);
		bindKill(MOBS);
		bindSeeCreature(MOBS);
		bindSeeCreature(GUARD1, GUARD2);
		bindSpawn(CHEST, GUARD2);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if ((npc != null) && !npc.isDead()) {
			switch (event) {
				case "CLEAR":
					npc.doDie(null);
					break;
				case "CLEAR_EVENT":
					npc.broadcastScriptEvent("CLEAR_ALL_INSTANT", 2000);
					npc.doDie(null);
					break;
				case "SPAWN_CHEST":
					addSpawn(CHEST, npc.getX() - 100, npc.getY(), npc.getZ() - 100, 0, false, 0);
					break;
			}
		}
		return null;
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon) {
		switch (npc.getId()) {
			case CHEST: {
				if (!isSummon && npc.isScriptValue(0)) {
					npc.setScriptValue(1);
					broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.YOU_WILL_BE_CURSED_FOR_SEEKING_THE_TREASURE);
					npc.setTarget(player);
					npc.doCast(BETRAYAL);
				} else if (isSummon || (getRandom(100) < CHEST_DIE_CHANCE)) {
					npc.dropItem(player, SACK, 1);
					npc.broadcastScriptEvent("CLEAR_ALL", 2000);
					npc.doDie(null);
					cancelQuestTimer("CLEAR_EVENT", npc, null);
				}
				break;
			}
			case GUARD1:
			case GUARD2: {
				npc.setTarget(player);
				npc.doCast(BLAZE);
				addAttackDesire(npc, player);
				break;
			}
			default: {
				if (isSummon) {
					addAttackDesire(npc, player);
				}
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (getRandom(1000) < SPAWN_CHANCE) {
			final int newZ = npc.getZ() + 100;
			addSpawn(GUARD2, npc.getX() + 100, npc.getY(), newZ, 0, false, 0);
			addSpawn(GUARD1, npc.getX() - 100, npc.getY(), newZ, 0, false, 0);
			addSpawn(GUARD1, npc.getX(), npc.getY() + 100, newZ, 0, false, 0);
			addSpawn(GUARD1, npc.getX(), npc.getY() - 100, newZ, 0, false, 0);
		}
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (creature.isPlayable()) {
			final var player = creature.getActingPlayer();
			if ((npc.getId() == GUARD1) || (npc.getId() == GUARD2)) {
				npc.setTarget(player);
				npc.doCast(BLAZE);
				addAttackDesire(npc, player);
			} else if (creature.isAffectedBySkill(BETRAYAL.getSkillId())) {
				addAttackDesire(npc, player);
			}
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == CHEST) {
			npc.setIsInvul(true);
			startQuestTimer("CLEAR_EVENT", 300000, npc, null);
		} else {
			startQuestTimer("SPAWN_CHEST", 10000, npc, null);
		}
	}
	
	@Override
	public void onEventReceived(NpcEventReceived event) {
		final var receiver = event.receiver();
		if ((receiver != null) && !receiver.isDead()) {
			switch (event.eventName()) {
				case "CLEAR_ALL":
					startQuestTimer("CLEAR", 60000, receiver, null);
					break;
				case "CLEAR_ALL_INSTANT":
					receiver.doDie(null);
					break;
			}
		}
	}
}