package com.github.rfsmassacre.rizeraces.abilities.race.angel;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
            if (failEvent(caster, target))
            {
                return AbilityResult.FAILED;
            }

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

    @Override
    public String formatReagent()
    {
        return null;
    }
}
