package com.github.rfsmassacre.rizeraces.tasks.elf;

import com.github.rfsmassacre.rizeraces.listeners.races.ElfListener;
import org.bukkit.entity.Arrow;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ArrowTrackTask implements Runnable
{
    private static final Set<Arrow> ARROWS = new HashSet<>();

    public static Set<Arrow> getArrows()
    {
        return ARROWS;
    }

    @Override
    public void run()
    {
        Iterator<Arrow> iterator = ARROWS.iterator();
        while (iterator.hasNext())
        {
            Arrow arrow = iterator.next();
            if (arrow.isInWater())
            {
                arrow.setGravity(true);
                iterator.remove();
            }
        }
    }
}
