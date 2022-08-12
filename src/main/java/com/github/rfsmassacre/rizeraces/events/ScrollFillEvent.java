package com.github.rfsmassacre.rizeraces.events;

import com.github.rfsmassacre.rizeraces.items.scrolls.ScrollItem;
import com.github.rfsmassacre.rizeraces.players.Origin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ScrollFillEvent extends Event implements Cancellable
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
    private final ScrollItem scroll;
    @Getter
    @Setter
    private int experience;

    public ScrollFillEvent(Origin origin, ScrollItem scroll, int experience)
    {
        this.origin = origin;
        this.scroll = scroll;
        this.experience = experience;
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
