package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.players.Origin.Race;

public abstract class InstantAbility extends Ability
{
    public InstantAbility(String internalName, Race race)
    {
        super(internalName, AbilityType.INSTANT, race);
    }
}
