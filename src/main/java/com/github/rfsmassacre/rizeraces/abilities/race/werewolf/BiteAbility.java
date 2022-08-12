package com.github.rfsmassacre.rizeraces.abilities.race.werewolf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BiteAbility extends TargetAbility
{
    private final double damage;

    public BiteAbility()
    {
        super("bite", Race.WEREWOLF);

        this.damage = getConfigDouble("damage");
    }

    @Override
    public AbilityResult cast(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return AbilityResult.FAILED;
        }

        if (!origin.isWolfForm())
        {
            return AbilityResult.NO_REAGENT;
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

            doDamage(caster, target);
            return AbilityResult.SUCCESS;
        }
    }

    @Override
    public String formatReagent()
    {
        return null;
    }

    public void doDamage(Player caster, LivingEntity target)
    {
        UUID playerId = caster.getUniqueId();
        if (onCooldown(playerId))
        {
            Locale locale = RizeRaces.getInstance().getLocale();
            locale.sendLocale(caster, true, "werewolf.ability.on-cooldown", "{cooldown}",
                    Locale.formatTime(getCooldown(playerId) / 20));

            return;
        }

        target.damage(damage);
        FXUtil.playSound(target.getLocation(), Sound.ENTITY_WOLF_GROWL);

        setCooldown(playerId);
    }
}
