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

import java.util.List;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcEventReceived;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcSkillFinished;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.FactionCall;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.util.Util;

/**
 * Sel Mahum Training Ground AI for squads and chefs.
 * @author GKR
 */
public final class SelMahumSquad extends AbstractNpcAI {
	// NPC's
	private static final int CHEF = 18908;
	private static final int FIRE = 18927;
	private static final int STOVE = 18933;
	
	private static final int OHS_Weapon = 15280;
	private static final int THS_Weapon = 15281;
	
	// Skills
	private static final SkillHolder SALMON_PORRIDGE_ATTACK = new SkillHolder(6330);
	private static final SkillHolder CAMP_FIRE_TIRED = new SkillHolder(6331);
	private static final SkillHolder CAMP_FIRE_FULL = new SkillHolder(6332);
	private static final SkillHolder SOUP_OF_FAILURE = new SkillHolder(6688);
	
	// Sel Mahum Squad Leaders
	private static final int[] SQUAD_LEADERS = {
		22786,
		22787,
		22788
	};
	
	private static final NpcStringId[] CHEF_FSTRINGS = {
		NpcStringId.I_BROUGHT_THE_FOOD,
		NpcStringId.COME_AND_EAT
	};
	
	private static final int FIRE_EFFECT_BURN = 1;
	private static final int FIRE_EFFECT_NONE = 2;
	
	private static final int MAHUM_EFFECT_EAT = 1;
	private static final int MAHUM_EFFECT_SLEEP = 2;
	private static final int MAHUM_EFFECT_NONE = 3;
	
	public SelMahumSquad() {
		bindAttack(CHEF);
		bindAttack(SQUAD_LEADERS);
		bindEventReceived(CHEF, FIRE, STOVE);
		bindEventReceived(SQUAD_LEADERS);
		bindFactionCall(SQUAD_LEADERS);
		bindKill(CHEF);
		bindMoveFinished(SQUAD_LEADERS);
		bindNodeArrived(CHEF);
		bindSkillSee(STOVE);
		bindSpawn(CHEF, FIRE);
		bindSpawn(SQUAD_LEADERS);
		bindSpellFinished(CHEF);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "chef_disable_reward": {
				npc.getVariables().set("REWARD_TIME_GONE", 1);
				break;
			}
			case "chef_heal_player": {
				healPlayer(npc, player);
				break;
			}
			case "chef_remove_invul": {
				if (npc.isMonster()) {
					npc.setIsInvul(false);
					npc.getVariables().remove("INVUL_REMOVE_TIMER_STARTED");
					if ((player != null) && !player.isDead() && npc.getKnownList().knowsThePlayer(player)) {
						addAttackDesire(npc, player);
					}
				}
				break;
			}
			case "chef_set_invul": {
				if (!npc.isDead()) {
					npc.setIsInvul(true);
				}
				break;
			}
			case "fire": {
				startQuestTimer("fire", 30000 + getRandom(5000), npc, null);
				npc.setDisplayEffect(FIRE_EFFECT_NONE);
				
				if (getRandom(GameTimeController.getInstance().isNight() ? 2 : 4) < 1) {
					npc.setDisplayEffect(FIRE_EFFECT_BURN); // fire burns
					npc.broadcastScriptEvent("SCE_CAMPFIRE_START", 600);
				} else {
					npc.setDisplayEffect(FIRE_EFFECT_NONE); // fire goes out
					npc.broadcastScriptEvent("SCE_CAMPFIRE_END", 600);
				}
				break;
			}
			case "fire_arrived": {
				// myself.i_quest0 = 1;
				npc.setIsRunning(false);
				npc.setTarget(npc);
				
				if (npc.isNoRndWalk()) {
					npc.doCast(CAMP_FIRE_TIRED);
					npc.setDisplayEffect(MAHUM_EFFECT_SLEEP);
				}
				if (npc.getVariables().getInt("BUSY_STATE") == 1) // Eating
				{
					npc.doCast(CAMP_FIRE_FULL);
					npc.setDisplayEffect(MAHUM_EFFECT_EAT);
				}
				
				startQuestTimer("remove_effects", 300000, npc, null);
				break;
			}
			case "notify_dinner": {
				npc.broadcastScriptEvent("SCE_DINNER_EAT", 600);
				break;
			}
			case "remove_effects": {
				// myself.i_quest0 = 0;
				npc.setIsRunning(true);
				npc.setDisplayEffect(MAHUM_EFFECT_NONE);
				break;
			}
			case "reset_full_bottle_prize": {
				npc.getVariables().remove("FULL_BARREL_REWARDING_PLAYER");
				break;
			}
			case "return_from_fire": {
				if (npc.isMonster() && !npc.isDead()) {
					((L2MonsterInstance) npc).returnHome();
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill) {
		if ((npc.getId() == CHEF) && (npc.getVariables().getInt("BUSY_STATE") == 0)) {
			if (npc.getVariables().getInt("INVUL_REMOVE_TIMER_STARTED") == 0) {
				startQuestTimer("chef_remove_invul", 180000, npc, attacker);
				startQuestTimer("chef_disable_reward", 60000, npc, null);
				npc.getVariables().set("INVUL_REMOVE_TIMER_STARTED", 1);
			}
			startQuestTimer("chef_heal_player", 1000, npc, attacker);
			startQuestTimer("chef_set_invul", 60000, npc, null);
			npc.getVariables().set("BUSY_STATE", 1);
		} else if (Util.contains(SQUAD_LEADERS, npc.getId())) {
			handlePreAttackMotion(npc);
		}
	}
	
	@Override
	public void onFactionCall(FactionCall event) {
		handlePreAttackMotion(event.npc());
	}
	
	@Override
	public void onEventReceived(NpcEventReceived event) {
		final var receiver = event.receiver();
		switch (event.eventName()) {
			case "SCE_DINNER_CHECK": {
				if (receiver.getId() == FIRE) {
					receiver.setDisplayEffect(FIRE_EFFECT_BURN);
					final L2Npc stove = addSpawn(STOVE, receiver.getX(), receiver.getY(), receiver.getZ() + 100, 0, false, 0);
					stove.setSummoner(receiver);
					startQuestTimer("notify_dinner", 2000, receiver, null); // @SCE_DINNER_EAT
					broadcastNpcSay(event.sender(), Say2.NPC_ALL, CHEF_FSTRINGS[getRandom(2)], 1250);
				}
				break;
			}
			case "SCE_CAMPFIRE_START": {
				if (!receiver.isNoRndWalk() && !receiver.isDead() && (receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && Util.contains(SQUAD_LEADERS, receiver.getId())) {
					receiver.setIsNoRndWalk(true); // Moving to fire - i_ai0 = 1
					receiver.setIsRunning(true);
					final var loc = event.sender().getPointInRange(100, 200);
					loc.setHeading(receiver.getHeading());
					receiver.stopMove(null);
					receiver.getVariables().set("DESTINATION_X", loc.getX());
					receiver.getVariables().set("DESTINATION_Y", loc.getY());
					receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
				}
				break;
			}
			case "SCE_CAMPFIRE_END": {
				if ((receiver.getId() == STOVE) && (receiver.getSummoner() == event.sender())) {
					receiver.deleteMe();
				} else if ((receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && Util.contains(SQUAD_LEADERS, receiver.getId())) {
					receiver.setIsNoRndWalk(false);
					receiver.getVariables().remove("BUSY_STATE");
					receiver.setRHandId(THS_Weapon);
					startQuestTimer("return_from_fire", 3000, receiver, null);
				}
				break;
			}
			case "SCE_DINNER_EAT": {
				if (!receiver.isDead() && (receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && (receiver.getVariables().getInt("BUSY_STATE", 0) == 0) && Util.contains(SQUAD_LEADERS, receiver.getId())) {
					if (receiver.isNoRndWalk()) // i_ai0 == 1
					{
						receiver.setRHandId(THS_Weapon);
					}
					receiver.setIsNoRndWalk(true); // Moving to fire - i_ai0 = 1
					receiver.getVariables().set("BUSY_STATE", 1); // Eating - i_ai3 = 1
					receiver.setIsRunning(true);
					broadcastNpcSay(receiver, Say2.NPC_ALL, (getRandom(3) < 1) ? NpcStringId.LOOKS_DELICIOUS : NpcStringId.LETS_GO_EAT);
					final var loc = event.sender().getPointInRange(100, 200);
					loc.setHeading(receiver.getHeading());
					receiver.stopMove(null);
					receiver.getVariables().set("DESTINATION_X", loc.getX());
					receiver.getVariables().set("DESTINATION_Y", loc.getY());
					receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
				}
				break;
			}
			case "SCE_SOUP_FAILURE": {
				if (Util.contains(SQUAD_LEADERS, receiver.getId())) {
					receiver.getVariables().set("FULL_BARREL_REWARDING_PLAYER", event.reference().getObjectId()); // TODO: Use it in 289 quest
					startQuestTimer("reset_full_bottle_prize", 180000, receiver, null);
				}
				break;
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if (npc.isMonster() && (npc.getVariables().getInt("REWARD_TIME_GONE") == 0)) {
			npc.dropItem(killer, 15492, 1);
		}
		cancelQuestTimer("chef_remove_invul", npc, null);
		cancelQuestTimer("chef_disable_reward", npc, null);
		cancelQuestTimer("chef_heal_player", npc, null);
		cancelQuestTimer("chef_set_invul", npc, null);
	}
	
	@Override
	public void onMoveFinished(L2Npc npc) {
		// Npc moves to fire
		if (npc.isNoRndWalk() && (npc.getX() == npc.getVariables().getInt("DESTINATION_X")) && (npc.getY() == npc.getVariables().getInt("DESTINATION_Y"))) {
			npc.setRHandId(OHS_Weapon);
			startQuestTimer("fire_arrived", 3000, npc, null);
		}
	}
	
	@Override
	public void onNodeArrived(L2Npc npc) {
		npc.broadcastScriptEvent("SCE_DINNER_CHECK", 300);
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if ((npc.getId() == STOVE) && (skill.getId() == 9075) && targets.contains(npc)) {
			npc.doCast(SOUP_OF_FAILURE);
			npc.broadcastScriptEvent("SCE_SOUP_FAILURE", 600, caster);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (npc.getId() == CHEF) {
			npc.setIsInvul(false);
		} else if (npc.getId() == FIRE) {
			startQuestTimer("fire", 1000, npc, null);
		} else if (Util.contains(SQUAD_LEADERS, npc.getId())) {
			npc.setDisplayEffect(3);
			npc.setIsNoRndWalk(false);
		}
	}
	
	@Override
	public void onSpellFinished(NpcSkillFinished event) {
		if ((event.skill() != null) && (event.skill().getId() == 6330)) {
			healPlayer(event.npc(), event.player());
		}
	}
	
	private void healPlayer(L2Npc npc, L2PcInstance player) {
		if ((player != null) && !player.isDead() && (npc.getVariables().getInt("INVUL_REMOVE_TIMER_STARTED") != 1) && ((npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK) || (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_CAST))) {
			npc.setTarget(player);
			npc.doCast(SALMON_PORRIDGE_ATTACK);
		} else {
			cancelQuestTimer("chef_set_invul", npc, null);
			npc.getVariables().remove("BUSY_STATE");
			npc.getVariables().remove("INVUL_REMOVE_TIMER_STARTED");
			npc.setIsRunning(false);
		}
	}
	
	private void handlePreAttackMotion(L2Npc attacked) {
		cancelQuestTimer("remove_effects", attacked, null);
		attacked.getVariables().remove("BUSY_STATE");
		attacked.setIsNoRndWalk(false);
		attacked.setDisplayEffect(MAHUM_EFFECT_NONE);
		if (attacked.getRightHandItem() == OHS_Weapon) {
			attacked.setRHandId(THS_Weapon);
		}
		// TODO: Check about i_quest0
	}
}