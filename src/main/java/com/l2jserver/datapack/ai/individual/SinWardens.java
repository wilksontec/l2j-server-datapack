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
package com.l2jserver.datapack.ai.individual;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Manages Sin Wardens disappearing and chat.
 * @author GKR
 */
public final class SinWardens extends AbstractNpcAI {
	private static final int[] SIN_WARDEN_MINIONS = {
		22424,
		22425,
		22426,
		22427,
		22428,
		22429,
		22430,
		22432,
		22433,
		22434,
		22435,
		22436,
		22437,
		22438
	};
	
	private final Map<Integer, Integer> killedMinionsCount = new ConcurrentHashMap<>();
	
	public SinWardens() {
		bindKill(SIN_WARDEN_MINIONS);
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.isMinion()) {
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead()) {
				int killedCount = killedMinionsCount.containsKey(master.getObjectId()) ? killedMinionsCount.get(master.getObjectId()) : 0;
				killedCount++;
				
				if ((killedCount) == 5) {
					master.broadcastPacket(new NpcSay(master.getObjectId(), Say2.NPC_ALL, master.getId(), NpcStringId.WE_MIGHT_NEED_NEW_SLAVES_ILL_BE_BACK_SOON_SO_WAIT));
					master.doDie(killer);
					killedMinionsCount.remove(master.getObjectId());
				} else {
					killedMinionsCount.put(master.getObjectId(), killedCount);
				}
			}
		}
	}
}
