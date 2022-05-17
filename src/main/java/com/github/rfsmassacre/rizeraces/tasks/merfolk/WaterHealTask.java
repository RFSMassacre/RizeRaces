package com.github.rfsmassacre.rizeraces.tasks.merfolk;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class WaterHealTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    public WaterHealTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        for (Origin origin : gson.getOrigins())
        {
            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }

            if (!origin.getRace().equals(Race.MERFOLK))
            {
                continue;
            }

            if (player.isDead())
            {
                continue;
            }

            if (player.isInWater())
            {
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (attribute == null)
                {
                    continue;
                }

                double maxHealth = attribute.getValue();
                double health = player.getHealth();
                double healBack = health + 1;

                if (healBack > maxHealth)
                {
                    healBack = maxHealth;
                }

                if (health < maxHealth)
                {
                    player.setHealth(healBack);
                }
            }
        }
    }
}
