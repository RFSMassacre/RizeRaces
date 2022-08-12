package com.github.rfsmassacre.rizeraces.abilities.race.demon;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class CurseAbility extends TargetAbility
{
    private final int duration;
    private final int witherStrength;
    private final int weaknessStrength;

    public CurseAbility()
    {
        super("curse", Race.DEMON);

        this.duration = getConfigInt("duration");
        this.witherStrength = getConfigInt("potion.wither-strength");
        this.weaknessStrength = getConfigInt("potion.weakness-strength");
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

            PotionUtil.applyPotion(target, new PotionEffect(PotionEffectType.WITHER, duration, witherStrength));
            PotionUtil.applyPotion(target, new PotionEffect(PotionEffectType.WEAKNESS, duration, weaknessStrength));
            playEffect(caster, target);
            if (target instanceof Player player)
            {
                locale.sendLocale(caster, true, "demon.curse.success", "{target}",
                        player.getDisplayName());
            }
            else
            {
                locale.sendLocale(caster, true, "demon.curse.success", "{target}",
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
