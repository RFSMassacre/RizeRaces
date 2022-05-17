package com.github.rfsmassacre.rizeraces.abilities.werewolf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PounceAbility extends InstantAbility
{
    private final double forwardVelocity;
    private final double upwardVelocity;

    @Getter
    private final double damage;
    @Getter
    private final double range;

    @Getter
    @Setter
    private Set<UUID> jumping;
    @Getter
    @Setter
    private Map<UUID, Long> timeLimit;
    @Getter
    private final long limit;

    public PounceAbility()
    {
        super("pounce", Race.WEREWOLF);

        this.upwardVelocity = getConfigDouble("velocity.upward") / 10.0;
        this.forwardVelocity = getConfigDouble("velocity.forward") / 10.0;
        this.damage = getConfigDouble("landing.damage");
        this.range = getConfigDouble("landing.range");

        this.jumping = new HashSet<>();
        this.timeLimit = new HashMap<>();
        this.limit = getConfigLong("time-limit");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        if (callEvent(caster, race))
        {
            return AbilityResult.FAILED;
        }

        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (!origin.isWolfForm())
        {
            return AbilityResult.NO_REAGENT;
        }

        if (onCooldown(playerId))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        Vector direction = caster.getLocation().getDirection();
        direction.setY(0).normalize().multiply(forwardVelocity).setY(upwardVelocity);
        caster.setVelocity(direction);
        jumping.add(playerId);
        timeLimit.put(playerId, System.currentTimeMillis());

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (!jumping.contains(playerId))
                {
                    this.cancel();
                    return;
                }

                Block under = caster.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (under.getType().isSolid())
                {
                    jumping.remove(playerId);
                    this.cancel();
                }
            }
        }.runTaskTimer(RizeRaces.getInstance(), 20L, 5L);

        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }
}
