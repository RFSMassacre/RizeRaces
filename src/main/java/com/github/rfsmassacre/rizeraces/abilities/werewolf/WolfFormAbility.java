package com.github.rfsmassacre.rizeraces.abilities.werewolf;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.managers.SkinManager;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfFormTickTask;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Set;
import java.util.UUID;


public class WolfFormAbility extends BuffAbility
{
    private final SkinManager skins;

    @Getter
    private final int duration;
    @Getter
    private final double punchDamage;

    public WolfFormAbility()
    {
        super("wolf-form", Race.WEREWOLF);

        this.skins = RizeRaces.getInstance().getSkinManager();

        this.duration = getConfigInt("duration");
        this.punchDamage = getConfigDouble("punch-damage");
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

        if (onCooldown(playerId))
        {
            Locale locale = RizeRaces.getInstance().getLocale();
            locale.sendLocale(caster, true, "merfolk.ability.on-cooldown", "{cooldown}",
                    Locale.formatTime(getCooldown(playerId) / 20));

            return AbilityResult.ON_COOLDOWN;
        }

        toggle(caster);
        return AbilityResult.SUCCESS;
    }

    @Override
    public boolean isActive(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        return origin != null && origin.isWolfForm();
    }

    @Override
    public void activate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        UUID playerId = caster.getUniqueId();
        if (onCooldown(playerId))
        {
            Locale locale = RizeRaces.getInstance().getLocale();
            locale.sendLocale(caster, true, "werewolf.ability.on-cooldown", "{cooldown}",
                    Locale.formatTime(getCooldown(playerId) / 20));

            return;
        }

        origin.setWolfForm(true);
        origin.setTransformTime(duration);
        skins.applySkin(caster, origin.getSkin());
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_WOLF_HOWL);
    }

    @Override
    public void deactivate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        //Prevent from turning off during a full moon.
        UUID playerId = caster.getUniqueId();
        origin.setWolfForm(false);
        origin.setTransformTime(0);
        skins.removeSkin(caster);
        FXUtil.playSound(caster.getLocation(), Sound.ENTITY_WOLF_GROWL);

        Configuration base = RizeRaces.getInstance().getBaseConfig();
        Set<PotionEffect> effects = ConfigUtil.getPotionEffects(base, "werewolf.wolf-effects");
        for (PotionEffect effect : effects)
        {
            caster.removePotionEffect(effect.getType());
        }

        caster.setAllowFlight(false);
        caster.setFlying(false);
        WolfFormTickTask.removeBossBar(playerId);
        setCooldown(playerId);
    }
}
