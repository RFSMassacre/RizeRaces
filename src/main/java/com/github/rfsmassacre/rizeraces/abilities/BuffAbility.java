package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
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

    public abstract boolean isActive(Player caster);

    public abstract void activate(Player caster);

    public abstract void deactivate(Player caster);

    public static void deactivateAll(Player caster)
    {
        for (Ability ability : Ability.getAbilities())
        {
            if (!(ability instanceof BuffAbility buffAbility))
            {
                return;
            }

            if (buffAbility.isActive(caster))
            {
                buffAbility.deactivate(caster);
            }
        }
    }
}
