package com.github.rfsmassacre.rizeraces.abilities.race.angel;

import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class BackpedalAbility extends InstantAbility
{
    private final double backwardVelocity;
    private final double upwardVelocity;

    public BackpedalAbility()
    {
        super("backpedal", Race.ANGEL);

        this.upwardVelocity = getConfigDouble("velocity.upward") / 10.0;
        this.backwardVelocity = getConfigDouble("velocity.backward") / 10.0;
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

        if (onCooldown(playerId))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        if (failEvent(caster))
        {
            return AbilityResult.FAILED;
        }

        Vector direction = caster.getLocation().getDirection();
        direction.setY(0).normalize().multiply(-backwardVelocity).setY(upwardVelocity);
        caster.setVelocity(direction);
        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
