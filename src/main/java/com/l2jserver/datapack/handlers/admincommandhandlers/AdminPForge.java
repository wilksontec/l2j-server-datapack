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
package com.l2jserver.datapack.handlers.admincommandhandlers;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.GameServer;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2BoatInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.L2GameClientPacket;
import com.l2jserver.gameserver.network.serverpackets.AdminForgePacket;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.mmocore.NioNetStringBuffer;

/**
 * This class handles commands for gm to forge packets
 * @author Maktakien, HorridoJoho
 */
public final class AdminPForge implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = {
		"admin_forge",
		"admin_forge_values",
		"admin_forge_send"
	};
	
	private String[] getOpCodes(StringTokenizer st) {
		Collection<String> opCodes = null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (";".equals(token)) {
				break;
			}
			
			if (opCodes == null) {
				opCodes = new LinkedList<>();
			}
			opCodes.add(token);
		}
		
		if (opCodes == null) {
			return null;
		}
		
		return opCodes.toArray(new String[opCodes.size()]);
	}
	
	private boolean validateOpCodes(String[] opCodes) {
		if ((opCodes == null) || (opCodes.length == 0) || (opCodes.length > 3)) {
			return false;
		}
		
		for (int i = 0; i < opCodes.length; ++i) {
			final String opCode = opCodes[i];
			long opCodeLong;
			try {
				opCodeLong = Long.parseLong(opCode);
			} catch (Exception e) {
				if (i > 0) {
					return true;
				}
				
				return false;
			}
			
			if (opCodeLong < 0) {
				return false;
			}
			
			if ((i == 0) && (opCodeLong > 255)) {
				return false;
			} else if ((i == 1) && (opCodeLong > 65535)) {
				return false;
			} else if ((i == 2) && (opCodeLong > 4294967295L)) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean validateFormat(String format) {
		for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
			switch (format.charAt(chIdx)) {
				case 'b':
				case 'B':
				case 'x':
				case 'X':
					// array
					break;
				case 'c':
				case 'C':
					// byte
					break;
				case 'h':
				case 'H':
					// word
					break;
				case 'd':
				case 'D':
					// dword
					break;
				case 'q':
				case 'Q':
					// qword
					break;
				case 'f':
				case 'F':
					// double
					break;
				case 's':
				case 'S':
					// string
					break;
				default:
					return false;
			}
		}
		
		return true;
	}
	
	private boolean validateMethod(String method) {
		switch (method) {
			case "sc":
			case "sb":
			case "cs":
				return true;
		}
		
		return false;
	}
	
	private void showValuesUsage(L2PcInstance activeChar) {
		activeChar.sendMessage("Usage: //forge_values opcode1[ opcode2[ opcode3]] ;[ format]");
		showMainPage(activeChar);
	}
	
	private void showSendUsage(L2PcInstance activeChar, String[] opCodes, String format) {
		activeChar.sendMessage("Usage: //forge_send sc|sb|cs opcode1[;opcode2[;opcode3]][ format value1 ... valueN] ");
		if (opCodes == null) {
			showMainPage(activeChar);
		} else {
			showValuesPage(activeChar, opCodes, format);
		}
	}
	
	private void showMainPage(L2PcInstance activeChar) {
		AdminHtml.showAdminHtml(activeChar, "pforge/main.htm");
	}
	
	private void showValuesPage(L2PcInstance activeChar, String[] opCodes, String format) {
		String sendBypass = null;
		String valuesHtml = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/pforge/values.htm");
		if (opCodes.length == 3) {
			valuesHtml = valuesHtml.replace("%opformat%", "chd");
			sendBypass = opCodes[0] + ";" + opCodes[1] + ";" + opCodes[2];
		} else if (opCodes.length == 2) {
			valuesHtml = valuesHtml.replace("%opformat%", "ch");
			sendBypass = opCodes[0] + ";" + opCodes[1];
		} else {
			valuesHtml = valuesHtml.replace("%opformat%", "c");
			sendBypass = opCodes[0];
		}
		
		valuesHtml = valuesHtml.replace("%opcodes%", sendBypass);
		
		String editorsHtml = "";
		
		if (format == null) {
			valuesHtml = valuesHtml.replace("%format%", "");
			editorsHtml = "";
		} else {
			valuesHtml = valuesHtml.replace("%format%", format);
			sendBypass += " " + format;
			
			String editorTemplate = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/pforge/inc/editor.htm");
			
			if (editorTemplate != null) {
				StringBuilder singleCharSequence = new StringBuilder(1);
				singleCharSequence.append(' ');
				
				for (int chIdx = 0; chIdx < format.length(); ++chIdx) {
					char ch = format.charAt(chIdx);
					singleCharSequence.setCharAt(0, ch);
					editorsHtml += editorTemplate.replace("%format%", singleCharSequence).replace("%editor_index%", String.valueOf(chIdx));
					sendBypass += " $v" + chIdx;
				}
			} else {
				editorsHtml = "";
			}
		}
		
		valuesHtml = valuesHtml.replace("%editors%", editorsHtml);
		valuesHtml = valuesHtml.replace("%send_bypass%", sendBypass);
		activeChar.sendPacket(new NpcHtmlMessage(valuesHtml));
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.equals("admin_forge")) {
			showMainPage(activeChar);
		} else if (command.startsWith("admin_forge_values ")) {
			try {
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken(); // skip command token
				
				if (!st.hasMoreTokens()) {
					showValuesUsage(activeChar);
					return false;
				}
				
				String[] opCodes = getOpCodes(st);
				if (!validateOpCodes(opCodes)) {
					activeChar.sendMessage("Invalid op codes!");
					showValuesUsage(activeChar);
					return false;
				}
				
				String format = null;
				if (st.hasMoreTokens()) {
					format = st.nextToken();
					if (!validateFormat(format)) {
						activeChar.sendMessage("Format invalid!");
						showValuesUsage(activeChar);
						return false;
					}
				}
				
				showValuesPage(activeChar, opCodes, format);
			} catch (Exception e) {
				e.printStackTrace();
				showValuesUsage(activeChar);
				return false;
			}
		} else if (command.startsWith("admin_forge_send ")) {
			try {
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken(); // skip command token
				
				if (!st.hasMoreTokens()) {
					showSendUsage(activeChar, null, null);
					return false;
				}
				
				String method = st.nextToken();
				if (!validateMethod(method)) {
					activeChar.sendMessage("Invalid method!");
					showSendUsage(activeChar, null, null);
					return false;
				}
				
				String[] opCodes = st.nextToken().split(";");
				if (!validateOpCodes(opCodes)) {
					activeChar.sendMessage("Invalid op codes!");
					showSendUsage(activeChar, null, null);
					return false;
				}
				
				String format = null;
				if (st.hasMoreTokens()) {
					format = st.nextToken();
					if (!validateFormat(format)) {
						activeChar.sendMessage("Format invalid!");
						showSendUsage(activeChar, null, null);
						return false;
					}
				}
				
				AdminForgePacket afp = null;
				ByteBuffer bb = null;
				for (int i = 0; i < opCodes.length; ++i) {
					char type;
					if (i == 0) {
						type = 'c';
					} else if (i == 1) {
						type = 'h';
					} else {
						type = 'd';
					}
					if (method.equals("sc") || method.equals("sb")) {
						if (afp == null) {
							afp = new AdminForgePacket();
						}
						afp.addPart((byte) type, opCodes[i]);
					} else {
						if (bb == null) {
							bb = ByteBuffer.allocate(32767);
						}
						write((byte) type, opCodes[i], bb);
					}
				}
				
				if (format != null) {
					for (int i = 0; i < format.length(); ++i) {
						if (!st.hasMoreTokens()) {
							activeChar.sendMessage("Not enough values!");
							showSendUsage(activeChar, null, null);
							return false;
						}
						
						L2Object target = null;
						L2BoatInstance boat = null;
						String value = st.nextToken();
						switch (value) {
							case "$oid":
								value = String.valueOf(activeChar.getObjectId());
								break;
							case "$boid":
								boat = activeChar.getBoat();
								if (boat != null) {
									value = String.valueOf(boat.getObjectId());
								} else {
									value = "0";
								}
								break;
							case "$title":
								value = activeChar.getTitle();
								break;
							case "$name":
								value = activeChar.getName();
								break;
							case "$x":
								value = String.valueOf(activeChar.getX());
								break;
							case "$y":
								value = String.valueOf(activeChar.getY());
								break;
							case "$z":
								value = String.valueOf(activeChar.getZ());
								break;
							case "$heading":
								value = String.valueOf(activeChar.getHeading());
								break;
							case "$toid":
								value = String.valueOf(activeChar.getTargetId());
								break;
							case "$tboid":
								target = activeChar.getTarget();
								if ((target != null) && (target instanceof L2Playable)) {
									boat = target.getActingPlayer().getBoat();
									if (boat != null) {
										value = String.valueOf(boat.getObjectId());
									} else {
										value = "0";
									}
								}
								break;
							case "$ttitle":
								target = activeChar.getTarget();
								if ((target != null) && (target instanceof L2Character)) {
									value = String.valueOf(((L2Character) target).getTitle());
								} else {
									value = "";
								}
								break;
							case "$tname":
								target = activeChar.getTarget();
								if (target != null) {
									value = String.valueOf(target.getName());
								} else {
									value = "";
								}
								break;
							case "$tx":
								target = activeChar.getTarget();
								if (target != null) {
									value = String.valueOf(target.getX());
								} else {
									value = "0";
								}
								break;
							case "$ty":
								target = activeChar.getTarget();
								if (target != null) {
									value = String.valueOf(target.getY());
								} else {
									value = "0";
								}
								break;
							case "$tz":
								target = activeChar.getTarget();
								if (target != null) {
									value = String.valueOf(target.getZ());
								} else {
									value = "0";
								}
								break;
							case "$theading":
								target = activeChar.getTarget();
								if (target != null) {
									value = String.valueOf(target.getHeading());
								} else {
									value = "0";
								}
								break;
						}
						
						if (method.equals("sc") || method.equals("sb")) {
							if (afp != null) {
								afp.addPart((byte) format.charAt(i), value);
							}
						} else {
							write((byte) format.charAt(i), value, bb);
						}
					}
				}
				
				if (method.equals("sc")) {
					activeChar.sendPacket(afp);
				} else if (method.equals("sb")) {
					activeChar.broadcastPacket(afp);
				} else if (bb != null) {
					bb.flip();
					L2GameClientPacket p = (L2GameClientPacket) GameServer.gameServer.getL2GamePacketHandler().handlePacket(bb, activeChar.getClient());
					if (p != null) {
						p.setBuffers(bb, activeChar.getClient(), new NioNetStringBuffer(2000));
						if (p.read()) {
							ThreadPoolManager.getInstance().executePacket(p);
						}
					}
				}
				
				showValuesPage(activeChar, opCodes, format);
			} catch (Exception e) {
				e.printStackTrace();
				showSendUsage(activeChar, null, null);
				return false;
			}
		}
		
		return true;
	}
	
	private boolean write(byte b, String string, ByteBuffer buf) {
		if ((b == 'C') || (b == 'c')) {
			buf.put(Byte.decode(string));
			return true;
		} else if ((b == 'D') || (b == 'd')) {
			buf.putInt(Integer.decode(string));
			return true;
		} else if ((b == 'H') || (b == 'h')) {
			buf.putShort(Short.decode(string));
			return true;
		} else if ((b == 'F') || (b == 'f')) {
			buf.putDouble(Double.parseDouble(string));
			return true;
		} else if ((b == 'S') || (b == 's')) {
			final int len = string.length();
			for (int i = 0; i < len; i++) {
				buf.putChar(string.charAt(i));
			}
			buf.putChar('\000');
			return true;
		} else if ((b == 'B') || (b == 'b') || (b == 'X') || (b == 'x')) {
			buf.put(new BigInteger(string).toByteArray());
			return true;
		} else if ((b == 'Q') || (b == 'q')) {
			buf.putLong(Long.decode(string));
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
