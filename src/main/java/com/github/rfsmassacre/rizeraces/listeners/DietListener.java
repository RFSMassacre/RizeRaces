package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FoodUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.List;

public class DietListener implements Listener
{
    private final OriginGson gson;
    private final Configuration config;

    public DietListener()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.config = RizeRaces.getInstance().getBaseConfig();
    }


    /*
     * Handling the diet of each race respectively.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerEat(PlayerItemConsumeEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = gson.getOrigin(player.getUniqueId());
        Race race = origin.getRace();
        String raceKey = race.toString().toLowerCase();
        List<String> dietList = config.getStringList("diet." + raceKey);
        Material food = event.getItem().getType();
        boolean cancel = config.getBoolean("diet.cancel-event");

        if (dietList.contains("ALL"))
        {
            return;
        }

        if (dietList.contains("NONE") || !dietList.contains(food.toString()))
        {
            if (cancel)
            {
                event.setCancelled(true);
            }
            else
            {
                int hunger = player.getFoodLevel() - FoodUtil.getFoodLevel(food);
                float saturation = player.getSaturation() - FoodUtil.getSaturationLevel(food);
                player.setFoodLevel(hunger);
                player.setSaturation(saturation);
            }
        }
    }
}
