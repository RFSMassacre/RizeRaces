package com.github.rfsmassacre.rizeraces.abilities.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BloodLustAbility extends BuffAbility
{
    @Getter
    private final int particles;

    public BloodLustAbility()
    {
        super("blood-lust", Race.VAMPIRE);

        this.reagent = getConfigInt("minimum-food-level");
        this.particles = getConfigInt("particles");
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

        if (caster.getFoodLevel() < reagent)
        {
            return AbilityResult.NO_REAGENT;
        }

        toggle(caster);
        return AbilityResult.SUCCESS;
    }

    @Override
    public boolean isActive(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        return origin != null && origin.getRace().equals(Race.VAMPIRE) && origin.isBloodLust();
    }

    @Override
    public void activate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        BuffAbility buffAbility = (BuffAbility)Ability.getAbility("bat-form");
        if (buffAbility.isActive(caster))
        {
            buffAbility.deactivate(caster);
        }

        FXUtil.playEffect(caster, Effect.GHAST_SHRIEK);
        FXUtil.smokeBurst(caster, particles);
        origin.setBloodLust(true);
    }

    @Override
    public void deactivate(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        FXUtil.playEffect(caster, Effect.GHAST_SHRIEK);
        FXUtil.smokeBurst(caster, particles);
        origin.setBloodLust(false);
        setCooldown(playerId);
    }
}
