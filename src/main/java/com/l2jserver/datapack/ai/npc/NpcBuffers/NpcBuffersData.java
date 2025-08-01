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
package com.l2jserver.datapack.ai.npc.NpcBuffers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.util.IXmlReader;

/**
 * NPC Buffers data.
 * @author UnAfraid
 */
public class NpcBuffersData implements IXmlReader {
	private static final Logger LOG = LoggerFactory.getLogger(NpcBuffersData.class);
	
	private final Map<Integer, NpcBufferData> _npcBuffers = new HashMap<>();
	
	protected NpcBuffersData() {
		load();
	}
	
	@Override
	public void load() {
		parseDatapackFile("data/ai/npc/buffer/NpcBuffersData.xml");
		LOG.info("Loaded {} buffers data.", _npcBuffers.size());
	}
	
	@Override
	public void parseDocument(Document doc) {
		StatsSet set;
		Node attr;
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
			if ("list".equalsIgnoreCase(n.getNodeName())) {
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
					if ("npc".equalsIgnoreCase(d.getNodeName())) {
						attrs = d.getAttributes();
						final int npcId = parseInteger(attrs, "id");
						final NpcBufferData npc = new NpcBufferData(npcId);
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling()) {
							switch (c.getNodeName()) {
								case "skill": {
									attrs = c.getAttributes();
									set = new StatsSet();
									for (int i = 0; i < attrs.getLength(); i++) {
										attr = attrs.item(i);
										set.set(attr.getNodeName(), attr.getNodeValue());
									}
									npc.addSkill(new NpcBufferSkillData(set));
									break;
								}
							}
						}
						_npcBuffers.put(npcId, npc);
					}
				}
			}
		}
	}
	
	public NpcBufferData getNpcBuffer(int npcId) {
		return _npcBuffers.get(npcId);
	}
	
	public Collection<NpcBufferData> getNpcBuffers() {
		return _npcBuffers.values();
	}
	
	public Set<Integer> getNpcBufferIds() {
		return _npcBuffers.keySet();
	}
}
