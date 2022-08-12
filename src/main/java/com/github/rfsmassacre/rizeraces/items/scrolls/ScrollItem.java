package com.github.rfsmassacre.rizeraces.items.scrolls;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.items.RizeItem;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ScrollItem extends RizeItem
{
    public enum Complexity
    {
        SIMPLE,
        ADVANCED,
        MASTER
    }

    public ScrollItem(Complexity complexity, Race race, boolean filled)
    {
        //Name = ComplexityScroll (Eg would be SimpleScroll)
        super(Material.BOOK, 1, filled ? "FilledScroll" : "EmptyScroll");

        setFilled(complexity, race, filled);
    }

    @Override
    protected Recipe createRecipe()
    {
        return null;
    }

    private void setGlow(boolean glow)
    {
        ItemMeta meta = item.getItemMeta();
        if (glow)
        {
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        else
        {
            for (Enchantment enchantment : meta.getEnchants().keySet())
            {
                meta.removeEnchant(enchantment);
            }
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
    }

    public void setAmount(int amount)
    {
        this.item.setAmount(amount);
    }

    public boolean isFilled()
    {
        return name.equals("FilledScroll");
    }

    public Complexity getComplexity()
    {
        return getComplexity(item);
    }

    public Race getRace()
    {
        return getRace(item);
    }

    public int getLevelRequired()
    {
        Complexity complexity = getComplexity(item);
        if (complexity == null)
        {
            return 0;
        }

        return getRequiredLevel(complexity);
    }
    public int getMaxLevel()
    {
        Complexity complexity = getComplexity(item);
        if (complexity == null)
        {
            return 0;
        }

        return getMaxLevel(complexity);
    }
    public int getMinLevel()
    {
        Complexity complexity = getComplexity(item);
        if (complexity == null)
        {
            return 0;
        }

        return getMinLevel(complexity);
    }

    private static int getRequiredLevel(Complexity complexity)
    {
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        return config.getInt("scroll.levels-required." + complexity.toString().toLowerCase());
    }
    private static int getMaxLevel(Complexity complexity)
    {
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        return config.getInt("scroll.max-level." + complexity.toString().toLowerCase());
    }
    private static int getMinLevel(Complexity complexity)
    {
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        return config.getInt("scroll.min-level." + complexity.toString().toLowerCase());
    }

    //Remember that empty scrolls MUST have a null race.
    private void setFilled(Complexity complexity, Race race, boolean filled)
    {
        Configuration config = RizeRaces.getInstance().getItemConfig();
        if (!filled && race == null)
        {
            String displayName = config.getString("EMPTYSCROLL.name");
            List<String> lore = config.getStringList("EMPTYSCROLL.lore");

            setDisplayName(displayName);
            setItemLore(lore);
            replaceHoldersName("{complexity}", Locale.capitalize(complexity.toString()));
            replaceHoldersLore("{level}", Integer.toString(getRequiredLevel(complexity)));

            setGlow(false);
        }
        else
        {
            String displayName = config.getString("FILLEDSCROLL.name");
            displayName = displayName.replace("{race}", Locale.capitalize(race.toString()));

            List<String> lore = config.getStringList("FILLEDSCROLL.lore");
            setDisplayName(displayName);
            setItemLore(lore);

            replaceHoldersName("{complexity}", Locale.capitalize(complexity.toString()));
            replaceHoldersLore("{min}", Integer.toString(getMinLevel(complexity)), "{max}",
                    Integer.toString(getMaxLevel(complexity)));

            setGlow(true);
        }
    }

    private static Complexity getComplexity(ItemStack item)
    {
        String displayName = item.getItemMeta().getDisplayName();
        for (Complexity complexity : Complexity.values())
        {
            if (displayName.contains(Locale.capitalize(complexity.toString())))
            {
                return complexity;
            }
        }

        return null;
    }
    private static Race getRace(ItemStack item)
    {
        String displayName = item.getItemMeta().getDisplayName();
        for (Race race : Race.values())
        {
            if (displayName.contains(Locale.capitalize(race.toString())))
            {
                return race;
            }
        }

        return null;
    }

    public static ScrollItem getScrollItem(ItemStack item)
    {
        if (item == null)
        {
            return null;
        }

        NBTItem otherItem = new NBTItem(item);
        NBTCompound compound = otherItem.getCompound("HeavenPlugin");
        if (compound == null)
        {
            return null;
        }

        String value = compound.getString("IID");
        if (value == null)
        {
            return null;
        }

        if (value.equals("EmptyScroll"))
        {
            //Get complexity
            Complexity complexity = getComplexity(item);
            if (complexity == null)
            {
                return null;
            }

            return new ScrollItem(complexity, null, false);
        }
        else if (value.equals("FilledScroll"))
        {
            //Get complexity
            Complexity complexity = getComplexity(item);
            if (complexity == null)
            {
                return null;
            }

            //Get race
            Race race = getRace(item);
            if (race == null)
            {
                return null;
            }

            return new ScrollItem(complexity, race, true);
        }
        else
        {
            return null;
        }
    }
}
