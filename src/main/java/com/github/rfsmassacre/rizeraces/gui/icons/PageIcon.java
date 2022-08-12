package com.github.rfsmassacre.rizeraces.gui.icons;

import com.github.rfsmassacre.rizeraces.gui.Icon;
import com.github.rfsmassacre.rizeraces.gui.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PageIcon extends Icon
{
    protected Menu menu;

    public PageIcon(int x, int y, String displayName, Menu menu)
    {
        super(x, y, 1, false, Material.PAPER, displayName, new ArrayList<>());

        this.menu = menu;
    }
    public PageIcon(int x, int y, Material material, String displayName, Menu menu)
    {
        super(x, y, 1, false, material, displayName, new ArrayList<>());

        this.menu = menu;
    }

    @Override
    public void onClick(Player clicker)
    {
        Menu.addMenu(clicker.getUniqueId(), menu);
        clicker.openInventory(menu.createInventory(clicker));
        clicker.updateInventory();
    }
}
