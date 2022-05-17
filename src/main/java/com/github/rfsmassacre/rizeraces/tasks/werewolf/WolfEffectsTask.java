package com.github.rfsmassacre.rizeraces.tasks.werewolf;

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

public class WolfEffectsTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Set<PotionEffect> effects;

    public WolfEffectsTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.effects = ConfigUtil.getPotionEffects(config, "werewolf.wolf-effects");
    }

    @Override
    public void run()
    {
        for (Origin origin : gson.getOrigins())
        {
            if (!origin.getRace().equals(Race.WEREWOLF))
            {
                continue;
            }

            if (!origin.isWolfForm())
            {
                continue;
            }

            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }

            for (PotionEffect effect : effects)
            {
                PotionUtil.applyPotion(player, effect);
            }
        }
    }
}
