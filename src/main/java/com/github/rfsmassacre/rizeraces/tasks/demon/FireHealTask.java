package com.github.rfsmassacre.rizeraces.tasks.demon;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class FireHealTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final double fireHeal;

    public FireHealTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.fireHeal = this.config.getDouble("demon.fire-heal");
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

            if (!origin.getRace().equals(Race.DEMON))
            {
                continue;
            }

            if (player.getFireTicks() > 0)
            {
                double health = player.getHealth();
                AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (healthAttribute == null)
                {
                    return;
                }

                double maxHealth = healthAttribute.getValue();
                if (health < maxHealth)
                {
                    double finalHeal = health + fireHeal;
                    if (finalHeal > maxHealth)
                    {
                        finalHeal = maxHealth;
                    }

                    player.setHealth(finalHeal);
                }
            }
        }
    }
}
