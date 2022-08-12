package com.github.rfsmassacre.rizeraces.abilities.race.merfolk;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;

public class BlindSongAbility extends TargetAbility
{
    private final int duration;
    private final double range;

    public BlindSongAbility()
    {
        super("blind-song", Race.MERFOLK);

        this.duration = getConfigInt("duration");
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

        Set<LivingEntity> targets = areaOfEffect(caster);
        if (targets.size() == 0)
        {
            return AbilityResult.NO_TARGET;
        }

        for (LivingEntity target : targets)
        {
            PotionUtil.applyPotion(target, new PotionEffect(PotionEffectType.BLINDNESS, duration, 0));
            target.getWorld().spawnParticle(Particle.SMOKE_NORMAL, target.getEyeLocation(), 8,
                    0.5, 0.5, 0.5, 0.5);
        }

        origin.addHydration(-reagent);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0F, 3.0F);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return Integer.toString((int)(reagent * 100));
    }
}
