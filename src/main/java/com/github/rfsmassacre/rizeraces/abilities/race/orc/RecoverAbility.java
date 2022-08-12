package com.github.rfsmassacre.rizeraces.abilities.race.orc;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.FXUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecoverAbility extends BuffAbility
{
    @Getter
    private final Map<UUID, Double> recovering;
    @Getter
    private final double heal;
    @Getter
    private final double percent;

    public RecoverAbility()
    {
        super("recover", Race.ORC);

        this.recovering = new HashMap<>();
        this.heal = getConfigDouble("heal");
        this.percent = getConfigDouble("percent");
    }

    @Override
    public boolean hasReagent(Player caster)
    {
        return true;
    }

    @Override
    public boolean isActive(Player caster)
    {
        return recovering.containsKey(caster.getUniqueId());
    }

    @Override
    public void activate(Player caster)
    {
        UUID playerId = caster.getUniqueId();
        this.recovering.put(playerId, 0.0);
        double health = caster.getHealth();
        AttributeInstance healthAttribute = caster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getValue();
        if (health < maxHealth)
        {
            double finalHeal = health + heal;
            if (finalHeal > maxHealth)
            {
                finalHeal = maxHealth;
            }

            caster.setHealth(finalHeal);
        }

        Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), () -> deactivate(caster), duration);
        FXUtil.playSound(caster.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 2.0F);
        setCooldown(playerId);
    }

    @Override
    public void deactivate(Player caster)
    {
        if (!recovering.containsKey(caster.getUniqueId()))
        {
            return;
        }

        double damage = recovering.get(caster.getUniqueId());
        double health = caster.getHealth();
        AttributeInstance healthAttribute = caster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        double maxHealth = healthAttribute.getValue();
        if (health < maxHealth)
        {
            double finalHeal = health + (damage * percent);
            if (finalHeal > maxHealth)
            {
                finalHeal = maxHealth;
            }

            caster.setHealth(finalHeal);
        }

        FXUtil.playSound(caster.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 2.0F);
        this.recovering.remove(caster.getUniqueId());
    }

    @Override
    public String formatReagent()
    {
        return null;
    }
}
