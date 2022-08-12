package com.github.rfsmassacre.rizeraces.abilities.race.vampire;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
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
    private final double returnHealth;
    @Getter
    private final double flightSpeed;
    @Getter
    private final double walkSpeed;

    public BatFormAbility()
    {
        super("bat-form", Race.VAMPIRE);

        this.reagent = getConfigInt("minimum-food-level");
        this.maxHealth = getConfigDouble("max-health");
        this.returnHealth = getConfigDouble("return-health");
        this.flightSpeed = getConfigDouble("fly-speed");
        this.walkSpeed = getConfigDouble("walk-speed");
    }

    @Override
    public boolean hasReagent(Player caster)
    {
        return caster.getFoodLevel() >= reagent;
    }

    @Override
    public boolean isActive(Player caster)
    {
        Origin origin = gson.getOrigin(caster.getUniqueId());
        return origin != null && origin.getRace().equals(Race.VAMPIRE) && origin.isBatForm();
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

        caster.setAllowFlight(false);
        caster.setFlying(false);
        caster.setFlySpeed(0.2f);
        caster.setWalkSpeed(0.2f);
        healthAttribute.setBaseValue(returnHealth);
        DisguiseAPI.undisguiseToAll(caster);
        origin.setBatForm(false);
        caster.setAllowFlight(false);
        caster.setFlying(false);
        setCooldown(playerId);
    }

    @Override
    public String formatReagent()
    {
        return Integer.toString((int)reagent);
    }
}
