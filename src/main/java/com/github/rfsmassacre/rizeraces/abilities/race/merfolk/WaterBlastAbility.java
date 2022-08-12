package com.github.rfsmassacre.rizeraces.abilities.race.merfolk;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.UUID;

public class WaterBlastAbility extends TargetAbility
{
    private final double upward;
    private final double backward;
    private final double damage;

    public WaterBlastAbility()
    {
        super("water-blast", Race.MERFOLK);

        this.upward = getConfigDouble("velocity.upward") / 10.0;
        this.backward = getConfigDouble("velocity.backward") / 10.0;
        this.reagent = getConfigDouble("hydration");
        this.damage = getConfigDouble("damage");
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

        Set<LivingEntity> targets = areaOfEffect(caster);
        if (targets.size() == 0)
        {
            return AbilityResult.NO_TARGET;
        }

        for (LivingEntity target : targets)
        {
            Location location = target.getLocation();
            World world = target.getWorld();
            world.playEffect(location, Effect.POTION_BREAK, 0);
            Vector vector = target.getLocation().toVector().subtract(caster.getLocation().toVector());
            vector.multiply(backward);
            vector.setY(upward);
            target.damage(damage, caster);

            //Dragons disappear when this is used.
            if (!target.getType().equals(EntityType.ENDER_DRAGON))
            {
                target.setVelocity(vector);
            }
        }

        origin.addHydration(-reagent);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        caster.getWorld().playEffect(caster.getLocation(), Effect.POTION_BREAK, 0);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return Integer.toString((int)(reagent * 100));
    }
}
