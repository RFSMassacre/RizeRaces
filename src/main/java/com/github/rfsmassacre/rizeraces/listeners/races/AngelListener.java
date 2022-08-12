package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.race.angel.HealingBowAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FoodUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class AngelListener implements Listener
{
    private final Configuration config;
    private final OriginGson gson;

    public AngelListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    /*
     * Angels don't get hurt by fall damage.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAngelHurt(EntityDamageEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.ANGEL))
        {
            return;
        }

        List<String> damages = config.getStringList("angel.block-damage");
        if (damages.contains(event.getCause().toString()))
        {
            event.setCancelled(true);
        }
    }

    /*
     * When landing on an entity, it should cancel the pain early and convert the damage into heals.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAngelArrowHeal(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity entity && event.getDamager() instanceof Arrow arrow))
        {
            return;
        }

        if (!(arrow.getShooter() instanceof Player angel))
        {
            return;
        }

        Origin origin = gson.getOrigin(angel.getUniqueId());
        if (!(origin.getRace().equals(Race.ANGEL) && origin.isArrowHealing()))
        {
            return;
        }

        event.setCancelled(true);

        HealingBowAbility ability = (HealingBowAbility)Ability.getAbility("healing-bow");
        double heal = event.getDamage() * ability.getPercent();
        double health = entity.getHealth();
        AttributeInstance healthAttribute = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getValue();
        if (health < maxHealth)
        {
            double finalHeal = health + heal;
            if (finalHeal > maxHealth)
            {
                finalHeal = maxHealth;
            }

            entity.setHealth(finalHeal);
        }

        arrow.remove();
        entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().add(0.0, 0.5, 0.0), 8,
                0.5, 0.5, 0.5, 0.5);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFoodConsume(PlayerItemConsumeEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.ANGEL))
        {
            return;
        }

        float saturation = player.getSaturation() + config.getInt("angel.extra-saturation");
        if (saturation > FoodUtil.MAX_FOOD)
        {
            saturation = FoodUtil.MAX_FOOD;
        }

        player.setSaturation(saturation);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAngelPotionApply(EntityPotionEffectEvent event)
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

        if (!origin.getRace().equals(Race.ANGEL))
        {
            return;
        }

        if (!event.getAction().equals(Action.ADDED))
        {
            return;
        }

        PotionEffectType potion = event.getModifiedType();
        if (potion.equals(PotionEffectType.POISON) || potion.equals(PotionEffectType.WITHER))
        {
            event.setCancelled(true);
        }
    }
}
