package com.github.rfsmassacre.rizeraces.tasks.demon;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.entity.Player;

public class WaterDamageTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final double waterDamage;

    public WaterDamageTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.waterDamage = this.config.getDouble("demon.water-damage");
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

            if (player.isInWater())
            {
                player.damage(waterDamage);
            }
        }
    }
}
