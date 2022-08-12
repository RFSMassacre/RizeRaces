package com.github.rfsmassacre.rizeraces.listeners;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.Ability.AbilityResult;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.abilities.TargetAbility;
import com.github.rfsmassacre.rizeraces.abilities.race.vampire.FangAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.data.PartyGson;
import com.github.rfsmassacre.rizeraces.events.AbilityCastEvent;
import com.github.rfsmassacre.rizeraces.events.AbilityTargetEvent;
import com.github.rfsmassacre.rizeraces.parties.Party;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import com.github.rfsmassacre.rizeraces.tasks.vampire.AbilityDisplayTask;
import com.github.rfsmassacre.rizeraces.utils.CombatUtil;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityListener implements Listener
{
    private final Locale locale;
    private final OriginGson originGson;
    private final PartyGson partyGson;

    private final Map<UUID, Long> trigger;
    private final long cooldown;

    public AbilityListener()
    {
        this.locale = RizeRaces.getInstance().getLocale();
        this.originGson = RizeRaces.getInstance().getOriginGson();
        this.partyGson = RizeRaces.getInstance().getPartyGson();
        this.trigger = new HashMap<>();
        this.cooldown = 750L;
    }

    /*
     * Prevent players from casting abilities from other races.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCastCancel(AbilityCastEvent event)
    {
        Player caster = event.getCaster();
        Origin origin = originGson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        //Cancel if the ability being cast is from the wrong race.
        if (!origin.getRace().equals(event.getRace()))
        {
            event.setCancelled(true);
            return;
        }

        //Cancel if the ability being cast is from the wrong role.
        Role role = origin.getRole();
        if (role != null && !origin.getRole().equals(event.getRole()))
        {
            event.setCancelled(true);
            return;
        }

        //Cancel if the caster isn't leveled enough
        if (origin.getLevel() < event.getAbility().getLevel())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Properly process beneficial abilities to party members.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPartyAbilityTarget(AbilityTargetEvent event)
    {
        Player caster = event.getCaster();
        UUID casterId = caster.getUniqueId();
        LivingEntity target = CombatUtil.getSource(event.getTarget());
        if (target == null)
        {
            target = event.getTarget();
        }

        TargetAbility ability = (TargetAbility)event.getAbility();
        Party targetParty = null;
        if (target != null)
        {
            targetParty = partyGson.getPlayerParty(target.getUniqueId());
        }

        //If the ability is beneficial only target those in the party.
        if (ability.isBeneficial())
        {
            if (targetParty == null)
            {
                event.setCancelled(true);
            }
            else if (!targetParty.contains(casterId))
            {
                event.setCancelled(true);
            }
        }
        //If the ability is harmful, don't target those in the party unless friendly fire is on.
        else
        {
            if (targetParty != null && targetParty.contains(casterId) && !targetParty.isFriendlyFire())
            {
                event.setCancelled(true);
            }
        }
    }

    /*
     * Prevent party members from hurting each other unless friendly fire is on.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPartyAttack(EntityDamageByEntityEvent event)
    {
        Player attacker = CombatUtil.getSource(event.getDamager());
        Player defender = CombatUtil.getSource(event.getEntity());
        if (attacker == null || defender == null)
        {
            return;
        }

        if (attacker.equals(defender))
        {
            event.setCancelled(true);
            return;
        }

        Party party = partyGson.getPlayerParty(attacker.getUniqueId());
        if (party != null && party.contains(defender.getUniqueId()) && !party.isFriendlyFire())
        {
            event.setCancelled(true);
        }
    }

    /*
     * Ability Activations
     */
    @EventHandler
    public void onAbilityCast(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode.equals(GameMode.CREATIVE) || gameMode.equals(GameMode.SPECTATOR))
        {
            return;
        }

        Origin origin = originGson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.isAbilityMode())
        {
            return;
        }

        int slot = player.getInventory().getHeldItemSlot();
        Ability ability = origin.getAbility(slot);
        if (ability == null)
        {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (!trigger.containsKey(playerId))
        {
            //Activate
            handleResult(origin, ability);
            trigger.put(playerId, System.currentTimeMillis());
        }
        else
        {
            long time = trigger.get(playerId);
            if (time > 0 && System.currentTimeMillis() - time > cooldown)
            {
                //Activate
                handleResult(origin, ability);
                trigger.remove(playerId);
            }
        }

        event.setCancelled(true);
    }

    private void handleResult(Origin origin, Ability ability)
    {
        //Activate
        Player player = origin.getPlayer();
        if (player == null)
        {
            return;
        }

        String raceName = origin.getRace().toString().toLowerCase();
        AbilityResult result = ability.cast(player);
        switch (result)
        {
            case ON_COOLDOWN:
            {
                locale.sendLocale(player, true, raceName + ".ability.on-cooldown", "{cooldown}",
                        Locale.formatTime((double) ability.getCooldown(player.getUniqueId()) / 20));
                break;
            }
            case NO_REAGENT:
            {
                String reagent = ability.formatReagent();
                locale.sendLocale(player, true, raceName + ".ability.cant-cast", "{reagent}",
                        reagent != null ? reagent : "0");
                break;
            }
            case NO_TARGET:
            {
                locale.sendLocale(player, true, raceName + ".ability.no-target");
                break;
            }
            default:
            {
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHotBarScroll(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        Origin origin = originGson.getOrigin(player.getUniqueId());
        if (origin == null)
        {
            return;
        }

        int slot = event.getNewSlot();
        AbilityDisplayTask.display(origin, slot);
    }

    /*
     * Deactivate buffs
     */
    @EventHandler
    public void onPlayerRespawn(PlayerPostRespawnEvent event)
    {
        Player player = event.getPlayer();
        BuffAbility.deactivateAll(player);
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            return;
        }

        player.setHealth(healthAttribute.getValue());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), () -> BuffAbility.deactivateAll(event.getPlayer()),
                1L);
    }

    /*
     * Extra thing
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerResurrect(EntityResurrectEvent event)
    {
        LivingEntity entity = event.getEntity();
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 4);
        entity.addPotionEffect(resistance);
    }

    /*
     * Prevent vampires from leeching demons and angels, since they don't get poisoned!
     */
    @EventHandler(ignoreCancelled = true)
    public void onVampFang(AbilityTargetEvent event)
    {
        if (!(event.getAbility() instanceof FangAbility))
        {
            return;
        }

        Player caster = event.getCaster();
        Origin origin = originGson.getOrigin(caster.getUniqueId());
        if (origin == null)
        {
            return;
        }

        if (!origin.getRace().equals(Race.VAMPIRE))
        {
            return;
        }

        if (!(event.getTarget() instanceof Player player))
        {
            return;
        }

        Origin target = originGson.getOrigin(player.getUniqueId());
        if (target == null)
        {
            return;
        }

        if (target.getRace().equals(Race.ANGEL) || target.getRace().equals(Race.DEMON))
        {
            event.setCancelled(true);
        }
    }
}
