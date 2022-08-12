package com.github.rfsmassacre.rizeraces;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.rizeraces.abilities.BuffAbility;
import com.github.rfsmassacre.rizeraces.commands.*;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.data.PartyGson;
import com.github.rfsmassacre.rizeraces.listeners.*;
import com.github.rfsmassacre.rizeraces.listeners.races.*;
import com.github.rfsmassacre.rizeraces.managers.SkinManager;
import com.github.rfsmassacre.rizeraces.moons.Moon;
import com.github.rfsmassacre.rizeraces.parties.Party;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.tasks.vampire.TemperatureTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.MoonTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfFormTickTask;
import com.github.rfsmassacre.rizeraces.utils.TaskUtil;
import com.github.rfsmassacre.spigot.files.TextManager;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

public final class RizeRaces extends JavaPlugin
{
    @Getter
    private static RizeRaces instance;

    @Getter
    private Configuration baseConfig;
    @Getter
    private Configuration abilityConfig;
    @Getter
    private Configuration itemConfig;
    @Getter
    private Locale locale;
    @Getter
    private OriginGson originGson;
    @Getter
    private PartyGson partyGson;
    @Getter
    private TextManager textManager;

    @Getter
    private SkinManager skinManager;

    @Override
    public void onEnable()
    {
        //Set instance
        instance = this;

        //Make data folder if running for the first time
        File file = getDataFolder();
        if (!file.exists())
        {
            file.mkdir();
        }

        //Prepare configurations
        this.baseConfig = new Configuration(this, "", "config.yml");
        this.abilityConfig = new Configuration(this, "", "abilities.yml");
        this.itemConfig = new Configuration(this, "", "items.yml");
        this.locale = new Locale(this, "", "locale.yml");
        this.originGson = new OriginGson(this);
        this.partyGson = new PartyGson(this);
        this.textManager = new TextManager(this, "text");

        //Managers
        this.skinManager = new SkinManager();

        //Enable listeners
        PluginManager plugins = Bukkit.getPluginManager();
        plugins.registerEvents(new LoginListener(), this);
        plugins.registerEvents(new DietListener(), this);
        plugins.registerEvents(new AbilityListener(), this);
        plugins.registerEvents(new VampireListener(), this);
        plugins.registerEvents(new WerewolfListener(), this);
        plugins.registerEvents(new MerfolkListener(), this);
        plugins.registerEvents(new AngelListener(), this);
        plugins.registerEvents(new DemonListener(), this);
        plugins.registerEvents(new ElfListener(), this);
        plugins.registerEvents(new OrcListener(), this);
        plugins.registerEvents(new RaceListener(), this);
        plugins.registerEvents(new ScrollListener(), this);

        //Initialize
        TaskUtil.startTasks();
        Ability.loadAbilities();
        Moon.loadMoons();

        //Commands
        getCommand("rizeraces").setExecutor(new RizeRacesCommand());
        getCommand("ability").setExecutor(new AbilityCommand());
        getCommand("race").setExecutor(new RaceCommand());
        getCommand("bloodbottle").setExecutor(new BloodBottleCommand());
        getCommand("party").setExecutor(new PartyCommand());
    }

    @Override
    public void onDisable()
    {
        //Remove buffs on everyone.
        for (Player player : Bukkit.getOnlinePlayers())
        {
            BuffAbility.deactivateAll(player);
        }

        for (Origin origin : originGson.getOrigins())
        {
            originGson.write(origin.getPlayerId().toString(), origin);
        }

        for (Party party : partyGson.getParties())
        {
            partyGson.write(party.getPartyId().toString(), party);
        }
    }

    public void onReload()
    {
        this.baseConfig.reload();
        this.abilityConfig.reload();
        this.itemConfig.reload();
        this.locale.reload();
        this.textManager.clearCacheFiles();

        for (Player player : Bukkit.getOnlinePlayers())
        {
            UUID playerId = player.getUniqueId();
            TemperatureTask.removeBossBar(playerId);
            WolfFormTickTask.removeBossBar(playerId);
            MoonTask.removeBossBar(playerId);
        }

        TaskUtil.cancelTasks();
        TaskUtil.startTasks();

        Ability.loadAbilities();
        Moon.loadMoons();
    }
}
