package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.entity.Player;

public class AbilityDisplayTask implements Runnable
{
    private final OriginGson gson;

    public AbilityDisplayTask()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        for (Origin origin : gson.getOrigins())
        {
            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }

            display(origin, player.getInventory().getHeldItemSlot());
        }
    }

    public static void display(Origin origin, int slot)
    {
        Player player = origin.getPlayer();
        if (player == null)
        {
            return;
        }

        if (!origin.isAbilityMode())
        {
            return;
        }

        Ability ability = origin.getAbility(slot);
        if (ability == null)
        {
            return;
        }

        Locale locale = RizeRaces.getInstance().getLocale();
        int cooldown = ability.getCooldown(player.getUniqueId());
        if (ability instanceof BuffAbility buffAbility)
        {
            if (buffAbility.isActive(player))
            {
                locale.sendActionLocale(player, false, "ability.display.status", "{ability}",
                        ability.getDisplayName(), "{status}", "&eACTIVE");
                return;
            }
            else if (cooldown > 0)
            {
                locale.sendActionLocale(player, false, "ability.display.on-cooldown", "{ability}",
                        ability.getDisplayName(), "{cooldown}", Integer.toString(cooldown / 20));
                return;
            }
            else if (!buffAbility.hasReagent(player))
            {
                locale.sendActionLocale(player, false, "ability.display.status", "{ability}",
                        ability.getDisplayName(), "{status}", "&cNO REAGENT");
                return;
            }
        }


        if (cooldown == 0)
        {
            locale.sendActionLocale(player, false, "ability.display.status", "{ability}",
                    ability.getDisplayName(), "{status}", "&aREADY");
        }
        else
        {
            locale.sendActionLocale(player, false, "ability.display.on-cooldown", "{ability}",
                    ability.getDisplayName(), "{cooldown}", Integer.toString(cooldown / 20));
        }
    }
}
