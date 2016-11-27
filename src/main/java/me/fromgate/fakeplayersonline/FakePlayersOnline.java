/*
 * FakePlayersOnline, Minecraft bukkit plugin
 * (c)2012-2015, fromgate, fromgate@gmail.com
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FakePlayersOnline extends JavaPlugin {

    static FakePlayersOnline instance;
    FPOUtil u;
    FPOCmd cmd;
    Logger log = Logger.getLogger("Minecraft");

    BukkitTask tidNpc;
    boolean tidNpcActive = false;

    // Конфигурация
    boolean serverLocked = false;
    boolean serverUnlockOnReload = false;
    int kickDelay = 10;

    boolean versionCheck = true;
    String language = "english";
    boolean saveLanguage = false;
    int npcListUpdateTime = 60; //в секундах

    boolean enableFakeMaxPlayers = true;
    int fakeMaxPlayers = 50;

    boolean listCommandOverride = true;
    String listCommands = "list,players,online,playerlist,who";

    //List<String> showList= new ArrayList<String>();

    boolean real = true;

    boolean fake = true;
    String fakePx = "&e";
    String realPx = "&6";
    String realPxAdmin = "&4";
    List<String> fakePlayers = new ArrayList<String>();

    boolean protocolLibEnabled = false;
    boolean npc = true;
    String npcPx = "&3";

    //FakeServerList
    boolean enableFakeServerList = true;
    boolean fakeMotd = true;
    boolean enableFixedCounter = false;
    int fixedCounter = 10;
    String motd = "&4FakePlayersOnline &6installed!";
    boolean enableFakePlayersInServerList = true;

    String joinMessage;
    String leaveMessage;

    List<String> npcList = new ArrayList<String>();

    @Override
    public void onEnable() {
        loadCfg();
        if (serverLocked) serverLocked = !serverUnlockOnReload;
        saveCfg();
        u = new FPOUtil(this, versionCheck, saveLanguage, language, "fakeplayers", "FakePlayersOnline", "fpo", "&3[FPO]&f");
        cmd = new FPOCmd(this);
        getCommand("fpo").setExecutor(cmd);
        instance = this;
        CitizensUtil.init();
        getServer().getPluginManager().registerEvents(u, this);

        try {
            FPOPLib.init();
            if (enableFakeServerList) FPOPLib.initPacketListener();
            u.log("Connected to ProtocolLib!");
            protocolLibEnabled = true;
        } catch (Throwable ignore) {
            u.log("ProtocolLib is not found!");
        }

        FPOcbo.init();
        if ((!FPOcbo.isBlocked()) || FPOPLib.isEnabled()) {
            restartTicks();
        }

        ShowList.refreshOnlineList();
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
        }
    }


    public void loadCfg() {
        versionCheck = getConfig().getBoolean("general.check-updates", true);
        ;
        language = getConfig().getString("general.language", "english");
        saveLanguage = getConfig().getBoolean("general.language-save", false);
        npcListUpdateTime = getConfig().getInt("general.npc-list-update-time", 60);
        joinMessage = getConfig().getString("fake-players.message.join", "&ePlayer &6%player% &ejoined to server");
        leaveMessage = getConfig().getString("fake-players.message.leave", "&ePlayer &6%player% &edisconnected");
        fake = getConfig().getBoolean("fake-players.enabled", true);
        fakePx = getConfig().getString("fake-players.prefix", "&e");
        fakePlayers = getConfig().getStringList("fake-players.list");
        npc = getConfig().getBoolean("citizens.enabled", true);
        npcPx = getConfig().getString("citizens.prefix", "&3");
        real = getConfig().getBoolean("real-players.enable-override", true);
        realPx = getConfig().getString("real-players.prefix", "&6");
        realPxAdmin = getConfig().getString("real-players.prefix-red", "&4");
        enableFakeMaxPlayers = getConfig().getBoolean("fake-max-players.enabled", false);
        fakeMaxPlayers = getConfig().getInt("fake-max-players.fake-max-online", getServer().getMaxPlayers());
        enableFakeServerList = getConfig().getBoolean("fake-server-list.enabled", true);
        fakeMotd = getConfig().getBoolean("fake-server-list.motd.enabled", true);
        motd = getConfig().getString("fake-server-list.motd.value", "&4FakePlayersOnline &6installed!");
        enableFixedCounter = getConfig().getBoolean("fake-server-list.constant-online-count.enabled", false);
        fixedCounter = getConfig().getInt("fake-server-list.constant-online-count.players-online", 10);
        serverLocked = getConfig().getBoolean("server-lock.is-locked", false);
        serverUnlockOnReload = getConfig().getBoolean("server-lock.unlock-after-reload", true);
        kickDelay = getConfig().getInt("server-lock.kick-delay", 10);
        enableFakePlayersInServerList = getConfig().getBoolean("fake-server-list.fake-players-hint-in-server-list", true);
        listCommandOverride = getConfig().getBoolean("list-command-override.enable", true);
        listCommands = getConfig().getString("list-command-override.command-list", listCommands);
    }

    public void saveCfg() {
        getConfig().set("general.check-updates", versionCheck);
        ;
        getConfig().set("general.language", language);
        getConfig().set("general.language-save", saveLanguage);
        getConfig().set("general.npc-list-update-time", npcListUpdateTime);
        getConfig().set("real-players.enable-override", real);
        getConfig().set("real-players.prefix", realPx);
        getConfig().set("real-players.prefix-red", realPxAdmin);
        getConfig().set("fake-players.enabled", fake);
        getConfig().set("fake-players.prefix", fakePx);
        getConfig().set("fake-players.list", fakePlayers);
        getConfig().set("citizens.enabled", npc);
        getConfig().set("citizens.prefix", npcPx);
        getConfig().set("fake-max-players.enabled", enableFakeMaxPlayers);
        getConfig().set("fake-max-players.fake-max-online", fakeMaxPlayers);
        getConfig().set("fake-server-list.enabled", enableFakeServerList);
        getConfig().set("fake-server-list.motd.enabled", fakeMotd);
        getConfig().set("fake-server-list.motd.value", motd);
        getConfig().set("fake-server-list.constant-online-count.enabled", enableFixedCounter);
        getConfig().set("fake-server-list.constant-online-count.players-online", fixedCounter);
        getConfig().set("fake-server-list.fake-players-hint-in-server-list", enableFakePlayersInServerList);
        getConfig().set("server-lock.is-locked", serverLocked);
        getConfig().set("server-lock.unlock-after-reload", serverUnlockOnReload);
        getConfig().set("server-lock.kick-delay", kickDelay);
        getConfig().set("fake-players.message.join", joinMessage);
        getConfig().set("fake-players.message.leave", leaveMessage);
        getConfig().set("list-command-override.enable", listCommandOverride);
        getConfig().set("list-command-override.command-list", listCommands);
        saveConfig();
    }


    public void fillNPCList() {
        if (!CitizensUtil.isEnabled()) return;
        if (!npc) return;
        npcList = CitizensUtil.getNPCList();
        ShowList.refreshOnlineList();
    }

    public void restartTicks() {
        if (tidNpcActive) getServer().getScheduler().cancelTask(tidNpc.getTaskId());
        if (CitizensUtil.isEnabled() && npc) {
            tidNpcActive = true;
            tidNpc = getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
                public void run() {
                    fillNPCList();
                }
            }, 20, npcListUpdateTime * 20);
        }
    }

    public int getMaxPlayers() {
        if (enableFakeMaxPlayers) return fakeMaxPlayers;
        return getServer().getMaxPlayers();
    }

    public int getPlayersOnline() {
        if (serverLocked) return getMaxPlayers();
        if (enableFakeServerList && enableFixedCounter) return fixedCounter;
        return Math.max(ShowList.size(), getServer().getOnlinePlayers().size() + fakePlayers.size() + npcList.size());
    }

    public String getMotd() {
        if (fakeMotd) return ChatColor.translateAlternateColorCodes('&', motd);
        return getServer().getMotd();
    }

    public void lockServerKick() {
        Bukkit.getServer().broadcastMessage(u.getMSG("msg_serverwillbelocked", 'e', '6', kickDelay));
        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers())
                    if (!p.hasPermission("fakeplayers.unlock"))
                        p.kickPlayer(u.getMSGnc("msg_serverislocked", '6', '6'));
            }
        }, kickDelay * 20);
    }

    public static FakePlayersOnline getPlugin() {
        return instance;
    }


}