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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ShowList {
	private static Set<FakePlayer> showList = new HashSet<FakePlayer>();
	private static Random random = new Random();
	
	public static void clear(){
		showList.clear();
	}
	
	public static void refreshOnlineList(){
		hideShowList();
		showAndFillShowList();
	}
	
	public static void showAndFillShowList(){
		for (Player player : Bukkit.getServer().getOnlinePlayers())
			ShowList.showAndFillShowList(player);
	}
	
	
	static void fillShowList(Player player){
		showList.clear();
		int pingMin = Bukkit.getServer().getOnlinePlayers().isEmpty() ? 0 : 1000;
		int pingMax = Bukkit.getServer().getOnlinePlayers().isEmpty() ? 1000 : 0;
		for (Player p : Bukkit.getServer().getOnlinePlayers()){
			if (!p.isOnline()) continue;
			if (player!=null&&!player.canSee(p)&&!player.hasPermission("fakeplayers.canseehidden")) continue;
			String plName = p.getPlayerListName();
			int ping = FPOcbo.getPlayerPing(p);
			pingMin = Math.min(pingMin, ping);
			pingMax = Math.max(pingMax, ping);
			
			if (FakePlayersOnline.getPlugin().real){
				plName = p.getName();
				String px = FakePlayersOnline.getPlugin().realPx;
				if (p.hasPermission("fakeplayers.red")) px = FakePlayersOnline.getPlugin().realPxAdmin;
				plName = ChatColor.translateAlternateColorCodes('&', px+plName);
				if (plName.length()>16) plName = plName.substring(0, 15);
			}
			p.setPlayerListName(plName);
			showList.add(new FakePlayer (p));
		}
		if (!FakePlayersOnline.getPlugin().fakePlayers.isEmpty()){
			for (int i = 0; i< FakePlayersOnline.getPlugin().fakePlayers.size(); i++){
				int ping = random.nextInt(pingMax-pingMin+1)+pingMin;
				String fakeName = FakePlayersOnline.getPlugin().fakePlayers.get(i);
				String fakeListName = ChatColor.translateAlternateColorCodes('&', FakePlayersOnline.getPlugin().fakePx+fakeName);
				if (fakeListName.length()>16) fakeListName = fakeListName.substring(0, 15);
				UUID uuid = UUIDCache.getUUID (fakeName);
				showList.add(new FakePlayer (fakeName, fakeListName,uuid, ping));
			}
		}

		if (!FakePlayersOnline.getPlugin().npcList.isEmpty())
			for (int i = 0; i< FakePlayersOnline.getPlugin().npcList.size(); i++){
				int ping = random.nextInt(pingMax-pingMin+1)+pingMin;
				String fakeName = FakePlayersOnline.getPlugin().npcList.get(i);
				String fakeListName = ChatColor.translateAlternateColorCodes('&', FakePlayersOnline.getPlugin().npcPx+fakeName);
				if (fakeListName.length()>16) fakeListName = fakeListName.substring(0, 15);
				UUID uuid = UUIDCache.getUUID (fakeName);
				showList.add(new FakePlayer (fakeName, fakeListName,uuid, ping));
			}
		
	}
	
	public static void showAndFillShowList(Player player){
		fillShowList(player);
		FPOPLib.sendFakePlayerPackets(player, showList, true);
	} 
	
	public static void hideShowList(){
		List<FakePlayer> realPlayers = new ArrayList<FakePlayer>();
		for (Player p : Bukkit.getServer().getOnlinePlayers()) realPlayers.add(new FakePlayer (p));
		FPOPLib.sendFakePlayerPackets(Bukkit.getServer().getOnlinePlayers(), realPlayers, false);
		FPOPLib.sendFakePlayerPackets(Bukkit.getServer().getOnlinePlayers(), showList, false);
		showList.clear();
	}

	public static Set<FakePlayer> getPlayerList() {
		return showList;
	}


	public static int size() {
		return showList.size();
	}

	public static String getPlayers() {
		if (showList.isEmpty()) return "";
		StringBuilder sb = null;
		for (FakePlayer fp : showList){
			if (sb == null) sb = new StringBuilder(fp.playerName);
			else sb.append(", ").append(fp.playerName);
		}
		return sb.toString();
	}

	

}
