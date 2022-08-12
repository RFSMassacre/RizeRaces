package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class RaceCommand extends SpigotCommand
{
    private final OriginGson gson;

    private Sound errorSound;
    private Sound successSound;
    private final float volume;
    private final float pitch;

    public RaceCommand()
    {
        super(RizeRaces.getInstance().getLocale(), "ability");

        Configuration config = RizeRaces.getInstance().getBaseConfig();
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

        addSubCommand(new InfoCommand());
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

    private class InfoCommand extends SubCommand
    {
        public InfoCommand()
        {
            super("", "rizeraces.race");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            Origin origin = gson.getOrigin(player.getUniqueId());
            String race = Locale.capitalize(origin.getRace().toString());
            String level = Locale.capitalize(Integer.toString(origin.getLevel()));
            locale.sendLocale(player, true, "race", "{level}", level, "{race}", race);
            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }
}
