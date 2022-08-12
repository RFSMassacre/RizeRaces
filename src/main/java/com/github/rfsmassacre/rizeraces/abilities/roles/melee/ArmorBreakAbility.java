package com.github.rfsmassacre.rizeraces.abilities.roles.melee;

import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.*;

public class ArmorBreakAbility extends TargetAbility
{
    private final double breakPercent;

    public ArmorBreakAbility(String internalName)
    {
        super(internalName, Role.MELEE);

        this.breakPercent = getConfigDouble("break-percent");
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

            EntityEquipment equipment = target.getEquipment();
            if (equipment == null)
            {
                return AbilityResult.NO_TARGET;
            }

            List<ItemStack> armorSet = Arrays.asList(equipment.getArmorContents());
            Collections.shuffle(armorSet);
            ItemStack armor = null;
            for (ItemStack brokenArmor : armorSet)
            {
                if (brokenArmor.getItemMeta() instanceof Damageable)
                {
                    armor = brokenArmor;
                }
            }

            if (armor == null)
            {
                return AbilityResult.NO_TARGET;
            }

            Damageable brokenArmor = (Damageable)armor.getItemMeta();
            int damage = brokenArmor.getDamage();
            int finalDamage = (int)(damage * breakPercent);
            brokenArmor.setDamage(finalDamage);
            armor.setItemMeta(brokenArmor);

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
