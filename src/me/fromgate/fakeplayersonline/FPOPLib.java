/*
 * FakePlayersOnline, Minecraft bukkit plugin
 * (c)2012, 2013, fromgate, fromgate@gmail.com
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
import java.util.List;
import org.bukkit.ChatColor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class FPOPLib {

    static FakePlayersOnline plg(){
        return FakePlayersOnline.instance;
    }

    public static void initPacketListener(){
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(FakePlayersOnline.instance, PacketType.Status.Server.OUT_SERVER_INFO) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        event.getPacket().getServerPings().getValues().get(0).setPlayersOnline(plg().getPlayersOnline());
                        event.getPacket().getServerPings().getValues().get(0).setPlayersMaximum(plg().getMaxPlayers());
                        event.getPacket().getServerPings().getValues().get(0).setMotD(plg().getMotd());
                        if (plg().enableFakePlayersInServerList){
                            List<WrappedGameProfile> players = new ArrayList<WrappedGameProfile>();
                            for (String player : plg().showList){
                                players.add(new WrappedGameProfile("id"+String.valueOf(players.size()+1), ChatColor.translateAlternateColorCodes('&', player)));
                            }
                            if (!players.isEmpty()) event.getPacket().getServerPings().getValues().get(0).setPlayers(players);
                        }
                    }
                });
    }
}
