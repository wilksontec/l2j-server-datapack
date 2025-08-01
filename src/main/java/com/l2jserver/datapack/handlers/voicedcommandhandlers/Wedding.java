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
package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.customs;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.SevenSigns;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.PlayerAction;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.CoupleManager;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.SiegeManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.L2Event;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.skills.AbnormalVisualEffect;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SetupGauge;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * Wedding voiced commands handler.
 * @author evill33t
 */
public class Wedding implements IVoicedCommandHandler {
	private static final Logger LOG = LoggerFactory.getLogger(Wedding.class);
	
	private static final String[] COMMANDS = {
		"divorce",
		"engage",
		"gotolove"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance player, String params) {
		if (!customs().allowWedding()) {
			return false;
		}
		
		if (player == null) {
			return false;
		}
		
		if (command.startsWith("engage")) {
			return engage(player);
		} else if (command.startsWith("divorce")) {
			return divorce(player);
		} else if (command.startsWith("gotolove")) {
			return goToLove(player);
		}
		return false;
	}
	
	public boolean divorce(L2PcInstance activeChar) {
		if (activeChar.getPartnerId() == 0) {
			return false;
		}
		
		int _partnerId = activeChar.getPartnerId();
		int _coupleId = activeChar.getCoupleId();
		long adenaAmount = 0;
		
		if (activeChar.isMarried()) {
			activeChar.sendMessage("You are now divorced.");
			
			adenaAmount = (activeChar.getAdena() / 100) * customs().getWeddingDivorceCosts();
			activeChar.getInventory().reduceAdena("Wedding", adenaAmount, activeChar, null);
			
		} else {
			activeChar.sendMessage("You have broken up as a couple.");
		}
		
		final L2PcInstance partner = L2World.getInstance().getPlayer(_partnerId);
		if (partner != null) {
			partner.setPartnerId(0);
			if (partner.isMarried()) {
				partner.sendMessage("Your spouse has decided to divorce you.");
			} else {
				partner.sendMessage("Your fiance has decided to break the engagement with you.");
			}
			
			// give adena
			if (adenaAmount > 0) {
				partner.addAdena("WEDDING", adenaAmount, null, false);
			}
		}
		CoupleManager.getInstance().deleteCouple(_coupleId);
		return true;
	}
	
	public boolean engage(L2PcInstance activeChar) {
		if (activeChar.getTarget() == null) {
			activeChar.sendMessage("You have no one targeted.");
			return false;
		} else if (!(activeChar.getTarget() instanceof L2PcInstance)) {
			activeChar.sendMessage("You can only ask another player to engage you.");
			return false;
		} else if (activeChar.getPartnerId() != 0) {
			activeChar.sendMessage("You are already engaged.");
			if (customs().weddingPunishInfidelity()) {
				activeChar.startAbnormalVisualEffect(true, AbnormalVisualEffect.BIG_HEAD); // give player a Big Head
				// lets recycle the sevensigns debuffs
				int skillId;
				
				int skillLevel = 1;
				
				if (activeChar.getLevel() > 40) {
					skillLevel = 2;
				}
				
				if (activeChar.isMageClass()) {
					skillId = 4362;
				} else {
					skillId = 4361;
				}
				
				final Skill skill = SkillData.getInstance().getSkill(skillId, skillLevel);
				if (!activeChar.isAffectedBySkill(skillId)) {
					skill.applyEffects(activeChar, activeChar);
				}
			}
			return false;
		}
		final L2PcInstance ptarget = (L2PcInstance) activeChar.getTarget();
		// check if player targets themselves
		if (ptarget.getObjectId() == activeChar.getObjectId()) {
			activeChar.sendMessage("Is there something wrong with you, are you trying to go out with youself?");
			return false;
		}
		
		if (ptarget.isMarried()) {
			activeChar.sendMessage("Player already married.");
			return false;
		}
		
		if (ptarget.isEngageRequest()) {
			activeChar.sendMessage("Player already asked by someone else.");
			return false;
		}
		
		if (ptarget.getPartnerId() != 0) {
			activeChar.sendMessage("Player already engaged with someone else.");
			return false;
		}
		
		if ((ptarget.getAppearance().getSex() == activeChar.getAppearance().getSex()) && !customs().weddingAllowSameSex()) {
			activeChar.sendMessage("Same-sex marriage is not available on this server.");
			return false;
		}
		
		// Check if target has player on friend list
		boolean foundOnFriendList = false;
		try (Connection con = ConnectionFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT friendId FROM character_friends WHERE charId=?")) {
			statement.setInt(1, ptarget.getObjectId());
			try (ResultSet rset = statement.executeQuery()) {
				while (rset.next()) {
					if (rset.getInt("friendId") == activeChar.getObjectId()) {
						foundOnFriendList = true;
					}
				}
			}
		} catch (Exception e) {
			LOG.warn("Could not read friend data:{}", String.valueOf(e));
		}
		
		if (!foundOnFriendList) {
			activeChar.sendMessage("The player you want to ask is not on your friends list, you must first be on each others friends list before you choose to engage.");
			return false;
		}
		
		ptarget.setEngageRequest(true, activeChar.getObjectId());
		ptarget.addAction(PlayerAction.USER_ENGAGE);
		
		final ConfirmDlg dlg = new ConfirmDlg(activeChar.getName() + " is asking to engage you. Do you want to start a new relationship?");
		dlg.addTime(15 * 1000);
		ptarget.sendPacket(dlg);
		return true;
	}
	
	public boolean goToLove(L2PcInstance activeChar) {
		if (!activeChar.isMarried()) {
			activeChar.sendMessage("You're not married.");
			return false;
		}
		
		if (activeChar.getPartnerId() == 0) {
			activeChar.sendMessage("Couldn't find your fiance in the Database - Inform a Gamemaster.");
			LOG.error("Married but couldn't find partner for {}", activeChar.getName());
			return false;
		}
		
		if (GrandBossManager.getInstance().getZone(activeChar) != null) {
			activeChar.sendMessage("You are inside a Boss Zone.");
			return false;
		}
		
		if (activeChar.isCombatFlagEquipped()) {
			activeChar.sendMessage("While you are holding a Combat Flag or Territory Ward you can't go to your love!");
			return false;
		}
		
		if (activeChar.isCursedWeaponEquipped()) {
			activeChar.sendMessage("While you are holding a Cursed Weapon you can't go to your love!");
			return false;
		}
		
		if (GrandBossManager.getInstance().getZone(activeChar) != null) {
			activeChar.sendMessage("You are inside a Boss Zone.");
			return false;
		}
		
		if (activeChar.isJailed()) {
			activeChar.sendMessage("You are in Jail!");
			return false;
		}
		
		if (activeChar.isInOlympiadMode()) {
			activeChar.sendMessage("You are in the Olympiad now.");
			return false;
		}
		
		if (L2Event.isParticipant(activeChar)) {
			activeChar.sendMessage("You are in an event.");
			return false;
		}
		
		if (activeChar.isInDuel()) {
			activeChar.sendMessage("You are in a duel!");
			return false;
		}
		
		if (activeChar.inObserverMode()) {
			activeChar.sendMessage("You are in the observation.");
			return false;
		}
		
		if ((SiegeManager.getInstance().getSiege(activeChar) != null) && SiegeManager.getInstance().getSiege(activeChar).isInProgress()) {
			activeChar.sendMessage("You are in a siege, you cannot go to your partner.");
			return false;
		}
		
		if (activeChar.isFestivalParticipant()) {
			activeChar.sendMessage("You are in a festival.");
			return false;
		}
		
		if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift()) {
			activeChar.sendMessage("You are in the dimensional rift.");
			return false;
		}
		
		// Thanks nbd
		if (!TvTEvent.onEscapeUse(activeChar.getObjectId())) {
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (activeChar.isInsideZone(ZoneId.NO_SUMMON_FRIEND)) {
			activeChar.sendMessage("You are in area which blocks summoning.");
			return false;
		}
		
		final L2PcInstance partner = L2World.getInstance().getPlayer(activeChar.getPartnerId());
		if ((partner == null) || !partner.isOnline()) {
			activeChar.sendMessage("Your partner is not online.");
			return false;
		}
		
		if (activeChar.getInstanceId() != partner.getInstanceId()) {
			activeChar.sendMessage("Your partner is in another World!");
			return false;
		}
		
		if (partner.isJailed()) {
			activeChar.sendMessage("Your partner is in Jail.");
			return false;
		}
		
		if (partner.isCursedWeaponEquipped()) {
			activeChar.sendMessage("Your partner is holding a Cursed Weapon and you can't go to your love!");
			return false;
		}
		
		if (GrandBossManager.getInstance().getZone(partner) != null) {
			activeChar.sendMessage("Your partner is inside a Boss Zone.");
			return false;
		}
		
		if (partner.isInOlympiadMode()) {
			activeChar.sendMessage("Your partner is in the Olympiad now.");
			return false;
		}
		
		if (L2Event.isParticipant(partner)) {
			activeChar.sendMessage("Your partner is in an event.");
			return false;
		}
		
		if (partner.isInDuel()) {
			activeChar.sendMessage("Your partner is in a duel.");
			return false;
		}
		
		if (partner.isFestivalParticipant()) {
			activeChar.sendMessage("Your partner is in a festival.");
			return false;
		}
		
		if (partner.isInParty() && partner.getParty().isInDimensionalRift()) {
			activeChar.sendMessage("Your partner is in dimensional rift.");
			return false;
		}
		
		if (partner.inObserverMode()) {
			activeChar.sendMessage("Your partner is in the observation.");
			return false;
		}
		
		if ((SiegeManager.getInstance().getSiege(partner) != null) && SiegeManager.getInstance().getSiege(partner).isInProgress()) {
			activeChar.sendMessage("Your partner is in a siege, you cannot go to your partner.");
			return false;
		}
		
		if (partner.isIn7sDungeon() && !activeChar.isIn7sDungeon()) {
			final int playerCabal = SevenSigns.getInstance().getPlayerCabal(activeChar.getObjectId());
			final boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
			final int compWinner = SevenSigns.getInstance().getCabalHighestScore();
			
			if (isSealValidationPeriod) {
				if (playerCabal != compWinner) {
					activeChar.sendMessage("Your Partner is in a Seven Signs Dungeon and you are not in the winner Cabal!");
					return false;
				}
			} else {
				if (playerCabal == SevenSigns.CABAL_NULL) {
					activeChar.sendMessage("Your Partner is in a Seven Signs Dungeon and you are not registered!");
					return false;
				}
			}
		}
		
		if (!TvTEvent.onEscapeUse(partner.getObjectId())) {
			activeChar.sendMessage("Your partner is in an event.");
			return false;
		}
		
		if (partner.isInsideZone(ZoneId.NO_SUMMON_FRIEND)) {
			activeChar.sendMessage("Your partner is in area which blocks summoning.");
			return false;
		}
		
		final int teleportTimer = (int) customs().getWeddingTeleportDuration();
		activeChar.sendMessage("After " + MILLISECONDS.toMinutes(teleportTimer) + " min. you will be teleported to your partner.");
		activeChar.getInventory().reduceAdena("Wedding", customs().getWeddingTeleportPrice(), activeChar, null);
		
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		// SoE Animation section
		activeChar.setTarget(activeChar);
		activeChar.disableAllSkills();
		
		final MagicSkillUse msk = new MagicSkillUse(activeChar, 1050, 1, teleportTimer, 0);
		Broadcast.toSelfAndKnownPlayersInRadius(activeChar, msk, 900);
		final SetupGauge sg = new SetupGauge(0, teleportTimer);
		activeChar.sendPacket(sg);
		// End SoE Animation section
		
		final EscapeFinalizer ef = new EscapeFinalizer(activeChar, partner.getLocation(), partner.isIn7sDungeon());
		// continue execution later
		activeChar.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(ef, teleportTimer));
		activeChar.forceIsCasting(GameTimeController.getInstance().getGameTicks() + (teleportTimer / GameTimeController.MILLIS_IN_TICK));
		
		return true;
	}
	
	static class EscapeFinalizer implements Runnable {
		private final L2PcInstance _activeChar;
		private final Location _partnerLoc;
		private final boolean _to7sDungeon;
		
		EscapeFinalizer(L2PcInstance activeChar, Location loc, boolean to7sDungeon) {
			_activeChar = activeChar;
			_partnerLoc = loc;
			_to7sDungeon = to7sDungeon;
		}
		
		@Override
		public void run() {
			if (_activeChar.isDead()) {
				return;
			}
			
			if ((SiegeManager.getInstance().getSiege(_partnerLoc) != null) && SiegeManager.getInstance().getSiege(_partnerLoc).isInProgress()) {
				_activeChar.sendMessage("Your partner is in siege, you can't go to your partner.");
				return;
			}
			
			_activeChar.setIsIn7sDungeon(_to7sDungeon);
			_activeChar.enableAllSkills();
			_activeChar.setIsCastingNow(false);
			
			try {
				_activeChar.teleToLocation(_partnerLoc);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public String[] getVoicedCommandList() {
		return COMMANDS;
	}
}
