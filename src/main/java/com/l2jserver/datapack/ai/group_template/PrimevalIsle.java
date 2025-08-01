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
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.character.npc.NpcSkillFinished;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.AttackableAggroRangeEnter;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.util.Util;

/**
 * Primeval Isle AI.
 * @author St3eT
 */
public final class PrimevalIsle extends AbstractNpcAI {
	// NPC
	private static final int EGG = 18344; // Ancient Egg
	private static final int SAILREN = 29065; // Sailren
	private static final int ORNIT = 22742; // Ornithomimus
	private static final int DEINO = 22743; // Deinonychus
	private static final int[] SPRIGNANT = {
		18345, // Sprigant (Anesthesia)
		18346, // Sprigant (Deadly Poison)
	};
	private static final int[] MONSTERS = {
		22196, // Velociraptor
		22198, // Velociraptor
		22200, // Ornithomimus
		22202, // Ornithomimus
		22203, // Deinonychus
		22205, // Deinonychus
		22208, // Pachycephalosaurus
		22210, // Pachycephalosaurus
		22211, // Wild Strider
		22213, // Wild Strider
		22223, // Velociraptor
		22224, // Ornithomimus
		22225, // Deinonychus
		22226, // Pachycephalosaurus
		22227, // Wild Strider
		22742, // Ornithomimus
		22743, // Deinonychus
	};
	private static final int[] TREX = {
		22215, // Tyrannosaurus
		22216, // Tyrannosaurus
		22217, // Tyrannosaurus
	};
	private static final int[] VEGETABLE = {
		22200, // Ornithomimus
		22201, // Ornithomimus
		22202, // Ornithomimus
		22203, // Deinonychus
		22204, // Deinonychus
		22205, // Deinonychus
		22224, // Ornithomimus
		22225, // Deinonychus
	};
	// Item
	private static final int DEINONYCHUS = 14828; // Deinonychus Mesozoic Stone
	// Skill
	private static final SkillHolder ANESTHESIA = new SkillHolder(5085, 1); // Anesthesia
	private static final SkillHolder DEADLY_POISON = new SkillHolder(5086, 1); // Deadly Poison
	private static final SkillHolder SELFBUFF1 = new SkillHolder(5087, 1); // Berserk
	private static final SkillHolder SELFBUFF2 = new SkillHolder(5087, 2); // Berserk
	private static final SkillHolder LONGRANGEDMAGIC1 = new SkillHolder(5120, 1); // Stun
	private static final SkillHolder PHYSICALSPECIAL1 = new SkillHolder(5083, 4); // Stun
	private static final SkillHolder PHYSICALSPECIAL2 = new SkillHolder(5081, 4); // Silence
	private static final SkillHolder PHYSICALSPECIAL3 = new SkillHolder(5082, 4); // NPC Spinning, Slashing Trick
	private static final SkillHolder CREW_SKILL = new SkillHolder(6172, 1); // Presentation - Tyranno
	private static final SkillHolder INVIN_BUFF_ON = new SkillHolder(5225, 1); // Invincible
	
	public PrimevalIsle() {
		bindSpawn(TREX);
		bindSpawn(SPRIGNANT);
		bindSpawn(MONSTERS);
		bindAggroRangeEnter(TREX);
		bindSpellFinished(TREX);
		bindAttack(EGG);
		bindAttack(TREX);
		bindAttack(MONSTERS);
		bindKill(EGG, SAILREN, DEINO, ORNIT);
		bindSeeCreature(TREX);
		bindSeeCreature(MONSTERS);
	}
	
	@Override
	public void onSpellFinished(NpcSkillFinished event) {
		final var npc = event.npc();
		final var skill = event.skill();
		if (skill.getId() == CREW_SKILL.getSkillId()) {
			startQuestTimer("START_INVUL", 4000, npc, null);
			final L2Npc target = (L2Npc) npc.getTarget();
			if (target != null) {
				target.doDie(npc);
			}
		}
		if (npc.isInCombat()) {
			final L2Attackable mob = (L2Attackable) npc;
			final L2Character target = mob.getMostHated();
			if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) < 60) {
				if (skill.getId() == SELFBUFF1.getSkillId()) {
					npc.setScriptValue(3);
					if ((target != null)) {
						npc.setTarget(target);
						mob.setIsRunning(true);
						mob.addDamageHate(target, 0, 555);
						mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					}
				}
			} else if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) < 30) {
				if (skill.getId() == SELFBUFF1.getSkillId()) {
					npc.setScriptValue(1);
					if ((target != null)) {
						npc.setTarget(target);
						mob.setIsRunning(true);
						mob.addDamageHate(target, 0, 555);
						mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					}
				} else if (skill.getId() == SELFBUFF2.getSkillId()) {
					npc.setScriptValue(5);
					if ((target != null)) {
						npc.setTarget(target);
						mob.setIsRunning(true);
						mob.addDamageHate(target, 0, 555);
						mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					}
				}
			}
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "USE_SKILL": {
				if ((npc != null) && !npc.isDead()) {
					npc.doCast((npc.getId() == SPRIGNANT[0] ? ANESTHESIA : DEADLY_POISON));
					startQuestTimer("USE_SKILL", 15000, npc, null);
				}
				break;
			}
			case "GHOST_DESPAWN": {
				if ((npc != null) && !npc.isDead()) {
					if (!npc.isInCombat()) {
						npc.deleteMe();
					} else {
						startQuestTimer("GHOST_DESPAWN", 1800000, npc, null);
					}
				}
				break;
			}
			case "TREX_ATTACK": {
				if ((npc != null) && (player != null)) {
					npc.setScriptValue(0);
					if (player.isInsideRadius(npc, 800, true, false)) {
						npc.setTarget(player);
						npc.doCast(LONGRANGEDMAGIC1);
						addAttackDesire(npc, player);
					}
				}
				break;
			}
			case "START_INVUL": {
				if ((npc != null) && !npc.isDead()) {
					npc.doCast(INVIN_BUFF_ON);
					startQuestTimer("START_INVUL_2", 30000, npc, null);
				}
				break;
			}
			case "START_INVUL_2": {
				if ((npc != null) && !npc.isDead()) {
					INVIN_BUFF_ON.getSkill().applyEffects(npc, npc);
				}
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onSeeCreature(L2Npc npc, L2Character creature) {
		if (Util.contains(MONSTERS, npc.getId())) {
			if (creature.isPlayer()) {
				final L2Attackable mob = (L2Attackable) npc;
				final int ag_type = npc.getTemplate().getParameters().getInt("ag_type", 0);
				final int probPhysicalSpecial1 = npc.getTemplate().getParameters().getInt("ProbPhysicalSpecial1", 0);
				final int probPhysicalSpecial2 = npc.getTemplate().getParameters().getInt("ProbPhysicalSpecial2", 0);
				final SkillHolder physicalSpecial1 = npc.getTemplate().getParameters().getObject("PhysicalSpecial1", SkillHolder.class);
				final SkillHolder physicalSpecial2 = npc.getTemplate().getParameters().getObject("PhysicalSpecial2", SkillHolder.class);
				
				if (((getRandom(100) < 30) && (npc.getId() == DEINO)) || ((npc.getId() == ORNIT) && npc.isScriptValue(0))) {
					mob.clearAggroList();
					npc.setScriptValue(1);
					npc.setRunning();
					
					final int distance = 3000;
					final int heading = Util.calculateHeadingFrom(creature, npc);
					final double angle = Util.convertHeadingToDegree(heading);
					final double radian = Math.toRadians(angle);
					final double sin = Math.sin(radian);
					final double cos = Math.cos(radian);
					final int newX = (int) (npc.getX() + (cos * distance));
					final int newY = (int) (npc.getY() + (sin * distance));
					final Location loc = GeoData.getInstance().moveCheck(npc.getX(), npc.getY(), npc.getZ(), newX, newY, npc.getZ(), npc.getInstanceId());
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc, 0);
				} else if (ag_type == 1) {
					if (getRandom(100) <= (probPhysicalSpecial1 * npc.getVariables().getInt("SKILL_MULTIPLER"))) {
						if (!npc.isSkillDisabled(physicalSpecial1.getSkillId())) {
							npc.setTarget(creature);
							npc.doCast(physicalSpecial1);
						}
					} else if (getRandom(100) <= (probPhysicalSpecial2 * npc.getVariables().getInt("SKILL_MULTIPLER"))) {
						if (!npc.isSkillDisabled(physicalSpecial2.getSkill())) {
							npc.setTarget(creature);
							npc.doCast(physicalSpecial2);
						}
					}
				}
			}
		} else if (Util.contains(VEGETABLE, creature.getId())) {
			npc.setTarget(creature);
			npc.doCast(CREW_SKILL);
			npc.setIsRunning(true);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, creature);
		}
	}
	
	@Override
	public void onAggroRangeEnter(AttackableAggroRangeEnter event) {
		final var npc = event.npc();
		if (npc.isScriptValue(0)) {
			npc.setScriptValue(1);
			broadcastNpcSay(npc, Say2.NPC_ALL, "?");
			((L2Attackable) npc).clearAggroList();
			startQuestTimer("TREX_ATTACK", 6000, npc, event.player());
		}
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (npc.getId() == EGG) {
			if ((getRandom(100) <= 80) && npc.isScriptValue(0)) {
				npc.setScriptValue(1);
				final L2Playable playable = isSummon ? attacker.getSummon() : attacker;
				for (L2Character characters : npc.getKnownList().getKnownCharactersInRadius(500)) {
					if ((characters != null) && (characters.isAttackable()) && (getRandomBoolean())) {
						L2Attackable monster = (L2Attackable) characters;
						addAttackDesire(monster, playable);
					}
				}
			}
		} else if (Util.contains(TREX, npc.getId())) {
			final L2Attackable mob = (L2Attackable) npc;
			final L2Character target = mob.getMostHated();
			
			if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 30) {
				if (npc.isScriptValue(3)) {
					if (!npc.isSkillDisabled(SELFBUFF1.getSkill())) {
						npc.doCast(SELFBUFF1);
					}
				} else if (npc.isScriptValue(1)) {
					if (!npc.isSkillDisabled(SELFBUFF2.getSkill())) {
						npc.doCast(SELFBUFF2);
					}
				}
			} else if ((((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 60) && (npc.isScriptValue(3))) {
				if (!npc.isSkillDisabled(SELFBUFF1.getSkill())) {
					npc.doCast(SELFBUFF1);
				}
			}
			
			if (Util.calculateDistance(npc, attacker, true, false) > 100) {
				if (!npc.isSkillDisabled(LONGRANGEDMAGIC1.getSkill()) && (getRandom(100) <= (10 * npc.getScriptValue()))) {
					npc.setTarget(attacker);
					npc.doCast(LONGRANGEDMAGIC1);
				}
			} else {
				if (!npc.isSkillDisabled(LONGRANGEDMAGIC1.getSkill()) && (getRandom(100) <= (10 * npc.getScriptValue()))) {
					npc.setTarget(target);
					npc.doCast(LONGRANGEDMAGIC1);
				}
				if (!npc.isSkillDisabled(PHYSICALSPECIAL1.getSkill()) && (getRandom(100) <= (5 * npc.getScriptValue()))) {
					npc.setTarget(target);
					npc.doCast(PHYSICALSPECIAL1);
				}
				if (!npc.isSkillDisabled(PHYSICALSPECIAL2.getSkill()) && (getRandom(100) <= (3 * npc.getScriptValue()))) {
					npc.setTarget(target);
					npc.doCast(PHYSICALSPECIAL2);
				}
				if (!npc.isSkillDisabled(PHYSICALSPECIAL3.getSkill()) && (getRandom(100) <= (5 * npc.getScriptValue()))) {
					npc.setTarget(target);
					npc.doCast(PHYSICALSPECIAL3);
				}
			}
		} else {
			L2Character target = null;
			final int probPhysicalSpecial1 = npc.getTemplate().getParameters().getInt("ProbPhysicalSpecial1", 0);
			final int probPhysicalSpecial2 = npc.getTemplate().getParameters().getInt("ProbPhysicalSpecial2", 0);
			final SkillHolder selfRangeBuff1 = npc.getTemplate().getParameters().getObject("SelfRangeBuff1", SkillHolder.class);
			final SkillHolder physicalSpecial1 = npc.getTemplate().getParameters().getObject("PhysicalSpecial1", SkillHolder.class);
			final SkillHolder physicalSpecial2 = npc.getTemplate().getParameters().getObject("PhysicalSpecial2", SkillHolder.class);
			
			if (((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 50) {
				npc.getVariables().set("SKILL_MULTIPLER", 2);
			} else {
				npc.getVariables().set("SKILL_MULTIPLER", 1);
			}
			
			if ((((npc.getCurrentHp() / npc.getMaxHp()) * 100) <= 30) && (npc.getVariables().getInt("SELFBUFF_USED") == 0)) {
				final L2Attackable mob = (L2Attackable) npc;
				target = mob.getMostHated();
				mob.clearAggroList();
				if (!npc.isSkillDisabled(selfRangeBuff1.getSkillId())) {
					npc.getVariables().set("SELFBUFF_USED", 1);
					npc.doCast(selfRangeBuff1);
					npc.setIsRunning(true);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
				}
			}
			
			if (target != null) {
				if (getRandom(100) <= (probPhysicalSpecial1 * npc.getVariables().getInt("SKILL_MULTIPLER"))) {
					if (!npc.isSkillDisabled(physicalSpecial1.getSkill())) {
						npc.setTarget(target);
						npc.doCast(physicalSpecial1);
					}
				}
				if (getRandom(100) <= (probPhysicalSpecial2 * npc.getVariables().getInt("SKILL_MULTIPLER"))) {
					if (!npc.isSkillDisabled(physicalSpecial2.getSkill())) {
						npc.setTarget(target);
						npc.doCast(physicalSpecial2);
					}
				}
			}
		}
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		if ((npc.getId() == DEINO) || ((npc.getId() == ORNIT) && !npc.isScriptValue(1))) {
			return;
		}
		if ((npc.getId() == SAILREN) || (getRandom(100) < 3)) {
			final L2PcInstance player = npc.getId() == SAILREN ? getRandomPartyMember(killer) : killer;
			if (player.getInventory().getSize(false) <= (player.getInventoryLimit() * 0.8)) {
				giveItems(player, DEINONYCHUS, 1);
				final L2ItemInstance summonItem = player.getInventory().getItemByItemId(DEINONYCHUS);
				final IItemHandler handler = ItemHandler.getInstance().getHandler(summonItem.getEtcItem());
				if ((handler != null) && !player.hasPet()) {
					handler.useItem(player, summonItem, true);
				}
				showOnScreenMsg(player, NpcStringId.LIFE_STONE_FROM_THE_BEGINNING_ACQUIRED, 2, 6000);
			} else {
				showOnScreenMsg(player, NpcStringId.WHEN_INVENTORY_WEIGHT_NUMBER_ARE_MORE_THAN_80_THE_LIFE_STONE_FROM_THE_BEGINNING_CANNOT_BE_ACQUIRED, 2, 6000);
			}
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		if (Util.contains(SPRIGNANT, npc.getId())) {
			startQuestTimer("USE_SKILL", 15000, npc, null);
		} else if (Util.contains(TREX, npc.getId())) {
			final int collectGhost = npc.getTemplate().getParameters().getInt("CollectGhost", 0);
			final int collectDespawn = npc.getTemplate().getParameters().getInt("CollectGhostDespawnTime", 30);
			
			if (collectGhost == 1) {
				startQuestTimer("GHOST_DESPAWN", collectDespawn * 60000, npc, null);
			}
		} else {
			npc.getVariables().set("SELFBUFF_USED", 0);
			npc.getVariables().set("SKILL_MULTIPLER", 1);
		}
	}
}