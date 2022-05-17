package com.github.rfsmassacre.rizeraces.utils;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.UUID;

public class BossBarUtil
{
    public static class PlayerBar
    {
        @Getter
        @Setter
        private Race race;
        @Getter
        @Setter
        private BossBar bossBar;

        public PlayerBar(Race race, BossBar bossBar)
        {
            this.race = race;
            this.bossBar = bossBar;
        }
    }

    public static BossBar createBossBar(String section)
    {
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        String title = Locale.format(config.getString("boss-bar." + section + ".title"));
        String color = config.getString("boss-bar." + section + ".color");
        String style = config.getString("boss-bar." + section + ".style");

        try
        {
            BarColor barColor = BarColor.valueOf(color);
            BarStyle barStyle = BarStyle.valueOf(style);
            return Bukkit.createBossBar(title, barColor, barStyle);
        }
        catch (IllegalArgumentException exception)
        {
            return null;
        }
    }
}
