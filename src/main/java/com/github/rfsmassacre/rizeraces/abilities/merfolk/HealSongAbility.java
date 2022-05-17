package com.github.rfsmassacre.rizeraces.abilities.merfolk;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HealSongAbility extends InstantAbility
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
        if (callEvent(caster, race))
        {
            return AbilityResult.FAILED;
        }

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

        List<Entity> entities = caster.getNearbyEntities(range, range, range);
        entities.add(caster);
        for (Entity entity : entities)
        {
            if (!(entity instanceof Player))
            {
                continue;
            }

            Player player = (Player)entity;
            double health = player.getHealth();
            AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
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

                player.setHealth(finalHeal);
            }

            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0.0, 0.5, 0.0), 8,
                    0.5, 0.5, 0.5, 0.5);
        }

        origin.addHydration(-reagent);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 2.5F);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }
}
