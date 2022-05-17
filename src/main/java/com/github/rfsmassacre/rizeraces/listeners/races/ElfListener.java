package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionType;

import java.util.HashSet;
import java.util.Set;

public class ElfListener implements Listener
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    private final Set<PotionType> beneficialTypes;

    public ElfListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.beneficialTypes = new HashSet<>();
        this.beneficialTypes.add(PotionType.FIRE_RESISTANCE);
        this.beneficialTypes.add(PotionType.INSTANT_HEAL);
        this.beneficialTypes.add(PotionType.INVISIBILITY);
        this.beneficialTypes.add(PotionType.JUMP);
        this.beneficialTypes.add(PotionType.LUCK);
        this.beneficialTypes.add(PotionType.NIGHT_VISION);
        this.beneficialTypes.add(PotionType.REGEN);
        this.beneficialTypes.add(PotionType.SLOW_FALLING);
        this.beneficialTypes.add(PotionType.SPEED);
        this.beneficialTypes.add(PotionType.STRENGTH);
        this.beneficialTypes.add(PotionType.WATER_BREATHING);
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

        /*
        if (!origin.getRace().equals(Race.ELF))
        {
            return;
        }
         */

        if (!(event.getProjectile() instanceof AbstractArrow arrow))
        {
            return;
        }

        if (event.getBow() != null && event.getBow().getType().equals(Material.CROSSBOW))
        {
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
        }

        arrow.setGravity(false);
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

        /*
        if (!origin.getRace().equals(Race.ELF))
        {
            return;
        }
         */

        event.setConsumeItem(false);
        player.updateInventory();
    }

    /*
     * Tipped arrows from Elves don't deal damage.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onElfTippedArrowDamage(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Arrow arrow))
        {
            return;
        }

        if (!(arrow.getShooter() instanceof Player player))
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        /*
        if (!origin.getRace().equals(Race.ELF))
        {
            return;
        }
         */

        if (beneficialTypes.contains(arrow.getBasePotionData().getType()))
        {
            event.setDamage(0.0);
        }

        if (event.isCancelled())
        {
            event.setCancelled(false);
        }
    }
}
