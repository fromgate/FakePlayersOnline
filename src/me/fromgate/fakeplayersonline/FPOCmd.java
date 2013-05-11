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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FPOCmd implements CommandExecutor {
	FakePlayersOnline plg;
	FPOUtil u;

	public FPOCmd (FakePlayersOnline plg){
		this.plg = plg;
		this.u = plg.u;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (sender instanceof Player){
			Player p = (Player) sender;
			if ((args.length>0)&&(u.checkCmdPerm(p, args[0]))){
				if (args.length==1) return ExecuteCmd (p, args[0]);
				else if (args.length==2) return ExecuteCmd (p, args[0],args[1]);
				else if (args.length>2){
					String arg = args[1];
					for (int i=2; i<args.length; i++)
						arg = arg+" "+args[i];
					return ExecuteCmd (p, args[0],arg);
				}
			} else u.printMSG(p, "cmd_cmdpermerr",'c');
		}
		return false;
	}

	public boolean ExecuteCmd (Player p, String cmd){
		if (cmd.equalsIgnoreCase("help")){
			u.PrintHlpList(p, 1, 12);
		} else if (cmd.equalsIgnoreCase("slots")){	
			plg.fakemaxplayersenable = !plg.fakemaxplayersenable;
			u.printEnDis(p, "msg_maxplayers",plg.fakemaxplayersenable);
		} else if (cmd.equalsIgnoreCase("listcmd")){	
			plg.listcmdoverride = !plg.listcmdoverride;
			u.printEnDis(p, "msg_fakelistcmdoverride",plg.listcmdoverride);
		} else if (cmd.equalsIgnoreCase("serverlist")){
			plg.fake_serverlist=!plg.fake_serverlist;
			u.printEnDis(p, "msg_fakeserverlist",plg.fake_serverlist);
		} else if (cmd.equalsIgnoreCase("online")){
			plg.fixedseverlist=!plg.fixedseverlist;
			u.printEnDis(p, "msg_fixedseverlist",plg.fixedseverlist);			
		} else if (cmd.equalsIgnoreCase("motd")){
			plg.fakemotd = !plg.fakemotd;
			u.printEnDis(p, "msg_fakemotd",plg.fakemotd);
		} else if (cmd.equalsIgnoreCase("list")){
			if (plg.fakeplayers.size()>0){
				String str ="";
				for (int i = 0; i<plg.fakeplayers.size();i++)
					str = str +", "+plg.fakeplayers.get(i);
				str = str.replaceFirst(", ", "");
				u.printMSG(p, "msg_fplist",str);
			} else u.printMSG(p, "msg_fplistempty");
	    } else if (cmd.equalsIgnoreCase("real")) {
	        plg.real = (!plg.real);
	        plg.refreshOnlineList();
	        u.printEnDis(p, "msg_realstatus", plg.real);
		} else if (cmd.equalsIgnoreCase("fake")){
			plg.fake = !plg.fake;
			u.printEnDis(p, "msg_fakestatus", plg.fake);
			plg.refreshOnlineList();
		} else if (cmd.equalsIgnoreCase("npc")){
			if (plg.npc_enabled){
				plg.npc = !plg.npc;
				u.printEnDis(p, "msg_npcstatus", plg.npc);
				plg.restartTicks();
				plg.refreshOnlineList();
			} else u.printMSG(p, "msg_citizenserror",'c');
		} else if (cmd.equalsIgnoreCase("reload")){
			plg.reloadConfig();
			plg.LoadCfg();
			plg.restartTicks();
			plg.refreshOnlineList();
			u.printMSG(p, "msg_reload");
		} else if (cmd.equalsIgnoreCase("cfg")){
			u.showCfg(p);
		} else return false;
		plg.SaveCfg();	
		
		return true;
	}

	public boolean ExecuteCmd (Player p, String cmd, String arg){
		if (cmd.equalsIgnoreCase("add")){
			plg.fakeplayers.add(arg);
			u.printMSG(p, "msg_fakeadded", arg);
			plg.refreshOnlineList();
		} else if (cmd.equalsIgnoreCase("listcmd")){
			plg.listcmd = arg;
			plg.listcmd = plg.listcmd.replace(" ", ",");
			plg.listcmd = plg.listcmd.replace(",,", ",");
			u.printMSG(p, "msg_listcmdalias", plg.listcmd);
		} else if (cmd.equalsIgnoreCase("del")){
			if (plg.fakeplayers.contains(arg)){
				plg.fakeplayers.remove(arg);
				u.printMSG(p, "msg_fakeremoved", arg);
				plg.refreshOnlineList();
			} else u.printMSG(p, "msg_fakeunknown", arg);
		} else if (cmd.equalsIgnoreCase("online")){
			if (u.isInteger(arg)){
				plg.fixedseverlist = true;
				plg.fixedonline = Integer.parseInt(arg);
				u.printMSG(p, "msg_fixedonline", plg.fixedonline);
			} else u.printMSG(p, "msg_wrongfixedonline", arg);
		} else if (cmd.equalsIgnoreCase("slots")){
			if (u.isInteger(arg)){
				plg.fakemaxplayersenable = true;
				plg.fakemaxplayers = Integer.parseInt(arg);
				u.printMSG(p, "msg_maxplayersslot", plg.fakemaxplayers);
			} else u.printMSG(p, "msg_wrongfixedonline", arg); 
		} else if (cmd.equalsIgnoreCase("motd")){
			if (arg.isEmpty()||(arg.equalsIgnoreCase("-clear"))) plg.motd = "&4FakePlayersOnline &6installed!";
			else plg.motd = arg;
			plg.fakemotd = true;
			u.printMSG(p, "msg_motd", arg);
		} else if (cmd.equalsIgnoreCase("help")){
			int pnum = 1;
			if (u.isIntegerGZ(arg)) pnum = Integer.parseInt(arg);
			u.PrintHlpList(p, pnum, 12);
		} else return false;
		plg.SaveCfg();
		return true;
	}

}