package com.github.rfsmassacre.rizeraces.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.MetadataValue;

public class CombatUtil
{
    public static Player getSource(Entity entity)
    {
        if (entity == null)
        {
            return null;
        }

        //Filter through the owner possibilities
        if (entity instanceof Player)
        {
            Player player = (Player)entity;
            if (player.hasMetadata("NPC"))
            {
                return null;
            }
            else
            {
                return player;
            }
        }
        else if (entity instanceof Projectile)
        {
            Projectile projectile = (Projectile)entity;
            if (projectile.getShooter() instanceof Player)
            {
                return (Player)projectile.getShooter();
            }
        }
        else
        {
            if (entity.hasMetadata("Player"))
            {
                MetadataValue value = entity.getMetadata("Player").get(0);
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (value.asString().contains(player.getName()))
                    {
                        return player;
                    }
                }
            }
        }

        return null;
    }

    /*
     * Mimics the armor mitigation system like in vanilla Minecraft.
     */
    public static double mitigateDamage(double damage, double defense, double toughness)
    {
        return damage * (1 - ((Math.min(20, Math.max(defense / 5, (defense - (damage / (2 + (toughness / 4))))))) / 25));
    }
    public static double mitigateDamage(double damage, Player player)
    {
        double armor = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();

        return mitigateDamage(damage, armor, toughness);
    }
    public static double reverseDamage(double damage, double defense, double toughness)
    {
        return damage / (1 - ((Math.min(20, Math.max(defense / 5, (defense - (damage / (2 + (toughness / 4))))))) / 25));
    }

    /*
     * FOV Functions
     */
    public static  boolean inFov(Player defender, Entity attacker, int lowDegree, int highDegree)
    {
        Location defenderLoc = defender.getLocation();
        Location attackerLoc = attacker.getLocation();

        //Angle of the defener
        //In Yaw
        float yaw = defenderLoc.getYaw();
        //In Degrees
        float degree = toDegrees(yaw);


        //Angle of the attacker
        //In degrees
        float antiDegree = (float)(Math.toDegrees(Math.atan2(defenderLoc.getZ() - attackerLoc.getZ(),
                defenderLoc.getX() - attackerLoc.getX())) + 90);
        antiDegree = (antiDegree + 360) % 360; //Convert from negative to positive
        //In yaw
        float antiYaw = toYaw(antiDegree);

        //True if this is within the given range of view
        float lowEnd = degree - lowDegree;
        float highEnd = degree + highDegree;

        if (antiDegree >= lowEnd && antiDegree <= highEnd)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private static float toYaw(float degrees)
    {
        float yaw = degrees;

        //Count backwards when beyond 180 degrees
        if (degrees > 180)
            yaw = -360 + degrees;

        return yaw;
    }
    private static float toDegrees(float yaw)
    {
        float degrees = yaw;

        //Count forwards when below 0 degrees
        if (yaw < 0)
            degrees = 360 + yaw;

        return degrees;
    }
}
