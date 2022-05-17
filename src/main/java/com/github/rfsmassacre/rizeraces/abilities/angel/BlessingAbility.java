package com.github.rfsmassacre.rizeraces.abilities.angel;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class BlessingAbility extends TargetAbility
{
    private final double yellowHealth;

    public BlessingAbility()
    {
        super("blessing", Race.ANGEL);

        this.yellowHealth = getConfigDouble("absorption-hearts");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        if (callEvent(caster, race))
        {
            return AbilityResult.FAILED;
        }

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

        LivingEntity target = getTargetEntity(caster);
        if (target == null)
        {
            return AbilityResult.NO_TARGET;
        }
        else
        {
            double maxAbsorption = target.getAbsorptionAmount() + yellowHealth;
            if (maxAbsorption > 20.0)
            {
                maxAbsorption = 20.0;
            }

            target.setAbsorptionAmount(maxAbsorption);
            playEffect(caster, target);
            if (target instanceof Player player)
            {
                locale.sendLocale(caster, true, "angel.blessing.success", "{target}",
                        player.getDisplayName());
            }
            else
            {
                locale.sendLocale(caster, true, "angel.blessing.success", "{target}",
                        target.getName());
            }

            setCooldown(playerId);
            return AbilityResult.SUCCESS;
        }
    }
}
