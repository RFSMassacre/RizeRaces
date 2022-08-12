package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.players.Origin.Role;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AbilityCastEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList()
    {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return HANDLERS;
    }

    private boolean cancel;
    @Getter
    private final Player caster;
    @Getter
    private final Race race;
    @Getter
    private final Role role;
    @Getter
    private final Ability ability;

    public AbilityCastEvent(Player caster, Race race, Ability ability)
    {
        this.caster = caster;
        this.race = race;
        this.role = null;
        this.ability = ability;
        this.cancel = false;
    }
    public AbilityCastEvent(Player caster, Role role, Ability ability)
    {
        this.caster = caster;
        this.race = null;
        this.role = role;
        this.ability = ability;
        this.cancel = false;
    }

    @Override
    public boolean isCancelled()
    {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }
}
