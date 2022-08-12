package com.github.rfsmassacre.rizeraces.abilities.race.angel;

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
    public boolean hasReagent(Player caster)
    {
        return true;
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

    @Override
    public String formatReagent()
    {
        return null;
    }
}
