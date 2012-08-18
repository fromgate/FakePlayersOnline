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

import org.bukkit.entity.Player;


public class FPOUtil extends FGUtilCore {
	FakePlayersOnline plg;

	public FPOUtil(FakePlayersOnline plg, boolean vcheck, boolean savelng, String lng,
			String devbukkitname, String version_name, String plgcmd, String px) {
		super(plg, vcheck, savelng, lng, devbukkitname, version_name, plgcmd, px);
		this.plg = plg;
		initMSG();
		initCmd();
	}

	private void initCmd(){
		AddCmd("help", "config", MSG("hlp_helpcmd", "/fpo help"));
		AddCmd("add", "config", MSG("hlp_helpadd", "/fpo add <fakeplayer>"));
		AddCmd("del", "config", MSG("hlp_helpdel", "/fpo del <fakeplayer>"));
		AddCmd("list", "config", MSG("hlp_helplist", "/fpo list"));
		AddCmd("fake", "config", MSG("hlp_helpfake", "/fpo fake"));
		AddCmd("npc", "config", MSG("hlp_helpnpc", "/fpo npc"));
		AddCmd("cfg", "config", MSG("hlp_helpcfg", "/fpo cfg"));
	}
	
	private void initMSG(){
		addMSG ("msg_fplist", "Fake players: %1%");
		addMSG ("msg_fplistempty", "No fake players in list!");
		addMSG ("msg_fakestatus", "Show fake players");
		addMSG ("msg_npcstatus", "Show NPCs");
		addMSG ("msg_fakeadded", "Fake player added: %1%");
		addMSG ("msg_fakeremoved", "Fake player removed: %1%");
		addMSG ("msg_fakeunknown", "Fake player not found: %1%");
		addMSG ("hlp_helpadd", "%1% - add fake player to the list");
		addMSG ("hlp_helpdel", "%1% - remove fake player from the list");
		addMSG ("hlp_helplist", "%1% - display current fake player list");
		addMSG ("hlp_helpnpc", "%1% - toggle displaying fakes in player list");
		addMSG ("hlp_helpnpc", "%1% - toggle displaying NPC in player list");
		addMSG ("hlp_helpcfg", "%1% - display current configuration");
		addMSG ("msg_configuration", "Configuration");
		addMSG ("msg_cfgfake", "Display fake players: %1%, in list: %2%");
		addMSG ("msg_cfgnpc", "Display NPC: %1%, in list: %2%");
		addMSG ("msg_citizenserror", "Citizens plugin not found (is it installed?)");
	}
	
	protected void showCfg(Player p){
		PrintMsg(p, "&6&l"+des.getName()+" v"+des.getVersion()+" &r&6| "+MSG("msg_configuration",'6'));
		PrintMSG(p, "msg_cfgfake",EnDis(plg.fake)+";"+plg.fakeplayers.size());
		PrintMSG(p, "msg_cfgnpc",EnDis(plg.npc)+";"+plg.npclist.size());
		
	}
}
