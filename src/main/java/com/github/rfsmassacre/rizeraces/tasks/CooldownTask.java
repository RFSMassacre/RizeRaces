package com.github.rfsmassacre.rizeraces.tasks;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.spigot.files.configs.Configuration;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class CooldownTask implements Runnable
{
    private final Configuration config;

    private final int interval;

    public CooldownTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();

        this.interval = config.getInt("threads.ability.cooldown");
    }

    @Override
    public void run()
    {
        /*
        for (Ability ability : Ability.getAbilities())
        {
            if (!(ability instanceof BuffAbility buffAbility))
            {
                continue;
            }

            Map<UUID, Integer> durations = buffAbility.getDurations();
            Iterator<Entry<UUID, Integer>> iterator = durations.entrySet().iterator();
            while (iterator.hasNext())
            {
                Entry<UUID, Integer> entry = iterator.next();
                int duration = entry.getValue() - interval;
                if (duration == -1)
                {
                    continue;
                }

                if (duration < 0)
                {
                    duration = 0;
                }
                entry.setValue(duration);

                if (duration == 0)
                {
                    iterator.remove();
                }
            }
        }
         */

        for (Ability ability : Ability.getAbilities())
        {
            Map<UUID, Integer> cooldowns = ability.getCooldowns();
            Iterator<Entry<UUID, Integer>> iterator = cooldowns.entrySet().iterator();
            while (iterator.hasNext())
            {
                Entry<UUID, Integer> entry = iterator.next();
                int cooldown = entry.getValue() - interval;
                if (cooldown < 0)
                {
                    cooldown = 0;
                }
                entry.setValue(cooldown);

                if (cooldown == 0)
                {
                    iterator.remove();
                }
            }
        }
    }
}
