package com.github.rfsmassacre.rizeraces.utils;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class SunUtil
{
    private static final int MID_DAY_TICKS = 6000;
    private static final int DAY_TICKS = 24000;
    private static final int HALF_DAY_TICKS = 12000;
    private static final int DAYTIME_TICKS = 14000;
    private static final int HALF_DAYTIME_TICKS = 7000;
    private static final double HALF_PI = 1.5707963267948966;
    private static final double MDTICKS_TO_ANGLE_FACTIOR = 2.243994752564138E-4;

    public static double getSolarRaditation(World world)
    {
        double absoluteAngle;
        if (!world.getEnvironment().equals(World.Environment.NORMAL) || world.hasStorm())
        {
            return 0.0;
        }
        int midDayTicks = (int)((world.getFullTime() - MID_DAY_TICKS) % DAY_TICKS);
        if (midDayTicks >= HALF_DAY_TICKS)
        {
            midDayTicks -= DAY_TICKS;
        }
        if ((absoluteAngle = Math.abs(MDTICKS_TO_ANGLE_FACTIOR * (double)midDayTicks)) >= HALF_PI)
        {
            return 0.0;
        }
        return Math.sin(HALF_PI - absoluteAngle);
    }

    public static double getTerrainOpacity(Block block, Map<Material, Double> blocks)
    {
        Double opacity;
        double terrain = 0.0;
        int x = block.getX();
        int z = block.getZ();
        World world = block.getWorld();
        int maxY = world.getMaxHeight() - 1;
        for (int y = block.getY(); y <= maxY && terrain < 1.0; terrain += opacity, ++y)
        {
            Material material = world.getBlockAt(x, y, z).getType();
            opacity = blocks.get(material);
            if (opacity == null)
            {
                opacity = 1.0;
            }
        }
        if (terrain > 1.0)
        {
            terrain = 1.0;
        }
        return terrain;
    }

    public static double getArmorOpacity(Player player, double armorPiece)
    {
        double armor = 0.0;
        for (ItemStack item : player.getInventory().getArmorContents())
        {
            if (item == null || item.getAmount() == 0 || item.getType().equals(Material.AIR))
            {
                continue;
            }
            armor += armorPiece;
        }
        return armor;
    }

    public static boolean inGodModeRegion(Player player)
    {
        if (RizeRaces.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null)
        {
            Location location = BukkitAdapter.adapt(player.getLocation());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(location);
            return set.testState(null, Flags.INVINCIBILITY);
        }

        return false;
    }

    public static boolean inSafeRegion(Player player)
    {
        if (RizeRaces.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null)
        {
            Location location = BukkitAdapter.adapt(player.getLocation());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(location);
            Configuration config = RizeRaces.getInstance().getBaseConfig();
            List<String> safeRegions = config.getStringList("vampire.radiation.safe-regions");
            for (ProtectedRegion region : set.getRegions())
            {
                if (safeRegions.contains(region.getId()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static double getPlayerIrradiation(Player player, Map<Material, Double> blocks, double armorPiece)
    {
        if (SunUtil.inGodModeRegion(player))
        {
            return 0.0;
        }

        World world = player.getWorld();
        double radiation = SunUtil.getSolarRaditation(world);
        if (radiation == 0.0)
        {
            return 0.0;
        }

        Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
        double terrain = SunUtil.getTerrainOpacity(block, blocks);
        if ((radiation *= 1.0 - terrain) == 0.0)
        {
            return 0.0;
        }

        double armor = SunUtil.getArmorOpacity(player, armorPiece);
        if ((radiation *= 1.0 - armor) == 0.0)
        {
            return 0.0;
        }

        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
        {
            return 0.0;
        }
        return radiation;
    }
}
