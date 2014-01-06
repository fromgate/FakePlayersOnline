FakePlayersOnline
=================

This plugin allows you to add additional fake players in your server player list, which you see when press "tab"-key in game. Fake players could be added manually or you can use NPC's name to show at player list.

Features
-----------

* Colorize real players name in list. In addition you can define color for name of your server admin group
* Display fake player names in list.
* Display NPC's (provided by Citizens plugin) name in list
* Add/remove fake players
* Override /list command and show same information as in TAB-list
* Fake serverlist menu to show fake counters for online players, reserverd slots and motd.

Why do I want it?
--------

You need to install FakePlayersOnline if you need to:

* Change color of player list
* Show a fake information about logged-in players;
* Show NPC's name to your players;
* Show an additional "moto" or short message in player list;

How to use it?
--------------

* Install FakePlayersOnline and [ProtocoLib](http://dev.bukkit.org/bukkit-plugins/protocollib/)
* Add fake players in list
* Press tab key

**This plugin is using library [ProtocoLib](http://dev.bukkit.org/bukkit-plugins/protocollib/) to provide a fake information at server list menu. If ProtocolLib is not installed you will be able to add fake players in TAB-list only.**

Commands
--------

* **/fpo help** - display help page
* **/fpo lock** - lock (and unlock) server. When server turns to locked all players will be kicked.
* **/fpo real** - toggle overriding real players in list
* **/fpo add <fakeplayer>** - add fake player to list
* **/fpo join <fakeplayer>** - add fake player to list and broadcast join-message
* **/fpo del <fakeplayer>** - remove fake player from list
* **/fpo leave <fakeplayer>** - remove fake player from the list and broadcast leave-message
* **/fpo list** - display fake playerlist
* **/fpo fake** - toggle displaying fake players in list
* **/fpo npc** - toggle displaying NPC's names in list
* **/fpo serverlist** - toggle fake online info at server list menu
* **/fpo slots [fake reserved slots]** - toggle faking the reserved slots counter or set it's value
* **/fpo listcmd [command1,command2..]** - toogle using the /list command overriding, or define /list command aliases
* **/fpo online [fake players online]** - toggle faking the fixed online counter or set it's value
* **/fpo motd [MOTD]** - toggle using the alternative MOTD or set it's value
* **/fpo cfg** - display current configuration

Permissions
-----------

* **fakeplayers.config** - allows to use all commands of the FakePlayersOnline
* **fakeplayers.canseehidden** - players with this permission will see name of hidden players in list (FakeQuit, Vanish, etc...)
* **fakeplayers.red** - will color name of players with same permission to red (by default, or any other color defined in config)
* **fakeplayers.unlock** - allows player to ignore lock-state of server (can connect to locked server, will not kicked after /fpo lock command)

Metrics and update checker
--------------------------
PlayEffect includes two features that use your server internet connection. First one is Metrics, using to collect [information about the plugin](http://mcstats.org/plugin/PlayEffect) (versions of plugin, of Java.. etc.) and second is update checker, checks new releases of plugin after PlayEffect startup and every half hour. This feature is using API provided by dev.bukkit.org. If you don't like this features you can easy disable it. To disable update checker you need to set parameter "version-check" to "false" in config.yml. Obtain more information about Metrics and learn how to switch off it, you can read [here](http://mcstats.org/learn-more/).