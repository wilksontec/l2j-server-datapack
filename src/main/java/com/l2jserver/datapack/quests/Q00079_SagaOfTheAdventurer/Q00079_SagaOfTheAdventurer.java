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
package com.l2jserver.datapack.quests.Q00079_SagaOfTheAdventurer;

import com.l2jserver.datapack.quests.AbstractSagaQuest;
import com.l2jserver.gameserver.model.Location;

/**
 * Saga of the Adventurer (79)
 * @author Emperorc
 */
public class Q00079_SagaOfTheAdventurer extends AbstractSagaQuest {
	public Q00079_SagaOfTheAdventurer() {
		super(79);
		_npc = new int[] {
			31603,
			31584,
			31579,
			31615,
			31619,
			31646,
			31647,
			31651,
			31654,
			31655,
			31658,
			31616
		};
		_items = new int[] {
			7080,
			7516,
			7081,
			7494,
			7277,
			7308,
			7339,
			7370,
			7401,
			7432,
			7102,
			0
		};
		_mob = new int[] {
			27299,
			27228,
			27302
		};
		_classId = new int[] {
			93
		};
		_previousClass = new int[] {
			0x08
		};
		_npcSpawnLocations = new Location[] {
			new Location(119518, -28658, -3811),
			new Location(181205, 36676, -4816),
			new Location(181215, 36676, -4812)
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
