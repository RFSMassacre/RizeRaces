package com.github.rfsmassacre.rizeraces.abilities.angel;

import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HealingBowAbility extends BuffAbility
{
    @Getter
    private final double percent;

    public HealingBowAbility()
    {
        super("healing-bow", Race.ANGEL);

        this.percent = getConfigDouble("healing-percent");
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

        toggle(caster);
        return AbilityResult.SUCCESS;
    }

    @Override
    public boolean isActive(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return false;
        }

        return origin.isArrowHealing();
    }

    @Override
    public void activate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        origin.setArrowHealing(true);
        locale.sendLocale(caster, true, "angel.healing-bow.enabled");
    }

    @Override
    public void deactivate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        origin.setArrowHealing(false);
        locale.sendLocale(caster, true, "angel.healing-bow.disabled");
        setCooldown(caster.getUniqueId());
    }
}
