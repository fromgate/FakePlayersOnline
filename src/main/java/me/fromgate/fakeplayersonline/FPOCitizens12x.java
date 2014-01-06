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

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;

public class FPOCitizens12x {
    public static List<String> getNPCList() {
        List<String> npclist = new ArrayList<String>();
        try {
            for (HumanNPC hpc: CitizensManager.getList().values())  
                npclist.add(hpc.getName());
        } catch (Exception e){
            FakePlayersOnline.instance.enableNpc = false;
            FakePlayersOnline.instance.u.log("Failed to connect to plugin Citizens 1.2.x");
            npclist.clear();
        }
        return npclist;
    }
    
    

}
