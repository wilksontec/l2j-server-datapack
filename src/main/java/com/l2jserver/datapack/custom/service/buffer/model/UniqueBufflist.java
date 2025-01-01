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
package com.l2jserver.datapack.custom.service.buffer.model;

import java.util.LinkedList;

import com.l2jserver.datapack.custom.service.base.util.htmltmpls.HTMLTemplatePlaceholder;
import com.l2jserver.datapack.custom.service.buffer.BufferServiceRepository.BuffType;
import com.l2jserver.datapack.custom.service.buffer.model.entity.BuffSkill;

/**
 * This class is here so we can actually get the name of this list and make placeholder adjustments easily while keeping outside code cleaner
 * @author HorridoJoho
 * @version 2.6.2.0
 */
public class UniqueBufflist extends LinkedList<BuffSkill> {
	private static final long serialVersionUID = -2586607798277226501L;
	
	public final int ulistId;
	public final String ulistName;
	public int numBuffs;
	public int numSongsDances;
	public HTMLTemplatePlaceholder placeholder;
	
	public UniqueBufflist(int ulistId, String ulistName) {
		this.ulistId = ulistId;
		this.ulistName = ulistName;
		numBuffs = 0;
		numSongsDances = 0;
		placeholder = new HTMLTemplatePlaceholder("unique", null).addChild("buffs", null).addChild("name", ulistName).addChild("num_buffs", "0").addChild("num_songs_dances", "0");
	}
	
	@Override
	public boolean add(BuffSkill e) {
		if (super.add(e)) {
			if (e.getType() == BuffType.BUFF) {
				++numBuffs;
				placeholder.getChild("num_buffs").setValue(String.valueOf(Integer.parseInt(placeholder.getChild("num_buffs").getValue()) + 1));
			} else {
				++numSongsDances;
				placeholder.getChild("num_songs_dances").setValue(String.valueOf(Integer.parseInt(placeholder.getChild("num_songs_dances").getValue()) + 1));
			}
			placeholder.getChild("buffs").addAliasChild(e.getId(), e.getPlaceholder());
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean remove(Object o) {
		if (super.remove(o)) {
			switch (((BuffSkill) o).getType()) {
				case BUFF:
					--numBuffs;
					break;
				case SONG_DANCE:
					--numSongsDances;
					break;
			}
			
			placeholder = new HTMLTemplatePlaceholder("unique", null).addChild("buffs", null).addChild("name", ulistName).addChild("num_buffs", String.valueOf(numBuffs)).addChild("num_songs_dances", String.valueOf(numSongsDances));
			for (BuffSkill buff : this) {
				placeholder.getChild("buffs").addAliasChild(buff.getId(), buff.getPlaceholder());
			}
			return true;
		}
		
		return false;
	}
}
