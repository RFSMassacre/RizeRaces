package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class HungerHealTask implements Runnable
{
    private OriginGson gson;

    public HungerHealTask()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        for (Origin origin : gson.getOrigins())
        {
            if (!origin.getRace().equals(Race.VAMPIRE))
            {
                continue;
            }

            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }
            if (player.isDead())
            {
                continue;
            }

            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute == null)
            {
                continue;
            }

            double maxHealth = attribute.getValue();
            double health = player.getHealth();
            double healBack = health + 1;
            int food = player.getFoodLevel();
            if (healBack > maxHealth)
            {
                healBack = maxHealth;
            }

            if (health < maxHealth && food > 0)
            {
                player.setHealth(healBack);
                player.setFoodLevel(food - 1);
            }
        }
    }
}
