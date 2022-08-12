package com.github.rfsmassacre.rizeraces.items.potions;

import com.github.rfsmassacre.rizeraces.items.RizeItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.PotionMeta;

public abstract class RizePotion extends RizeItem
{
    public RizePotion(String name, Color color)
    {
        super(Material.POTION, 1, name);

        setColor(color);
        addFlag(ItemFlag.HIDE_POTION_EFFECTS);

        this.recipe = createRecipe();
    }
    public RizePotion(String name, Color color, boolean splash)
    {
        super(splash ? Material.SPLASH_POTION : Material.POTION, 1, name);

        setColor(color);
        addFlag(ItemFlag.HIDE_POTION_EFFECTS);

        this.recipe = createRecipe();
    }

    protected void setColor(Color color)
    {
        //Set the color of the potion
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
    }
    public Color getColor()
    {
        //Get the color of the potion
        PotionMeta meta = (PotionMeta)item.getItemMeta();
        return meta.getColor();
    }
}
