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
package com.l2jserver.datapack.conquerablehalls.FortressOfResistance;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.clanhall.ClanHallSiegeEngine;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * Fortress of Resistance clan hall siege Script.
 * @author BiggBoss
 */
public final class FortressOfResistance extends ClanHallSiegeEngine {
	private static final int MESSENGER = 35382;
	private static final int BLOODY_LORD_NURKA = 35375;
	
	private static final Location[] NURKA_COORDS = {
		new Location(45109, 112124, -1900), // 30%
		new Location(47653, 110816, -2110), // 40%
		new Location(47247, 109396, -2000)  // 30%
	};
	
	private L2Spawn _nurka;
	private final Map<Integer, Long> _damageToNurka = new HashMap<>();
	private NpcHtmlMessage _messengerMsg;
	
	private FortressOfResistance() {
		super(FORTRESS_RESSISTANCE);
		bindFirstTalk(MESSENGER);
		bindKill(BLOODY_LORD_NURKA);
		bindAttack(BLOODY_LORD_NURKA);
		buildMessengerMessage();
		
		try {
			_nurka = new L2Spawn(BLOODY_LORD_NURKA);
			_nurka.setAmount(1);
			_nurka.setRespawnDelay(10800);
//			@formatter:off
//			int chance = getRandom(100) + 1;
//			if (chance <= 30)
//			{
//				coords = NURKA_COORDS[0];
//			}
//			else if ((chance > 30) && (chance <= 70))
//			{
//				coords = NURKA_COORDS[1];
//			}
//			else
//			{
//				coords = NURKA_COORDS[2];
//			}
//			@formatter:on
			_nurka.setLocation(NURKA_COORDS[0]);
		} catch (Exception e) {
			_log.warn("{}: Couldnt set the Bloody Lord Nurka spawn!", getName(), e);
		}
	}
	
	private void buildMessengerMessage() {
		String html = HtmCache.getInstance().getHtm(null, "com/l2jserver/datapack/conquerablehalls/FortressOfResistance/partisan_ordery_brakel001.htm");
		if (html != null) {
			// FIXME: We don't have an object id to put in here :(
			_messengerMsg = new NpcHtmlMessage();
			_messengerMsg.setHtml(html);
			_messengerMsg.replace("%nextSiege%", Util.formatDate(_hall.getSiegeDate().getTime(), "yyyy-MM-dd HH:mm:ss"));
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		player.sendPacket(_messengerMsg);
		return null;
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon) {
		if (!_hall.isInSiege()) {
			return;
		}
		
		int clanId = player.getClanId();
		if (clanId > 0) {
			long clanDmg = (_damageToNurka.containsKey(clanId)) ? _damageToNurka.get(clanId) + damage : damage;
			_damageToNurka.put(clanId, clanDmg);
			
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (!_hall.isInSiege()) {
			return;
		}
		
		_missionAccomplished = true;
		
		synchronized (this) {
			npc.getSpawn().stopRespawn();
			npc.deleteMe();
			cancelSiegeTask();
			endSiege();
		}
	}
	
	@Override
	public L2Clan getWinner() {
		int winnerId = 0;
		long counter = 0;
		for (Entry<Integer, Long> e : _damageToNurka.entrySet()) {
			long dam = e.getValue();
			if (dam > counter) {
				winnerId = e.getKey();
				counter = dam;
			}
		}
		return ClanTable.getInstance().getClan(winnerId);
	}
	
	@Override
	public void onSiegeStarts() {
		_nurka.init();
	}
	
	@Override
	public void onSiegeEnds() {
		buildMessengerMessage();
	}
	
	public static void main(String[] args) {
		new FortressOfResistance();
	}
}