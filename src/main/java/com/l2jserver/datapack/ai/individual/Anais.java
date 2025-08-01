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

import java.util.ArrayList;
import java.util.Map;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.quest.QuestTimer;

/**
 * Anais AI.
 * @author nonom
 */
public final class Anais extends AbstractNpcAI {
	// NPCs
	private static final int ANAIS = 25701;
	private static final int DIVINE_BURNER = 18915;
	private static final int GRAIL_WARD = 18929;
	// Skill
	private static final SkillHolder DIVINE_NOVA = new SkillHolder(6326);
	// Instances
	ArrayList<L2Npc> _divineBurners = new ArrayList<>(4);
	private L2PcInstance _nextTarget = null;
	private L2Npc _current = null;
	private int _pot = 0;
	
	public Anais() {
		bindAttack(ANAIS);
		bindSpawn(DIVINE_BURNER);
		bindKill(GRAIL_WARD);
	}
	
	private void burnerOnAttack(int pot, L2Npc anais) {
		L2Npc npc = _divineBurners.get(pot);
		npc.setDisplayEffect(1);
		npc.setIsRunning(false);
		if (pot < 4) {
			_current = npc;
			QuestTimer checkAround = getQuestTimer("CHECK", anais, null);
			if (checkAround == null) // || !checkAround.getIsActive()
			{
				startQuestTimer("CHECK", 3000, anais, null);
			}
		} else {
			cancelQuestTimer("CHECK", anais, null);
		}
	}
	
	@Override
	public String onEvent(String event, L2Npc npc, L2PcInstance player) {
		switch (event) {
			case "CHECK": {
				if (!npc.isAttackingNow()) {
					cancelQuestTimer("CHECK", npc, null);
				}
				if ((_current != null) || (_pot < 4)) {
					final Map<Integer, L2PcInstance> players = npc.getKnownList().getKnownPlayers();
					final L2PcInstance target = players.get(getRandom(players.size() - 1));
					_nextTarget = target;
					if (_nextTarget == null) {
						_nextTarget = (L2PcInstance) npc.getTarget();
					}
					final L2Npc b = _divineBurners.get(_pot);
					_pot = _pot + 1;
					b.setDisplayEffect(1);
					b.setIsRunning(false);
					L2Npc ward = addSpawn(GRAIL_WARD, new Location(b.getX(), b.getY(), b.getZ()), true, 0);
					((L2Attackable) ward).addDamageHate(_nextTarget, 0, 999);
					ward.setIsRunning(true);
					ward.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _nextTarget, null);
					startQuestTimer("GUARD_ATTACK", 1000, ward, _nextTarget, true);
					startQuestTimer("SUICIDE", 20000, ward, null);
					ward.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _nextTarget);
				}
				break;
			}
			case "GUARD_ATTACK": {
				if (_nextTarget != null) {
					final double distance = npc.calculateDistance(_nextTarget, false, false);
					if (distance < 100) {
						npc.doCast(DIVINE_NOVA);
					} else if (distance > 2000) {
						npc.doDie(null);
						cancelQuestTimer("GUARD_ATTACK", npc, player);
					}
				}
				break;
			}
			case "SUICIDE": {
				npc.doCast(DIVINE_NOVA);
				cancelQuestTimer("GUARD_ATTACK", npc, _nextTarget);
				if (_current != null) {
					_current.setDisplayEffect(2);
					_current.setIsRunning(false);
					_current = null;
				}
				npc.doDie(null);
				break;
			}
		}
		return super.onEvent(event, npc, player);
	}
	
	@Override
	public void onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
		if (_pot == 0) {
			burnerOnAttack(0, npc);
		} else if ((npc.getCurrentHp() <= (npc.getMaxRecoverableHp() * 0.75)) && (_pot == 1)) {
			burnerOnAttack(1, npc);
		} else if ((npc.getCurrentHp() <= (npc.getMaxRecoverableHp() * 0.5)) && (_pot == 2)) {
			burnerOnAttack(2, npc);
		} else if ((npc.getCurrentHp() <= (npc.getMaxRecoverableHp() * 0.25)) && (_pot == 3)) {
			burnerOnAttack(3, npc);
		}
	}
	
	@Override
	public void onSpawn(L2Npc npc) {
		_divineBurners.add(npc);
	}
	
	@Override
	public void onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
		npc.doCast(DIVINE_NOVA.getSkill());
		cancelQuestTimer("GUARD_ATTACK", npc, _nextTarget);
		cancelQuestTimer("CHECK", npc, null);
		if (_current != null) {
			_current.setDisplayEffect(2);
			_current.setIsRunning(false);
			_current = null;
		}
	}
}
