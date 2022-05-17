package com.github.rfsmassacre.rizeraces.abilities.vampire;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FoodUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class FangAbility extends TargetAbility
{
    @Getter
    private final int foodSteal;
    @Getter
    private final int poisonLength;
    @Getter
    private final int poisonStrength;
    @Getter
    private final List<String> blockedMobs;

    public FangAbility()
    {
        super("fang", Race.VAMPIRE);

        this.foodSteal = getConfigInt("food-steal");
        this.poisonLength = getConfigInt("poison.length");
        this.poisonStrength = getConfigInt("poison.strength");
        this.blockedMobs = getConfigStringList("blocked-mobs");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        if (callEvent(caster, race))
        {
            return AbilityResult.FAILED;
        }

        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (onCooldown(caster.getUniqueId()))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        LivingEntity target = getTargetEntity(caster);
        if (target == null)
        {
            return AbilityResult.NO_TARGET;
        }
        else
        {
            foodSteal(caster, target);
            return AbilityResult.SUCCESS;
        }
    }

    public void foodSteal(Player caster, LivingEntity target)
    {
        if (target.hasMetadata("NPC"))
        {
            return;
        }

        if (blockedMobs.contains(target.getType().toString()))
        {
            return;
        }

        if (caster.getFoodLevel() == FoodUtil.MAX_FOOD)
        {
            return;
        }

        if (target.getHealth() <= 1.0)
        {
            return;
        }

        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(caster, target,
                DamageCause.ENTITY_ATTACK, 0.0);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damageEvent.isCancelled())
        {
            return;
        }

        //Poison
        PotionEffect effect = new PotionEffect(PotionEffectType.POISON, poisonLength, poisonStrength);
        PotionUtil.applyPotion(target, effect);

        //Food Steal
        int foodLevel = caster.getFoodLevel() + foodSteal;
        if (foodLevel > FoodUtil.MAX_FOOD)
        {
            foodLevel = FoodUtil.MAX_FOOD;
        }

        caster.setFoodLevel(foodLevel);
        setCooldown(caster.getUniqueId());
    }
}
