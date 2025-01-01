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
package com.l2jserver.datapack.custom.service.discord.commands.moderation;

import static com.l2jserver.gameserver.config.Configuration.discord;

import java.awt.Color;

import com.l2jserver.datapack.custom.service.discord.AbstractCommand;
import com.l2jserver.gameserver.Shutdown;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Abort Command.
 * @author Stalitsa
 * @version 2.6.2.0
 */
public class AbortCommand extends AbstractCommand {
	
	private static final String[] COMMANDS = {
		"abort",
		"abt"
	};
	
	@Override
	public String[] getCommands() {
		return COMMANDS;
	}
	
	@Override
	public void executeCommand(MessageReceivedEvent event, String[] args) {
		EmbedBuilder eb = new EmbedBuilder().setColor(Color.RED);
		
		if (!canExecute(event)) {
			return;
		}
		
		if (args.length != 1) {
			eb.setDescription("Please use the command without any Arguments");
			event.getChannel().asTextChannel().sendMessageEmbeds(eb.build()).queue();
			event.getMessage().addReaction(CROSS_MARK).queue();
			return;
		}
		
		String gmName = event.getAuthor().getAsMention();
		String commandName = args[0].substring(discord().getPrefix().length()).toUpperCase();
		Shutdown.getInstance().telnetAbort(event.getAuthor().getName()); // Using telnet method.
		eb.setDescription("GM: {" + gmName + "} issued command. **" + commandName + "** --- Shutdown/Restart Aborted.");
		event.getChannel().sendMessageEmbeds(eb.build()).queue();
		event.getMessage().addReaction(CHECK_MARK).queue();
	}
}