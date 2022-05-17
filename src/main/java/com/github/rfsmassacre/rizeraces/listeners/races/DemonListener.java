package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.demon.RageAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.potion.PotionEffectType;

public class DemonListener implements Listener
{
    private final Configuration config;
    private final OriginGson gson;

    public DemonListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDemonPotionApply(EntityPotionEffectEvent event)
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

        if (!origin.getRace().equals(Race.DEMON))
        {
            return;
        }

        if (!event.getAction().equals(Action.ADDED))
        {
            return;
        }

        PotionEffectType potion = event.getModifiedType();
        if (PotionUtil.getNegativeEffects().contains(potion))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDemonFireTick(EntityDamageEvent event)
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

        if (!origin.getRace().equals(Race.DEMON))
        {
            return;
        }

        if (event.getCause().equals(DamageCause.LAVA) || event.getCause().equals(DamageCause.FIRE)
                || event.getCause().equals(DamageCause.FIRE_TICK))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDemonRageAttack(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.DEMON))
        {
            return;
        }

        RageAbility ability = (RageAbility)Ability.getAbility("rage");
        if (ability.isActive(player))
        {
            event.setDamage(event.getDamage() * ability.getPercent());
        }
    }
}
