package com.github.rfsmassacre.rizeraces.abilities.race.human;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.Player;

/**
 * Points your compass in the direction of a target.
 */
public class TrackAbility extends InstantAbility
{
    public TrackAbility()
    {
        super("track", Race.HUMAN);
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        return null;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
