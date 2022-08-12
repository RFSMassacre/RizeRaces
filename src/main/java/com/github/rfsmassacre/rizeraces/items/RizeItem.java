package com.github.rfsmassacre.rizeraces.items;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import com.github.rfsmassacre.spigot.items.HeavenItem;
import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class RizeItem extends HeavenItem
{
    public RizeItem(Material material, int amount, String name)
    {
        super(RizeRaces.getInstance(), material, amount, name, name, new ArrayList<>());

        Configuration config = RizeRaces.getInstance().getItemConfig();
        String displayName = config.getString(name.toUpperCase() + ".name");
        List<String> lore = config.getStringList(name.toUpperCase() + ".lore");
        this.setDisplayName(Locale.format(displayName));
        for (int slot = 0; slot < lore.size(); slot++)
        {
            String line = lore.get(slot);
            lore.set(slot, Locale.format(line));
        }

        this.setItemLore(lore);
        this.recipe = this.createRecipe();
    }

    protected void replaceHoldersName(String... holders)
    {
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();
        for (int holder = 0; holder < holders.length; holder += 2)
        {
            name = name.replace(holders[holder], holders[holder + 1]);
        }

        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }
    protected void replaceHoldersLore(String... holders)
    {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
        {
            lore = new ArrayList<>();
        }

        for (int index = 0; index < lore.size(); index++)
        {
            String line = lore.get(index);
            for (int holder = 0; holder < holders.length; holder += 2)
            {
                line = line.replace(holders[holder], holders[holder + 1]);
            }
            lore.set(index, Locale.format(line));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
