package com.github.rfsmassacre.rizeraces.abilities.race.elf;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DodgeAbility extends InstantAbility
{
    private final double upwardVelocity;
    private final double forwardVelocity;

    public DodgeAbility()
    {
        super("dodge", Race.ELF);

        this.upwardVelocity = getConfigDouble("velocity.upward") / 10.0;
        this.forwardVelocity = getConfigDouble("velocity.forward") / 10.0;
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

        if (onCooldown(caster.getUniqueId()))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        Vector velocity = caster.getLocation().getDirection();
        velocity.setY(0).normalize().multiply(forwardVelocity).setY(upwardVelocity);
        caster.setVelocity(velocity);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
