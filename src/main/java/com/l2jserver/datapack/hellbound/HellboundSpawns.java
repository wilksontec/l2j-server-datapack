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
package com.l2jserver.datapack.hellbound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.util.IXmlReader;

/**
 * Hellbound Spawns parser.
 * @author Zoey76
 */
public final class HellboundSpawns implements IXmlReader {
	
	private static final Logger LOG = LoggerFactory.getLogger(HellboundSpawns.class);
	
	private final List<L2Spawn> _spawns = new ArrayList<>();
	
	private final Map<L2Spawn, int[]> _spawnLevels = new HashMap<>();
	
	public HellboundSpawns() {
		load();
	}
	
	@Override
	public void load() {
		_spawns.clear();
		_spawnLevels.clear();
		parseDatapackFile("data/hellbound/hellboundSpawns.xml");
		LOG.info("Loaded {} Hellbound spawns.", _spawns.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
			if ("list".equals(node.getNodeName())) {
				for (Node npc = node.getFirstChild(); npc != null; npc = npc.getNextSibling()) {
					parseSpawn(npc);
				}
			}
		}
	}
	
	/**
	 * Parses the spawn.
	 * @param npc the NPC to parse
	 */
	private void parseSpawn(Node npc) {
		if ("npc".equals(npc.getNodeName())) {
			final Node id = npc.getAttributes().getNamedItem("id");
			if (id == null) {
				LOG.warn("Missing NPC Id, skipping record!");
				return;
			}
			
			final int npcId = Integer.parseInt(id.getNodeValue());
			Location loc = null;
			int delay = 0;
			int randomInterval = 0;
			int minLevel = 1;
			int maxLevel = 100;
			for (Node element = npc.getFirstChild(); element != null; element = element.getNextSibling()) {
				final NamedNodeMap attrs = element.getAttributes();
				switch (element.getNodeName()) {
					case "location": {
						loc = new Location(parseInteger(attrs, "x"), parseInteger(attrs, "y"), parseInteger(attrs, "z"), parseInteger(attrs, "heading", 0));
						break;
					}
					case "respawn": {
						delay = parseInteger(attrs, "delay");
						randomInterval = attrs.getNamedItem("randomInterval") != null ? parseInteger(attrs, "randomInterval") : 1;
						break;
					}
					case "hellboundLevel": {
						minLevel = parseInteger(attrs, "min", 1);
						maxLevel = parseInteger(attrs, "max", 100);
						break;
					}
				}
			}
			
			try {
				final L2Spawn spawn = new L2Spawn(npcId);
				spawn.setAmount(1);
				if (loc == null) {
					LOG.warn("Hellbound spawn location is null!");
				}
				spawn.setLocation(loc);
				spawn.setRespawnDelay(delay, randomInterval);
				_spawnLevels.put(spawn, new int[] {
					minLevel,
					maxLevel
				});
				SpawnTable.getInstance().addNewSpawn(spawn, false);
				_spawns.add(spawn);
			} catch (Exception ex) {
				LOG.warn("Couldn't load spawns!", ex);
			}
		}
	}
	
	/**
	 * Gets all Hellbound spawns.
	 * @return the list of Hellbound spawns.
	 */
	public List<L2Spawn> getSpawns() {
		return _spawns;
	}
	
	/**
	 * Gets the spawn minimum level.
	 * @param spawn the spawn
	 * @return the spawn minimum level
	 */
	public int getSpawnMinLevel(L2Spawn spawn) {
		return _spawnLevels.containsKey(spawn) ? _spawnLevels.get(spawn)[0] : 1;
	}
	
	/**
	 * Gets the spawn maximum level.
	 * @param spawn the spawn
	 * @return the spawn maximum level
	 */
	public int getSpawnMaxLevel(L2Spawn spawn) {
		return _spawnLevels.containsKey(spawn) ? _spawnLevels.get(spawn)[1] : 1;
	}
	
	public static HellboundSpawns getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		protected static final HellboundSpawns INSTANCE = new HellboundSpawns();
	}
}