package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Set;

public class DarkEffectTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Set<PotionEffect> bloodLust;
    private final Set<PotionEffect> passives;


    public DarkEffectTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.bloodLust = ConfigUtil.getPotionEffects(config, "vampire.blood-lust.effects");
        this.passives = ConfigUtil.getPotionEffects(config, "vampire.passives.effects");
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

            if (!origin.getRace().equals(Race.VAMPIRE))
            {
                continue;
            }

            for (PotionEffect effect : passives)
            {
                PotionUtil.applyPotion(player, effect);
            }

            if (origin.isBloodLust())
            {
                for (PotionEffect effect : bloodLust)
                {
                    PotionUtil.applyPotion(player, effect);
                }
            }
        }
    }
}
