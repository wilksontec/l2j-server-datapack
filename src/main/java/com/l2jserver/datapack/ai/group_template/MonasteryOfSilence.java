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
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.AttackableAggroRangeEnter;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/**
 * Monastery of Silence AI.
 * @author Kerberos, nonom
 */
public final class MonasteryOfSilence extends AbstractNpcAI {
	// NPCs
	private static final int CAPTAIN = 18910; // Solina Knight Captain
	private static final int KNIGHT = 18909; // Solina Knights
	private static final int SCARECROW = 18912; // Scarecrow
	private static final int GUIDE = 22789; // Guide Solina
	private static final int SEEKER = 22790; // Seeker Solina
	private static final int SAVIOR = 22791; // Savior Solina
	private static final int ASCETIC = 22793; // Ascetic Solina
	private static final int[] DIVINITY_CLAN = {
		22794, // Divinity Judge
		22795, // Divinity Manager
	};
	// Skills
	private static final SkillHolder ORDEAL_STRIKE = new SkillHolder(6303); // Trial of the Coup
	private static final SkillHolder LEADER_STRIKE = new SkillHolder(6304); // Shock
	private static final SkillHolder SAVER_STRIKE = new SkillHolder(6305); // Sacred Gnosis
	private static final SkillHolder SAVER_BLEED = new SkillHolder(6306); // Solina Strike
	private static final SkillHolder LEARNING_MAGIC = new SkillHolder(6308); // Opus of the Wave
	private static final SkillHolder STUDENT_CANCEL = new SkillHolder(6310); // Loss of Quest
	private static final SkillHolder WARRIOR_THRUSTING = new SkillHolder(6311); // Solina Thrust
	private static final SkillHolder KNIGHT_BLESS = new SkillHolder(6313); // Solina Bless
	// Misc
	private static final NpcStringId[] DIVINITY_MSG = {
		NpcStringId.S1_WHY_WOULD_YOU_CHOOSE_THE_PATH_OF_DARKNESS,
		NpcStringId.S1_HOW_DARE_YOU_DEFY_THE_WILL_OF_EINHASAD
	};
	private static final NpcStringId[] SOLINA_KNIGHTS_MSG = {
		NpcStringId.PUNISH_ALL_THOSE_WHO_TREAD_FOOTSTEPS_IN_THIS_PLACE,
		NpcStringId.WE_ARE_THE_SWORD_OF_TRUTH_THE_SWORD_OF_SOLINA,
		NpcStringId.WE_RAISE_OUR_BLADES_FOR_THE_GLORY_OF_SOLINA
	};
	
	public MonasteryOfSilence() {
		bindSkillSee(DIVINITY_CLAN);
		bindAttack(KNIGHT, CAPTAIN, GUIDE, SEEKER, ASCETIC);
		bindNpcHate(GUIDE, SEEKER, SAVIOR, ASCETIC);
		bindAggroRangeEnter(GUIDE, SEEKER, SAVIOR, ASCETIC);
		bindSpawn(SCARECROW);
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "TRAINING": {
				for (L2Character character : npc.getKnownList().getKnownCharactersInRadius(400)) {
					if ((getRandom(100) < 30) && character.isNpc() && !character.isDead() && !character.isInCombat()) {
						if ((character.getId() == CAPTAIN) && (getRandom(100) < 10) && npc.isScriptValue(0)) {
							final L2Npc captain = (L2Npc) character;
							broadcastNpcSay(captain, Say2.NPC_ALL, SOLINA_KNIGHTS_MSG[getRandom(SOLINA_KNIGHTS_MSG.length)]);
							captain.setScriptValue(1);
							startQuestTimer("TIMER", 10000, captain, null);
						} else if (character.getId() == KNIGHT) {
							character.setRunning();
							((L2Attackable) character).addDamageHate(npc, 0, 100);
							character.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, npc, null);
						}
					}
				}
				break;
			}
			case "DO_CAST": {
				if ((npc != null) && (player != null) && (getRandom(100) < 3)) {
					if (npc.checkDoCastConditions(STUDENT_CANCEL.getSkill())) {
						npc.setTarget(player);
						npc.doCast(STUDENT_CANCEL);
					}
					npc.setScriptValue(0);
				}
				break;
			}
			case "TIMER": {
				if (npc != null) {
					npc.setScriptValue(0);
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon) {
		final L2Attackable mob = (L2Attackable) npc;
		switch (npc.getId()) {
			case KNIGHT: {
				if ((getRandom(100) < 10) && (mob.getMostHated() == player) && mob.checkDoCastConditions(WARRIOR_THRUSTING.getSkill())) {
					npc.setTarget(player);
					npc.doCast(WARRIOR_THRUSTING);
				}
				break;
			}
			case CAPTAIN: {
				if ((getRandom(100) < 20) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5)) && npc.isScriptValue(0)) {
					if (npc.checkDoCastConditions(KNIGHT_BLESS.getSkill())) {
						npc.setTarget(npc);
						npc.doCast(KNIGHT_BLESS);
					}
					npc.setScriptValue(1);
					broadcastNpcSay(npc, Say2.ALL, NpcStringId.FOR_THE_GLORY_OF_SOLINA);
					addAttackDesire(addSpawn(KNIGHT, npc), player);
				}
				break;
			}
			case GUIDE: {
				if ((getRandom(100) < 3) && (mob.getMostHated() == player) && npc.checkDoCastConditions(ORDEAL_STRIKE.getSkill())) {
					npc.setTarget(player);
					npc.doCast(ORDEAL_STRIKE);
				}
				break;
			}
			case SEEKER: {
				if ((getRandom(100) < 33) && (mob.getMostHated() == player) && npc.checkDoCastConditions(SAVER_STRIKE.getSkill())) {
					npc.setTarget(npc);
					npc.doCast(SAVER_STRIKE);
				}
				break;
			}
			case ASCETIC: {
				if ((mob.getMostHated() == player) && npc.isScriptValue(0)) {
					npc.setScriptValue(1);
					startQuestTimer("DO_CAST", 20000, npc, player);
				}
				break;
			}
		}
	}
	
	@Override
	public boolean onNpcHate(L2Attackable mob, L2PcInstance player, boolean isSummon) {
		return player.getActiveWeaponInstance() != null;
	}
	
	@Override
	public void onAggroRangeEnter(AttackableAggroRangeEnter event) {
		final var player = event.player();
		if (player.getActiveWeaponInstance() != null) {
			final var npc = event.npc();
			SkillHolder skill = null;
			switch (npc.getId()) {
				case GUIDE: {
					if (getRandom(100) < 3) {
						skill = LEADER_STRIKE;
					}
					break;
				}
				case SEEKER: {
					skill = SAVER_BLEED;
					break;
				}
				case SAVIOR: {
					skill = LEARNING_MAGIC;
					break;
				}
				case ASCETIC: {
					if (getRandom(100) < 3) {
						skill = STUDENT_CANCEL;
					}
					
					if (npc.isScriptValue(0)) {
						npc.setScriptValue(1);
						startQuestTimer("DO_CAST", 20000, npc, player);
					}
					break;
				}
			}
			
			if ((skill != null) && npc.checkDoCastConditions(skill.getSkill())) {
				npc.setTarget(player);
				npc.doCast(skill);
			}
			
			if (!npc.isInCombat()) {
				broadcastNpcSay(npc, Say2.NPC_ALL, NpcStringId.YOU_CANNOT_CARRY_A_WEAPON_WITHOUT_AUTHORIZATION);
			}
			
			addAttackDesire(npc, player);
		}
	}
	
	@Override
	public void onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon) {
		if (skill.hasEffectType(L2EffectType.AGGRESSION)) {
			for (var obj : targets) {
				if (obj.equals(npc)) {
					broadcastNpcSay(npc, Say2.NPC_ALL, DIVINITY_MSG[getRandom(DIVINITY_MSG.length)], caster.getName());
					addAttackDesire(npc, caster);
					break;
				}
			}
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		npc.setIsInvul(true);
		npc.disableCoreAI(true);
		startQuestTimer("TRAINING", 30000, npc, null, true);
	}
}
