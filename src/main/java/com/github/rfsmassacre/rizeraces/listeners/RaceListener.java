package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.events.RaceChangeEvent;
import com.github.rfsmassacre.rizeraces.managers.SkinManager;
import com.github.rfsmassacre.rizeraces.moons.Moon;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.tasks.demon.RageTickTask;
import com.github.rfsmassacre.rizeraces.tasks.merfolk.HydrationTask;
import com.github.rfsmassacre.rizeraces.tasks.vampire.TemperatureTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.MoonTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfFormTickTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class RaceListener implements Listener
{
    /*
     * Remove all buffs when changing races.
     */
    @EventHandler(ignoreCancelled = true)
    public void onRaceChange(RaceChangeEvent event)
    {
        Player player = event.getOrigin().getPlayer();
        if (player == null)
        {
            return;
        }

        //Remove all buffs
        UUID playerId = player.getUniqueId();
        BuffAbility.deactivateAll(player);
        RizeRaces.getInstance().getSkinManager().removeSkin(player);

        //Remove boss bars
        TemperatureTask.removeBossBar(playerId);
        MoonTask.removeBossBar(playerId);
        WolfFormTickTask.removeBossBar(playerId);
        HydrationTask.removeBossBar(playerId);
        RageTickTask.removeBossBar(playerId);

        //Remove race stuff
        Moon.removeTransformId(playerId);
    }

    /*
     * Prevent other races from flying by accident.
     */
    @EventHandler(ignoreCancelled = true)
    public void onChangeFromVampire(RaceChangeEvent event)
    {
        Race race = event.getRace();
        Player player = event.getOrigin().getPlayer();
        if (player == null)
        {
            return;
        }

        if (!race.equals(Race.VAMPIRE))
        {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    /*
     * Ensure werewolves initially get a random skin that will stay persistent afterwards.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBecomeWerewolf(RaceChangeEvent event)
    {
        event.getOrigin().setSkin(SkinManager.getRandomSkin());
    }
}
