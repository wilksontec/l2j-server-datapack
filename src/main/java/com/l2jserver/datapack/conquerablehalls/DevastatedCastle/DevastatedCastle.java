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
package com.l2jserver.datapack.conquerablehalls.DevastatedCastle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Devastated Castle clan hall siege script.
 * @author BiggBoss
 */
public final class DevastatedCastle extends ClanHallSiegeEngine {
	private static final int GUSTAV = 35410;
	private static final int MIKHAIL = 35409;
	private static final int DIETRICH = 35408;
	private static final double GUSTAV_TRIGGER_HP = NpcData.getInstance().getTemplate(GUSTAV).getBaseHpMax() / 12;
	
	private static Map<Integer, Integer> _damageToGustav = new HashMap<>();
	
	private DevastatedCastle() {
		super(DEVASTATED_CASTLE);
		bindKill(GUSTAV);
		bindSpawn(MIKHAIL);
		bindSpawn(DIETRICH);
		bindAttack(GUSTAV);
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == MIKHAIL) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.GLORY_TO_ADEN_THE_KINGDOM_OF_THE_LION_GLORY_TO_SIR_GUSTAV_OUR_IMMORTAL_LORD);
		} else if (npc.getId() == DIETRICH) {
			broadcastNpcSay(npc, Say2.NPC_SHOUT, NpcStringId.SOLDIERS_OF_GUSTAV_GO_FORTH_AND_DESTROY_THE_INVADERS);
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
				if (_damageToGustav.containsKey(id)) {
					int newDamage = _damageToGustav.get(id);
					newDamage += damage;
					_damageToGustav.put(id, newDamage);
				} else {
					_damageToGustav.put(id, damage);
				}
			}
			
			if ((npc.getCurrentHp() < GUSTAV_TRIGGER_HP) && (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST)) {
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, SkillData.getInstance().getSkill(4235, 1), npc);
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (!_hall.isInSiege()) {
			return;
		}
		
		_missionAccomplished = true;
		
		if (npc.getId() == GUSTAV) {
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
		for (Entry<Integer, Integer> e : _damageToGustav.entrySet()) {
			final int damage = e.getValue();
			if (damage > counter) {
				counter = damage;
				damagest = e.getKey();
			}
		}
		return ClanTable.getInstance().getClan(damagest);
	}
	
	public static void main(String[] args) {
		new DevastatedCastle();
	}
}