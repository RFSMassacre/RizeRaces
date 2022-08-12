package com.github.rfsmassacre.rizeraces.gui.icons;

import com.github.rfsmassacre.rizeraces.gui.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DividerIcon extends Icon
{
    public DividerIcon(int x, int y)
    {
        super(x, y, 1, false, Material.BLACK_STAINED_GLASS_PANE, "&6&lHeaven&e&lRaces",
                new ArrayList<>());
    }
    public DividerIcon(int x, int y, Material material)
    {
        super(x, y, 1, false, material, "&6&lHeaven&e&lRaces", new ArrayList<>());
    }

    @Override
    public void onClick(Player clicker)
    {
        //Do nothing
    }
}
