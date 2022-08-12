package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.race.werewolf.PounceAbility;
import com.github.rfsmassacre.rizeraces.abilities.race.werewolf.WolfFormAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;


public class WerewolfListener implements Listener
{
    private final OriginGson gson;

    public WerewolfListener()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @EventHandler(ignoreCancelled = true)
    public void onWerewolfFall(EntityDamageEvent event)
    {
        if (!event.getCause().equals(DamageCause.FALL))
        {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
        {
            return;
        }

        Player player = (Player)entity;
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.WEREWOLF))
        {
            return;
        }

        if (origin.isWolfForm())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWerewolfDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.WEREWOLF))
        {
            return;
        }

        if (origin.isWolfForm())
        {
            FXUtil.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL);
        }
    }

    //On punch, should be calculated before all other plugins to make it more effective.
    //Weapons are useless.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onWerewolfPunch(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player player && entity instanceof LivingEntity))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.WEREWOLF))
        {
            return;
        }

        WolfFormAbility ability = (WolfFormAbility)Ability.getAbility("wolf-form");
        if (!ability.isActive(player))
        {
            return;
        }

        boolean emptyHand = player.getInventory().getItemInMainHand().getType().equals(Material.AIR);
        if (emptyHand)
        {
            event.setDamage(event.getDamage() + ability.getPunchDamage());
        }
        else
        {
            event.setDamage(1.0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWerewolfLand(PlayerMoveEvent event)
    {
        Location to = event.getTo();
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return;
        }

        PounceAbility ability = (PounceAbility) Ability.getAbility("pounce");
        Set<UUID> jumping = ability.getJumping();
        if (!origin.getRace().equals(Race.WEREWOLF))
        {
            jumping.remove(playerId);
            return;
        }

        if (!origin.isWolfForm())
        {
            jumping.remove(playerId);
            return;
        }

        if (!jumping.contains(playerId))
        {
            return;
        }

        if (player.getFallDistance() < 0.5F)
        {
            return;
        }

        Block under = to.getBlock().getRelative(BlockFace.DOWN);
        if (under.isLiquid())
        {
            jumping.remove(playerId);
            return;
        }

        if (under.isPassable())
        {
            return;
        }

        if (!under.getType().isSolid())
        {
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();
        double range = ability.getRange();
        for (int particle = 0; particle < ability.getRange(); particle++)
        {
            world.spawnParticle(Particle.EXPLOSION_LARGE, location, (int)range * 2, particle,
                    1, particle);
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        for (LivingEntity target : ability.getTargets(player))
        {
            target.damage(ability.getDamage(), player);
        }

        jumping.remove(playerId);
    }

    /*
    @EventHandler
    public void onWerewolfLand(EntityDamageEvent event)
    {
        if (!event.getCause().equals(DamageCause.FALL))
        {
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
        {
            return;
        }

        Player player = (Player)entity;
        UUID playerId = player.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return;
        }

        PounceAbility ability = (PounceAbility)Ability.getAbility("pounce");
        Set<UUID> jumping = ability.getJumping();
        if (!origin.getRace().equals(Race.WEREWOLF))
        {
            jumping.remove(playerId);
            return;
        }

        if (!origin.isWolfForm())
        {
            jumping.remove(playerId);
            return;
        }

        if (!jumping.contains(playerId))
        {
            return;
        }

        Map<UUID, Long> timeLimit = ability.getTimeLimit();
        long time = timeLimit.get(playerId);
        long timeLeft = System.currentTimeMillis() - time;
        if (time > 0 && timeLeft > ability.getLimit())
        {
            timeLimit.remove(playerId);
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();
        double range = ability.getRange();

        for (int particle = 0; particle < ability.getRange(); particle++)
        {
            world.spawnParticle(Particle.EXPLOSION_LARGE, location, (int)range * 2, particle,
                    1, particle);
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
        for (Entity nearbyEntity : player.getNearbyEntities(range, 1.5, range))
        {
            if (!(nearbyEntity instanceof LivingEntity))
            {
                continue;
            }

            LivingEntity target = (LivingEntity)nearbyEntity;
            target.damage(ability.getDamage(), player);
        }

        jumping.remove(playerId);
        timeLimit.remove(playerId);
    }
     */

    @EventHandler
    public void onWerewolfQuit(PlayerQuitEvent event)
    {
        PounceAbility ability = (PounceAbility)Ability.getAbility("pounce");

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        ability.getJumping().remove(playerId);
        ability.getTimeLimit().remove(playerId);
    }
}
