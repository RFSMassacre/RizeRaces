package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.events.RaceChangeEvent;
import com.github.rfsmassacre.rizeraces.items.scrolls.ScrollItem;
import com.github.rfsmassacre.rizeraces.items.scrolls.ScrollItem.Complexity;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RizeRacesCommand extends SpigotCommand
{
    private final Configuration config;
    private final OriginGson gson;

    private Sound errorSound;
    private Sound successSound;
    private final float volume;
    private final float pitch;

    public RizeRacesCommand()
    {
        super(RizeRaces.getInstance().getLocale(), "rizeraces");

        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.volume = 1.0F;
        this.pitch = 0.75F;

        String errorName = config.getString("command-sounds.error");
        String successName = config.getString("command-sounds.success");

        try
        {
            this.errorSound = Sound.valueOf(errorName);
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            //Do nothing
        }

        try
        {
            this.successSound = Sound.valueOf(successName);
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            //Do nothing
        }

        addSubCommand(new ReloadCommand());
        addSubCommand(new ChangeRaceCommand());
        addSubCommand(new EmptyScrollCommand());
        addSubCommand(new FilledScrollCommand());
        addSubCommand(new SetLevelCommand());
    }

    @Override
    protected void onFail(CommandSender sender)
    {
        locale.sendLocale(sender, true, "error.no-perm");
        playSound(sender, errorSound);
    }

    @Override
    protected void onInvalidArgs(CommandSender sender)
    {
        locale.sendLocale(sender, true, "error.invalid-args");
        playSound(sender, errorSound);
    }

    private void playSound(CommandSender sender, Sound sound)
    {
        if (!(sender instanceof Player player))
        {
            return;
        }

        if (sound == null)
        {
            return;
        }

        player.playSound(player, sound, volume, pitch);
    }

    /*
     * Change Race Command
     */
    private class ChangeRaceCommand extends SubCommand
    {
        public ChangeRaceCommand()
        {
            super("changerace", "rizeraces.changerace");
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (args.length < 2)
            {
                locale.sendLocale(sender, true, "change-race.invalid-args");
            }
            else if (args.length == 2)
            {
                if (isConsole(sender))
                {
                    locale.sendLocale(sender, true, "error.console");
                    return;
                }

                try
                {
                    Race race = Race.valueOf(args[1].toUpperCase());
                    Player player = (Player)sender;
                    UUID playerId = player.getUniqueId();
                    Origin origin = gson.getOrigin(playerId);
                    RaceChangeEvent event = new RaceChangeEvent(origin, race);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled())
                    {
                        locale.sendLocale(sender, true, "change-race.cancelled-event");
                        return;
                    }

                    origin.reset();
                    origin.setRace(event.getRace());
                    gson.writeAsync(playerId.toString(), origin);

                    locale.sendLocale(player, true, "change-race.success.target", "{race}",
                            race.toString());
                }
                catch (IllegalArgumentException | NullPointerException exception)
                {
                    locale.sendLocale(sender, true, "change-race.invalid-race");
                    playSound(sender, errorSound);
                }
            }
            else
            {
                if (!sender.hasPermission("rizeraces.changerace.others"))
                {
                    onFail(sender);
                    return;
                }

                try
                {
                    Race race = Race.valueOf(args[1].toUpperCase());
                    Player player = Bukkit.getPlayer(args[2]);
                    if (player == null)
                    {
                        locale.sendLocale(sender, true, "error.player.online", "{player}", args[1]);
                        playSound(sender, errorSound);
                        return;
                    }

                    UUID playerId = player.getUniqueId();
                    Origin origin = gson.getOrigin(playerId);
                    RaceChangeEvent event = new RaceChangeEvent(origin, race);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled())
                    {
                        locale.sendLocale(sender, true, "change-race.cancelled-event");
                        return;
                    }

                    origin.reset();
                    origin.setRace(event.getRace());
                    gson.writeAsync(playerId.toString(), origin);

                    locale.sendLocale(player, true, "change-race.success.target", "{race}",
                            race.toString());
                    if (!sender.equals(player))
                    {
                        locale.sendLocale(sender, true, "change-race.success.self", "{player}",
                                player.getDisplayName(), "{race}", race.toString());
                    }
                }
                catch (IllegalArgumentException | NullPointerException exception)
                {
                    locale.sendLocale(sender, true, "change-race.invalid-race");
                    playSound(sender, errorSound);
                }
            }
        }

        /*
        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            Player player = null;
            if (args.length == 2)
            {
                if (isConsole(sender))
                {
                    locale.sendLocale(sender, true, "error.console");
                    return;
                }
                else
                {
                    player = (Player)sender;
                }
            }
            else if (args.length > 2)
            {
                player = Bukkit.getPlayer(args[2]);
                if (player == null)
                {
                    locale.sendLocale(sender, true, "error.player.online", "{player}", args[1]);
                    playSound(sender, errorSound);
                    return;
                }
            }
            try
            {
                if (player == null)
                {
                    locale.sendLocale(sender, true, "error.player.online", "{player}", args[1]);
                    playSound(sender, errorSound);
                    return;
                }

                Race race = Race.valueOf(args[1]);
                Origin origin = new Origin(player, race);
                RaceChangeEvent event = new RaceChangeEvent(origin, race);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled())
                {
                   locale.sendLocale(sender, true, "change-race.cancelled-event");
                   return;
                }

                gson.addOrigin(origin);
                gson.writeAsync(player.getUniqueId().toString(), origin);

                locale.sendLocale(player, true, "change-race.success.target", "{race}",
                        race.toString());
                if (!sender.equals(player))
                {
                    locale.sendLocale(sender, true, "change-race.success.self", "{player}",
                            player.getDisplayName(), "{race}", race.toString());
                }

                playSound(sender, successSound);
            }
            catch (IllegalArgumentException | NullPointerException exception)
            {
                locale.sendLocale(sender, true, "change-race.invalid-race");
                playSound(sender, errorSound);
            }
        }
         */

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                for (Race race : Race.values())
                {
                    suggestions.add(race.toString());
                }
            }
            else if (args.length == 3 && sender.hasPermission("rizeraces.changerace.others"))
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    suggestions.add(player.getName());
                }
            }

            return suggestions;
        }
    }

    private class EmptyScrollCommand extends SubCommand
    {
        public EmptyScrollCommand()
        {
            super("emptyscroll", "rizeraces.admin.emptyscroll");
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //rr emptyscroll <complexity> <amount> [player]
            if (args.length < 3)
            {
                locale.sendLocale(sender, true, "empty-scroll.invalid-args");
                playSound(sender, errorSound);
            }
            else if (args.length == 3)
            {
                if (!(sender instanceof Player player))
                {
                    locale.sendLocale(sender, true, "error.console");
                    return;
                }

                try
                {
                    Complexity complexity = Complexity.valueOf(args[1].toUpperCase());
                    int amount = Integer.parseInt(args[2]);
                    ItemStack emptyScroll = new ScrollItem(complexity, null, false).getItemStack();
                    emptyScroll.setAmount(amount);
                    PlayerInventory inventory = player.getInventory();
                    if (inventory.firstEmpty() == -1)
                    {
                        player.getWorld().dropItem(player.getLocation(), emptyScroll);
                        locale.sendLocale(player, true, "empty-scroll.success.dropped",
                                "{complexity}", Locale.capitalize(complexity.toString()), "{amount}",
                                Integer.toString(amount));
                    }
                    else
                    {
                        inventory.addItem(emptyScroll);
                        locale.sendLocale(player, true, "empty-scroll.success.inventory",
                                "{complexity}", Locale.capitalize(complexity.toString()), "{amount}",
                                Integer.toString(amount));
                    }

                    playSound(player, successSound);
                }
                catch (IllegalArgumentException exception)
                {
                    locale.sendLocale(sender, true, "empty-scroll.invalid-args");
                    playSound(player, errorSound);
                }
            }
            else
            {
                Player player = Bukkit.getPlayer(args[3]);
                if (player == null)
                {
                    locale.sendLocale(sender, true, "error.no-player");
                    playSound(sender, errorSound);
                    return;
                }

                try
                {
                    Complexity complexity = Complexity.valueOf(args[1].toUpperCase());
                    int amount = Integer.parseInt(args[2]);
                    ItemStack emptyScroll = new ScrollItem(complexity, null, false).getItemStack();
                    emptyScroll.setAmount(amount);
                    PlayerInventory inventory = player.getInventory();
                    if (inventory.firstEmpty() == -1)
                    {
                        player.getWorld().dropItem(player.getLocation(), emptyScroll);
                        locale.sendLocale(player, true, "empty-scroll.success.dropped",
                                "{complexity}", Locale.capitalize(complexity.toString()), "{amount}",
                                Integer.toString(amount));
                    }
                    else
                    {
                        inventory.addItem(emptyScroll);
                        locale.sendLocale(player, true, "empty-scroll.success.inventory",
                                "{complexity}", Locale.capitalize(complexity.toString()), "{amount}",
                                Integer.toString(amount));
                    }

                    playSound(player, successSound);
                }
                catch (IllegalArgumentException exception)
                {
                    locale.sendLocale(sender, true, "filled-scroll.invalid-args");
                    playSound(player, errorSound);
                }

            }
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                for (Complexity complexity : Complexity.values())
                {
                    suggestions.add(complexity.toString());
                }
            }
            else if (args.length == 3)
            {
                for (int amount = 1; amount <= 64; amount++)
                {
                    suggestions.add(Integer.toString(amount));
                }
            }
            else if (args.length == 4)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!sender.equals(player))
                    {
                        suggestions.add(player.getName());
                    }
                }
            }
            return suggestions;
        }
    }

    private class FilledScrollCommand extends SubCommand
    {
        public FilledScrollCommand()
        {
            super("filledscroll", "rize.admin.filledscroll");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //rr scroll <complexity> <race> <amount> [player]
            if (args.length < 4)
            {
                locale.sendLocale(sender, true, "filled-scroll.invalid-args");
                playSound(sender, errorSound);
            }
            else if (args.length == 4)
            {
                if (!(sender instanceof Player player))
                {
                    locale.sendLocale(sender, true, "error.console");
                    return;
                }

                try
                {
                    Complexity complexity = Complexity.valueOf(args[1].toUpperCase());
                    Race race = Race.valueOf(args[2].toUpperCase());
                    int amount = Integer.parseInt(args[3]);
                    ItemStack filledScroll = new ScrollItem(complexity, race, true).getItemStack();
                    filledScroll.setAmount(amount);
                    PlayerInventory inventory = player.getInventory();
                    if (inventory.firstEmpty() == -1)
                    {
                        player.getWorld().dropItem(player.getLocation(), filledScroll);
                        locale.sendLocale(player, true, "filled-scroll.success.dropped", "{race}",
                                Locale.capitalize(race.toString()), "{complexity}",
                                Locale.capitalize(complexity.toString()), "{amount}", Integer.toString(amount));
                    }
                    else
                    {
                        inventory.addItem(filledScroll);
                        locale.sendLocale(player, true, "filled-scroll.success.inventory", "{race}",
                                Locale.capitalize(race.toString()), "{complexity}",
                                Locale.capitalize(complexity.toString()), "{amount}", Integer.toString(amount));
                    }
                }
                catch (IllegalArgumentException exception)
                {
                    locale.sendLocale(sender, true, "filled-scroll.invalid-args");
                    playSound(player, errorSound);
                }
            }
            else
            {
                Player player = Bukkit.getPlayer(args[3]);
                if (player == null)
                {
                    locale.sendLocale(sender, true, "error.no-player");
                    playSound(sender, errorSound);
                    return;
                }

                try
                {
                    Complexity complexity = Complexity.valueOf(args[1].toUpperCase());
                    Race race = Race.valueOf(args[2].toUpperCase());
                    int amount = Integer.parseInt(args[3]);
                    ItemStack filledScroll = new ScrollItem(complexity, race, true).getItemStack();
                    filledScroll.setAmount(amount);
                    PlayerInventory inventory = player.getInventory();
                    if (inventory.firstEmpty() == -1)
                    {
                        player.getWorld().dropItem(player.getLocation(), filledScroll);
                        locale.sendLocale(player, true, "filled-scroll.success.dropped", "{race}",
                                Locale.capitalize(race.toString()), "{complexity}",
                                Locale.capitalize(complexity.toString()), "{amount}", Integer.toString(amount));
                    }
                    else
                    {
                        inventory.addItem(filledScroll);
                        locale.sendLocale(player, true, "filled-scroll.success.inventory", "{race}",
                                Locale.capitalize(race.toString()), "{complexity}",
                                Locale.capitalize(complexity.toString()), "{amount}", Integer.toString(amount));
                    }

                    locale.sendLocale(player, true, "filled-scroll.success.sender", "{race}",
                            Locale.capitalize(race.toString()), "{complexity}",
                            Locale.capitalize(complexity.toString()), "{amount}", Integer.toString(amount));
                    playSound(sender, successSound);
                }
                catch (IllegalArgumentException exception)
                {
                    locale.sendLocale(sender, true, "filled-scroll.invalid-args");
                    playSound(player, errorSound);
                }
            }
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                for (Complexity complexity : Complexity.values())
                {
                    suggestions.add(complexity.toString());
                }
            }
            else if (args.length == 3)
            {
                for (Race race : Race.values())
                {
                    suggestions.add(race.toString());
                }
            }
            else if (args.length == 4)
            {
                for (int amount = 1; amount <= 64; amount++)
                {
                    suggestions.add(Integer.toString(amount));
                }
            }
            else if (args.length == 5)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!sender.equals(player))
                    {
                        suggestions.add(player.getName());
                    }
                }
            }

            return suggestions;
        }
    }

    private class SetLevelCommand extends SubCommand
    {
        private enum Action
        {
            SET,
            ADD,
            REMOVE
        }

        public SetLevelCommand()
        {
            super("setlevel", "rizeraces.admin.setlevel");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //rr setlevel <player> <SET/ADD/REMOVE> <race> <amount>
            if (args.length < 5)
            {
                locale.sendLocale(sender, true, "set-level.invalid-args");
                playSound(sender, errorSound);
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null)
            {
                locale.sendLocale(sender, true, "error.no-player");
                playSound(sender, errorSound);
                return;
            }

            Origin origin = gson.getOrigin(player.getUniqueId());
            Action action;
            try
            {
                action = Action.valueOf(args[2].toUpperCase());
            }
            catch (IllegalArgumentException exception)
            {
                locale.sendLocale(sender, true, "set-level.invalid-action");
                playSound(sender, errorSound);
                return;
            }

            Race race;
            try
            {
                race = Race.valueOf(args[3].toUpperCase());
            }
            catch (IllegalArgumentException exception)
            {
                locale.sendLocale(sender, true, "set-level.invalid-race");
                playSound(sender, errorSound);
                return;
            }

            int amount;
            try
            {
                amount = Integer.parseInt(args[4]);
            }
            catch (NumberFormatException exception)
            {
                locale.sendLocale(sender, true, "set-level.not-number");
                playSound(sender, errorSound);
                return;
            }

            switch (action)
            {
                case SET:
                {
                    origin.setLevel(race, amount);
                }
                case ADD:
                {
                    origin.addLevel(race, amount);
                }
                case REMOVE:
                {
                    origin.addLevel(race, -amount);
                }
            }

            int maxLevel = config.getInt("max-level");
            int level = origin.getLevel(race);
            if (level > maxLevel)
            {
                origin.setLevel(maxLevel);
            }
            else if (level < 0)
            {
                origin.setLevel(0);
            }

            if (!sender.equals(player))
            {
                locale.sendLocale(sender, true, "set-level.success.sender", "{player}",
                        player.getDisplayName(), "{level}", Integer.toString(origin.getLevel()), "{race}",
                        Locale.capitalize(race.toString()));
            }

            locale.sendLocale(player, true, "set-level.success.target", "{level}",
                    Integer.toString(origin.getLevel()), "{race}", Locale.capitalize(race.toString()));
            playSound(sender, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    suggestions.add(player.getName());
                }
            }
            else if (args.length == 3)
            {
                for (Action action : Action.values())
                {
                    suggestions.add(action.toString());
                }
            }
            else if (args.length == 4)
            {
                for (Race race : Race.values())
                {
                    suggestions.add(race.toString());
                }
            }
            else if (args.length == 5)
            {
                int maxLevel = config.getInt("max-level");
                for (int amount = 0; amount <= maxLevel; amount++)
                {
                    suggestions.add(Integer.toString(amount));
                }
            }

            return suggestions;
        }
    }

    private class ReloadCommand extends SubCommand
    {
        public ReloadCommand()
        {
            super("reload", "rizeraces.admin.reload");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            RizeRaces.getInstance().onReload();
            locale.sendLocale(sender, true, "reload");
            playSound(sender, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }
}
