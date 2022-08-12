package com.github.rfsmassacre.rizeraces.listeners;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.events.RaceLevelUpEvent;
import com.github.rfsmassacre.rizeraces.events.ScrollFillEvent;
import com.github.rfsmassacre.rizeraces.items.scrolls.ScrollItem;
import com.github.rfsmassacre.rizeraces.items.scrolls.ScrollItem.Complexity;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScrollListener implements Listener
{
    private static final int BASE_LEVEL_UP = 1;

    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    //private final Map<UUID, Long> emptyTime;
    private final Map<UUID, BukkitTask> emptyTasks;

    //private final Map<UUID, Long> filledTime;
    private final Map<UUID, BukkitTask> filledTasks;

    public ScrollListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();

        //this.emptyTime = new HashMap<>();
        this.emptyTasks = new HashMap<>();

        //this.filledTime = new HashMap<>();
        this.filledTasks = new HashMap<>();
    }

    //Cancel task if applicable.
    private void cancelTask(UUID playerId, Map<UUID, BukkitTask> tasks)
    {
        BukkitTask bukkitTask = tasks.get(playerId);
        if (bukkitTask != null)
        {
            bukkitTask.cancel();
            tasks.remove(playerId);
        }
    }

    //Scheduling a delayed task and canceling before doing it again.
    private void scheduleTaskLater(UUID playerId, Runnable task, int delay, Map<UUID, BukkitTask> tasks)
    {
        cancelTask(playerId, tasks);

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), task, delay);
        tasks.put(playerId, bukkitTask);
    }

    /*
     * Right click Filled Scroll to consume and level up.
     */
    @EventHandler
    public void onScrollRightClick(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        if (!(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)))
        {
            return;
        }
        if (!EquipmentSlot.HAND.equals(event.getHand()))
        {
            return;
        }

        Player player = event.getPlayer();
        ScrollItem scroll = ScrollItem.getScrollItem(event.getItem());
        if (scroll == null)
        {
            return;
        }

        Origin origin = gson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        //Consume and level up
        if (scroll.isFilled())
        {
            if (!scroll.getRace().equals(origin.getRace()))
            {
                locale.sendLocale(player, true, "filled-scroll.consume.wrong-race");
                return;
            }

            Complexity complexity = scroll.getComplexity();
            int minLevel = scroll.getMinLevel();
            int maxLevel = scroll.getMaxLevel();
            int level = origin.getLevel();
            if (level < minLevel)
            {
                locale.sendLocale(player, true, "filled-scroll.consume.min-failed", "{complexity}",
                        Locale.capitalize(complexity.toString()));
                return;
            }
            if (level > maxLevel)
            {
                locale.sendLocale(player, true, "filled-scroll.consume.max-failed", "{complexity}",
                        Locale.capitalize(complexity.toString()));
                return;
            }

            UUID playerId = player.getUniqueId();
            int timeOut = config.getInt("scroll.confirm-timeout");
            //Confirm message
            if (!filledTasks.containsKey(playerId))
            {
                locale.sendLocale(player, true, "filled-scroll.consume.confirm");

                scheduleTaskLater(playerId, () -> filledTasks.remove(playerId), timeOut, filledTasks);
            }
            else
            {
                cancelTask(playerId, filledTasks);

                RaceLevelUpEvent levelUpEvent = new RaceLevelUpEvent(origin, scroll, BASE_LEVEL_UP);
                Bukkit.getPluginManager().callEvent(levelUpEvent);
                if (levelUpEvent.isCancelled())
                {
                    locale.sendLocale(player, true, "filled-scroll.consume.failed");
                    return;
                }

                origin.addLevel(levelUpEvent.getLevelUps());
                player.getInventory().removeItem(scroll.getItemStack());
                locale.sendLocale(player, true, "filled-scroll.consume.success", "{level}",
                        Integer.toString(origin.getLevel()));
            }
        }
        //Fill with XP after confirmation.
        else
        {
            int levelRequired = scroll.getLevelRequired();
            int level = player.getLevel();
            if (level < levelRequired)
            {
                locale.sendLocale(player, true, "empty-scroll.fill.not-enough",
                        "{requiredLevel}", Integer.toString(levelRequired), "{currentLevel}",
                        Integer.toString(level));
                return;
            }

            UUID playerId = player.getUniqueId();
            int timeOut = config.getInt("scroll.confirm-timeout");
            //Confirm message
            if (!emptyTasks.containsKey(playerId))
            {
                locale.sendLocale(player, true, "empty-scroll.fill.confirm", "{level}",
                        Integer.toString(levelRequired));

                scheduleTaskLater(playerId, () -> emptyTasks.remove(playerId), timeOut, emptyTasks);
            }
            //Success
            else
            {
                cancelTask(playerId, emptyTasks);

                ScrollFillEvent fillEvent = new ScrollFillEvent(origin, scroll, levelRequired);
                Bukkit.getPluginManager().callEvent(fillEvent);
                if (fillEvent.isCancelled())
                {
                    locale.sendLocale(player, true, "empty-scroll.consume.failed");
                    return;
                }

                player.setLevel(level - fillEvent.getExperience());
                PlayerInventory inventory = player.getInventory();
                inventory.removeItem(scroll.getItemStack());

                Complexity complexity = scroll.getComplexity();
                Race race = origin.getRace();
                ScrollItem filledScroll = new ScrollItem(complexity, race, true);
                inventory.addItem(filledScroll.getItemStack());
                locale.sendLocale(player, true, "empty-scroll.fill.success", "{complexity}",
                        Locale.capitalize(complexity.toString()), "{race}", Locale.capitalize(race.toString()));
            }
        }
    }
}
