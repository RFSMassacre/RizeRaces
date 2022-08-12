package com.github.rfsmassacre.rizeraces.tasks;

import com.github.rfsmassacre.rizeraces.gui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.UUID;

public class InventoryUpdateTask implements Runnable
{
    @Override
    public void run()
    {
        for (Iterator<UUID> iterator = Menu.getMenus().keySet().iterator(); iterator.hasNext();)
        {
            UUID viewerId = iterator.next();
            Player player = Bukkit.getPlayer(viewerId);
            if (player == null)
            {
                continue;
            }

            Menu menu = Menu.getMenu(player.getUniqueId());
            String title = player.getOpenInventory().getTitle();
            if (menu.getTitle().equals(title))
            {
                menu.updateInventory(player);
            }
            else
            {
                iterator.remove();
            }
        }
    }
}
