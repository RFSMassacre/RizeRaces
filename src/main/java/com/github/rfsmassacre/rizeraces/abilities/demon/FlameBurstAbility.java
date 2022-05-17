package com.github.rfsmassacre.rizeraces.abilities.demon;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class FlameBurstAbility extends InstantAbility
{
    private final int duration;
    private final double range;

    public FlameBurstAbility()
    {
        super("flame-burst", Race.DEMON);

        this.duration = getConfigInt("duration");
        this.range = getConfigDouble("range");
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

        List<Entity> entities = caster.getNearbyEntities(range, range, range);
        for (Entity entity : entities)
        {
            if (!(entity instanceof LivingEntity livingEntity))
            {
                continue;
            }
            else if (entity instanceof Player target)
            {
                Origin targetOrigin = gson.getOrigin(target.getUniqueId());
                if (targetOrigin != null)
                {
                    if (targetOrigin.getRace().equals(Race.DEMON))
                    {
                        continue;
                    }
                }
            }

            livingEntity.setFireTicks(duration);
            livingEntity.getWorld().spawnParticle(Particle.FLAME, livingEntity.getEyeLocation(), 8,
                    0.5, 0.5, 0.5, 0.5);
        }

        FXUtil.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }
}
