package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.items.potions.BlackBloodPotion;
import com.github.rfsmassacre.rizeraces.items.potions.RedBloodPotion;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class BloodBottleCommand extends SpigotCommand
{
    private final Configuration config;
    private final Locale locale;
    private final OriginGson gson;

    private Sound errorSound;
    private Sound successSound;
    private final float volume;
    private final float pitch;

    public BloodBottleCommand()
    {
        super(RizeRaces.getInstance().getLocale(), "ability");

        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();
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

        addSubCommand(new BloodCommand());
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

    private class BloodCommand extends SubCommand
    {
        public BloodCommand()
        {
            super("", "rizeraces.bloodbottle");
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
            if (origin == null)
            {
                return;
            }

            ItemStack bottle = player.getInventory().getItemInMainHand();
            if (!bottle.getType().equals(Material.GLASS_BOTTLE))
            {
                locale.sendLocale(player, true, "vampire.bottle.no-bottle");
                return;
            }

            double health = player.getHealth();
            double cost = config.getDouble("vampire.bottle.cost");
            if (health <= cost)
            {
                locale.sendLocale(player, true, "vampire.bottle.no-hp");
                return;
            }

            if (origin.getRace().equals(Race.VAMPIRE))
            {
                if (bottle.getAmount() == 1)
                {
                    player.getInventory().setItemInMainHand(new BlackBloodPotion().getItemStack());
                }
                else
                {
                    player.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
                    player.getInventory().addItem(new BlackBloodPotion().getItemStack());
                }
            }
            else
            {
                if (bottle.getAmount() == 1)
                {
                    player.getInventory().setItemInMainHand(new RedBloodPotion().getItemStack());
                }
                else
                {
                    player.getInventory().removeItem(new ItemStack(Material.GLASS_BOTTLE, 1));
                    player.getInventory().addItem(new RedBloodPotion().getItemStack());
                }
            }

            player.updateInventory();
            player.playEffect(EntityEffect.HURT);
            player.setHealth(health - cost);
            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }
}
