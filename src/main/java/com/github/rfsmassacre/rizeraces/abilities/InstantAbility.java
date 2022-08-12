package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.events.AbilityCastEvent;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class InstantAbility extends Ability
{
    public InstantAbility(String internalName, Race race)
    {
        super(internalName, AbilityType.INSTANT, race);
    }
    public InstantAbility(String internalName, Role role)
    {
        super(internalName, AbilityType.INSTANT, role);
    }

    public boolean failEvent(Player caster)
    {
        AbilityCastEvent event = new AbilityCastEvent(caster, race, this);
        if (race == null)
        {
            event = new AbilityCastEvent(caster, role, this);
        }

        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }
}
