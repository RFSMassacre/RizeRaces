package com.github.rfsmassacre.rizeraces.listeners.races;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.race.orc.RecoverAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;
import java.util.UUID;

public class OrcListener implements Listener
{
    private final Configuration config;
    private final OriginGson gson;

    public OrcListener()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
    }

    @EventHandler(ignoreCancelled = true)
    public void onOrcRecover(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
        {
            return;
        }

        UUID playerId = player.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.ORC))
        {
            return;
        }

        RecoverAbility ability = (RecoverAbility)Ability.getAbility("recover");
        if (!ability.isActive(player))
        {
            return;
        }

        Map<UUID, Double> recovering = ability.getRecovering();
        double damage = recovering.get(playerId);
        recovering.put(playerId, damage + event.getFinalDamage());
    }
}
