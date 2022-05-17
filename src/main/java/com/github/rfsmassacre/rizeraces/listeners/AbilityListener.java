package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.Ability.AbilityResult;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.events.AbilityCastEvent;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityListener implements Listener
{
    private final Locale locale;
    private final OriginGson gson;

    private final Map<UUID, Long> trigger;
    private final long cooldown;

    public AbilityListener()
    {
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.trigger = new HashMap<>();
        this.cooldown = 750L;
    }

    /*
     * Prevent players from casting abilities from other races.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCastCancel(AbilityCastEvent event)
    {
        Player caster = event.getCaster();
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(event.getRace()))
        {
            event.setCancelled(true);
        }

        if (origin.getLevel(event.getRace()) < event.getAbility().getLevel())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Ability Activations
     */
    @EventHandler
    public void onAbilityCast(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.isAbilityMode())
        {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        Ability ability = origin.getAbility(slot);
        if (ability == null)
        {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (!trigger.containsKey(playerId))
        {
            //Activate
            handleResult(origin, ability);
            trigger.put(playerId, System.currentTimeMillis());
        }
        else
        {
            long time = trigger.get(playerId);
            if (time > 0 && System.currentTimeMillis() - time > cooldown)
            {
                //Activate
                handleResult(origin, ability);
                trigger.remove(playerId);
            }
        }

        event.setCancelled(true);
    }

    private void handleResult(Origin origin, Ability ability)
    {
        //Activate
        Player player = origin.getPlayer();
        if (player == null)
        {
            return;
        }

        String raceName = origin.getRace().toString().toLowerCase();
        AbilityResult result = ability.cast(player);
        switch (result)
        {
            case ON_COOLDOWN:
                locale.sendLocale(player, true, raceName + ".ability.on-cooldown", "{cooldown}",
                        Locale.formatTime(ability.getCooldown(player.getUniqueId()) / 20));
                break;
            case NO_REAGENT:
                locale.sendLocale(player, true, raceName + ".ability.cant-cast", "{reagent}",
                        Integer.toString((int)(ability.getReagent())));
                break;
            case NO_TARGET:
                locale.sendLocale(player, true, raceName + ".ability.no-target");
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHotBarScroll(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.isAbilityMode())
        {
            return;
        }

        int slot = event.getNewSlot();
        Ability ability = origin.getAbility(slot);
        if (ability == null)
        {
            return;
        }

        locale.sendActionMessage(player, ability.getDisplayName());
    }

    /*
     * Deactivate buffs
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        BuffAbility.deactivateAll(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), () -> BuffAbility.deactivateAll(event.getPlayer()),
                1L);
    }

    /*
     * Extra thing
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerResurrect(EntityResurrectEvent event)
    {
        LivingEntity entity = event.getEntity();
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4);
        entity.addPotionEffect(resistance);
    }
}
