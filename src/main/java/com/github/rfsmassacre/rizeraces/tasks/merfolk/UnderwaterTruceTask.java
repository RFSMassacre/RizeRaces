package com.github.rfsmassacre.rizeraces.tasks.merfolk;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.List;

public class UnderwaterTruceTask implements Runnable
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    public UnderwaterTruceTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        List<String> mobNames = this.config.getStringList("merfolk.truce.mobs");
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

            if (origin.getTruceTicks() <= 0)
            {
                continue;
            }

            int tick = config.getInt("threads.merfolk.truce");
            origin.addTruceTicks(-tick);
            if (origin.getTruceTicks() > 0)
            {
                continue;
            }

            locale.sendLocale(origin.getPlayer(), true, "merfolk.truce.restored");
            for (Entity entity : player.getNearbyEntities(64.0, 16.0, 64.0))
            {
                if (!(entity instanceof Mob mob))
                {
                    continue;
                }

                String mobType = entity.getType().toString();
                LivingEntity target = mob.getTarget();
                if (mobNames.contains(mobType) && (target != null && target.equals(origin.getPlayer())))
                {
                    mob.setTarget(null);
                }
            }
        }
    }
}
