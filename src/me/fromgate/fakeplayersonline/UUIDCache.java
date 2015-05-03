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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UUIDCache {
	private static String apiUrl = "https://api.mojang.com/users/profiles/minecraft/";
	private static Map<String,UUID> cache = new HashMap<String,UUID>();

	public static UUID getUUID(String playerName) {
		Player player = Bukkit.getPlayerExact(playerName);
		if (player!=null){
			setUUID (playerName, player.getUniqueId());
		}
		if (cache.containsKey(playerName)) return cache.get(playerName);
		UUID uuid = getMojangUUID (playerName);	
		if (uuid==null)	uuid = new UUID ("FakePlayersOnline".hashCode(),playerName.hashCode());
		setUUID (playerName,uuid);
		return uuid;
	}

	private static UUID getMojangUUID(String playerName){
        try {
        	URL url = new URL(apiUrl+playerName);
        	URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "FakePlayersOnline");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                JSONObject latest =(JSONObject)new JSONParser().parse(reader.readLine());
                String idStr= (String) latest.get("id");
                return UUID.fromString(idStr.replaceFirst( "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;        
	}

	public static void setUUID(String playerName, UUID uniqueId) {
		cache.put(playerName, uniqueId);
	}


	public static void remove(String playerName) {
		if (cache.containsKey(playerName))
			cache.remove(playerName);
		
	}

}
