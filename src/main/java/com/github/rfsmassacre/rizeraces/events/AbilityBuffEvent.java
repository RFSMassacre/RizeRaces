package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import lombok.Getter;
import org.bukkit.entity.Player;

public class AbilityBuffEvent extends AbilityCastEvent
{
    @Getter
    private final boolean toggleOn;

    public AbilityBuffEvent(Player caster, Race race, boolean toggleOn, BuffAbility ability)
    {
        super(caster, race, ability);

        this.toggleOn = toggleOn;
    }
    public AbilityBuffEvent(Player caster, Role role, boolean toggleOn, BuffAbility ability)
    {
        super(caster, role, ability);

        this.toggleOn = toggleOn;
    }
}
