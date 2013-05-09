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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FPOcbo {
	private static String version = "";
	private static String [] tested_versions = {"v1_4_5", "v1_4_6", "v1_4_R1",
		                                        "v1_5_R1","v1_5_R2","v1_5_R3"};
	private static String cboPrefix = "org.bukkit.craftbukkit.";
	private static String nmsPrefix = "net.minecraft.server.";
	private static boolean block_executing = false;
	
	
	
	private static Class<?> CraftEntity;
	private static Field CraftEntity_entity;
	private static Class<?> EntityPlayer;
	private static Field entityPlayer_netServerHandler;
	private static Field entityPlayer_ping;
	private static Field entityPlayer_listName;
	private static Class<?> NetServerHandler;
	private static Class<?> Packet;
	private static Class<?> Packet201PlayerInfo;
	private static Method sendPacket;
	private static Constructor<?> newPacket;

	public static void init(){
		try{
			Object s = Bukkit.getServer();
			Method m = s.getClass().getMethod("getHandle");
			Object cs = m.invoke(s);
			String className = cs.getClass().getName();
			String [] v = className.split("\\.");
			if (v.length==5){
				version = v[3];
				cboPrefix = "org.bukkit.craftbukkit."+version+".";
				nmsPrefix = "net.minecraft.server."+version+".";;
			}
		} catch (Exception e){
		}

		try {
			EntityPlayer = nmsClass("EntityPlayer");
			String playerConnectionClass = "PlayerConnection";
			String playerConnectionField = "playerConnection";
			if (version.isEmpty()||version.equalsIgnoreCase("v1_4_5")){
				playerConnectionClass = "NetServerHandler";
				playerConnectionField = "netServerHandler";
			}
			
			CraftEntity = cboClass("entity.CraftEntity");
			CraftEntity_entity = CraftEntity.getDeclaredField("entity");
			CraftEntity_entity.setAccessible(true);
			
			
			entityPlayer_netServerHandler = EntityPlayer.getField(playerConnectionField);
			entityPlayer_ping = EntityPlayer.getField("ping");
			entityPlayer_listName = EntityPlayer.getField("listName");
			NetServerHandler = nmsClass (playerConnectionClass);
			Packet = nmsClass("Packet");
			Packet201PlayerInfo = nmsClass("Packet201PlayerInfo");//Class.forName("net.minecraft.server.v1_4_5.Packet201PlayerInfo");
			sendPacket = NetServerHandler.getMethod("sendPacket", Packet);
			newPacket = Packet201PlayerInfo.getConstructor(String.class, boolean.class, int.class);
		} catch (Exception e){
			block_executing = true;
			e.printStackTrace();
		}
	}

	public static String getMinecraftVersion(){
		return version;
	}

	public static boolean isTestedVersion(){
		if (version.isEmpty()) return true;
		for (int i = 0; i< tested_versions.length;i++){
			if (tested_versions[i].equalsIgnoreCase(version)) return true;
		}
		return false;
	}
	
	public static boolean isBlocked(){
		return block_executing;
	}


	private static Class<?> nmsClass(String classname) throws Exception{
		return Class.forName(nmsPrefix+classname);
	}
	
	private static Class<?> cboClass(String classname) throws Exception{
		return Class.forName(cboPrefix+classname);
	}
	
	public static int getPlayerPing (Player p){
		if (block_executing) return 0;
		try {
			Object craftEntity = p;
			Object nmsPlayer = CraftEntity_entity.get(craftEntity);
			return entityPlayer_ping.getInt(nmsPlayer);
		} catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String getPlayerListName (Player p){
		if (block_executing) return p.getName();
		try {
			Object craftEntity = p;
			Object nmsPlayer = CraftEntity_entity.get(craftEntity);
			return (String) entityPlayer_listName.get(nmsPlayer);
		} catch (Exception e){
			e.printStackTrace();
			return p.getName();
		}
	}

	
	public static void sendFakePlayerPacket(Player p, String playername, boolean online, int ping){
		if (block_executing) return;
		try{
			Object craftEntity = p;
			Object nmsPlayer = CraftEntity_entity.get(craftEntity);
			Object netSenderHandler = entityPlayer_netServerHandler.get(nmsPlayer);
			Object packet = newPacket.newInstance(playername, online, ping);
			sendPacket.invoke(netSenderHandler, packet);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	

}
