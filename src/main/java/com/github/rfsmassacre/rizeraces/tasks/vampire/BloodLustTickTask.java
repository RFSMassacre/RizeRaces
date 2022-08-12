package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.race.vampire.BloodLustAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.Player;

public class BloodLustTickTask implements Runnable
{
    private final OriginGson gson;

    public BloodLustTickTask()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @Override
    public void run()
    {
        BloodLustAbility ability = (BloodLustAbility)Ability.getAbility("blood-lust");
        for (Origin origin : gson.getOrigins())
        {
            if (!origin.getRace().equals(Race.VAMPIRE))
            {
                continue;
            }

            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }
            if (player.isDead())
            {
                continue;
            }
            if (!origin.isBloodLust())
            {
                continue;
            }

            int food = player.getFoodLevel();
            if (food > 0)
            {
                player.setFoodLevel(food - 1);
                continue;
            }

            ability.deactivate(player);
        }
    }
}
