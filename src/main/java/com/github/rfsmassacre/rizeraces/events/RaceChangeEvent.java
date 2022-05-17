package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RaceChangeEvent extends Event implements Cancellable
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
    private final Origin origin;
    @Getter
    @Setter
    private Race race;

    public RaceChangeEvent(Origin origin, Race race)
    {
        this.origin = origin;
        this.race = race;
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
