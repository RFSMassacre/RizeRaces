package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AbilityTargetEvent extends AbilityCastEvent
{
    @Getter
    private final LivingEntity target;
    @Getter
    private final Block block;

    public AbilityTargetEvent(Player caster, LivingEntity target, Race race, TargetAbility ability)
    {
        super(caster, race, ability);

        this.target = target;
        this.block = null;
    }
    public AbilityTargetEvent(Player caster, Block block, Race race, TargetAbility ability)
    {
        super(caster, race, ability);

        this.target = null;
        this.block = block;
    }

    public AbilityTargetEvent(Player caster, LivingEntity target, Role role, TargetAbility ability)
    {
        super(caster, role, ability);

        this.target = target;
        this.block = null;
    }
    public AbilityTargetEvent(Player caster, Block block, Role role, TargetAbility ability)
    {
        super(caster, role, ability);

        this.target = null;
        this.block = block;
    }
}
