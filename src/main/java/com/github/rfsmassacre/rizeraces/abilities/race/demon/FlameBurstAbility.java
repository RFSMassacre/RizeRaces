package com.github.rfsmassacre.rizeraces.abilities.race.demon;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
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

public class FlameBurstAbility extends TargetAbility
{
    private final int duration;

    public FlameBurstAbility()
    {
        super("flame-burst", Race.DEMON);

        this.duration = getConfigInt("duration");
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

        if (failEvent(caster))
        {
            return AbilityResult.FAILED;
        }

        for (LivingEntity target : areaOfEffect(caster))
        {
            if (target instanceof Player)
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

            target.setFireTicks(duration);
            target.getWorld().spawnParticle(Particle.FLAME, target.getEyeLocation(), 8,
                    0.5, 0.5, 0.5, 0.5);
        }

        FXUtil.playSound(caster.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0F, 1.0F);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
