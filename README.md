Survival Games Reloaded [![Travis-CI](https://api.travis-ci.org/ThunderGemios10/Survival-Games.svg)](https://travis-ci.org/ThunderGemios10/Survival-Games)
=================================
Survival Games is a plugin that has the aim of creating a fully automated hunger games style experience. The plugin was created to be simple to use, and to be easily setup on any type of server, no matter the size or type! Survival Games provides a way for your server to have a full fledged, fully automated hunger games experience for any player. For more information on the aim of the plugin, watch popular youtuber AntVenom play Survival Games [HERE](http://www.youtube.com/watch?v=Lxqk1SRqW6I).

Requirments
-----------
Before installing this plugin, a few things are required.
 - [WorldEdit](http://dev.bukkit.org/server-mods/worldedit/)
 - MySQL Database (**optional**) - If you wish to use stats, you must have an SQL database.

Conflicts
---------
 - Any plugin that modifies damage amounts by applying more damage can sometimes cause issues and make the player actually die (ie McMMO).
 - Some locking plugins such as LWC have been reported to have issues with the lobby signs, but should work normally if protection is turned of for those signs.
 - WORLD PORTAL will make this plugin completely break if the lobby is not in a world that is loaded at start!  

Features
--------
This is not just another Hunger Games plugin. This plugin aims to bring a fully automated, fully fledged gaming system to your server.
 - Full automation
 - Automatic arena regeneration
 - Chest reset / randomly filled chest
 - Auto game start
 - Spectators
 - In-game Lobby with real-time player and arena stats
 - Multiple arenas
 - Simultaneous games
 - Web-based stats
 - Easy arena setup
 - Per Arena Permissions
 - Lightning and thunder on player deaths (Simulate cannons)
 - Multiworld Support
 - Events API - Economy, Kits + More!
Plus many, many more features included in this plugin!

Original Website
----------------
The documentation for the original plugin can be found at
http://dev.bukkit.org/bukkit-plugins/survival-games/pages/setup/installing/

Note that this updated plugin also has Bandages, and a modified and extended Chest Refil system.

Permissions
-----------
The players should be in Survival mode, and need to have permissions to place, interact with and break blocks.  You can define which blocks may be placed and broken in the `config.yml` file.

To join arena number 1, players need to have the following permissions:

 - sg.arenas.join.1  - Allows player to join lobby 1
=======
 - All known bugs, fixed.
 - Kits menu & config
 - Death match
 - and more!
Note: Devs and important people of this plugin have colored names on lobby signs

Read Before Posting!
--------------------
 - A full tutorial on kits, economy and new features will be released soon!
 - /sg addwall was changed to /sg setstatswall <arena> in versions 0.6.0 and above!
 - Need support? Find me on IRC @ irc.esper.net #survivalgames or use the webchat: http://webchat.esper.net/?channels=survivalgames  
*New Permissions:*

If you have for example 6 arenas, and you want every player to be able to join all arenas, give each rank the following permissions:

 - sg.arena.join.<arena#> (Replace <arena#> with the arena number.)
 - sg.arena.join
 - sg.arena.vote - Allows player to vote for start
 - sg.arena.spectate - Allows a user to spectate a game 
 
If you have problems with players being unable to join a game, make sure that there is no '.all' or '.*' permission for the 'join' group.  Grant permissions explicitly.

Staff can also have the following permissions:

 - sg.arena.forcestart - Allows the user to Start Sg (/sg start)
 - sg.arena.disable - Allows the user to disable an arena
 - sg.arena.enable - Allows the user to enable an arena
 - sg.arena.nocmdblock - Allows user to bypass ingame cmd block 

To create an Arena and Lobby, you should be an Operator or have the following:

 - sg.arena.create - Allows the user to create an arena
 - sg.arena.setarenaspawns - Allows user to set spawns
 - sg.arena.resetspawns - Allows the user to reset all spawnpoints
 - sg.arena.delete - Allows a user to delete an arena
 - sg.lobby.set - Allows the user to set the lobby spawn and wall 

Creating a World
----------------
There are several Survival Games worlds you can download, or you can of course make your own.  The World should have PVP enabled, and be in Survival mode.  You can optionally disable spawning monsters; this may be a good idea as players can otherwise be attacked while waiting for the game to start.

You should have some sort of wall around your playing arena to prevent players from leaving.  Outside, you need to have a Lobby area somewhere for players to gather and select an Arena.
 
Creating a Lobby
----------------
Create your lobby are somewhere outside of the Arena.  Generally, you will want to use WorldGuard or similar to protect it from damage, and disable PvP within the lobby, though this is optional.  The Lobby will usually have your World spawn point, or be connected to it somehow.

Stand in the Lobby in the location you want people to respawn after a game, and run `/sg setlobbyspawn`

You can create a Wall for an Arena (once you have made the arena) like this:
 -  place two Wall Signs next to each other
 -  select them (using the WorldEdit Wand and left-click on the first sign, right-click on the second)
 -  Use the command `/sg addwall X` where X is the Arena number -- if you only have one Arena, this will be 1.

Creating an arena
-----------------
This required the WorldEdit wand.

Use `//wand` to obtain the WorldEdit wand (usually, a golden axe).  With the wand, select the entire arena area by left-clicking one corner, and right-clicking the opposite corner.  Don't forget to cover all the vertical area as well!  You can use `//expand vert` to expand your selected area from bedrock to the top of the sky if you want; see the WorldEdit documentation for other WorldEdit commands.

Once selected, use `/sg creatarena` to create the arena, and find out its number.  You can use `/sg listarenas` to list all the currently defined Arenas.

The next thing to do is to define the spawn points for the arena.  Use `/sg setspawn 1` to set your current location to be spawn point number 1.  You can then use `/sg setspawn next` repeatedly to keep adding new spawn points.  The number of spawn points is, of course, the maximum number of players for your arena.

Now you have an arena, you need to have a Wallsign for it in the Lobby.  See the above section for help on doing this.

Defining Kits
-------------
To be added.

Bandages
--------
The 'paper' item acts in-game as a Bandage.  Right-clicking when holding a piece of paper will add 5 hearts and destroy the paper.

Automatic Chest Refills
-----------------------
Chests will be randomly filled at the time they are first opened.  If the item in Slot 0 of the chest is a wool block, then the Base level of the chest will be set to the colour code of the wool plus 1; otherwise, it will default to using Level 1.

A chest Base Level will be randomly increased by up to `chest.maxincrese` in the chest.yml file, with a chance of 1 in `chest.ratio` of going up a level.  When a level is determined, then a random number between `chest.min` and `chest.max` is selected and this many random items are picked from the `chest.lvlX` group (for level X).  The same item may be picked multiple times.

If the `clear-chest` option in `config.yml` is true, then the chest is emptied before random items are selected.  Otherwise, only the Wool Block (if present) is removed, and the other items are left in place.

Items defined in the `chest.lvlX` sections may be given in multiple forms, optionally quoted.  The identifier can be given as a name or as a number, and all fields after the quantity are optional.  Here are some examples:
- 236,1   (Item number and quantity)
- diamond_sword,1   (Item name and quantity)
- dye,5,2   (Item name/number, quantity, and DV)
- "diamond_sword,1,0,sharpness:2 looting:1"  (Item name/number, quantity, DV, enchantment list)
- "iron_sword,1,0,sharpness:2 fire:5,Excalibur"  (Item name/number, quantity, DV, enchantment list, and name)
The Enchantment names are the lower-case class names from Bukkit without underscores.

If the `restock-chest` option in config.yml is set, chests will be reset (new random items added) at the first midnight.  If `restock-chest-repeat` is set, this will be done every midnight.

Chests will be reset at the end of the game, along with any blocks which have been changed.

Commands
--------
To be added.

Playing the game
----------------
Players should start in the Lobby.  They right-click on the sign, and this takes them into the game (or places them in the queue if the game is already full or in progress).

In the `config.yml` you can set `auto-start-players` to be the minimum number of players required to automatically start the game.  If there are fewer players, then players can use `/sg vote` until more than `auto-start-vote` percent of them have voted, at which point the game will start.

Players can use `/sg leave` to leave the game early; they can use `/sg leavequeue` or `/sg lq` to leave the queue.
