package com.github.rfsmassacre.rizeraces.abilities.vampire;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import java.util.UUID;

public class BatFormAbility extends BuffAbility
{
    @Getter
    private final double maxHealth;
    @Getter
    private final double flightSpeed;
    @Getter
    private final double walkSpeed;

    public BatFormAbility()
    {
        super("bat-form", Race.VAMPIRE);

        this.reagent = getConfigInt("minimum-food-level");
        this.maxHealth = getConfigDouble("max-health");
        this.flightSpeed = getConfigDouble("fly-speed");
        this.walkSpeed = getConfigDouble("walk-speed");
    }

    @Override
    public boolean isActive(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        return origin != null && origin.getRace().equals(Race.VAMPIRE) && origin.isBatForm();
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
            return AbilityResult.ON_COOLDOWN;
        }

        if (caster.getFoodLevel() < reagent)
        {
            return AbilityResult.NO_REAGENT;
        }

        toggle(caster);
        return AbilityResult.SUCCESS;
    }

    @Override
    public void activate(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        AttributeInstance healthAttribute = caster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        BuffAbility buffAbility = (BuffAbility)Ability.getAbility("blood-lust");
        if (buffAbility.isActive(caster))
        {
            buffAbility.deactivate(caster);
        }

        MobDisguise disguise = new MobDisguise(DisguiseType.BAT);
        caster.setAllowFlight(true);
        caster.setFlying(true);
        caster.setFlySpeed((float)flightSpeed);
        caster.setWalkSpeed((float)walkSpeed);
        healthAttribute.setBaseValue(maxHealth);
        DisguiseAPI.disguiseToAll(caster, disguise);
        origin.setBatForm(true);
    }

    @Override
    public void deactivate(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        Origin origin = gson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        AttributeInstance healthAttribute = caster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getDefaultValue();
        caster.setAllowFlight(false);
        caster.setFlying(false);
        caster.setFlySpeed(0.2f);
        caster.setWalkSpeed(0.2f);
        healthAttribute.setBaseValue(maxHealth);
        DisguiseAPI.undisguiseToAll(caster);
        origin.setBatForm(false);
        caster.setAllowFlight(false);
        caster.setFlying(false);
        setCooldown(playerId);
    }
}
