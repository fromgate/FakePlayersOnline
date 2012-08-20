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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FPOCmd implements CommandExecutor  {
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
			if ((args.length>0)&&(u.CheckCmdPerm(p, args[0]))){
				if (args.length==1) return ExecuteCmd (p, args[0]);
				else if (args.length==2) return ExecuteCmd (p, args[0],args[1]);
				else if (args.length>2){
					String arg = args[1];
					for (int i=2; i<args.length; i++)
						arg = arg+" "+args[i];
					return ExecuteCmd (p, args[0],arg);
				}
			} else u.PrintMSG(p, "cmd_cmdpermerr",'c');
		}  
		return false;
	}
	
	public boolean ExecuteCmd (Player p, String cmd){
		if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p);
			return true;
		} else if (cmd.equalsIgnoreCase("list")){
			if (plg.fakeplayers.size()>0){
				String str ="";
				for (int i = 0; i<plg.fakeplayers.size();i++)
					str = str +", "+plg.fakeplayers.get(i);
				str = str.replaceFirst(", ", "");
				u.PrintMSG(p, "msg_fplist",str);
			} else u.PrintMSG(p, "msg_fplistempty");
			return true;
		} else if (cmd.equalsIgnoreCase("fake")){
			plg.sendAllFakes(false);
			plg.fake = !plg.fake;
			plg.sendAllFakes(true);
			u.PrintEnDis(p, "msg_fakestatus", plg.fake);
			plg.SaveCfg();
			return true;
		} else if (cmd.equalsIgnoreCase("npc")){
			if (plg.npc_enabled){
				plg.sendAllFakes(false);
				plg.npc = !plg.npc;
				plg.sendAllFakes(true);
				u.PrintEnDis(p, "msg_npcstatus", plg.npc);
				plg.SaveCfg();
				plg.restartTicks();
			} else u.PrintMSG(p, "msg_citizenserror",'c');
			
			return true;			
		} else if (cmd.equalsIgnoreCase("cfg")){
			u.showCfg(p);
			return true;
		}
		return false;
	}
	
	public boolean ExecuteCmd (Player p, String cmd, String arg){
		if (cmd.equalsIgnoreCase("add")){
			plg.sendAllFakes(false);
			plg.fakeplayers.add(arg);
			u.PrintMSG(p, "msg_fakeadded", arg);
			plg.sendAllFakes(true);
			plg.SaveCfg();
			return true;
		} else if (cmd.equalsIgnoreCase("del")){
			if (plg.fakeplayers.contains(arg)){
				plg.sendAllFakes(false);
				plg.fakeplayers.remove(arg);
				plg.sendAllFakes(true);
				u.PrintMSG(p, "msg_fakeremoved", arg);
				plg.SaveCfg();
			} else u.PrintMSG(p, "msg_fakeunknown", arg);
			return true;
		} else if (cmd.equalsIgnoreCase("help")){
			u.PrintHLP(p, arg);
			return true;
		} 
		return false;
	}

}
