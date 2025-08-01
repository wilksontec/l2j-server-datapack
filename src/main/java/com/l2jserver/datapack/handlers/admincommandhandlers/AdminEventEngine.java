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

import static com.l2jserver.gameserver.config.Configuration.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.data.xml.impl.TransformData;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.model.entity.L2Event.EventState;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.gameserver.util.StringUtil;

/**
 * This class handles following admin commands: - admin = shows menu
 * @version $Revision: 1.3.2.1.2.4 $ $Date: 2005/04/11 10:06:06 $
 */
public class AdminEventEngine implements IAdminCommandHandler {
	
	private static final String[] ADMIN_COMMANDS = {
		"admin_event",
		"admin_event_new",
		"admin_event_choose",
		"admin_event_store",
		"admin_event_set",
		"admin_event_change_teams_number",
		"admin_event_announce",
		"admin_event_panel",
		"admin_event_control_begin",
		"admin_event_control_teleport",
		"admin_add",
		"admin_event_see",
		"admin_event_del",
		"admin_delete_buffer",
		"admin_event_control_sit",
		"admin_event_name",
		"admin_event_control_kill",
		"admin_event_control_res",
		"admin_event_control_poly",
		"admin_event_control_unpoly",
		"admin_event_control_transform",
		"admin_event_control_untransform",
		"admin_event_control_prize",
		"admin_event_control_chatban",
		"admin_event_control_kick",
		"admin_event_control_finish"
	};
	
	private static String tempBuffer = "";
	private static String tempName = "";
	private static boolean npcsDeleted = false;
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar) {
		StringTokenizer st = new StringTokenizer(command);
		String actualCommand = st.nextToken();
		try {
			if (actualCommand.equals("admin_event")) {
				if (L2Event.eventState != EventState.OFF) {
					showEventControl(activeChar);
				} else {
					showMainPage(activeChar);
				}
			} else if (actualCommand.equals("admin_event_new")) {
				showNewEventPage(activeChar);
			} else if (actualCommand.startsWith("admin_add")) {
				// There is an exception here for not using the ST. We use spaces (ST delim) for the event info.
				tempBuffer += command.substring(10);
				showNewEventPage(activeChar);
			} else if (actualCommand.startsWith("admin_event_see")) {
				// There is an exception here for not using the ST. We use spaces (ST delim) for the event name.
				String eventName = command.substring(16);
				try {
					final NpcHtmlMessage adminReply = new NpcHtmlMessage();
					
					try (FileInputStream fis = new FileInputStream(server().getDatapackRoot() + "/data/events/" + eventName);
						InputStreamReader isr = new InputStreamReader(fis);
						BufferedReader br = new BufferedReader(isr)) {
						adminReply.setFile("en", "data/html/mods/EventEngine/Participation.htm");
						adminReply.replace("%eventName%", eventName);
						adminReply.replace("%eventCreator%", br.readLine());
						adminReply.replace("%eventInfo%", br.readLine());
						adminReply.replace("npc_%objectId%_event_participate", "admin_event"); // Weird, but nice hack, isnt it? :)
						adminReply.replace("button value=\"Participate\"", "button value=\"Back\"");
						activeChar.sendPacket(adminReply);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (actualCommand.startsWith("admin_event_del")) {
				// There is an exception here for not using the ST. We use spaces (ST delim) for the event name.
				String eventName = command.substring(16);
				File file = new File(server().getDatapackRoot() + "/data/events/" + eventName);
				file.delete();
				showMainPage(activeChar);
			} else if (actualCommand.startsWith("admin_event_name")) {
				// There is an exception here for not using the ST. We use spaces (ST delim) for the event name.
				tempName += command.substring(17);
				showNewEventPage(activeChar);
			} else if (actualCommand.equalsIgnoreCase("admin_delete_buffer")) {
				tempBuffer = "";
				showNewEventPage(activeChar);
			} else if (actualCommand.startsWith("admin_event_store")) {
				try {
					try (FileOutputStream file = new FileOutputStream(new File(server().getDatapackRoot(), "data/events/" + tempName));
						PrintStream p = new PrintStream(file)) {
						p.println(activeChar.getName());
						p.println(tempBuffer);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				tempBuffer = "";
				tempName = "";
				showMainPage(activeChar);
			} else if (actualCommand.startsWith("admin_event_set")) {
				// There is an exception here for not using the ST. We use spaces (ST delim) for the event name.
				L2Event._eventName = command.substring(16);
				showEventParameters(activeChar, 2);
			} else if (actualCommand.startsWith("admin_event_change_teams_number")) {
				showEventParameters(activeChar, Integer.parseInt(st.nextToken()));
			} else if (actualCommand.startsWith("admin_event_panel")) {
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_announce")) {
				L2Event._npcId = Integer.parseInt(st.nextToken());
				L2Event._teamsNumber = Integer.parseInt(st.nextToken());
				StringBuilder temp = new StringBuilder(" ");
				String temp2 = "";
				while (st.hasMoreElements()) {
					temp.append(st.nextToken()).append(" ");
				}
				
				st = new StringTokenizer(temp.toString(), "-");
				
				int i = 1;
				
				while (st.hasMoreElements()) {
					temp2 = st.nextToken();
					if (!temp2.equals(" ")) {
						L2Event._teamNames.put(i++, temp2.substring(1, temp2.length() - 1));
					}
				}
				
				activeChar.sendMessage(L2Event.startEventParticipation());
				Broadcast.toAllOnlinePlayers(activeChar.getName() + " has started an event. You will find a participation NPC somewhere around you.");
				
				PlaySound _snd = Music.B03_F.getPacket();
				activeChar.sendPacket(_snd);
				activeChar.broadcastPacket(_snd);
				
				final NpcHtmlMessage adminReply = new NpcHtmlMessage();
				
				final String replyMSG = StringUtil.concat("<html><title>[ L2J EVENT ENGINE ]</title><body><br>", "<center>The event <font color=\"LEVEL\">", L2Event._eventName, "</font> has been announced, now you can type //event_panel to see the event panel control</center><br>", "</body></html>");
				adminReply.setHtml(replyMSG);
				activeChar.sendPacket(adminReply);
			} else if (actualCommand.startsWith("admin_event_control_begin")) {
				// Starts the event and sends a message of the result
				activeChar.sendMessage(L2Event.startEvent());
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_finish")) {
				// Finishes the event and sends a message of the result
				activeChar.sendMessage(L2Event.finishEvent());
			} else if (actualCommand.startsWith("admin_event_control_teleport")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					int teamId = Integer.parseInt(st.nextToken());
					
					for (L2PcInstance player : L2Event._teams.get(teamId)) {
						player.setTitle(L2Event._teamNames.get(teamId));
						player.teleToLocation(activeChar.getLocation(), true);
						player.setInstanceId(activeChar.getInstanceId());
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_sit")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					// Integer.parseInt(st.nextToken()) == teamId
					for (L2PcInstance player : L2Event._teams.get(Integer.parseInt(st.nextToken()))) {
						if (player.getEventStatus() == null) {
							continue;
						}
						
						player.getEventStatus().setSitForced(!player.getEventStatus().isSitForced());
						if (player.getEventStatus().isSitForced()) {
							player.sitDown();
						} else {
							player.standUp();
						}
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_kill")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					for (L2PcInstance player : L2Event._teams.get(Integer.parseInt(st.nextToken()))) {
						player.reduceCurrentHp(player.getMaxHp() + player.getMaxCp() + 1, activeChar, null);
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_res")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					for (L2PcInstance player : L2Event._teams.get(Integer.parseInt(st.nextToken()))) {
						if ((player == null) || !player.isDead()) {
							continue;
						}
						player.restoreExp(100.0);
						player.doRevive();
						player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
						player.setCurrentCp(player.getMaxCp());
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_poly")) {
				int teamId = Integer.parseInt(st.nextToken());
				String[] polyIds = new String[st.countTokens()];
				int i = 0;
				while (st.hasMoreElements()) // Every next ST should be a polymorph ID
				{
					polyIds[i++] = st.nextToken();
				}
				
				for (L2PcInstance player : L2Event._teams.get(teamId)) {
					player.getPoly().setPolyInfo("npc", polyIds[Rnd.get(polyIds.length)]);
					player.teleToLocation(player.getLocation(), true);
					CharInfo info1 = new CharInfo(player);
					player.broadcastPacket(info1);
					UserInfo info2 = new UserInfo(player);
					player.sendPacket(info2);
					player.broadcastPacket(new ExBrExtraUserInfo(player));
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_unpoly")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					for (L2PcInstance player : L2Event._teams.get(Integer.parseInt(st.nextToken()))) {
						player.getPoly().setPolyInfo(null, "1");
						player.decayMe();
						player.spawnMe(player.getX(), player.getY(), player.getZ());
						CharInfo info1 = new CharInfo(player);
						player.broadcastPacket(info1);
						UserInfo info2 = new UserInfo(player);
						player.sendPacket(info2);
						player.broadcastPacket(new ExBrExtraUserInfo(player));
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_transform")) {
				int teamId = Integer.parseInt(st.nextToken());
				int[] transIds = new int[st.countTokens()];
				int i = 0;
				while (st.hasMoreElements()) // Every next ST should be a transform ID
				{
					transIds[i++] = Integer.parseInt(st.nextToken());
				}
				
				for (L2PcInstance player : L2Event._teams.get(teamId)) {
					int transId = transIds[Rnd.get(transIds.length)];
					if (!TransformData.getInstance().transformPlayer(transId, player)) {
						AdminData.getInstance().broadcastMessageToGMs("EventEngine: Unknow transformation id: " + transId);
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_untransform")) {
				while (st.hasMoreElements()) // Every next ST should be a team number
				{
					for (L2PcInstance player : L2Event._teams.get(Integer.parseInt(st.nextToken()))) {
						player.stopTransformation(true);
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_kick")) {
				if (st.hasMoreElements()) // If has next token, it should be player name.
				{
					while (st.hasMoreElements()) {
						L2PcInstance player = L2World.getInstance().getPlayer(st.nextToken());
						if (player != null) {
							L2Event.removeAndResetPlayer(player);
						}
					}
				} else {
					if ((activeChar.getTarget() != null) && (activeChar.getTarget() instanceof L2PcInstance)) {
						L2Event.removeAndResetPlayer((L2PcInstance) activeChar.getTarget());
					}
				}
				showEventControl(activeChar);
			} else if (actualCommand.startsWith("admin_event_control_prize")) {
				int[] teamIds = new int[st.countTokens() - 2];
				int i = 0;
				while ((st.countTokens() - 2) > 0) // The last 2 tokens are used for "n" and "item id"
				{
					teamIds[i++] = Integer.parseInt(st.nextToken());
				}
				
				String[] n = st.nextToken().split("\\*");
				int itemId = Integer.parseInt(st.nextToken());
				
				for (int teamId : teamIds) {
					rewardTeam(activeChar, teamId, Integer.parseInt(n[0]), itemId, n.length == 2 ? n[1] : "");
				}
				showEventControl(activeChar);
			}
		} catch (Exception e) {
			e.printStackTrace();
			AdminData.getInstance().broadcastMessageToGMs("EventEngine: Error! Possible blank boxes while executing a command which requires a value in the box?");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
	
	private String showStoredEvents() {
		final File dir = new File(server().getDatapackRoot(), "/data/events");
		if (dir.isFile()) {
			return "<font color=\"FF0000\">The directory '" + dir.getAbsolutePath() + "' is a file or is corrupted!</font><br>";
		}
		
		String note = "";
		if (!dir.exists()) {
			note = "<font color=\"FF0000\">The directory '" + dir.getAbsolutePath() + "' does not exist!</font><br><font color=\"0099FF\">Trying to create it now...<br></font><br>";
			if (dir.mkdirs()) {
				note += "<font color=\"006600\">The directory '" + dir.getAbsolutePath() + "' has been created!</font><br>";
			} else {
				note += "<font color=\"FF0000\">The directory '" + dir.getAbsolutePath() + "' hasn't been created!</font><br>";
				return note;
			}
		}
		
		final String[] files = dir.list();
		final StringBuilder result = new StringBuilder(files.length * 500);
		result.append("<table>");
		for (String fileName : files) {
			StringUtil
				.append(result, "<tr><td align=center>", fileName, " </td></tr><tr><td><table cellspacing=0><tr><td><button value=\"Select Event\" action=\"bypass -h admin_event_set ", fileName, "\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"View Event\" action=\"bypass -h admin_event_see ", fileName, "\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Delete Event\" action=\"bypass -h admin_event_del ", fileName, "\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></td></tr>", "<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>");
		}
		
		result.append("</table>");
		
		return note + result.toString();
	}
	
	public void showMainPage(L2PcInstance activeChar) {
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		
		final String replyMSG = StringUtil.concat("<html><title>[ L2J EVENT ENGINE ]</title><body>" + "<br><center><button value=\"Create NEW event \" action=\"bypass -h admin_event_new\" width=150 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
			+ "<center><br><font color=LEVEL>Stored Events:</font><br></center>", showStoredEvents(), "</body></html>");
		adminReply.setHtml(replyMSG);
		activeChar.sendPacket(adminReply);
	}
	
	public void showNewEventPage(L2PcInstance activeChar) {
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		
		final StringBuilder replyMSG = StringUtil.startAppend(500, "<html><title>[ L2J EVENT ENGINE ]</title><body><br><br><center><font color=LEVEL>Event name:</font><br>");
		
		if (tempName.isEmpty()) {
			replyMSG.append("You can also use //event_name text to insert a new title");
			replyMSG.append("<center><multiedit var=\"name\" width=260 height=24> <button value=\"Set Event Name\" action=\"bypass -h admin_event_name $name\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		} else {
			replyMSG.append(tempName);
		}
		
		replyMSG.append("<br><br><font color=LEVEL>Event description:</font><br></center>");
		
		if (tempBuffer.isEmpty()) {
			replyMSG.append("You can also use //add text to add text or //delete_buffer to remove the text.");
		} else {
			replyMSG.append(tempBuffer);
		}
		
		replyMSG.append("<center><multiedit var=\"txt\" width=270 height=100> <button value=\"Add text\" action=\"bypass -h admin_add $txt\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		replyMSG.append("<button value=\"Remove text\" action=\"bypass -h admin_delete_buffer\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		
		if (!(tempName.isEmpty() && tempBuffer.isEmpty())) {
			replyMSG.append("<br><button value=\"Store Event Data\" action=\"bypass -h admin_event_store\" width=160 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		
		replyMSG.append("</center></body></html>");
		
		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public void showEventParameters(L2PcInstance activeChar, int teamnumbers) {
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body><title>[ L2J EVENT ENGINE ]</title><br><center> Current event: <font color=\"LEVEL\">");
		sb.append(L2Event._eventName);
		sb.append("</font></center><br>INFO: To start an event, you must first set the number of teams, then type their names in the boxes and finally type the NPC ID that will be the event manager (can be any existing npc) next to the \"Announce Event!\" button.<br><table width=100%>");
		sb.append("<tr><td><button value=\"Announce Event!\" action=\"bypass -h admin_event_announce $event_npcid ");
		sb.append(teamnumbers);
		sb.append(" ");
		for (int i = 1; (i - 1) < teamnumbers; i++) // Event announce params
		{
			sb.append("$event_teams_name");
			sb.append(i);
			sb.append(" - ");
		}
		sb.append("\" width=140 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("<td><edit var=\"event_npcid\" width=100 height=20></td></tr>");
		sb.append("<tr><td><button value=\"Set number of teams\" action=\"bypass -h admin_event_change_teams_number $event_teams_number\" width=140 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("<td><edit var=\"event_teams_number\" width=100 height=20></td></tr>");
		sb.append("</table><br><center> <br><br>");
		sb.append("<font color=\"LEVEL\">Teams' names:</font><br><table width=100% cellspacing=8>");
		for (int i = 1; (i - 1) < teamnumbers; i++) // Team names params
		{
			sb.append("<tr><td align=center>Team #");
			sb.append(i);
			sb.append(" name:</td><td><edit var=\"event_teams_name");
			sb.append(i);
			sb.append("\" width=150 height=15></td></tr>");
		}
		sb.append("</table></body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private void showEventControl(L2PcInstance activeChar) {
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>[ L2J EVENT ENGINE ]</title><body><br><center>Current event: <font color=\"LEVEL\">");
		sb.append(L2Event._eventName);
		sb.append("</font></center><br><table cellspacing=-1 width=280><tr><td align=center>Type the team ID(s) that will be affected by the commands. Commands with '*' work with only 1 team ID in the field, while '!' - none.</td></tr><tr><td align=center><edit var=\"team_number\" width=100 height=15></td></tr>");
		sb.append("<tr><td>&nbsp;</td></tr><tr><td><table width=200>");
		if (!npcsDeleted) {
			sb.append("<tr><td><button value=\"Start!\" action=\"bypass -h admin_event_control_begin\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Destroys all event npcs so no more people can't participate now on</font></td></tr>");
		}
		
		sb.append("<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><button value=\"Teleport\" action=\"bypass -h admin_event_control_teleport $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Teleports the specified team to your position</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr><tr><td><button value=\"Sit/Stand\" action=\"bypass -h admin_event_control_sit $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Sits/Stands up the team</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><button value=\"Kill\" action=\"bypass -h admin_event_control_kill $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Finish with the life of all the players in the selected team</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr><tr><td><button value=\"Resurrect\" action=\"bypass -h admin_event_control_res $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Resurrect Team's members</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><table cellspacing=-1><tr><td><button value=\"Polymorph*\" action=\"bypass -h admin_event_control_poly $team_number $poly_id\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr><tr><td><edit var=\"poly_id\" width=98 height=15></td></tr></table></td>"
			+ "<td><font color=\"LEVEL\">Polymorphs the team into the NPC with the ID specified. Multiple IDs result in randomly chosen one for each player.</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr><tr><td><button value=\"UnPolymorph\" action=\"bypass -h admin_event_control_unpoly $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Unpolymorph the team</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><table cellspacing=-1><tr><td><button value=\"Transform*\" action=\"bypass -h admin_event_control_transform $team_number $transf_id\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr><tr><td><edit var=\"transf_id\" width=98 height=15></td></tr>"
			+ "</table></td><td><font color=\"LEVEL\">Transforms the team into the transformation with the ID specified. Multiple IDs result in randomly chosen one for each player.</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr><tr><td><button value=\"UnTransform\" action=\"bypass -h admin_event_control_untransform $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Untransforms the team</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><table cellspacing=-1><tr><td><button value=\"Give Item\" action=\"bypass -h admin_event_control_prize $team_number $n $id\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><table><tr><td width=32>Num</td><td><edit var=\"n\" width=60 height=15></td></tr>"
			+ "<tr><td>ID</td><td><edit var=\"id\" width=60 height=15></td></tr></table></td><td><font color=\"LEVEL\">Give the specified item id to every single member of the team, you can put 5*level, 5*kills or 5 in the number field for example</font></td></tr>" + "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><table cellspacing=-1><tr><td><button value=\"Kick Player\" action=\"bypass -h admin_event_control_kick $player_name\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr><tr><td><edit var=\"player_name\" width=98 height=15></td></tr></table></td>"
			+ "<td><font color=\"LEVEL\">Kicks the specified player(s) from the event. Blank field kicks target.</font></td></tr>" + "<tr><td>&nbsp;</td></tr>"
			+ "<tr><td><button value=\"End!\" action=\"bypass -h admin_event_control_finish\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Will finish the event teleporting back all the players</font></td></tr>"
			+ "<tr><td>&nbsp;</td></tr></table></td></tr></table></body></html>");
		
		adminReply.setHtml(sb.toString());
		activeChar.sendPacket(adminReply);
	}
	
	private void rewardTeam(L2PcInstance activeChar, int team, int n, int id, String type) {
		int num = n;
		for (L2PcInstance player : L2Event._teams.get(team)) {
			if (type.equalsIgnoreCase("level")) {
				num = n * player.getLevel();
			} else if (type.equalsIgnoreCase("kills") && (player.getEventStatus() != null)) {
				num = n * player.getEventStatus().getKills().size();
			} else {
				num = n;
			}
			
			player.addItem("Event", id, num, activeChar, true);
			
			final NpcHtmlMessage adminReply = new NpcHtmlMessage();
			adminReply.setHtml("<html><body> CONGRATULATIONS! You should have been rewarded. </body></html>");
			player.sendPacket(adminReply);
		}
	}
}
