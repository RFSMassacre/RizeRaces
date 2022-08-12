package com.github.rfsmassacre.rizeraces.abilities.race.elf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.InstantAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CamouflageAbility extends InstantAbility
{
    @Getter
    private final Set<UUID> melding;

    @Getter
    private final int duration;

    public CamouflageAbility()
    {
        super("camouflage", Race.ELF);

        this.melding = new HashSet<>();
        this.duration = getConfigInt("duration");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(playerId);
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (onCooldown(caster.getUniqueId()))
        {
            return AbilityResult.ON_COOLDOWN;
        }

        melding.add(playerId);
        caster.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0));
        Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), () -> melding.remove(playerId), duration);

        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
