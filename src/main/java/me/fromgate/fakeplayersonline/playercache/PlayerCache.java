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

package me.fromgate.fakeplayersonline.playercache;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCache {
    private static String apiUrl = "https://api.mojang.com/users/profiles/minecraft/";

    // {"id":"b523fd6332b34e8aa608676d6f66c32c","name":"fromgate","properties":[{"signature":"DmRKPXA6GJAB0X813D351RT45dqixwC3exssmLD6N5bBiciGD+4dsWUNTlZfPMK521dH9jlMtqjxhK54CFo4/jCmf+E6e6vKekINzApifFVMR02axkXR8YE3BYDUsGfOChwrF9IecukMw2/RwV3c5bJY6QJqRrvlm3uOxzUAo/wEY5dMV57uOp3vuYaVFuZpOyI3t7k8HRdzJ6Srb6UgsaxZn0DXRBeXIe9IMhTO59Ctwm9JWMmg54+ngkq68bSH9wasHcWfU1kFBUbhbJKYFNKu80SEucf+EDeqkF3uAKqZoQtta/WZBWzoSXFZOA4lo0F/3ICYrO2BX0d9Hi9+d/7ZHHTKnIi6rWclzZTekclMtf9rf0C2huN7MwRFKdhA+oR3JkWGcBG2b3sOy7SKFKQ6LxgWeA+NipY6X7ztLLo6dcSK/dCQl8/e9haIDNtGg9zrqCF3yeOjbQapSsjbnGI/r/mUQvpWfDkf535uS/ootqSkJhBz9jGbnJA8QTAftdHY/KiLGgMLzCKNvvzPyZjDttc29w6jex1pxSLTUPnIaBpNOts9SLpeQXscDQDyOp2r42hTq1/JeIFRVCBT0lAeMBc/SvGaMrSo9dRKWjPi443bfPNP985rR+jLsvd4Ojl1zrmopTOTg4NWgBEDKLua1WZe/N8jxx0xDeu4MpM=","name":"textures","value":"eyJ0aW1lc3RhbXAiOjE0Nzk5ODc1MTMzODksInByb2ZpbGVJZCI6ImI1MjNmZDYzMzJiMzRlOGFhNjA4Njc2ZDZmNjZjMzJjIiwicHJvZmlsZU5hbWUiOiJmcm9tZ2F0ZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg3YzI5ODkyMjdjMjEzOTRhNWVjNTQwMzY2ZTE1NWQxNmRlNDk1MzQ1NGE2MzRjNWI5YjI3OTE5ZjM4NDg3In19fQ=="}]}


    private static Map<String, PlayerUnit> cache = new HashMap<>();

    @SuppressWarnings("deprecation")
    public static PlayerUnit getPlayerUnit (String playerName) {
        if (cache.containsKey(playerName)) {
            return cache.get(playerName);
        }

        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return setUUID(playerName, player.getUniqueId());
        }

        UUID uuid = getMojangUUID(playerName);

        if (uuid == null) {
            uuid = new UUID("FakePlayersOnline".hashCode(), playerName.hashCode());
        }
        return setUUID(playerName, uuid);
    }



    private static UUID getMojangUUID(String playerName) {
        try {
            URL url = new URL(apiUrl + playerName);
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "FakePlayersOnline");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JSONObject latest = (JSONObject) new JSONParser().parse(reader.readLine());
            String idStr = (String) latest.get("id");
            return UUID.fromString(idStr.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PlayerUnit setUUID(String playerName, UUID uniqueId) {
        PlayerUnit unit = new PlayerUnit(uniqueId);
        cache.put(playerName, unit);
        return unit;
    }


    public static void remove(String playerName) {
        if (cache.containsKey(playerName)){
            cache.remove(playerName);
        }
    }

}
