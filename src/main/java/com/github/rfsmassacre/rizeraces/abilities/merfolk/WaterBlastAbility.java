package com.github.rfsmassacre.rizeraces.abilities.merfolk;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class WaterBlastAbility extends InstantAbility
{
    private final double upward;
    private final double backward;
    private final double range;

    public WaterBlastAbility()
    {
        super("water-blast", Race.MERFOLK);

        this.upward = getConfigDouble("velocity.upward") / 10.0;
        this.backward = getConfigDouble("velocity.backward") / 10.0;
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
        for (Entity entity : entities)
        {
            if (!(entity instanceof LivingEntity))
            {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity)entity;
            Location location = livingEntity.getLocation();
            World world = livingEntity.getWorld();
            world.playEffect(location, Effect.POTION_BREAK, 0);
            Vector vector = livingEntity.getLocation().toVector().subtract(caster.getLocation().toVector());
            vector.multiply(backward);
            vector.setY(upward);
            livingEntity.setVelocity(vector);
        }

        origin.addHydration(-reagent);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }
}
