package com.github.rfsmassacre.rizeraces.tasks.werewolf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.moons.Moon;
import com.github.rfsmassacre.rizeraces.moons.Moon.MoonPhase;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.BossBarUtil;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.SunUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoonTask implements Runnable
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    private final Map<Material, Double> blocks;

    private static final HashMap<UUID, BossBar> BOSS_BARS = new HashMap<>();

    public MoonTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.blocks = ConfigUtil.getMaterialDouble(config, "ceiling-blocks");
    }

    @Override
    public void run()
    {
        BuffAbility ability = (BuffAbility)Ability.getAbility("wolf-form");
        for (Origin origin : gson.getOrigins())
        {
            if (!origin.getRace().equals(Race.WEREWOLF))
            {
                continue;
            }

            Player player = origin.getPlayer();
            if (player == null)
            {
                return;
            }

            UUID playerId = player.getUniqueId();
            Moon moon = Moon.getMoon(player.getWorld());
            if (moon == null)
            {
                continue;
            }

            if (MoonPhase.FULL_MOON.equals(moon.getCurrentPhase()))
            {
                BossBar bossBar = BOSS_BARS.get(playerId);
                if (bossBar == null)
                {
                    bossBar = BossBarUtil.createBossBar("full-moon");
                    if (bossBar == null)
                    {
                        continue;
                    }

                    bossBar.setProgress(1.0 - ((double)(moon.getTime() - MoonPhase.FULL_MOON.getStart()) /
                            (double)(MoonPhase.FULL_MOON.getEnd() - MoonPhase.FULL_MOON.getStart())));
                    bossBar.addPlayer(player);
                    bossBar.setVisible(true);
                    BOSS_BARS.put(playerId, bossBar);
                }

                bossBar.setProgress(1.0 - ((double)(moon.getTime() - MoonPhase.FULL_MOON.getStart()) /
                        (double)(MoonPhase.FULL_MOON.getEnd() - MoonPhase.FULL_MOON.getStart())));

                Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
                double terrain = SunUtil.getTerrainOpacity(block, blocks);
                if (terrain == 1.0 || origin.isWolfForm())
                {
                    continue;
                }

                moon.addTransformed(player.getUniqueId());
                if (!ability.isActive(player))
                {
                    ability.resetCooldown(playerId);
                    ability.activate(player);
                    origin.setTransformTime(-1);
                    locale.sendLocale(player, true, "werewolf.full-moon");
                }
            }
            else
            {
                removeBossBar(playerId);
                if (!moon.hasTransformed(playerId))
                {
                    continue;
                }

                moon.removeTransformed(playerId);
                if (ability.isActive(player))
                {
                    ability.deactivate(player);
                }

                locale.sendLocale(player, true, "werewolf.sun");
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
