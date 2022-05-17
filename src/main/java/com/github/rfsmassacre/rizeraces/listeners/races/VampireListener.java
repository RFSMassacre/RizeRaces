package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.CombatUtil;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.rizeraces.utils.FoodUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VampireListener implements Listener
{
    private final OriginGson gson;
    private final Configuration config;
    private final Locale locale;

    public VampireListener()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
    }

    /*
     * Vampires do not naturally lose food bars.
     */
    @EventHandler(ignoreCancelled = true)
    public void onVampireHunger(FoodLevelChangeEvent event)
    {
        HumanEntity entity = event.getEntity();
        if (!(entity instanceof Player player))
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

        float food = event.getFoodLevel();
        if (food < player.getFoodLevel())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires do not naturally regenerate health.
     */
    @EventHandler(ignoreCancelled = true)
    public void onVampireHeal(EntityRegainHealthEvent event)
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

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (!event.getRegainReason().equals(RegainReason.CUSTOM))
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires do not get certain damage types.
     */
    @EventHandler(ignoreCancelled = true)
    public void onVampireHurt(EntityDamageEvent event)
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

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        List<String> damages = config.getStringList("vampire.block-damage");
        if (damages.contains(event.getCause().toString()))
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires can lifesteal from specified entities.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVampireLifesteal(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof LivingEntity entity))
        {
            return;
        }

        if (player.hasMetadata("NPC"))
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

        if (!event.getCause().equals(DamageCause.ENTITY_ATTACK))
        {
            return;
        }

        List<String> entities = config.getStringList("vampire.lifesteal.entities");
        if (!entities.contains(entity.getType().toString()))
        {
            return;
        }

        double heal = config.getDouble("vampire.lifesteal.percent");
        double health = player.getHealth();
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getValue();
        if (event.getDamage() >= 2.0 && health < maxHealth)
        {
            double damage = event.getFinalDamage();
            if (damage > entity.getHealth())
            {
                damage = entity.getHealth();
            }

            double finalHeal = health + (damage * heal);
            if (finalHeal > maxHealth)
            {
                finalHeal = maxHealth;
            }

            player.setHealth(finalHeal);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobTargetVampire(EntityTargetEvent event)
    {
        if (!(event.getTarget() instanceof Player player))
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

        List<String> mobNames = config.getStringList("vampire.truce.mobs");
        String mobType = event.getEntityType().toString();
        if (mobNames.contains(mobType) && origin.getTruceTicks() <= 0)
        {
            if (event.getEntity() instanceof Ghast)
            {
                event.setTarget(null);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVampireHitMob(EntityDamageByEntityEvent event)
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

        int truceBreak = this.config.getInt("vampire.truce.length");
        List<String> mobNames = this.config.getStringList("vampire.truce.mobs");
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
                locale.sendLocale(player, true, "vampire.truce.broken");
            }

            origin.setTruceTicks(truceBreak);
        }
    }

    /*
     * Vampires gain food bars from attacking enemies with blood.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVampireFoodsteal(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof LivingEntity entity))
        {
            return;
        }

        if (entity.hasMetadata("NPC"))
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

        List<String> mobNames = this.config.getStringList("vampire.food.blocked-mobs");
        if (mobNames.contains(event.getEntityType().toString()))
        {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.GLASS_BOTTLE))
        {
            return;
        }

        double damage = event.getDamage();
        double foodRatio = this.config.getDouble("vampire.food.food-ratio");
        if (entity.getHealth() < event.getDamage())
        {
            damage = entity.getHealth();
        }
        double foodLevel = (double)player.getFoodLevel() + (damage * foodRatio);
        if (foodLevel > FoodUtil.MAX_FOOD)
        {
            foodLevel = FoodUtil.MAX_FOOD;
        }

        player.setFoodLevel((int)Math.round(foodLevel));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBloodLustLifeSteal(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player) || !(event.getEntity() instanceof LivingEntity))
        {
            return;
        }

        if (player.hasMetadata("NPC"))
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
        if (!origin.isBloodLust())
        {
            return;
        }

        if (!event.getCause().equals(DamageCause.ENTITY_ATTACK))
        {
            return;
        }

        double heal = config.getDouble("vampire.blood-lust.life-steal");
        double health = player.getHealth();
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getValue();
        if (event.getDamage() >= 2.0 && health < maxHealth)
        {
            double finalHeal = health + heal;
            if (finalHeal > maxHealth)
            {
                finalHeal = maxHealth;
            }

            player.setHealth(finalHeal);
        }
    }

    @EventHandler
    public void onVampireDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        origin.setTemperature(0.0);
        FXUtil.smokeBurst(player, 30);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBatAttack(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player player))
        {
            return;
        }

        if (event.getEntity().hasMetadata("NPC"))
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

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBatDamaged(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        if (event.getEntity().hasMetadata("NPC"))
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

        if (origin.isBatForm())
        {
            //Ensure bats get 1 shot.
            double damage = config.getDouble("vampire.bat-form.damage");
            event.setDamage(damage);
        }
    }

    /*
     * Vampires in bat form can't interact with stuff.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires in bat form can't shoot bows.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatShoot(EntityShootBowEvent event)
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

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires in bat form can't break blocks.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatBreakBlock(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires in bat form can't pick up items.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatItemPickUp(EntityPickupItemEvent event)
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

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires in bat form can't drop items.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatItemDrop(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Vampires in bat form can't consume items.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBatItemConsume(PlayerItemConsumeEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (origin.isBatForm())
        {
            event.setCancelled(true);
        }
    }
}
