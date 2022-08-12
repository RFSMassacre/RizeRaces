package com.github.rfsmassacre.rizeraces.abilities.race.elf;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ArrowRainAbility extends TargetAbility
{
    private final double height;
    private final double arrows;
    private final float spread;
    private final float speed;
    private final double damage;

    public ArrowRainAbility()
    {
        super("arrow-rain", Race.ELF);

        this.height = getConfigDouble("height");
        this.arrows = getConfigDouble("arrows");
        this.spread = (float)getConfigDouble("spread");
        this.speed = (float)getConfigDouble("speed");
        this.damage = getConfigDouble("damage");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (onCooldown(caster.getUniqueId()))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        Block block = getTargetBlock(caster);
        if (block == null)
        {
            return AbilityResult.NO_TARGET;
        }
        else
        {
            if (failEvent(caster, block))
            {
                return AbilityResult.FAILED;
            }

            for (int y = (int)height; y > 0; y--)
            {
                Block above = block.getRelative(BlockFace.UP, y);
                if (above.isSolid())
                {
                    return AbilityResult.NO_TARGET;
                }
            }

            Vector down = new Vector(0, -1, 0);
            Location location = block.getLocation().add(0, height, 0);
            for (int index = 0; index < arrows; index++)
            {
                Arrow arrow = block.getWorld().spawnArrow(location, down, speed, spread / 10.0F);
                arrow.setShooter(caster);
                arrow.setDamage(damage);
                arrow.setPickupStatus(PickupStatus.CREATIVE_ONLY);
            }

            setCooldown(caster.getUniqueId());
            return AbilityResult.SUCCESS;
        }
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
