package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.race.elf.CamouflageAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.tasks.elf.ArrowTrackTask;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ElfListener implements Listener
{
    private final Configuration config;
    private final OriginGson gson;

    public ElfListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    /*
     * Elves shoot straight arrows from bows and crossbows.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onElfShootBow(EntityShootBowEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.ELF))
        {
            return;
        }

        if (!(event.getProjectile() instanceof Arrow arrow))
        {
            return;
        }

        if (event.getBow() != null && event.getBow().getType().equals(Material.CROSSBOW))
        {
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
        }

        double length = config.getDouble("elf.arrow-velocity");
        arrow.setVelocity(arrow.getVelocity().multiply(length));
        arrow.setGravity(false);
        ArrowTrackTask.getArrows().add(arrow);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if (event.getEntity() instanceof Arrow arrow)
        {
            ArrowTrackTask.getArrows().remove(arrow);
        }
    }

    /*
     * Elves do not consume items when loading a crossbow.
     */
    @EventHandler(ignoreCancelled = true)
    public void onElfLoadCrossbow(EntityLoadCrossbowEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.ELF))
        {
            return;
        }

        event.setConsumeItem(false);
        player.updateInventory();
    }

    @EventHandler(ignoreCancelled = true)
    public void onShadowDamage(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        CamouflageAbility ability = (CamouflageAbility)Ability.getAbility("camouflage");
        if (ability.getMelding().contains(player.getUniqueId()))
        {
            event.setCancelled(true);
        }
    }
}
