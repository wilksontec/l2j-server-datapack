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
package com.l2jserver.datapack.quests.Q00073_SagaOfTheDuelist;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;

/**
 * Saga of the Duelist (73)
 * @author Emperorc
 */
public class Q00073_SagaOfTheDuelist extends AbstractSagaQuest {
	/**
	 * Third Class Transfer Quest - Duelist: The quest asks for "Top-grade Meat" which can now be acquired directly through NPC Tunatun, instead of through an additional quest from NPC Tunatun.
	 */
	private final int TUNATUN = 31537;
	private final int TOPQUALITYMEAT = 7546;
	
	public Q00073_SagaOfTheDuelist() {
		super(73);
		_npc = new int[] {
			30849,
			31624,
			31226,
			31331,
			31639,
			31646,
			31647,
			31653,
			31654,
			31655,
			31656,
			31277
		};
		_items = new int[] {
			7080,
			7537,
			7081,
			7488,
			7271,
			7302,
			7333,
			7364,
			7395,
			7426,
			7096,
			7546
		};
		_mob = new int[] {
			27289,
			27222,
			27281
		};
		_classId = new int[] {
			88
		};
		_previousClass = new int[] {
			0x02
		};
		_npcSpawnLocations = new Location[] {
			new Location(164650, -74121, -2871),
			new Location(47429, -56923, -2383),
			new Location(47391, -56929, -2370)
		};
		_text = new String[] {
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try and fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};
		registerNPCs();
		
		bindTalk(TUNATUN);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player) {
		if (npc.getId() == TUNATUN) {
			String htmltext = getNoQuestMsg(player);
			QuestState st = getQuestState(player, false);
			if ((st != null) && st.isCond(3)) {
				if (!st.hasQuestItems(TOPQUALITYMEAT)) {
					st.giveItems(TOPQUALITYMEAT, 1);
					return "tunatun_01.htm";
				}
				return "tunatun_02.htm";
			}
			return htmltext;
		}
		return super.onTalk(npc, player);
	}
}
