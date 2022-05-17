package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.managers.SkinManager;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.tasks.demon.RageTickTask;
import com.github.rfsmassacre.rizeraces.tasks.merfolk.HydrationTask;
import com.github.rfsmassacre.rizeraces.tasks.vampire.TemperatureTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.MoonTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfFormTickTask;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class LoginListener implements Listener
{
    private final Configuration config;
    private final OriginGson gson;

    public LoginListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        //Kick if skins aren't loaded./
        if (!SkinManager.isLoaded())
        {
            event.disallow(Result.KICK_OTHER, Locale.format(config.getString("kick-message")));
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        player.setFlying(false);

        //Read file async, add them to the cache
        gson.readAsync(playerId.toString(), (origin) ->
        {
            if (origin == null)
            {
                origin = new Origin(player);
                gson.write(playerId.toString(), origin);
            }

            gson.addOrigin(origin);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        UUID playerId = event.getPlayer().getUniqueId();
        Origin origin = gson.getOrigin(playerId);

        TemperatureTask.removeBossBar(playerId);
        MoonTask.removeBossBar(playerId);
        WolfFormTickTask.removeBossBar(playerId);
        HydrationTask.removeBossBar(playerId);
        RageTickTask.removeBossBar(playerId);

        //Write file async, remove them from the cache
        gson.writeAsync(playerId.toString(), origin);
        gson.removeOrigin(playerId);
    }
}
