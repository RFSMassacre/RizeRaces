package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class LevelListener implements Listener
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    public LevelListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    /*
     * Read experience points from vanilla levels.
     *
     * NOT IMPLEMENTED
     *
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLevel(PlayerExpChangeEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        double percent = config.getDouble("equation.percent");
        int level = origin.getLevel();
        origin.addExperience((long)(event.getAmount() * percent));
        int newLevel = origin.getLevel();
        if (newLevel > level)
        {
            locale.sendLocale(player, true, "level.level-up", "{level}",
                    Integer.toString(newLevel));
        }
    }
     */


}
