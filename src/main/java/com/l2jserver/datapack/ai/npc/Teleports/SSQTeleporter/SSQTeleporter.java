/*
 * Copyright © 2004-2023 L2J DataPack
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
package com.l2jserver.datapack.ai.npc.Teleports.SSQTeleporter;

import com.l2jserver.datapack.ai.npc.AbstractNpcAI;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * SSQ Teleporters
 * @author Charus
 */
public final class SSQTeleporter extends AbstractNpcAI {
	
	// NPCs
	private final static int[] NECRO_ENTER = {
		31095, 31096, 31097, 31098, 31099, 31100, 31101, 31102
	};
	
	private final static int[] NECRO_EXIT = {
		31103, 31104, 31105, 31106, 31107, 31108, 31109, 31110
	};
	
	private final static int[] CATA_ENTER = {
		31114, 31115, 31116, 31117, 31118, 31119
	};
	
	private final static int[] CATA_EXIT = {
		31120, 31121, 31122, 31123, 31124, 31125
	};

	public SSQTeleporter() {
		super(SSQTeleporter.class.getSimpleName(), "ai/npc/Teleports");
		addStartNpc(NECRO_ENTER);
		addStartNpc(NECRO_EXIT);
		addStartNpc(CATA_ENTER);
		addStartNpc(CATA_EXIT);
		addFirstTalkId(NECRO_ENTER);
		addFirstTalkId(NECRO_EXIT);
		addFirstTalkId(CATA_ENTER);
		addFirstTalkId(CATA_EXIT);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
		final SevenSigns ss = SevenSigns.getInstance();
		final int ssqPart = ss.getPlayerCabal(player.getObjectId());
		
		final int role = npc.getTemplate().getParameters().getInt("Role", 0);
		final int signNumber = npc.getTemplate().getParameters().getInt("SignNumber", 0);
		
		final int x = npc.getTemplate().getParameters().getInt("PosX");
		final int y = npc.getTemplate().getParameters().getInt("PosY");
		final int z = npc.getTemplate().getParameters().getInt("PosZ");
		
		int ask = Integer.parseInt(event.split(";")[0]);
		switch (ask) {
			case 506 -> {
				int reply = Integer.parseInt(event.split(";")[1]);
				switch (reply) {
					case 1 -> {
						if (ss.isCompetitionPeriod()) {
							if (ssqPart != SevenSigns.CABAL_NULL) {
								player.teleToLocation(x, y, z);
								player.setIsIn7sDungeon(true);
							} else {
								if (role == 1) {
									player.teleToLocation(x, y, z);
									player.setIsIn7sDungeon(false);
								} else {
									return "ss_teleporter_q0506_01.htm";
								}
							}
						} else {
							if (ss.isSealValidationPeriod() || ss.isCompResultsPeriod()) {
								if (ssqPart == SevenSigns.CABAL_NULL) {
									return "ss_teleporter_q0506_01.htm";
								}
								if (ss.getSealOwner(signNumber) == SevenSigns.CABAL_NULL) {
									player.teleToLocation(x, y, z);
									player.setIsIn7sDungeon(true);
								} else {
									if (ssqPart == ss.getSealOwner(signNumber)) {
										player.teleToLocation(x, y, z);
										player.setIsIn7sDungeon(true);
									} else {
										if (role == 1) {
											player.teleToLocation(x, y, z);
											player.setIsIn7sDungeon(false);
										} else {
											return "ss_teleporter_q0506_02.htm";
										}
									}
								}
							} else {
								player.teleToLocation(x, y, z);
								player.setIsIn7sDungeon(false);
							}
						}
					}
				}
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player) {
		return "ss_teleporter001.htm";
	}
}