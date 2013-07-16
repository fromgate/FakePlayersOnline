package me.fromgate.fakeplayersonline;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;

public class FPOPLib {
	public static void initPacketListener(final FakePlayersOnline plg){
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plg, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGHEST, GamePhase.LOGIN, Packets.Server.KICK_DISCONNECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				try {
					final StructureModifier<String> packetStr = event.getPacket().getSpecificModifier(String.class);
					String [] ln = packetStr.read(0).split("\u0000");
					if (ln.length!=6) return;
					/*
					            //&1#73#1.6.1#fromgate's test server#0#20/*
					            //////////////////////////////////////////
					  	        0 &1
								1 61 - версия? // 73 для 1.6.1 - версия протокола	 
								2 1.5.2 - версия игры 
								3 fromgate's test server - motd
								4 0 - текущее число игроков
								5 20 - максимальное число
					 */
					packetStr.write(0, ln[0]+"\u0000"+ln[1]+"\u0000"+ln[2]+"\u0000"+plg.getMotd()+"\u0000"+plg.getPlayersOnline()+"\u0000"+plg.getMaxPlayers());
				}catch (Exception e){
				}
			}
		});
	}

}
