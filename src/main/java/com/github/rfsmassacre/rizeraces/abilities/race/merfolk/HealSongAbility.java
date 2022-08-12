package com.github.rfsmassacre.rizeraces.abilities.race.merfolk;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HealSongAbility extends TargetAbility
{
    private final double heal;
    private final double range;

    public HealSongAbility()
    {
        super("heal-song", Race.MERFOLK);

        this.heal = getConfigDouble("heal");
        this.range = getConfigDouble("range");
        this.reagent = getConfigDouble("hydration");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (onCooldown(playerId))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        if (origin.getHydration() < reagent)
        {
            return AbilityResult.NO_REAGENT;
        }

        if (failEvent(caster))
        {
            return AbilityResult.FAILED;
        }

        for (LivingEntity target : areaOfEffect(caster))
        {
            double health = target.getHealth();
            AttributeInstance healthAttribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (healthAttribute == null)
            {
                continue;
            }

            double maxHealth = healthAttribute.getValue();
            if (health < maxHealth)
            {
                double finalHeal = health + heal;
                if (finalHeal > maxHealth)
                {
                    finalHeal = maxHealth;
                }

                target.setHealth(finalHeal);
            }

            target.getWorld().spawnParticle(Particle.HEART, target.getLocation().add(0.0, 0.5, 0.0), 8,
                    0.5, 0.5, 0.5, 0.5);
        }

        origin.addHydration(-reagent);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 2.5F);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return Integer.toString((int)(reagent * 100));
    }
}
