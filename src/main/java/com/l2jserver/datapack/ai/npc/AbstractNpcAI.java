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
package com.l2jserver.datapack.ai.npc;

import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Abstract NPC AI class for datapack based AIs.
 * @author UnAfraid, Zoey76
 */
public abstract class AbstractNpcAI extends Quest {
	/**
	 * Simple on first talk event handler.
	 */
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return npc.getId() + ".html";
	}
	
	/**
	 * Registers the following events to the current script:<br>
	 * <ul>
	 * <li>ON_ATTACK</li>
	 * <li>ON_KILL</li>
	 * <li>ON_SPAWN</li>
	 * <li>ON_SPELL_FINISHED</li>
	 * <li>ON_SKILL_SEE</li>
	 * <li>ON_FACTION_CALL</li>
	 * <li>ON_AGGR_RANGE_ENTER</li>
	 * </ul>
	 * @param mobs
	 */
	public void registerMobs(int... mobs) {
		bindAttack(mobs);
		bindKill(mobs);
		bindSpawn(mobs);
		bindSpellFinished(mobs);
		bindSkillSee(mobs);
		bindAggroRangeEnter(mobs);
		bindFactionCall(mobs);
	}
	
	/**
	 * Broadcasts NpcSay packet to all known players with custom string.
	 * @param npc
	 * @param type
	 * @param text
	 */
	protected void broadcastNpcSay(L2Npc npc, int type, String text) {
		Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), text));
	}
	
	/**
	 * Broadcasts NpcSay packet to all known players with npc string id.
	 * @param npc
	 * @param type
	 * @param stringId
	 */
	protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId) {
		Broadcast.toKnownPlayers(npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId));
	}
	
	/**
	 * Broadcasts NpcSay packet to all known players with npc string id.
	 * @param npc
	 * @param type
	 * @param stringId
	 * @param parameters
	 */
	protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId, String... parameters) {
		final var say = new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId);
		if (parameters != null) {
			for (var parameter : parameters) {
				say.addStringParameter(parameter);
			}
		}
		Broadcast.toKnownPlayers(npc, say);
	}
	
	/**
	 * Broadcasts NpcSay packet to all known players with custom string in specific radius.
	 * @param npc
	 * @param type
	 * @param text
	 * @param radius
	 */
	protected void broadcastNpcSay(L2Npc npc, int type, String text, int radius) {
		Broadcast.toKnownPlayersInRadius(npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), text), radius);
	}
	
	/**
	 * Broadcasts NpcSay packet to all known players with npc string id in specific radius.
	 * @param npc
	 * @param type
	 * @param stringId
	 * @param radius
	 */
	protected void broadcastNpcSay(L2Npc npc, int type, NpcStringId stringId, int radius) {
		Broadcast.toKnownPlayersInRadius(npc, new NpcSay(npc.getObjectId(), type, npc.getTemplate().getDisplayId(), stringId), radius);
	}
	
	/**
	 * Broadcasts SocialAction packet to self and known players.
	 * @param character
	 * @param actionId
	 */
	protected void broadcastSocialAction(L2Character character, int actionId) {
		Broadcast.toSelfAndKnownPlayers(character, new SocialAction(character.getObjectId(), actionId));
	}
	
	/**
	 * Broadcasts SocialAction packet to self and known players in specific radius.
	 * @param character
	 * @param actionId
	 * @param radius
	 */
	protected void broadcastSocialAction(L2Character character, int actionId, int radius) {
		Broadcast.toSelfAndKnownPlayersInRadius(character, new SocialAction(character.getObjectId(), actionId), radius);
	}
	
	public void spawnMinions(final L2Npc npc, final String spawnName) {
		for (var is : npc.getTemplate().getParameters().getMinionList(spawnName)) {
			addMinion((L2MonsterInstance) npc, is.getId());
		}
	}
	
	/**
	 * Teleports a party to x, y, z
	 * @param npc
	 * @param party
	 * @param x
	 * @param y
	 * @param z
	 * @param maxDist
	 */
	protected void teleportParty(L2Npc npc, L2Party party, int x, int y, int z, int maxDist) {
		if (party != null) {
			for (L2PcInstance pc : party.getMembers()) {
				if (npc.calculateDistance(pc, true, false) <= maxDist) {
					pc.teleToLocation(x, y, z);
				}
			}
		}
	}
	
	/**
	 * Teleports a party to location
	 * @param npc
	 * @param party
	 * @param x
	 * @param y
	 * @param z
	 * @param maxDist
	 */
	protected void teleportParty(L2Npc npc, L2Party party, Location loc, int maxDist) {
		if (party != null) {
			for (L2PcInstance pc : party.getMembers()) {
				if (npc.calculateDistance(pc, true, false) <= maxDist) {
					pc.teleToLocation(loc);
				}
			}
		}
	}
}