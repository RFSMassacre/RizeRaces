package com.github.rfsmassacre.rizeraces.tasks.werewolf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.BossBarUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class WolfFormTickTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final int interval;

    private static final HashMap<UUID, BossBar> BOSS_BARS = new HashMap<>();

    public WolfFormTickTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.interval = config.getInt("threads.werewolf.wolf-form");
    }

    @Override
    public void run()
    {
        BuffAbility ability = (BuffAbility) Ability.getAbility("wolf-form");
        for (Origin origin : gson.getOrigins())
        {
            if (!origin.getRace().equals(Race.WEREWOLF))
            {
                continue;
            }

            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }

            UUID playerId = player.getUniqueId();
            if (!origin.getRace().equals(Race.WEREWOLF))
            {
                continue;
            }

            if (!ability.isActive(player))
            {
                continue;
            }

            if (origin.getTransformTime() != -1)
            {
                origin.tickTransformTime(interval);
            }

            if (origin.getTransformTime() == 0)
            {
                ability.deactivate(player);
                removeBossBar(playerId);
                continue;
            }


            BossBar bossBar = BOSS_BARS.get(playerId);
            if (origin.getTransformTime() != -1)
            {
                if (bossBar == null)
                {
                    bossBar = BossBarUtil.createBossBar("werewolf");
                    if (bossBar == null)
                    {
                        continue;
                    }
                }

                bossBar.setProgress((double)origin.getTransformTime() / (double)ability.getDuration());
                bossBar.addPlayer(player);
                bossBar.setVisible(true);
                BOSS_BARS.put(playerId, bossBar);
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
