package com.github.rfsmassacre.rizeraces.abilities.race.orc;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Set;
import java.util.UUID;

public class WarCryAbility extends TargetAbility
{
    @Getter
    private final Set<PotionEffect> warEffects;

    public WarCryAbility()
    {
        super("war-cry", Race.ORC);

        this.warEffects = ConfigUtil.getPotionEffects(config, "war-cry.effects");
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

        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1.0F, 0.1F);
        PotionUtil.applyPotions(caster, warEffects);

        for (LivingEntity target : areaOfEffect(caster))
        {
            if (target instanceof Mob mob)
            {
                mob.setTarget(caster);
            }
        }

        setCooldown(playerId);
        return AbilityResult.SUCCESS;
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
