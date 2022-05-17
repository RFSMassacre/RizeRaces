package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AbilityTargetEvent extends AbilityCastEvent
{
    @Getter
    private final LivingEntity target;

    public AbilityTargetEvent(Player caster, LivingEntity target, Race race, Ability ability)
    {
        super(caster, race, ability);

        this.target = target;
    }
}
