package com.github.rfsmassacre.rizeraces.utils;

import org.bukkit.Material;

import java.util.HashMap;

public class FoodUtil
{
    public static final int MAX_FOOD = 20;
    private static final HashMap<Material, Integer> foodLevels = new HashMap<>();
    private static final HashMap<Material, Float> saturationLevels = new HashMap<>();

    static
    {
        foodLevels.put(Material.APPLE, 4);
        foodLevels.put(Material.BAKED_POTATO, 5);
        foodLevels.put(Material.BEETROOT, 1);
        foodLevels.put(Material.BEETROOT_SOUP, 6);
        foodLevels.put(Material.BREAD, 5);
        foodLevels.put(Material.CARROT, 3);
        foodLevels.put(Material.CHORUS_FRUIT, 4);
        foodLevels.put(Material.COOKED_BEEF, 8);
        foodLevels.put(Material.COOKED_CHICKEN, 6);
        foodLevels.put(Material.COOKED_COD, 5);
        foodLevels.put(Material.COOKED_MUTTON, 6);
        foodLevels.put(Material.COOKED_PORKCHOP, 8);
        foodLevels.put(Material.COOKED_RABBIT, 5);
        foodLevels.put(Material.COOKED_SALMON, 6);
        foodLevels.put(Material.COOKIE, 4);
        foodLevels.put(Material.DRIED_KELP, 1);
        foodLevels.put(Material.GOLDEN_APPLE, 4);
        foodLevels.put(Material.ENCHANTED_GOLDEN_APPLE, 4);
        foodLevels.put(Material.GOLDEN_CARROT, 6);
        foodLevels.put(Material.MELON_SLICE, 2);
        foodLevels.put(Material.MUSHROOM_STEW, 6);
        foodLevels.put(Material.POISONOUS_POTATO, 2);
        foodLevels.put(Material.POTATO, 1);
        foodLevels.put(Material.PUFFERFISH, 1);
        foodLevels.put(Material.PUMPKIN_PIE, 8);
        foodLevels.put(Material.RABBIT_STEW, 10);
        foodLevels.put(Material.BEEF, 3);
        foodLevels.put(Material.CHICKEN, 2);
        foodLevels.put(Material.COD, 2);
        foodLevels.put(Material.MUTTON, 2);
        foodLevels.put(Material.PORKCHOP, 3);
        foodLevels.put(Material.RABBIT, 3);
        foodLevels.put(Material.SALMON, 2);
        foodLevels.put(Material.ROTTEN_FLESH, 4);
        foodLevels.put(Material.SPIDER_EYE, 2);
        foodLevels.put(Material.SUSPICIOUS_STEW, 6);
        foodLevels.put(Material.SWEET_BERRIES, 2);
        foodLevels.put(Material.TROPICAL_FISH, 1);
        foodLevels.put(Material.GLOW_BERRIES, 2);

        saturationLevels.put(Material.APPLE, 2.4F);
        saturationLevels.put(Material.BAKED_POTATO, 6.0F);
        saturationLevels.put(Material.BEETROOT, 1.2F);
        saturationLevels.put(Material.BEETROOT_SOUP, 7.2F);
        saturationLevels.put(Material.BREAD, 6.0F);
        saturationLevels.put(Material.CARROT, 3.6F);
        saturationLevels.put(Material.CHORUS_FRUIT, 2.4F);
        saturationLevels.put(Material.COOKED_BEEF, 12.8F);
        saturationLevels.put(Material.COOKED_CHICKEN, 7.2F);
        saturationLevels.put(Material.COOKED_COD, 6.0F);
        saturationLevels.put(Material.COOKED_MUTTON, 9.6F);
        saturationLevels.put(Material.COOKED_PORKCHOP, 12.8F);
        saturationLevels.put(Material.COOKED_RABBIT, 6.0F);
        saturationLevels.put(Material.COOKED_SALMON, 9.6F);
        saturationLevels.put(Material.COOKIE, 0.4F);
        saturationLevels.put(Material.DRIED_KELP, 0.6F);
        saturationLevels.put(Material.GOLDEN_APPLE, 9.6F);
        saturationLevels.put(Material.ENCHANTED_GOLDEN_APPLE, 9.6F);
        saturationLevels.put(Material.GOLDEN_CARROT, 14.4F);
        saturationLevels.put(Material.MELON_SLICE, 1.2F);
        saturationLevels.put(Material.MUSHROOM_STEW, 7.2F);
        saturationLevels.put(Material.POISONOUS_POTATO, 1.2F);
        saturationLevels.put(Material.POTATO, 0.6F);
        saturationLevels.put(Material.PUFFERFISH, 0.2F);
        saturationLevels.put(Material.PUMPKIN_PIE, 4.8F);
        saturationLevels.put(Material.RABBIT_STEW, 12.0F);
        saturationLevels.put(Material.BEEF, 1.8F);
        saturationLevels.put(Material.CHICKEN, 1.2F);
        saturationLevels.put(Material.COD, 0.4F);
        saturationLevels.put(Material.MUTTON, 1.2F);
        saturationLevels.put(Material.PORKCHOP, 1.8F);
        saturationLevels.put(Material.RABBIT, 1.8F);
        saturationLevels.put(Material.SALMON, 0.4F);
        saturationLevels.put(Material.ROTTEN_FLESH, 0.8F);
        saturationLevels.put(Material.SPIDER_EYE, 3.2F);
        saturationLevels.put(Material.SUSPICIOUS_STEW, 7.2F);
        saturationLevels.put(Material.SWEET_BERRIES, 0.4F);
        saturationLevels.put(Material.TROPICAL_FISH, 0.2F);
        saturationLevels.put(Material.GLOW_BERRIES, 0.4F);
    }

    public static int getFoodLevel(Material material)
    {
        if (foodLevels.containsKey(material))
        {
            return foodLevels.get(material);
        }
        return 0;
    }
    public static float getSaturationLevel(Material material)
    {
        if (saturationLevels.containsKey(material))
        {
            return saturationLevels.get(material);
        }
        return 0.0F;
    }
}
