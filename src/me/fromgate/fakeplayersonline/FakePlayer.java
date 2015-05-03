/*
 * FakePlayersOnline, Minecraft bukkit plugin
 * (c)2012-2015, fromgate, fromgate@gmail.com
 * http://dev.bukkit.org/server-mods/fakeplayers/
 *
 * This file is part of FakePlayersOnline.
 *
 * FakePlayersOnline is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FakePlayersOnline is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FakePlayersOnline. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.fromgate.fakeplayersonline;

import java.util.UUID;

import org.bukkit.entity.Player;

public class FakePlayer {
	UUID uuid;
	String playerName;
	String displayName;
	int ping;
	
	public FakePlayer (Player player){
		this.uuid = player.getUniqueId();
		this.playerName = player.getName();
		this.displayName = player.getDisplayName();
		this.ping = FPOcbo.getPlayerPing(player);
	}

	public FakePlayer(String fakeName, String fakeListName, UUID uuid, int ping) {
		this.uuid = uuid;
		this.playerName = fakeName;
		this.displayName = fakeListName;
		this.ping = ping;
	}
	
	
}
