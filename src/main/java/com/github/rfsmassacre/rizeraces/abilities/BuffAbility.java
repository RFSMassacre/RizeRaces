package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.events.AbilityBuffEvent;
import com.github.rfsmassacre.rizeraces.events.AbilityCastEvent;
import com.github.rfsmassacre.rizeraces.events.AbilityTargetEvent;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BuffAbility extends Ability
{
    @Getter
    private final Map<UUID, Integer> durations;

    @Getter
    protected int duration;

    public BuffAbility(String internalName, Race race)
    {
        super(internalName, AbilityType.BUFF, race);

        this.durations = new HashMap<>();
        this.duration = getConfigInt("duration");
    }
    public BuffAbility(String internalName, Role role)
    {
        super(internalName, AbilityType.BUFF, role);

        this.durations = new HashMap<>();
        this.duration = getConfigInt("duration");
    }

    public boolean failEvent(Player caster)
    {
        AbilityCastEvent event = new AbilityBuffEvent(caster, race, !isActive(caster), this);
        if (race == null)
        {
            event = new AbilityBuffEvent(caster, role, !isActive(caster), this);
        }

        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
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

        if (isActive(caster))
        {
            deactivate(caster);
            return AbilityResult.SUCCESS;
        }

        if (onCooldown(playerId))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        if (!hasReagent(caster))
        {
            return AbilityResult.NO_REAGENT;
        }

        if (failEvent(caster))
        {
            return AbilityResult.FAILED;
        }

        activate(caster);
        return AbilityResult.SUCCESS;
    }

    protected void toggle(Player caster)
    {
        if (isActive(caster))
        {
            deactivate(caster);
        }
        else
        {
            activate(caster);
        }
    }

    public abstract boolean hasReagent(Player caster);

    public abstract boolean isActive(Player caster);

    public abstract void activate(Player caster);

    public abstract void deactivate(Player caster);

    public static void deactivateAll(Player caster)
    {
        for (Ability ability : Ability.getAbilities())
        {
            if (!(ability instanceof BuffAbility buffAbility))
            {
                continue;
            }

            if (buffAbility.isActive(caster))
            {
                buffAbility.deactivate(caster);
            }
        }
    }
}
