package com.github.rfsmassacre.rizeraces.abilities.race.demon;

import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.tasks.demon.RageTickTask;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RageAbility extends BuffAbility
{
    @Getter
    private final double percent;

    public RageAbility()
    {
        super("rage", Race.DEMON);

        this.percent = getConfigDouble("percent");
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
        return origin != null && origin.getRace().equals(Race.DEMON) && origin.isEnraged();
    }

    @Override
    public void activate(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return;
        }

        origin.setEnraged(true);
        origin.setRageTime(duration);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0F, 0.25F);
        //locale.sendLocale(caster, true, "demon.rage.enabled");
    }

    @Override
    public void deactivate(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return;
        }

        origin.setEnraged(false);
        origin.setRageTime(0);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_RAVAGER_HURT, 1.0F, 0.25F);
        //locale.sendLocale(caster, true, "demon.rage.disabled");
        RageTickTask.removeBossBar(playerId);
        setCooldown(playerId);
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
