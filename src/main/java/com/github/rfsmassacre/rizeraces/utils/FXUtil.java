package com.github.rfsmassacre.rizeraces.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

public class FXUtil
{
    public static void playEffect(Location location, int direction, Effect effect)
    {
        if (location != null)
        {
            location.getWorld().playEffect(location, effect, direction);
        }
    }

    public static void playEffect(Location location, Effect effect)
    {
        FXUtil.playEffect(location, new Random().nextInt(9), effect);
    }

    public static void playEffect(Player player, Effect effect)
    {
        FXUtil.playEffect(FXUtil.getRandomPlayerLocation(player), effect);
    }

    public static void playSound(Location location, Sound sound, float volume, float pitch)
    {
        if (location != null)
        {
            location.getWorld().playSound(location, sound, volume, pitch);
        }
    }

    public static void playSound(Location location, Sound sound)
    {
        FXUtil.playSound(location, sound, 1.0f, 1.0f);
    }

    public static Location getRandomPlayerLocation(Player player)
    {
        return FXUtil.getRandomPlayerLocation(player, false);
    }

    public static Location getRandomPlayerLocation(Player player, boolean flip)
    {
        Location location = new Random().nextBoolean() ? player.getLocation() : player.getEyeLocation();
        double x = new Random().nextDouble();
        double y = new Random().nextDouble();
        double z = new Random().nextDouble();
        if (flip) {
            x = -x;
            y = -y;
            z = -z;
        }
        location.add(x, y, z);
        return location;
    }

    private static long roundProbability(double value)
    {
        long number = (long)Math.floor(value);
        double probability = value % 1.0;
        if (new Random().nextDouble() < probability) {
            ++number;
        }
        return number;
    }

    public static void smokeBurst(Player player, int smokeCount)
    {
        for (long count = FXUtil.roundProbability(smokeCount); count > 0L; --count)
        {
            FXUtil.playEffect(player.getLocation(), Effect.SMOKE);
        }
    }

    public static void flameBurst(Player player, int flameCount)
    {
        for (long count = FXUtil.roundProbability(flameCount); count > 0L; --count)
        {
            FXUtil.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES);
        }
    }

    public static void enderBurst(Player player, int enderCount)
    {
        for (long count = FXUtil.roundProbability(enderCount); count > 0L; --count)
        {
            FXUtil.playEffect(player.getLocation(), Effect.ENDER_SIGNAL);
        }
    }

    public static void smokeTrail(Location location, int bloodCount)
    {
        long innerCount;
        Location one = location.clone();
        Location two = one.clone().add(0.0, 1.0, 0.0);
        long count1 = FXUtil.roundProbability(bloodCount);
        long count2 = FXUtil.roundProbability(bloodCount);
        for (innerCount = count1; innerCount > 0L; --innerCount)
        {
            FXUtil.playEffect(one, Effect.SMOKE);
        }
        for (innerCount = count2; innerCount > 0L; --innerCount)
        {
            FXUtil.playEffect(two, Effect.SMOKE);
        }
    }
}
