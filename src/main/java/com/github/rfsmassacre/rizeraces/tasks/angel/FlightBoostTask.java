package com.github.rfsmassacre.rizeraces.tasks.angel;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FlightBoostTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    public FlightBoostTask()
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

            if (!origin.getRace().equals(Race.ANGEL))
            {
                continue;
            }

            if (!player.isGliding())
            {
                continue;
            }

            double length = config.getDouble("angel.elytra-velocity") / 10;
            Vector velocity = player.getLocation().getDirection().normalize().multiply(length);
            player.setVelocity(velocity);
        }
    }
}
