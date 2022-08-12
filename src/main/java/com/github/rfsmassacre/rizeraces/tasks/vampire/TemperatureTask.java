package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.BossBarUtil;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.SunUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TemperatureTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Map<Material, Double> blocks;
    private final double armorPiece;
    private final double base;
    private final double up;
    private final double down;

    private final double burnStart;
    private final int burnDuration;
    private final Set<PotionEffect> radiationEffects;

    private static final HashMap<UUID, BossBar> BOSS_BARS = new HashMap<>();

    public TemperatureTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.blocks = ConfigUtil.getMaterialDouble(config, "ceiling-blocks");
        this.armorPiece = config.getDouble("vampire.radiation.armor-piece");
        this.base = config.getDouble("vampire.radiation.base-temperature");
        this.up = config.getDouble("vampire.radiation.multiplier.up");
        this.down = config.getDouble("vampire.radiation.multiplier.down");
        this.burnStart = config.getDouble("vampire.radiation.burn.start");
        this.burnDuration = config.getInt("vampire.radiation.burn.duration");
        this.radiationEffects = ConfigUtil.getPotionEffects(config, "vampire.radiation.effects");
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

            UUID playerId = player.getUniqueId();
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            {
                origin.setTemperature(0.0);
                removeBossBar(playerId);
                continue;
            }

            //Radiation calculation
            double radiation = SunUtil.getPlayerIrradiation(player, blocks, armorPiece) + base;
            if (radiation < 0.0)
            {
                origin.addTemperature(radiation * down);
            }
            else
            {
                origin.addTemperature(radiation * up);
            }

            //Radiation sickness
            for (PotionEffect effect : radiationEffects)
            {
                double start = config.getDouble("vampire.radiation.effects." +
                        effect.getType().getName().toLowerCase() + ".start");

                if (origin.getTemperature() >= start)
                {
                    player.addPotionEffect(effect);
                }
            }

            if (origin.getTemperature() >= burnStart)
            {
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                player.setFireTicks(burnDuration);
            }

            if (origin.getTemperature() > 0)
            {
                BossBar bossBar = BOSS_BARS.get(playerId);
                if (bossBar == null)
                {
                    bossBar = BossBarUtil.createBossBar("vampire");
                    if (bossBar == null)
                    {
                        continue;
                    }

                    bossBar.setProgress(origin.getTemperature());
                    bossBar.addPlayer(player);
                    bossBar.setVisible(true);
                    BOSS_BARS.put(playerId, bossBar);
                }

                bossBar.setProgress(origin.getTemperature());
            }
            else
            {
                removeBossBar(playerId);
            }
        }
    }

    public static void removeBossBar(UUID playerId)
    {
        BossBar bossBar = BOSS_BARS.get(playerId);
        if (bossBar == null)
        {
            return;
        }

        bossBar.removeAll();
        bossBar.setVisible(false);
        BOSS_BARS.remove(playerId);
    }
}