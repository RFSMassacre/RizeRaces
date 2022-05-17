package com.github.rfsmassacre.rizeraces.utils;

import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.text.DecimalFormat;
import java.util.*;

public class ConfigUtil
{
    public static Map<Material, Integer> getMaterialInteger(Configuration config, String key)
    {
        Map<Material, Integer> materials = new HashMap<Material, Integer>();
        List<String> strings = config.getStringList(key);
        for (String string : strings)
        {
            String[] parts = string.split(":");
            Material material = Material.valueOf((String)parts[0]);
            int amount = Integer.parseInt(parts[1]);
            materials.put(material, amount);
        }
        return materials;
    }

    public static Map<Material, Double> getMaterialDouble(Configuration config, String key)
    {
        Map<Material, Double> materials = new HashMap<Material, Double>();
        List<String> strings = config.getStringList(key);
        for (String string : strings)
        {
            String[] parts = string.split(":");
            Material material = Material.valueOf((String)parts[0]);
            double amount = Double.parseDouble(parts[1]);
            materials.put(material, amount);
        }
        return materials;
    }

    public static List<ItemStack> getItemStackList(Configuration config, String key)
    {
        List<ItemStack> items = new ArrayList<>();
        List<String> strings = config.getStringList(key);
        for (String string : strings)
        {
            String[] parts = string.split(":");
            int amount = Integer.parseInt(parts[1]);
            if (parts[0].equalsIgnoreCase("WATER_BOTTLE"))
            {
                ItemStack waterBottle = new ItemStack(Material.POTION);
                PotionMeta meta = (PotionMeta)waterBottle.getItemMeta();
                if (meta == null)
                {
                    continue;
                }

                meta.clearCustomEffects();
                meta.setBasePotionData(new PotionData(PotionType.WATER));
                waterBottle.setItemMeta((ItemMeta)meta);
                items.add(waterBottle);
                continue;
            }

            Material material = Material.valueOf((String)parts[0]);
            items.add(new ItemStack(material, amount));
        }
        return items;
    }

    public static Set<Material> getMaterialSet(Configuration config, String key)
    {
        Set<Material> materials = new HashSet<Material>();
        List<String> strings = config.getStringList(key);
        for (String string : strings) {
            String[] parts = string.split(":");
            Material material = Material.valueOf((String)parts[0]);
            materials.add(material);
        }
        return materials;
    }

    public static Set<PotionEffect> getPotionEffects(Configuration config, String key)
    {
        Set<PotionEffect> potionEffects = new HashSet<PotionEffect>();
        ConfigurationSection section = config.getSection(key);
        for (String innerKey : section.getKeys(false))
        {
            PotionEffectType effectType = PotionEffectType.getByName(innerKey.toUpperCase());
            int duration = config.getInt(key + "." + innerKey + ".duration");
            int strength = config.getInt(key + "." + innerKey + ".strength");
            if (effectType == null || duration <= 0)
            {
                continue;
            }

            PotionEffect potionEffect = new PotionEffect(effectType, duration, strength);
            potionEffects.add(potionEffect);
        }
        return potionEffects;
    }
}
