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

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.annotations.Id;
import com.l2jserver.gameserver.model.events.annotations.RegisterEvent;
import com.l2jserver.gameserver.model.events.annotations.RegisterType;
import com.l2jserver.gameserver.model.events.impl.character.CreatureAttacked;
import com.l2jserver.gameserver.model.events.impl.character.CreatureKill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;

/**
 * Sin Eater AI.
 * @author St3eT
 */
public final class SinEater extends AbstractNpcAI {
	// NPCs
	private static final int SIN_EATER = 12564;
	
	public SinEater() {
		bindSummonSpawn(SIN_EATER);
		bindSummonTalk(SIN_EATER);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equals("TALK") && (player != null) && (player.getSummon() != null)) {
			if (getRandom(100) < 30) {
				final int random = getRandom(100);
				final L2Summon summon = player.getSummon();
				
				if (random < 20) {
					broadcastSummonSay(summon, NpcStringId.YAWWWWN_ITS_SO_BORING_HERE_WE_SHOULD_GO_AND_FIND_SOME_ACTION);
				} else if (random < 40) {
					broadcastSummonSay(summon, NpcStringId.HEY_IF_YOU_CONTINUE_TO_WASTE_TIME_YOU_WILL_NEVER_FINISH_YOUR_PENANCE);
				} else if (random < 60) {
					broadcastSummonSay(summon, NpcStringId.I_KNOW_YOU_DONT_LIKE_ME_THE_FEELING_IS_MUTUAL);
				} else if (random < 80) {
					broadcastSummonSay(summon, NpcStringId.I_NEED_A_DRINK);
				} else {
					broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_DRAGGING_ON_TOO_LONG_AT_THIS_RATE_I_WONT_MAKE_IT_HOME_BEFORE_THE_SEVEN_SEALS_ARE_BROKEN);
				}
			}
			startQuestTimer("TALK", 60000, null, player);
		}
		return super.onEvent(event, npc, player);
	}
	
	@RegisterEvent(EventType.CREATURE_KILL)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(SIN_EATER)
	public void onCreatureKill(CreatureKill event) {
		final int random = getRandom(100);
		final L2Summon summon = (L2Summon) event.target();
		
		if (random < 30) {
			broadcastSummonSay(summon, NpcStringId.OH_THIS_IS_JUST_GREAT_WHAT_ARE_YOU_GOING_TO_DO_NOW);
		} else if (random < 70) {
			broadcastSummonSay(summon, NpcStringId.YOU_INCONSIDERATE_MORON_CANT_YOU_EVEN_TAKE_CARE_OF_LITTLE_OLD_ME);
		} else {
			broadcastSummonSay(summon, NpcStringId.OH_NO_THE_MAN_WHO_EATS_ONES_SINS_HAS_DIED_PENITENCE_IS_FURTHER_AWAY);
		}
	}
	
	@RegisterEvent(EventType.CREATURE_ATTACKED)
	@RegisterType(ListenerRegisterType.NPC)
	@Id(SIN_EATER)
	public void onCreatureAttacked(CreatureAttacked event) {
		if (getRandom(100) < 30) {
			final int random = getRandom(100);
			final L2Summon summon = (L2Summon) event.target();
			
			if (random < 35) {
				broadcastSummonSay(summon, NpcStringId.OH_THAT_SMARTS);
			} else if (random < 70) {
				broadcastSummonSay(summon, NpcStringId.HEY_MASTER_PAY_ATTENTION_IM_DYING_OVER_HERE);
			} else {
				broadcastSummonSay(summon, NpcStringId.WHAT_HAVE_I_DONE_TO_DESERVE_THIS);
			}
		}
	}
	
	@Override
	public void onSummonSpawn(L2Summon summon) {
		broadcastSummonSay(summon, getRandomBoolean() ? NpcStringId.HEY_IT_SEEMS_LIKE_YOU_NEED_MY_HELP_DOESNT_IT : NpcStringId.ALMOST_GOT_IT_OUCH_STOP_DAMN_THESE_BLOODY_MANACLES);
		startQuestTimer("TALK", 60000, null, summon.getOwner());
	}
	
	@Override
	public void onSummonTalk(L2Summon summon) {
		if (getRandom(100) < 10) {
			final int random = getRandom(100);
			
			if (random < 25) {
				broadcastSummonSay(summon, NpcStringId.USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH);
			} else if (random < 50) {
				broadcastSummonSay(summon, NpcStringId.HEY_WHAT_DO_YOU_EXPECT_OF_ME);
			} else if (random < 75) {
				broadcastSummonSay(summon, NpcStringId.UGGGGGH_PUSH_ITS_NOT_COMING_OUT);
			} else {
				broadcastSummonSay(summon, NpcStringId.AH_I_MISSED_THE_MARK);
			}
		}
	}
	
	private void broadcastSummonSay(L2Summon summon, NpcStringId npcstringId) {
		summon.broadcastPacket(new NpcSay(summon.getObjectId(), Say2.NPC_ALL, summon.getId(), npcstringId));
	}
}