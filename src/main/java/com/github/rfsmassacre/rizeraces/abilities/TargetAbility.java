package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class TargetAbility extends Ability
{
    @Getter
    protected final int range;
    @Getter
    protected final List<String> allowedEntities;

    private Particle particle;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private int count;
    private float speed;

    protected final boolean showEffect;

    private static final Set<Material> TRANSPARENT = new HashSet<>(Arrays.asList(Material.AIR, Material.CAVE_AIR,
            Material.VOID_AIR, Material.WATER));

    public TargetAbility(String internalName, Race race)
    {
        super(internalName, AbilityType.TARGET, race);

        this.range = getConfigInt("range");
        this.allowedEntities = getConfigStringList("allowed-entities");
        this.showEffect = getConfigBoolean("show-effect");
        if (showEffect)
        {
            try
            {
                this.particle = Particle.valueOf(getConfigString("effect.particle"));
            }
            catch (IllegalArgumentException exception)
            {
                this.particle = null;
            }

            this.xOffset = getConfigDouble("effect.offset.x");
            this.yOffset = getConfigDouble("effect.offset.y");
            this.zOffset = getConfigDouble("effect.offset.z");
            this.count = getConfigInt("effect.count");
            this.speed = getConfigInt("effect.speed");
        }
    }

    protected LivingEntity getTargetEntity(Player caster)
    {
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : caster.getNearbyEntities(range, range, range))
        {
            if (entity instanceof LivingEntity)
            {
                entities.add((LivingEntity)entity);
            }
        }

        // Find target
        BlockIterator blockIterator;
        try
        {
            blockIterator = new BlockIterator(caster, range);
        }
        catch (IllegalStateException exception)
        {
            return null;
        }

        Block block;
        Location location;

        int blockX;
        int blockY;
        int blockZ;

        double entityX;
        double entityY;
        double entityZ;

        // How far can a target be from the line of sight along the x, y, and z directions
        double xLower = 0.75;
        double xUpper = 1.75;
        double yLower = 1;
        double yUpper = 2.5;
        double zLower = 0.75;
        double zUpper = 1.75;

        // Loop through player's line of sight
        while (blockIterator.hasNext())
        {
            block = blockIterator.next();
            blockX = block.getX();
            blockY = block.getY();
            blockZ = block.getZ();

            // Line of sight is broken, stop without target
            if (block.isSolid())
            {
                break;
            }

            // Check for entities near this block in the line of sight
            for (LivingEntity target : entities)
            {
                location = target.getLocation();
                entityX = location.getX();
                entityY = location.getY();
                entityZ = location.getZ();

                if (!(blockX - xLower <= entityX && entityX <= blockX + xUpper))
                {
                    continue;
                }

                if (!(blockY - yLower <= entityY && entityY <= blockY + yUpper))
                {
                    continue;
                }

                if (!(blockZ - zLower <= entityZ && entityZ <= blockZ + zUpper))
                {
                    continue;
                }

                // Check for invalid target
                if (target instanceof Player player)
                {
                    GameMode gameMode = player.getGameMode();
                    if (gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR))
                    {
                        continue;
                    }
                }

                return target;
            }
        }

        return null;
    }

    protected Block getTargetBlock(Player caster)
    {
        try
        {
            return caster.getTargetBlock(TRANSPARENT, range);
        }
        catch (IllegalStateException exception)
        {
            return null;
        }
    }

    protected void playEffect(Player caster, LivingEntity entity)
    {
        Location casterLocation = caster.getEyeLocation();
        Location entityLocation = entity.getEyeLocation();
        if (casterLocation.distance(entityLocation) > range)
        {
            return;
        }

        Location clone1 = casterLocation.clone();
        Location clone2 = entityLocation.clone();

        int ceiling = (int)Math.ceil(clone1.distance(clone2)) - 1;
        if (ceiling <= 0)
        {
            return;
        }

        Vector vector = entityLocation.toVector().subtract(casterLocation.toVector()).normalize();
        Location location = clone1.clone();
        for (int index = 0; index < ceiling; index++)
        {
            location.add(vector);
            location.getWorld().spawnParticle(particle, location, count, xOffset, yOffset, zOffset, speed);
        }
    }
}
