package com.github.rfsmassacre.rizeraces.tasks.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.vampire.BloodLustAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.Player;

public class BloodLustTickTask implements Runnable
{
    private final OriginGson gson;
    private final BloodLustAbility ability;

    public BloodLustTickTask()
    {
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.ability = (BloodLustAbility)Ability.getAbility("blood-lust");
    }

    @Override
    public void run()
    {
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
