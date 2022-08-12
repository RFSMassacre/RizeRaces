package com.github.rfsmassacre.rizeraces.tasks.elf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Set;

public class ThresholdTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;
    private final Set<PotionEffect> thresholdEffects;

    public ThresholdTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.thresholdEffects = ConfigUtil.getPotionEffects(config, "elf.threshold.effects");
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

            if (!origin.getRace().equals(Race.ELF))
            {
                continue;
            }

            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute == null)
            {
                return;
            }

            double maxHealth = attribute.getValue();
            double health = player.getHealth();
            double threshold = config.getDouble("elf.threshold.percent");
            if (health / maxHealth <= threshold)
            {
                for (PotionEffect effect : thresholdEffects)
                {
                    PotionEffect current = player.getPotionEffect(effect.getType());
                    if (current == null || current.getAmplifier() < effect.getAmplifier())
                    {
                        PotionUtil.applyPotion(player, effect);
                    }
                }
            }
        }
    }
}
