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

import org.bukkit.Bukkit;
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
    public boolean onCommand(CommandSender p, Command cmd, String cmdLabel, String[] args) {
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
        return false;
    }

    public boolean ExecuteCmd (CommandSender p, String cmd){
        if (cmd.equalsIgnoreCase("help")){
            u.PrintHlpList(p, 1, 12);
        } else if (cmd.equalsIgnoreCase("lock")){
            plg.serverLocked = !plg.serverLocked;
            if (plg.serverLocked) {
                plg.lockServerKick();
                u.printMSG(p, "msg_locktounlock");
            } else u.printMSG(p, "msg_serverunlocked");


        } else if (cmd.equalsIgnoreCase("slots")){	
            plg.enableFakeMaxPlayers = !plg.enableFakeMaxPlayers;
            u.printEnDis(p, "msg_maxplayers",plg.enableFakeMaxPlayers);
        } else if (cmd.equalsIgnoreCase("listcmd")){	
            plg.listCommandOverride = !plg.listCommandOverride;
            u.printEnDis(p, "msg_fakelistcmdoverride",plg.listCommandOverride);
        } else if (cmd.equalsIgnoreCase("serverlist")){
            plg.enableFakeServerList=!plg.enableFakeServerList;
            u.printEnDis(p, "msg_fakeserverlist",plg.enableFakeServerList);
        } else if (cmd.equalsIgnoreCase("online")){
            plg.enableFixedCounter=!plg.enableFixedCounter;
            u.printEnDis(p, "msg_fixedseverlist",plg.enableFixedCounter);			
        } else if (cmd.equalsIgnoreCase("motd")){
            plg.fakeMotd = !plg.fakeMotd;
            u.printEnDis(p, "msg_fakemotd",plg.fakeMotd);
        } else if (cmd.equalsIgnoreCase("list")){
            if (plg.fakePlayers.size()>0){
                String str ="";
                for (int i = 0; i<plg.fakePlayers.size();i++)
                    str = str +", "+plg.fakePlayers.get(i);
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
            if (plg.enableNpc){
                plg.npc = !plg.npc;
                u.printEnDis(p, "msg_npcstatus", plg.npc);
                plg.restartTicks();
                plg.refreshOnlineList();
            } else u.printMSG(p, "msg_citizenserror",'c');
        } else if (cmd.equalsIgnoreCase("reload")){
            plg.reloadConfig();
            plg.loadCfg();
            plg.restartTicks();
            plg.refreshOnlineList();
            u.printMSG(p, "msg_reload");
        } else if (cmd.equalsIgnoreCase("cfg")){
            u.showCfg(p);
        } else return false;
        plg.saveCfg();	

        return true;
    }

    public void broadcastJoinLeaveMsg (String message, String fakeName){
        for (Player p : Bukkit.getOnlinePlayers()){
            u.printMsg(p, message.replace("%player%", p.hasPermission("fakeplayers.config") ? fakeName+" (fake)" : fakeName));
        }
        
    }
    
    public boolean ExecuteCmd (CommandSender p, String cmd, String arg){
        if (cmd.equalsIgnoreCase("add")){
            plg.fakePlayers.add(arg);
            u.printMSG(p, "msg_fakeadded", arg);
            plg.refreshOnlineList();
        } else if (cmd.equalsIgnoreCase("join")){
            plg.fakePlayers.add(arg);
            broadcastJoinLeaveMsg(plg.joinMessage,arg);
            plg.refreshOnlineList();
        } else if (cmd.equalsIgnoreCase("leave")){
            if (plg.fakePlayers.contains(arg)){
                plg.fakePlayers.remove(arg);
                broadcastJoinLeaveMsg(plg.leaveMessage,arg);
                plg.refreshOnlineList();
            } else u.printMSG(p, "msg_fakeunknown", arg);
        } else if (cmd.equalsIgnoreCase("listcmd")){
            plg.listCommands = arg;
            plg.listCommands = plg.listCommands.replace(" ", ",");
            plg.listCommands = plg.listCommands.replace(",,", ",");
            u.printMSG(p, "msg_listcmdalias", plg.listCommands);
        } else if (cmd.equalsIgnoreCase("del")){
            if (plg.fakePlayers.contains(arg)){
                plg.fakePlayers.remove(arg);
                u.printMSG(p, "msg_fakeremoved", arg);
                plg.refreshOnlineList();
            } else u.printMSG(p, "msg_fakeunknown", arg);
        } else if (cmd.equalsIgnoreCase("online")){
            if (u.isInteger(arg)){
                plg.enableFixedCounter = true;
                plg.fixedCounter = Integer.parseInt(arg);
                u.printMSG(p, "msg_fixedonline", plg.fixedCounter);
            } else u.printMSG(p, "msg_wrongfixedonline", arg);
        } else if (cmd.equalsIgnoreCase("slots")){
            if (u.isInteger(arg)){
                plg.enableFakeMaxPlayers = true;
                plg.fakeMaxPlayers = Integer.parseInt(arg);
                u.printMSG(p, "msg_maxplayersslot", plg.fakeMaxPlayers);
            } else u.printMSG(p, "msg_wrongfixedonline", arg); 
        } else if (cmd.equalsIgnoreCase("motd")){
            if (arg.isEmpty()||(arg.equalsIgnoreCase("-clear"))) plg.motd = "&4FakePlayersOnline &6installed!";
            else plg.motd = arg;
            plg.fakeMotd = true;
            u.printMSG(p, "msg_motd", arg);
        } else if (cmd.equalsIgnoreCase("help")){
            int pnum = 1;
            if (u.isIntegerGZ(arg)) pnum = Integer.parseInt(arg);
            u.PrintHlpList(p, pnum, 12);
        } else return false;
        plg.saveCfg();
        return true;
    }

}