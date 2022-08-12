package com.github.rfsmassacre.rizeraces.tasks.orc;

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

public class WarEffectTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Set<PotionEffect> passiveEffects;
    private final Set<PotionEffect> sneakEffects;

    public WarEffectTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.passiveEffects = ConfigUtil.getPotionEffects(config, "orc.passive-effects");
        this.sneakEffects = ConfigUtil.getPotionEffects(config, "orc.sneak-effects");
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

            if (!origin.getRace().equals(Race.ORC))
            {
                continue;
            }

            PotionUtil.applyPotions(player, passiveEffects);
            if (player.isSneaking())
            {
                PotionUtil.applyPotions(player, sneakEffects);
            }
        }
    }
}
