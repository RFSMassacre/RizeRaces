package com.github.rfsmassacre.rizeraces.gui;

import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class Icon
{
    protected int x;
    protected int y;
    protected int amount;
    protected boolean glowing;
    protected String displayName;
    protected Material material;
    protected List<String> lore;

    public Icon(int x, int y, int amount, boolean glowing, Material material,
                String displayName, List<String> lore)
    {
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.glowing = glowing;
        this.displayName = Locale.format(displayName);
        this.material = material;
        this.lore = lore;

        for (int index = 0; index < lore.size(); index++)
        {
            this.lore.set(index, Locale.format(lore.get(index)));
        }
    }

    public int getSlot()
    {
        return (x + ((y - 1) * 9)) - 1;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getAmount()
    {
        return amount;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    public Material getMaterial()
    {
        return material;
    }
    public List<String> getLore()
    {
        return lore;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = Locale.format(displayName);
    }
    public void setLore(List<String> lore)
    {
        this.lore = lore;

        for (int index = 0; index < lore.size(); index++)
        {
            this.lore.set(index, Locale.format(lore.get(index)));
        }
    }

    public ItemStack getItemStack()
    {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (glowing)
        {
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }

    /*
     * Also define a function to each slot.
     */
    public abstract void onClick(Player clicker);
}
