package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.CombatUtil;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Set;

public class MerfolkListener implements Listener
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    private final Set<PotionEffect> reflectionEffects;

    public MerfolkListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.reflectionEffects = ConfigUtil.getPotionEffects(config, "merfolk.reflection-effects");
    }

    /*
     * Merfolk deal more damage when using tridents.
     */
    @EventHandler(ignoreCancelled = true)
    public void onTridentDamage(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        Player player = null;
        if (damager instanceof Player)
        {
            player = (Player)damager;
        }
        else if (damager instanceof Trident)
        {
            Trident trident = (Trident)damager;
            if (trident.getShooter() instanceof Player)
            {
                player = (Player)trident.getShooter();
            }
        }

        if (player == null)
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.MERFOLK))
        {
            return;
        }

        double damage = config.getDouble("merfolk.trident.damage");
        event.setDamage(event.getDamage() + damage);
    }

    /*
     * Merfolks reflect poison when being melee attacked.
     */
    @EventHandler(ignoreCancelled = true)
    public void onMerfolkHurt(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof LivingEntity))
        {
            return;
        }

        LivingEntity damager = (LivingEntity)event.getDamager();
        if (!(event.getEntity() instanceof Player))
        {
            return;
        }

        Player player = (Player)event.getEntity();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.MERFOLK))
        {
            return;
        }

        if (event.getDamage() > 0.0)
        {
            for (PotionEffect effect : reflectionEffects)
            {
                PotionUtil.applyPotion(damager, effect);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobTargetMerfolk(EntityTargetEvent event)
    {
        if (!(event.getTarget() instanceof Player))
        {
            return;
        }

        Player player = (Player)event.getTarget();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.MERFOLK))
        {
            return;
        }

        List<String> mobNames = config.getStringList("merfolk.truce.mobs");
        String mobType = event.getEntityType().toString();
        if (mobNames.contains(mobType) && origin.getTruceTicks() <= 0)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMerfolkHitMob(EntityDamageByEntityEvent event)
    {
        Player player = CombatUtil.getSource(event.getDamager());
        if (player == null)
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        int truceBreak = this.config.getInt("merfolk.truce.length");
        List<String> mobNames = this.config.getStringList("merfolk.truce.mobs");
        String mobType = event.getEntityType().toString();
        boolean mythicMob = false;
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
        {
            mythicMob = MythicMobs.inst().getAPIHelper().isMythicMob(event.getEntity());
        }

        if (mobNames.contains(mobType) && !mythicMob)
        {
            if (origin.getTruceTicks() <= 0)
            {
                locale.sendLocale(player, true, "merfolk.truce.broken");
            }

            origin.setTruceTicks(truceBreak);
        }
    }
}
