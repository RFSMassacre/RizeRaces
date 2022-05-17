package com.github.rfsmassacre.rizeraces.moons;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.util.*;

public class Moon
{
    public enum MoonPhase
    {
        FULL_MOON(13000L, 24000L, 8),
        WANING_GIBBOUS(37000L, 48000L, 7),
        LAST_QUARTER(61000L, 72000L, 6),
        WANING_CRESCENT(85000L, 94000L, 5),
        NEW_MOON(109000L, 120000L, 4),
        WAXING_CRESCENT(133000L, 144000L, 3),
        FIRST_QUARTER(157000L, 168000L, 2),
        WAXING_GIBBOUS(181000L, 192000L, 1);

        @Getter
        private final long start;
        @Getter
        private final long end;
        @Getter
        private final int position;

        MoonPhase(long start, long end, int position)
        {
            this.start = start;
            this.end = end;
            this.position = position;
        }

        public boolean inCycle(Moon moon)
        {
            long ticks = moon.getTime();
            return ticks >= this.start && ticks <= this.end;
        }
    }

    private static final long WEEKEND = 192000L;
    private static final HashMap<World, Moon> MOONS = new HashMap<>();

    @Getter
    private final World world;
    @Getter
    private final Set<UUID> transformedIds;

    public long getTime()
    {
        return world.getFullTime() % WEEKEND;
    }

    public Moon(World world)
    {
        this.world = world;
        this.transformedIds = new HashSet<>();
    }

    public boolean hasTransformed(UUID playerId)
    {
        return this.transformedIds.contains(playerId);
    }
    public void addTransformed(UUID playerId)
    {
        this.transformedIds.add(playerId);
    }
    public void removeTransformed(UUID playerId)
    {
        this.transformedIds.remove(playerId);
    }
    public void clearTransformed()
    {
        this.transformedIds.clear();
    }

    public MoonPhase getCurrentPhase()
    {
        for (MoonPhase phase : MoonPhase.values())
        {
            if (!phase.inCycle(this))
            {
                continue;
            }

            return phase;
        }

        return null;
    }

    public MoonPhase getNextPhase()
    {
        for (MoonPhase phase : MoonPhase.values())
        {
            if (getTime() > phase.start)
            {
                continue;
            }
            return phase;
        }
        return null;
    }

    public MoonPhase getMoonPhase()
    {
        return this.getCurrentPhase() != null ? this.getCurrentPhase() : this.getNextPhase();
    }
    public void setPhase(MoonPhase phase)
    {
        this.world.setFullTime(phase.start);
    }

    public static void loadMoons()
    {
        MOONS.clear();
        for (World world : Bukkit.getWorlds())
        {
            if (world.getEnvironment().equals(Environment.NORMAL))
            {
                MOONS.put(world, new Moon(world));
            }
        }
    }

    public static Moon getMoon(World world)
    {
        return MOONS.get(world);
    }
    public static Collection<Moon> getMoons()
    {
        return MOONS.values();
    }
    public static void removeTransformId(UUID playerId)
    {
        for (Moon moon : getMoons())
        {
            moon.removeTransformed(playerId);
        }
    }
}
