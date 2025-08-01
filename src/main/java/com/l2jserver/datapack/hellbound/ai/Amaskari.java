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
package com.l2jserver.datapack.hellbound.ai;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.datapack.hellbound.HellboundEngine;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Manages Amaskari's and minions' chat and some skill usage.
 * @author GKR
 */
public final class Amaskari extends AbstractNpcAI {
	// NPCs
	private static final int AMASKARI = 22449;
	private static final int AMASKARI_PRISONER = 22450;
	// Skills
	// private static SkillHolder INVINCIBILITY = new SkillHolder(5417, 1);
	private static final int BUFF_ID = 4632;
	private static SkillHolder[] BUFF = {
		new SkillHolder(BUFF_ID, 1),
		new SkillHolder(BUFF_ID, 2),
		new SkillHolder(BUFF_ID, 3)
	};
	// Misc
	private static final NpcStringId[] AMASKARI_NPCSTRING_ID = {
		NpcStringId.ILL_MAKE_EVERYONE_FEEL_THE_SAME_SUFFERING_AS_ME,
		NpcStringId.HA_HA_YES_DIE_SLOWLY_WRITHING_IN_PAIN_AND_AGONY,
		NpcStringId.MORE_NEED_MORE_SEVERE_PAIN,
		NpcStringId.SOMETHING_IS_BURNING_INSIDE_MY_BODY
	};
	private static final NpcStringId[] MINIONS_NPCSTRING_ID = {
		NpcStringId.AHH_MY_LIFE_IS_BEING_DRAINED_OUT,
		NpcStringId.THANK_YOU_FOR_SAVING_ME,
		NpcStringId.IT_WILL_KILL_EVERYONE,
		NpcStringId.EEEK_I_FEEL_SICKYOW
	};
	
	public Amaskari() {
		bindKill(AMASKARI, AMASKARI_PRISONER);
		bindAttack(AMASKARI);
		bindSpawn(AMASKARI_PRISONER);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		if (event.equalsIgnoreCase("stop_toggle")) {
			broadcastNpcSay(npc, Say2.NPC_ALL, AMASKARI_NPCSTRING_ID[2]);
			((L2MonsterInstance) npc).clearAggroList();
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			npc.setIsInvul(false);
			// npc.doCast(INVINCIBILITY)
		} else if (event.equalsIgnoreCase("onspawn_msg") && (npc != null) && !npc.isDead()) {
			if (getRandom(100) > 20) {
				broadcastNpcSay(npc, Say2.NPC_ALL, MINIONS_NPCSTRING_ID[2]);
			} else if (getRandom(100) > 40) {
				broadcastNpcSay(npc, Say2.NPC_ALL, MINIONS_NPCSTRING_ID[3]);
			}
			startQuestTimer("onspawn_msg", (getRandom(8) + 1) * 30000, npc, null);
		}
		return null;
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if ((npc.getId() == AMASKARI) && (getRandom(1000) < 25)) {
			broadcastNpcSay(npc, Say2.NPC_ALL, AMASKARI_NPCSTRING_ID[0]);
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions()) {
				if ((minion != null) && !minion.isDead() && (getRandom(10) == 0)) {
					broadcastNpcSay(minion, Say2.NPC_ALL, MINIONS_NPCSTRING_ID[0]);
					minion.setCurrentHp(minion.getCurrentHp() - (minion.getCurrentHp() / 5));
				}
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.getId() == AMASKARI_PRISONER) {
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead()) {
				broadcastNpcSay(master, Say2.NPC_ALL, AMASKARI_NPCSTRING_ID[1]);
				final BuffInfo info = master.getEffectList().getBuffInfoBySkillId(BUFF_ID);
				if ((info != null) && (info.getSkill().getAbnormalLvl() == 3) && master.isInvul()) {
					master.setCurrentHp(master.getCurrentHp() + (master.getCurrentHp() / 5));
				} else {
					master.clearAggroList();
					master.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					if (info == null) {
						master.doCast(BUFF[0]);
					} else if (info.getSkill().getAbnormalLvl() < 3) {
						master.doCast(BUFF[info.getSkill().getAbnormalLvl()]);
					} else {
						broadcastNpcSay(master, Say2.NPC_ALL, AMASKARI_NPCSTRING_ID[3]);
						// master.doCast(INVINCIBILITY)
						master.setIsInvul(true);
						startQuestTimer("stop_toggle", 10000, master, null);
					}
				}
			}
		} else if (npc.getId() == AMASKARI) {
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions()) {
				if ((minion != null) && !minion.isDead()) {
					if (getRandom(1000) > 300) {
						broadcastNpcSay(minion, Say2.NPC_ALL, MINIONS_NPCSTRING_ID[1]);
					}
					HellboundEngine.getInstance().updateTrust(30, true);
					minion.deleteMe();
				}
			}
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		startQuestTimer("onspawn_msg", (getRandom(3) + 1) * 30000, npc, null);
	}
}