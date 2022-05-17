package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.TextManager;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbilityCommand extends SpigotCommand
{
    private final OriginGson gson;
    private final TextManager text;

    private Sound errorSound;
    private Sound successSound;
    private final float volume;
    private final float pitch;

    public AbilityCommand()
    {
        super(RizeRaces.getInstance().getLocale(), "ability");

        Configuration config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.text = RizeRaces.getInstance().getTextManager();
        this.volume = 1.0F;
        this.pitch = 0.75F;

        this.text.cacheTextFile("ability-help.txt");

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

        addSubCommand(new HelpCommand());
        addSubCommand(new AbilityToggleCommand());
        addSubCommand(new AbilityBindCommand());
        addSubCommand(new AbilityUnbindCommand());
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

    private class AbilityToggleCommand extends SubCommand
    {
        public AbilityToggleCommand()
        {
            super("toggle", "rizeraces.ability.toggle");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (isConsole(sender))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            Player player = (Player)sender;
            Origin origin = gson.getOrigin(player.getUniqueId());
            if (origin.isAbilityMode())
            {
                origin.setAbilityMode(false);
                locale.sendLocale(player, true, "ability.toggle.disabled");
            }
            else
            {
                origin.setAbilityMode(true);
                locale.sendLocale(player, true, "ability.toggle.enabled");
            }

            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }

    private class AbilityBindCommand extends SubCommand
    {
        public AbilityBindCommand()
        {
            super("bind", "rizeraces.ability.bind");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (isConsole(sender))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            Player player = (Player)sender;
            Origin origin = gson.getOrigin(player.getUniqueId());
            if (args.length < 2)
            {
                locale.sendLocale(player, true, "bind.invalid-args");
                return;
            }

            int slot = player.getInventory().getHeldItemSlot();
            Ability ability = null;
            for (Ability found : Ability.getAbilities())
            {
                if (found.getName().equalsIgnoreCase(args[1]) && found.getRace().equals(origin.getRace()))
                {
                    ability = found;
                    break;
                }
            }

            if (ability == null)
            {
                locale.sendLocale(player, true, "bind.invalid-ability");
                return;
            }

            if (ability.getLevel() < origin.getLevel(origin.getRace()))
            {
                locale.sendLocale(player, true, "bind.not-level");
                return;
            }

            origin.bindAbility(slot, ability);
            locale.sendLocale(player, true, "bind.success", "{ability}", ability.getDisplayName(),
                    "{slot}", Integer.toString(slot + 1));
            locale.sendActionMessage(player, ability.getDisplayName());
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (isConsole(sender))
            {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            Player player = (Player)sender;
            Origin origin = gson.getOrigin(player.getUniqueId());
            if (origin == null)
            {
                return Collections.emptyList();
            }

            if (args.length == 2)
            {
                for (Ability ability : Ability.getAbilities())
                {
                    if (ability.getRace().equals(origin.getRace()))
                    {
                        suggestions.add(ability.getName());
                    }
                }
            }

            return suggestions;
        }
    }

    private class AbilityUnbindCommand extends SubCommand
    {
        public AbilityUnbindCommand()
        {
            super("unbind", "rizeraces.ability.unbind");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (isConsole(sender))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            Player player = (Player)sender;
            Origin origin = gson.getOrigin(player.getUniqueId());
            int slot = player.getInventory().getHeldItemSlot();
            Ability ability = origin.getAbility(slot);
            if (ability == null)
            {
                locale.sendLocale(player, true, "unbind.no-ability");
                return;
            }

            origin.unbindAbility(slot);
            locale.sendLocale(player, true, "unbind.success", "{ability}", ability.getDisplayName(),
                    "{slot}", Integer.toString(slot + 1));
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }

    private class HelpCommand extends SubCommand
    {
        public HelpCommand()
        {
            super("help", "rizeraces.ability.help");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            locale.sendMessage(sender, String.join("\n", text.loadTextFile("ability-help.txt")));
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }
}
