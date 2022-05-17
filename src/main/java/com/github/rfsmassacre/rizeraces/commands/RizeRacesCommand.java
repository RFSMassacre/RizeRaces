package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.events.RaceChangeEvent;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        if (!(sender instanceof Player))
        {
            return;
        }

        if (sound == null)
        {
            return;
        }

        Player player = (Player)sender;
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
