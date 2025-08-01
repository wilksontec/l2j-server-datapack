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
package com.l2jserver.datapack.conquerablehalls.FortressOfTheDead;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Fortress of the Dead clan hall siege script.
 * @author BiggBoss
 */
public final class FortressOfTheDead extends ClanHallSiegeEngine {
	private static final int LIDIA = 35629;
	private static final int ALFRED = 35630;
	private static final int GISELLE = 35631;
	
	private static Map<Integer, Integer> _damageToLidia = new HashMap<>();
	
	public FortressOfTheDead() {
		super(FORTRESS_OF_DEAD);
		bindKill(LIDIA);
		bindKill(ALFRED);
		bindKill(GISELLE);
		
		bindSpawn(LIDIA);
		bindSpawn(ALFRED);
		bindSpawn(GISELLE);
		
		bindAttack(LIDIA);
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == LIDIA) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.HMM_THOSE_WHO_ARE_NOT_OF_THE_BLOODLINE_ARE_COMING_THIS_WAY_TO_TAKE_OVER_THE_CASTLE_HUMPH_THE_BITTER_GRUDGES_OF_THE_DEAD_YOU_MUST_NOT_MAKE_LIGHT_OF_THEIR_POWER);
		} else if (npc.getId() == ALFRED) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.HEH_HEH_I_SEE_THAT_THE_FEAST_HAS_BEGUN_BE_WARY_THE_CURSE_OF_THE_HELLMANN_FAMILY_HAS_POISONED_THIS_LAND);
		} else if (npc.getId() == GISELLE) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD_IT_IS_THE_CALLING_OF_MY_DAUGHTER_THE_FEAST_OF_BLOOD_WILL_NOW_BEGIN);
		}
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (!_hall.isInSiege()) {
			return;
		}
		
		synchronized (this) {
			final L2Clan clan = attacker.getClan();
			
			if ((clan != null) && checkIsAttacker(clan)) {
				final int id = clan.getId();
				if ((id > 0) && _damageToLidia.containsKey(id)) {
					int newDamage = _damageToLidia.get(id);
					newDamage += damage;
					_damageToLidia.put(id, newDamage);
				} else {
					_damageToLidia.put(id, damage);
				}
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (!_hall.isInSiege()) {
			return;
		}
		
		final int npcId = npc.getId();
		
		if ((npcId == ALFRED) || (npcId == GISELLE)) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL);
		}
		if (npcId == LIDIA) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.GRARR_FOR_THE_NEXT_2_MINUTES_OR_SO_THE_GAME_ARENA_ARE_WILL_BE_CLEANED_THROW_ANY_ITEMS_YOU_DONT_NEED_TO_THE_FLOOR_NOW);
			_missionAccomplished = true;
			synchronized (this) {
				cancelSiegeTask();
				endSiege();
			}
		}
	}
	
	@Override
	public L2Clan getWinner() {
		int counter = 0;
		int damagest = 0;
		for (Entry<Integer, Integer> e : _damageToLidia.entrySet()) {
			final int damage = e.getValue();
			if (damage > counter) {
				counter = damage;
				damagest = e.getKey();
			}
		}
		return ClanTable.getInstance().getClan(damagest);
	}
	
	@Override
	public void startSiege() {
		// Siege must start at night
		int hoursLeft = (GameTimeController.getInstance().getGameTime() / 60) % 24;
		
		if ((hoursLeft < 0) || (hoursLeft > 6)) {
			cancelSiegeTask();
			long scheduleTime = (24 - hoursLeft) * 10 * 60000;
			_siegeTask = ThreadPoolManager.getInstance().scheduleGeneral(new SiegeStarts(), scheduleTime);
		} else {
			super.startSiege();
		}
	}
	
	public static void main(String[] args) {
		new FortressOfTheDead();
	}
}
