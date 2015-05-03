/*
 * FakePlayersOnline, Minecraft bukkit plugin
 * (c)2012-2014, fromgate, fromgate@gmail.com
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
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;

public class FPOCitizens12x {

	private static boolean enabled;

	public static void init(){
		enabled = false;
		Plugin test = Bukkit.getPluginManager().getPlugin("Citizens");
		if (test == null) return;
		enabled = (test != null);
		try {
			Class.forName("net.citizensnpcs.api.CitizensManager");
		} catch (ClassNotFoundException e) {
			FakePlayersOnline.instance.getLogger().info("Failed to connect to plugin Citizens 1.2.x");
			return;
		}
		enabled = true;
	}

	public static boolean isEnabled(){
		return enabled;
	}

	public static List<String> getNPCList() {
		List<String> npclist = new ArrayList<String>();
		if (!enabled) return npclist;
		try {
			for (HumanNPC hpc: CitizensManager.getList().values())  
				npclist.add(hpc.getName());
		} catch (Throwable e){
			enabled = false;
			FakePlayersOnline.instance.getLogger().info("Failed to connect to plugin Citizens 1.2.x");
		}
		return npclist;
	}



}
