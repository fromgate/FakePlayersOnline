/*  
 *  FakePlayersOnline, Minecraft bukkit plugin
 *  (c)2012, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/skyfall/
 *    
 *  This file is part of Dogtags.
 *  
 *  FakePlayersOnline is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  FakePlayersOnline is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with WeatherMan.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package fromgate.fakeplayersonline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.server.Packet201PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;

public class FakePlayersOnline extends JavaPlugin {

	FPOUtil u;
	FPOCmd cmd;
	Logger log = Logger.getLogger("Minecraft");
	int tid_fake = -1;
	boolean tid_fake_active = false;
	int tid_npc = -1;
	boolean tid_npc_active = false;


	// Конфигурация
	boolean version_check = true;
	String language = "english";
	boolean language_save = false;
	int fakeupdatetime = 5; //секунды
	int npclistupdatetime = 60; //в секундах

	boolean fake = true;
	String fake_px = "&e";
	List<String> fakeplayers = new ArrayList<String>();


	boolean npc_enabled = false;
	boolean npc = true;
	String npc_px = "&3";

	boolean ban = true;
	String ban_px = "&4";

	List<String> npclist = new ArrayList<String>();
	List<String> banned = new ArrayList<String>();

	@Override
	public void onEnable() {
		LoadCfg();
		SaveCfg();
		u = new FPOUtil (this, version_check, language_save, language, "fakeplayers", "FakePlayersOnline","fpo","&3[FPO]&f");
		cmd = new FPOCmd (this);
		getCommand ("fpo").setExecutor(cmd);

		npc_enabled = isCitzensInstalled();

		if (npc_enabled) log.info("[FakePlayersOnline] Citizens is found!");
		else log.info("[FakePlayersOnline] Citizens is not found!");

		restartTicks();

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
			log.info("[FakePlayersOnline] failed to submit stats to the Metrics (mcstats.org)");
		}

	}




	public void sendAllFakes(boolean show){
		for (Player p: Bukkit.getOnlinePlayers()){
			if (p.isOnline()) {
				sendFakePlayers (p,show);
				if (npc_enabled&&npc) sendNPC(p, show);
			}
		}
	}

	public void sendAllNPC (boolean show){
		for (Player p: Bukkit.getOnlinePlayers())
			if (npc_enabled&&npc&&p.isOnline()) sendNPC(p, show);
	}




	public void sendFakePlayers (Player p, boolean show){
		if (fakeplayers.size()>0)
			for (String fakeplayer : fakeplayers){
				String fp = ChatColor.translateAlternateColorCodes('&', fake_px+fakeplayer);
				if (fp.length()>16) fp = fp.substring(0, 15);
				((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(fp,show,10));
			}
	}

	public void sendNPC (Player p, boolean show){
		if (npclist.size()>0)
			for (String fakenpc : npclist){
				String fp = ChatColor.translateAlternateColorCodes('&', npc_px+fakenpc);
				if (fp.length()>16) fp = fp.substring(0, 15);
				((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet201PlayerInfo(fp,show,10));
			}
	}

	public void LoadCfg(){
		version_check = getConfig().getBoolean("general.check-updates", true);;
		language = getConfig().getString("general.language", "english");
		language_save = getConfig().getBoolean("general.language-save", false);
		npclistupdatetime = getConfig().getInt("general.npc-list-update-time",60);
		fakeupdatetime = getConfig().getInt("general.send-fakes-interval",5);
		fake = getConfig().getBoolean("fake-players.enabled", true);
		fake_px = getConfig().getString("fake-players.prefix", "&e");
		fakeplayers = getConfig().getStringList("fake-players.list");
		npc = getConfig().getBoolean("citizens.enabled", true);
		npc_px = getConfig().getString("citizens.prefix", "&3");

	}

	public void SaveCfg(){
		getConfig().set("general.check-updates", version_check);;
		getConfig().set("general.language", language);
		getConfig().set("general.send-fakes-interval",fakeupdatetime);
		getConfig().set("general.npc-list-update-time",npclistupdatetime);
		getConfig().set("fake-players.enabled", fake);
		getConfig().set("fake-players.prefix", fake_px);
		getConfig().set("fake-players.list",fakeplayers);
		getConfig().set("citizens.enabled", npc);
		getConfig().set("citizens.prefix", npc_px);
		saveConfig();
	}


	public boolean isCitzensInstalled(){
		PluginManager pm = getServer().getPluginManager();
		Plugin test = pm.getPlugin("Citizens");
		return (test != null);
	}

	public void fillNPCList(){
		npclist.clear();
		if (npc_enabled&&npc){
			sendAllNPC(false);
			for (HumanNPC hpc: CitizensManager.getList().values())	
				npclist.add(hpc.getName());
			sendAllNPC(true);
		}
	}

	public void restartTicks(){

		if (tid_fake_active) getServer().getScheduler().cancelTask(tid_fake);
		if (tid_npc_active) getServer().getScheduler().cancelTask(tid_npc);


		tid_fake_active = true;
		tid_fake = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
			public void run (){
				sendAllFakes(true);
			}
		}, 1, fakeupdatetime*20);  

		if (npc_enabled&&npc){
			tid_npc_active = true;
			tid_npc= getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
				public void run (){
					fillNPCList();
				}
			}, 20, npclistupdatetime*20);		
		}
	}



}
