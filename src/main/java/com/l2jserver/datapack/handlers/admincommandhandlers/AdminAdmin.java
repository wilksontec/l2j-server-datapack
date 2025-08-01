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

import static com.l2jserver.gameserver.config.Configuration.character;
import static com.l2jserver.gameserver.config.Configuration.rates;

import java.util.StringTokenizer;

import org.aeonbits.owner.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands: - admin|admin1/admin2/admin3/admin4/admin5 = slots for the 5 starting admin menus - gmliston/gmlistoff = includes/excludes active character from /gmlist results - silence = toggles private messages acceptance mode - diet = toggles weight penalty mode -
 * tradeoff = toggles trade acceptance mode - reload = reloads specified component from multisell|skill|npc|htm|item - set/set_menu/set_mod = alters specified server setting - saveolymp = saves olympiad state manually - manualhero = cycles olympiad and calculate new heroes.
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2007/07/28 10:06:06 $
 */
public class AdminAdmin implements IAdminCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AdminAdmin.class);
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_admin",
		"admin_admin1",
		"admin_admin2",
		"admin_admin3",
		"admin_admin4",
		"admin_admin5",
		"admin_admin6",
		"admin_admin7",
		"admin_gmliston",
		"admin_gmlistoff",
		"admin_silence",
		"admin_diet",
		"admin_tradeoff",
		"admin_set",
		"admin_set_mod",
		"admin_saveolymp",
		"admin_sethero",
		"admin_givehero",
		"admin_endolympiad",
		"admin_setconfig",
		"admin_config_server",
		"admin_gmon"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		if (command.startsWith("admin_admin")) {
			showMainPage(activeChar, command);
		} else if (command.equals("admin_config_server")) {
			showConfigPage(activeChar);
		} else if (command.startsWith("admin_gmliston")) {
			AdminData.getInstance().showGm(activeChar);
			activeChar.sendMessage("Registered into gm list");
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		} else if (command.startsWith("admin_gmlistoff")) {
			AdminData.getInstance().hideGm(activeChar);
			activeChar.sendMessage("Removed from gm list");
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		} else if (command.startsWith("admin_silence")) {
			if (activeChar.isSilenceMode()) // already in message refusal mode
			{
				activeChar.setSilenceMode(false);
				activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
			} else {
				activeChar.setSilenceMode(true);
				activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		} else if (command.startsWith("admin_saveolymp")) {
			Olympiad.getInstance().saveOlympiadStatus();
			activeChar.sendMessage("olympiad system saved.");
		} else if (command.startsWith("admin_endolympiad")) {
			try {
				Olympiad.getInstance().manualSelectHeroes();
			} catch (Exception e) {
				LOG.warn("An error occurred while ending olympiad: {}", e.getMessage(), e);
			}
			activeChar.sendMessage("Heroes formed.");
		} else if (command.startsWith("admin_sethero")) {
			if (activeChar.getTarget() == null) {
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			final L2PcInstance target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getActingPlayer() : activeChar;
			target.setHero(!target.isHero());
			target.broadcastUserInfo();
		} else if (command.startsWith("admin_givehero")) {
			if (activeChar.getTarget() == null) {
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			final L2PcInstance target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getActingPlayer() : activeChar;
			if (Hero.getInstance().isHero(target.getObjectId())) {
				activeChar.sendMessage("This player has already claimed the hero status.");
				return false;
			}
			
			if (!Hero.getInstance().isUnclaimedHero(target.getObjectId())) {
				activeChar.sendMessage("This player cannot claim the hero status.");
				return false;
			}
			Hero.getInstance().claimHero(target);
		} else if (command.startsWith("admin_diet")) {
			try {
				StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				if (st.nextToken().equalsIgnoreCase("on")) {
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode on");
				} else if (st.nextToken().equalsIgnoreCase("off")) {
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode off");
				}
			} catch (Exception ex) {
				if (activeChar.getDietMode()) {
					activeChar.setDietMode(false);
					activeChar.sendMessage("Diet mode off");
				} else {
					activeChar.setDietMode(true);
					activeChar.sendMessage("Diet mode on");
				}
			} finally {
				activeChar.refreshOverloaded();
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		} else if (command.startsWith("admin_tradeoff")) {
			try {
				String mode = command.substring(15);
				if (mode.equalsIgnoreCase("on")) {
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal enabled");
				} else if (mode.equalsIgnoreCase("off")) {
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal disabled");
				}
			} catch (Exception ex) {
				if (activeChar.getTradeRefusal()) {
					activeChar.setTradeRefusal(false);
					activeChar.sendMessage("Trade refusal disabled");
				} else {
					activeChar.setTradeRefusal(true);
					activeChar.sendMessage("Trade refusal enabled");
				}
			}
			AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
		} else if (command.startsWith("admin_set")) {
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			try {
				String configName = st.nextToken();
				String name = st.nextToken();
				String value = st.nextToken();
				
				final var field = Configuration.class.getDeclaredField(configName);
				if (field.getType().isInstance(Mutable.class)) {
					try {
						((Mutable) field.get(Configuration.class)).setProperty(name, value);
						activeChar.sendMessage("Set " + name + "=" + value + " in " + configName + ".");
					} catch (Exception ex) {
						activeChar.sendMessage("Failed to set " + name + "=" + value + " in " + configName + ".");
					}
				}
			} catch (Exception ex) {
				activeChar.sendMessage("Usage: //setconfig <parameter> <value>");
			} finally {
				showConfigPage(activeChar);
			}
		} else if (command.startsWith("admin_gmon")) {
			// nothing
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private void showMainPage(L2PcInstance activeChar, String command) {
		final var filename = switch (command.substring(11)) {
			case "1" -> "main";
			case "2" -> "game";
			case "3" -> "effects";
			case "4" -> "server";
			case "5" -> "mods";
			case "6" -> "char";
			case "7" -> "gm";
			default -> "main";
		};
		AdminHtml.showAdminHtml(activeChar, filename + "_menu.htm");
	}
	
	public void showConfigPage(L2PcInstance activeChar) {
		final var html = new StringBuilder("<html><title>L2J :: Config</title><body>")
			.append("<center><table width=270><tr>")
			.append("<td width=60><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>")
			.append("<td width=150>Config Server Panel</td><td width=60><button value=\"Back\" action=\"bypass -h admin_admin4\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>")
			.append("</tr></table></center><br>")
			.append("<center><table width=260><tr><td width=140></td><td width=40></td><td width=40></td></tr>")
			.append("<tr><td><font color=\"00AA00\">Drop:</font></td><td></td><td></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Rate EXP</font> = ")
			.append(rates().getRateXp())
			.append("</td>")
			.append("<td><edit var=\"param1\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig RateXp $param1\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Rate SP</font> = ")
			.append(rates().getRateSp())
			.append("</td><td><edit var=\"param2\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig RateSp $param2\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Rate Drop Spoil</font> = ")
			.append(rates().getCorpseDropChanceMultiplier())
			.append("</td><td><edit var=\"param4\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig RateDropSpoil $param4\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td width=140></td><td width=40></td><td width=40></td></tr>")
			.append("<tr><td><font color=\"00AA00\">Enchant:</font></td><td></td><td></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Enchant Element Stone</font> = ")
			.append(character().getEnchantChanceElementStone())
			.append("</td><td><edit var=\"param8\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementStone $param8\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Enchant Element Crystal</font> = ")
			.append(character().getEnchantChanceElementCrystal())
			.append("</td><td><edit var=\"param9\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementCrystal $param9\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Enchant Element Jewel</font> = ")
			.append(character().getEnchantChanceElementJewel())
			.append("</td><td><edit var=\"param10\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementJewel $param10\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("<tr><td><font color=\"LEVEL\">Enchant Element Energy</font> = ")
			.append(character().getEnchantChanceElementEnergy())
			.append("</td><td><edit var=\"param11\" width=40 height=15></td>")
			.append("<td><button value=\"Set\" action=\"bypass -h admin_setconfig EnchantChanceElementEnergy $param11\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>")
			.append("</table></body></html>")
			.toString();
		activeChar.sendPacket(new NpcHtmlMessage(html));
	}
}
