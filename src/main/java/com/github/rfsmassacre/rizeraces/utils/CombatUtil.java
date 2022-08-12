package com.github.rfsmassacre.rizeraces.utils;

import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CombatUtil
{
    public enum ArmorType
    {
        LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS,
                Material.LEATHER_BOOTS),
        CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS,
                Material.CHAINMAIL_BOOTS),
        GOLDEN(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
        IRON(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS,
                Material.DIAMOND_BOOTS),
        NETHERITE(Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_BOOTS);

        private final Set<Material> armors;

        ArmorType(Material... armors)
        {
            this.armors = new HashSet<>(Arrays.asList(armors));
        }

        public static ArmorType getArmorType(Material material)
        {
            for (ArmorType type : ArmorType.values())
            {
                if (type.armors.contains(material))
                {
                    return type;
                }
            }

            return null;
        }
    }

    public static void setMaxHealth(Configuration config, Race race, Player player)
    {
        double maxHealth = config.getDouble("max-health." + race.toString().toLowerCase());
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null)
        {
            attribute.setBaseValue(maxHealth);
        }
    }

    public static Player getSource(Entity entity)
    {
        if (entity == null)
        {
            return null;
        }

        //Filter through the owner possibilities
        if (entity instanceof Player player)
        {
            if (player.hasMetadata("NPC"))
            {
                return null;
            }
            else
            {
                return player;
            }
        }
        else if (entity instanceof Projectile projectile)
        {
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
        return damage * (1 - ((Math.min(20, Math.max(defense / 5, (defense - (damage / (2 + (toughness / 4))))))) /
                25));
    }
    public static double mitigateDamage(double damage, Player player)
    {
        double armor = player.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();

        return mitigateDamage(damage, armor, toughness);
    }
    public static double reverseDamage(double damage, double defense, double toughness)
    {
        return damage / (1 - ((Math.min(20, Math.max(defense / 5, (defense - (damage / (2 + (toughness / 4))))))) /
                25));
    }

    /*
     * FOV Functions
     */
    public static  boolean inFov(Player defender, Entity attacker, int lowDegree, int highDegree)
    {
        Location defenderLoc = defender.getLocation();
        Location attackerLoc = attacker.getLocation();

        //Angle of the defender
        //In Yaw
        float yaw = defenderLoc.getYaw();
        //In Degrees
        float degree = toDegrees(yaw);


        //Angle of the attacker
        //In degrees
        float antiDegree = (float)(Math.toDegrees(Math.atan2(defenderLoc.getZ() - attackerLoc.getZ(),
                defenderLoc.getX() - attackerLoc.getX())) + 90);
        antiDegree = (antiDegree + 360) % 360; //Convert from negative to positive

        //True if this is within the given range of view
        float lowEnd = degree - lowDegree;
        float highEnd = degree + highDegree;

        return antiDegree >= lowEnd && antiDegree <= highEnd;
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
