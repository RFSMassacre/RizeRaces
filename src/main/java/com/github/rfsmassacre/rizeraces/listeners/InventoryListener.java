package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.gui.Icon;
import com.github.rfsmassacre.rizeraces.gui.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener
{
    /*
     * Prevent players from changing the menu.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMenuClick(InventoryClickEvent event)
    {
        Player player = (Player)event.getWhoClicked();
        String title = player.getOpenInventory().getTitle();
        Menu menu = Menu.getMenu(player.getUniqueId());
        if (menu != null && menu.getTitle().equals(title))
        {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            for (Icon icon : menu.getIcons())
            {
                if ((icon.getItemStack().isSimilar(item)
                        || icon.getItemStack().getType().equals(Material.PLAYER_HEAD))
                        && icon.getSlot() == event.getSlot())
                {
                    icon.onClick(player);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,
                            0.5F, 1.0F);
                    break;
                }
            }
        }
    }
}
