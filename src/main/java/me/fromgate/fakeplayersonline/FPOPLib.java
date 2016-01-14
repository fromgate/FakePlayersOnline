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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class FPOPLib {

	private static boolean enabled = false;
	private static ProtocolManager protocolManager;

	public static boolean isEnabled(){
		return enabled;
	}

	static FakePlayersOnline plg(){
		return FakePlayersOnline.instance;
	}

	public static void init(){
		try{
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib")!=null){
				protocolManager = ProtocolLibrary.getProtocolManager();
				enabled = true;
			} 
		} catch (Throwable e){
		}
	}

	public static void sendFakePlayerPackets(Player player, Collection<FakePlayer> playerList, boolean show) {
		if (!enabled) return;
		PacketContainer fakePlayerPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
		List<PlayerInfoData> pInfos = new ArrayList<PlayerInfoData>();
		for (FakePlayer pStr : playerList){
			UUID u = pStr.uuid; 			
			WrappedGameProfile wgp = new WrappedGameProfile (u,pStr.playerName);
			PlayerInfoData pi = new PlayerInfoData(wgp, 16, NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(pStr.displayName));
			pInfos.add(pi);
		}
		fakePlayerPacket.getPlayerInfoAction().write(0, show ? PlayerInfoAction.ADD_PLAYER : PlayerInfoAction.REMOVE_PLAYER);
		fakePlayerPacket.getPlayerInfoDataLists().write(0, pInfos);
		try {
			protocolManager.sendServerPacket(player, fakePlayerPacket);
		} catch (InvocationTargetException e) {
		}


	}

	public static void sendFakePlayerPackets(Collection<? extends Player> collection, Collection<FakePlayer> playerList, boolean show) {
		if (!enabled) return;
		for (Player player : collection)
			sendFakePlayerPackets (player, playerList, show);
	}

	public static void initPacketListener(){
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(FakePlayersOnline.instance, PacketType.Status.Server.OUT_SERVER_INFO) {
					@Override
					public void onPacketSending(PacketEvent event) {
						ShowList.fillShowList(null);
						WrappedServerPing ping = (WrappedServerPing)event.getPacket().getServerPings().read(0);
						ping.setPlayersOnline(plg().getPlayersOnline()); 
						ping.setPlayersMaximum(plg().getMaxPlayers());
						ping.setMotD(plg().getMotd());
						if (plg().enableFakePlayersInServerList){
							List<WrappedGameProfile> players = new ArrayList<WrappedGameProfile>();
							for (FakePlayer fp : ShowList.getPlayerList()){
								players.add(new WrappedGameProfile(fp.uuid, ChatColor.translateAlternateColorCodes('&', fp.displayName)));
							}
							if (!players.isEmpty()) {
								ping.setPlayersVisible(true);
								ping.setPlayers(players);
							}
						}
						event.getPacket().getServerPings().write(0, ping);
					}
				});
	}
}
