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
package com.l2jserver.datapack.quests.Q00067_SagaOfTheDoombringer;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;

/**
 * Saga of the Doombringer (67)
 * @author Emperorc
 */
public final class Q00067_SagaOfTheDoombringer extends AbstractSagaQuest {
	public Q00067_SagaOfTheDoombringer() {
		super(67);
		_npc = new int[] {
			32138,
			31627,
			32223,
			32227,
			32254,
			31646,
			31647,
			31650,
			31654,
			31655,
			31656,
			32227
		};
		_items = new int[] {
			7080,
			9721,
			7081,
			9740,
			9722,
			9725,
			9728,
			9731,
			9734,
			9737,
			9717,
			0
		};
		_mob = new int[] {
			27324,
			27325,
			27326
		};
		_classId = new int[] {
			131
		};
		_previousClass = new int[] {
			0x7f
		};
		_npcSpawnLocations = new Location[] {
			new Location(191046, -40640, -3042),
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
	}
}
