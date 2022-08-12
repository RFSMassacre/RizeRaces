package com.github.rfsmassacre.rizeraces.managers;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinVariant;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.exception.SkinRequestException;
import net.skinsrestorer.api.property.IProperty;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class SkinManager
{
    private static boolean LOADED = false;
    public static boolean isLoaded()
    {
        return LOADED;
    }

    private final SkinsRestorerAPI api;

    private final Configuration config;
    private final Locale locale;

    private final HashMap<String, IProperty> skins;

    public SkinManager()
    {
        this.api = SkinsRestorerAPI.getApi();

        this.config = RizeRaces.getInstance().getBaseConfig();
        this.locale = RizeRaces.getInstance().getLocale();

        this.skins = new HashMap<>();

        if (api != null)
        {
            generateSkins(true);
        }
    }

    public void applySkin(Player player, String skinName)
    {
        Bukkit.getScheduler().runTaskAsynchronously(RizeRaces.getInstance(), () ->
        {
            try
            {
                IProperty skin = skins.get("default");
                if (skinName != null && skins.containsKey(skinName))
                {
                    skin = skins.get(skinName);
                }

                api.applySkin(new PlayerWrapper(player), skin);
            }
            catch (Exception exception)
            {
                locale.sendLocale(Bukkit.getConsoleSender(), true, "skin.cannot-apply",
                        "{player}", player.getDisplayName());

                exception.printStackTrace();
            }
        });
    }

    public void removeSkin(Player player)
    {
        Bukkit.getScheduler().runTaskAsynchronously(RizeRaces.getInstance(), () ->
        {
            try
            {
                IProperty emptySkin = api.createProperty("textures", "", "");
                api.applySkin(new PlayerWrapper(player), emptySkin);
            }
            catch (Exception exception)
            {
                locale.sendLocale(Bukkit.getConsoleSender(), true, "skin.cannot-clear",
                        "{player}", player.getDisplayName());

                exception.printStackTrace();
            }
        });
    }

    private String getSkinType(String type)
    {
        String skinType = config.getString("skin." + type + ".skin-type");
        return skinType == null ? "SLIM" : skinType;
    }

    private String getSkinURL(String type)
    {
        return config.getString("skin." + type + ".url");
    }

    private void generateSkins()
    {
        ConfigurationSection section = config.getSection("skin");
        if (section == null)
        {
            return;
        }

        locale.sendLocale(Bukkit.getConsoleSender(), true, "skin.loading");

        skins.clear();
        for (String skinName : section.getKeys(false))
        {
            String url = getSkinURL(skinName);
            String skinType = getSkinType(skinName);
            try
            {
                IProperty skin = api.genSkinUrl(url, SkinVariant.valueOf(skinType));
                skins.put(skinName, skin);
            }
            catch (SkinRequestException exception)
            {
                locale.sendLocale(Bukkit.getConsoleSender(), true, "skin.cannot-load", "{skin}",
                        skinName);

                exception.printStackTrace();
            }
        }

        locale.sendLocale(Bukkit.getConsoleSender(), true, "skin.loaded");
        LOADED = true;
    }
    public void generateSkins(boolean async)
    {
        if (async)
        {
            Bukkit.getScheduler().runTaskAsynchronously(RizeRaces.getInstance(), (Runnable)this::generateSkins);
        }
        else
        {
            generateSkins();
        }
    }

    public static String getRandomSkin()
    {
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        ConfigurationSection section = config.getSection("skin");
        if (section == null)
        {
            return null;
        }

        List<String> skins = new ArrayList<>(section.getKeys(false));
        Random random = new Random(System.currentTimeMillis());
        return skins.get(random.nextInt(skins.size() - 1));
    }
}
