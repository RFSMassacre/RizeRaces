package com.github.rfsmassacre.rizeraces.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class PotionUtil
{
    private static Set<PotionEffectType> NEGATIVE_EFFECTS = new HashSet<>();

    static
    {
        NEGATIVE_EFFECTS.add(PotionEffectType.BAD_OMEN);
        NEGATIVE_EFFECTS.add(PotionEffectType.BLINDNESS);
        NEGATIVE_EFFECTS.add(PotionEffectType.CONFUSION);
        //NEGATIVE_EFFECTS.add(PotionEffectType.HARM);
        NEGATIVE_EFFECTS.add(PotionEffectType.HUNGER);
        NEGATIVE_EFFECTS.add(PotionEffectType.POISON);
        NEGATIVE_EFFECTS.add(PotionEffectType.SLOW);
        NEGATIVE_EFFECTS.add(PotionEffectType.SLOW_DIGGING);
        NEGATIVE_EFFECTS.add(PotionEffectType.UNLUCK);
        NEGATIVE_EFFECTS.add(PotionEffectType.WEAKNESS);
        NEGATIVE_EFFECTS.add(PotionEffectType.WITHER);
    }

    public static Set<PotionEffectType> getNegativeEffects()
    {
        return NEGATIVE_EFFECTS;
    }

    public static void applyPotion(LivingEntity entity, PotionEffect effect)
    {
        PotionEffect first = entity.getPotionEffect(effect.getType());
        if (first == null || first.getAmplifier() <= effect.getAmplifier())
        {
            entity.addPotionEffect(effect);
        }
    }

    public static void applyPotions(LivingEntity entity, Set<PotionEffect> effects)
    {
        for (PotionEffect effect : effects)
        {
            applyPotion(entity, effect);
        }
    }
}
