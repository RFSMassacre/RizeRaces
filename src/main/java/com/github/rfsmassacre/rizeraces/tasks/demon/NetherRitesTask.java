package com.github.rfsmassacre.rizeraces.tasks.demon;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Set;

public class NetherRitesTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Set<PotionEffect> rageEffects;
    private final Set<PotionEffect> netherEffects;

    public NetherRitesTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.rageEffects = ConfigUtil.getPotionEffects(this.config, "demon.rage-effects");
        this.netherEffects = ConfigUtil.getPotionEffects(this.config, "demon.nether-effects");
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

            if (origin.isEnraged())
            {
                PotionUtil.applyPotions(player, rageEffects);
            }

            if (player.getWorld().getEnvironment().equals(Environment.NETHER))
            {
                PotionUtil.applyPotions(player, netherEffects);
            }
        }
    }
}
