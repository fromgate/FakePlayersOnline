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

import java.io.IOException;

/* 
 * TODO
 * - /fpo lock
 * 
 * fakeplayers.unlock - возможность зайти на сервер
 * 
 */
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;

public class FakePlayersOnline extends JavaPlugin {

	FPOUtil u;
	FPOCmd cmd;
	Logger log = Logger.getLogger("Minecraft");

	BukkitTask tid_npc;
	boolean tid_npc_active = false;

	// Конфигурация
	boolean serverlocked = false;
	boolean serverunlockreload = false;
	int kick_delay = 10;
	
	boolean version_check = true;
	String language = "english";
	boolean language_save = false;
	int npclistupdatetime = 60; //в секундах

	boolean fakemaxplayersenable=true;
	int fakemaxplayers = 50;

	boolean listcmdoverride = true;
	String listcmd = "list,players,online,playerlist";

	List<String> showlist= new ArrayList<String>();

	boolean real = true;

	boolean fake = true;
	String fake_px = "&e";
	String real_px = "&6";
	String real_px_admin = "&4";
	List<String> fakeplayers = new ArrayList<String>();

	boolean npc_enabled = false;
	boolean npc = true;
	String npc_px = "&3";
	boolean fake_serverlist = true;

	//FakeServerList
	boolean fakeserverlist = true;
	boolean fakemotd = true;
	boolean fixedseverlist = false;
	int fixedonline = 10;
	String motd = "&4FakePlayersOnline &6installed!";

	List<String> npclist = new ArrayList<String>();




	@Override
	public void onEnable() {
		LoadCfg();
		if (serverlocked) serverlocked = !serverunlockreload;
		SaveCfg();
		u = new FPOUtil (this, version_check, language_save, language, "fakeplayers", "FakePlayersOnline","fpo","&3[FPO]&f");
		cmd = new FPOCmd (this);
		getCommand ("fpo").setExecutor(cmd);
		npc_enabled = isCitzensInstalled();
		getServer().getPluginManager().registerEvents(u, this);
		if (!npc_enabled) log.info("[FakePlayersOnline] Citizens is not found!");

		if (isProtocolLibInstalled()){
			if (fake_serverlist) FPOPLib.initPacketListener(this);	
		} else log.info("[FakePlayersOnline] ProtocolLib is not found!");


		FPOcbo.init();
		if (FPOcbo.isBlocked()){
			log.info("[FakePlayersOnline] +---------------------------------------------------------------------+");
			log.info("[FakePlayersOnline] + WARNING!!! THIS PLUGIN IS NOT COMPATIBLE WITH CRAFTBUKKIT "+FPOcbo.getMinecraftVersion().replace('_', '.')+"    +");
			log.info("[FakePlayersOnline] + FakePlayersOnline will not work correctly. Please check updates at: +");
			log.info("[FakePlayersOnline] + http://dev.bukkit.org/server-mods/fakeplayers/                      +");
			log.info("[FakePlayersOnline] +---------------------------------------------------------------------+");
		} else {
			if (!FPOcbo.isTestedVersion()) {
				log.info("[FakePlayersOnline] +--------------------------------------------------------------------------+");
				log.info("[FakePlayersOnline] + This version of FakePlayersOnline was not tested with CraftBukkit "+FPOcbo.getMinecraftVersion().replace('_', '.')+" +");
				log.info("[FakePlayersOnline] + Check updates at http://dev.bukkit.org/server-mods/fakeplayers/          +");
				log.info("[FakePlayersOnline] + or use this version at your own risk                                     +");
				log.info("[FakePlayersOnline] +--------------------------------------------------------------------------+");
			}

			restartTicks();
		}

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			log.info("[FakePlayersOnline] failed to submit stats to the Metrics (mcstats.org)");
		}

	}


	public void LoadCfg(){
		version_check = getConfig().getBoolean("general.check-updates", true);;
		language = getConfig().getString("general.language", "english");
		language_save = getConfig().getBoolean("general.language-save", false);
		npclistupdatetime = getConfig().getInt("general.npc-list-update-time",60);
		fake = getConfig().getBoolean("fake-players.enabled", true);
		fake_px = getConfig().getString("fake-players.prefix", "&e");
		fakeplayers = getConfig().getStringList("fake-players.list");
		npc = getConfig().getBoolean("citizens.enabled", true);
		npc_px = getConfig().getString("citizens.prefix", "&3");
		real = getConfig().getBoolean("real-players.enable-override", true);
		real_px = getConfig().getString("real-players.prefix", "&6");
		real_px_admin = getConfig().getString("real-players.prefix-red", "&4");
		fakemaxplayersenable = getConfig().getBoolean("fake-max-players.enabled", false);
		fakemaxplayers = getConfig().getInt("fake-max-players.fake-max-online", getServer().getMaxPlayers());
		fake_serverlist = getConfig().getBoolean("fake-server-list.enabled", true);
		fakemotd = getConfig().getBoolean("fake-server-list.motd.enabled", true);
		motd = getConfig().getString("fake-server-list.motd.value", "&4FakePlayersOnline &6installed!");
		fixedseverlist = getConfig().getBoolean("fake-server-list.constant-online-count.enabled", false);
		fixedonline = getConfig().getInt("fake-server-list.constant-online-count.players-online", 10);
		serverlocked = getConfig().getBoolean("server-lock.is-locked",false);
		serverunlockreload = getConfig().getBoolean("server-lock.unlock-after-reload",true);
		kick_delay = getConfig().getInt("server-lock.kick-delay",10);
	}

	public void SaveCfg(){
		getConfig().set("general.check-updates", version_check);;
		getConfig().set("general.language", language);
		getConfig().set("general.language-save", language_save);
		getConfig().set("general.npc-list-update-time",npclistupdatetime);
		getConfig().set("real-players.enable-override", real);
		getConfig().set("real-players.prefix", real_px);
		getConfig().set("real-players.prefix-red", real_px_admin );
		getConfig().set("fake-players.enabled", fake);
		getConfig().set("fake-players.prefix", fake_px);
		getConfig().set("fake-players.list",fakeplayers);
		getConfig().set("citizens.enabled", npc);
		getConfig().set("citizens.prefix", npc_px);
		getConfig().set("fake-max-players.enabled", fakemaxplayersenable );
		getConfig().set("fake-max-players.fake-max-online", fakemaxplayers);
		getConfig().set("fake-server-list.enabled", fake_serverlist);
		getConfig().set("fake-server-list.motd.enabled", fakemotd);
		getConfig().set("fake-server-list.motd.value", motd);
		getConfig().set("fake-server-list.constant-online-count.enabled", fixedseverlist);
		getConfig().set("fake-server-list.constant-online-count.players-online", fixedonline);
		getConfig().set("server-lock.is-locked",serverlocked);
		getConfig().set("server-lock.unlock-after-reload",serverunlockreload);
		getConfig().set("server-lock.kick-delay",kick_delay);
		saveConfig();
	}


	public boolean isCitzensInstalled(){
		PluginManager pm = getServer().getPluginManager();
		Plugin test = pm.getPlugin("Citizens");
		return (test != null);
	}

	public void fillNPCList(){
		npclist.clear();
		if (!npc_enabled) return;
		if (!npc) return;
		try {
			for (HumanNPC hpc: CitizensManager.getList().values())	
				npclist.add(hpc.getName());
		} catch (Exception e){
			npc_enabled = false;
			log.info("[FakePlayersOnline] Error while connecting to Citizens 1.2.x");
			npclist.clear();
		}
		refreshOnlineList();
	}


	public void restartTicks(){
		if (tid_npc_active) getServer().getScheduler().cancelTask(tid_npc.getTaskId());
		if (npc_enabled&&npc){
			tid_npc_active = true;
			tid_npc= getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
				public void run (){
					fillNPCList();
				}
			}, 20, npclistupdatetime*20);	
		}
	}

	public void refreshOnlineList(){
		hideShowList();
		showAndFillShowList();
	}
	public void hideShowList(){
		for (Player p : getServer().getOnlinePlayers()){
			for (Player pp : getServer().getOnlinePlayers()){
				FPOcbo.sendFakePlayerPacket(p, pp.getPlayerListName(), false, 0);
			}
			for (String s : showlist){
				FPOcbo.sendFakePlayerPacket(p,s, false, 0);
			}
		}
		showlist.clear();
	}

	public void showAndFillShowList(){
		showlist.clear();
		for (Player p : Bukkit.getServer().getOnlinePlayers()){
			if (!p.isOnline()) continue;
			String ppn = p.getPlayerListName();
			if (real){
				ppn = p.getName();
				String px = real_px;
				if (p.hasPermission("fakeplayers.red")) px = real_px_admin;
				ppn = ChatColor.translateAlternateColorCodes('&', px+ppn);
				if (ppn.length()>16) ppn = ppn.substring(0, 15);
			}
			p.setPlayerListName(ppn);
			showlist.add(ppn);
			for (Player pp : getServer().getOnlinePlayers()){
				if (pp.canSee(p)||pp.hasPermission("fakeplayers.canseehidden"))
					FPOcbo.sendFakePlayerPacket(pp, ppn, true, FPOcbo.getPlayerPing(p));
			}
		}
		int index = showlist.size();
		if (!fakeplayers.isEmpty()){
			for (int i = 0; i< fakeplayers.size(); i++){
				String fp = ChatColor.translateAlternateColorCodes('&', fake_px+fakeplayers.get(i));
				if (fp.length()>16) fp = fp.substring(0, 15);
				showlist.add(fp);
			}
		}
		if (!npclist.isEmpty())
			for (int i = 0; i< npclist.size(); i++){
				String fp = ChatColor.translateAlternateColorCodes('&', npc_px+npclist.get(i));
				if (fp.length()>16) fp = fp.substring(0, 15);
				showlist.add(fp);
			}
		if (index<showlist.size()){
			for (int i = index; i<showlist.size(); i++)
				for (Player pp : getServer().getOnlinePlayers()){
					FPOcbo.sendFakePlayerPacket(pp, showlist.get(i), true, 10);
				}				
		}
	}


	public int getMaxPlayers(){
		if (fakemaxplayersenable) return fakemaxplayers;
		return getServer().getMaxPlayers();
	}

	public int getPlayersOnline(){
		if (serverlocked) return getMaxPlayers();
		if (fake_serverlist&&fixedseverlist) return fixedonline;
		return Math.max(showlist.size(), getServer().getOnlinePlayers().length+fakeplayers.size()+npclist.size());
	}

	public String getMotd(){
		if (fakemotd) return ChatColor.translateAlternateColorCodes('&', motd);
		return getServer().getMotd();
	}

	public boolean isProtocolLibInstalled(){
		PluginManager pm = getServer().getPluginManager();
		Plugin test = pm.getPlugin("ProtocolLib");
		return (test != null);
	}
	
	public void lockServerKick(){
		Bukkit.getServer().broadcastMessage(u.getMSG("msg_serverwillbelocked",'e','6',kick_delay));
		getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable(){
			@Override
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers())
					if (!p.hasPermission("fakeplayers.unlock")) 
						p.kickPlayer(u.getMSGnc("msg_serverislocked",'6','6'));
			}
		}, kick_delay*20);
	}
	
	

}