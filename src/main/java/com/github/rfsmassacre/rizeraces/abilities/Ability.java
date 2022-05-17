package com.github.rfsmassacre.rizeraces.abilities;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.angel.BackpedalAbility;
import com.github.rfsmassacre.rizeraces.abilities.angel.BlessingAbility;
import com.github.rfsmassacre.rizeraces.abilities.angel.HealingBowAbility;
import com.github.rfsmassacre.rizeraces.abilities.demon.CurseAbility;
import com.github.rfsmassacre.rizeraces.abilities.demon.FlameBurstAbility;
import com.github.rfsmassacre.rizeraces.abilities.demon.RageAbility;
import com.github.rfsmassacre.rizeraces.abilities.merfolk.BlindSongAbility;
import com.github.rfsmassacre.rizeraces.abilities.merfolk.HealSongAbility;
import com.github.rfsmassacre.rizeraces.abilities.merfolk.WaterBlastAbility;
import com.github.rfsmassacre.rizeraces.abilities.vampire.BatFormAbility;
import com.github.rfsmassacre.rizeraces.abilities.vampire.BloodLustAbility;
import com.github.rfsmassacre.rizeraces.abilities.werewolf.BiteAbility;
import com.github.rfsmassacre.rizeraces.abilities.werewolf.PounceAbility;
import com.github.rfsmassacre.rizeraces.abilities.werewolf.WolfFormAbility;
import com.github.rfsmassacre.rizeraces.abilities.vampire.FangAbility;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.events.AbilityCastEvent;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Ability
{
    public enum AbilityType
    {
        INSTANT,
        BUFF,
        TARGET,
    }

    public enum AbilityResult
    {
        ON_COOLDOWN,
        NO_REAGENT,
        NO_TARGET,
        SUCCESS,
        FAILED
    }

    private static final HashMap<String, Ability> CACHE = new HashMap<>();

    protected final Configuration config;
    protected final Locale locale;
    protected final OriginGson gson;

    @Getter
    @Setter
    protected String internalName;
    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    protected String displayName;
    @Getter
    protected AbilityType type;
    @Getter
    protected Race race;
    @Getter
    protected int level;

    @Getter
    protected int cooldown;
    @Getter
    protected final Map<UUID, Integer> cooldowns;

    @Getter
    protected double reagent;

    public Ability(String internalName, AbilityType type, Race race)
    {
        this.config = RizeRaces.getInstance().getAbilityConfig();
        this.locale = RizeRaces.getInstance().getLocale();
        this.gson = RizeRaces.getInstance().getOriginGson();

        this.internalName = internalName;
        this.name = getConfigString("name");
        this.displayName = Locale.format(getConfigString("display-name"));
        this.type = type;
        this.race = race;
        this.level = getConfigInt("level");

        this.cooldown = getConfigInt("cooldown");
        this.cooldowns = new HashMap<>();

        this.reagent = 0.0;
    }

    public abstract AbilityResult cast(Player caster);

    //Returns the ongoing cooldown time rather than the initial cooldown time.
    public int getCooldown(UUID playerId)
    {
        if (cooldowns.containsKey(playerId))
        {
            return cooldowns.get(playerId);
        }

        return 0;
    }
    public void setCooldown(UUID playerId)
    {
        this.cooldowns.put(playerId, cooldown);
    }
    public void resetCooldown(UUID playerId)
    {
        this.cooldowns.remove(playerId);
    }
    public boolean onCooldown(UUID playerId)
    {
        return getCooldown(playerId) > 0;
    }

    protected boolean callEvent(Player caster, Race race)
    {
        AbilityCastEvent event = new AbilityCastEvent(caster, race, this);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    public static Ability getAbility(String internalName)
    {
        return CACHE.get(internalName);
    }
    public static Collection<Ability> getAbilities()
    {
        return CACHE.values();
    }
    public static void addAbility(Ability ability)
    {
        CACHE.put(ability.getInternalName(), ability);
    }
    public static void removeAbility(Ability ability)
    {
        CACHE.remove(ability.getInternalName());
    }

    public static void loadAbilities()
    {
        CACHE.clear();

        addAbility(new BatFormAbility());
        addAbility(new BloodLustAbility());
        addAbility(new FangAbility());

        addAbility(new WolfFormAbility());
        addAbility(new PounceAbility());
        addAbility(new BiteAbility());

        addAbility(new HealSongAbility());
        addAbility(new BlindSongAbility());
        addAbility(new WaterBlastAbility());

        addAbility(new HealingBowAbility());
        addAbility(new BackpedalAbility());
        addAbility(new BlessingAbility());

        addAbility(new RageAbility());
        addAbility(new CurseAbility());
        addAbility(new FlameBurstAbility());
    }

    /*
     * Easier config retrieval so there are no need to type "internalName" endlessly.
     */
    public String getConfigString(String key)
    {
        return config.getString(internalName + "." + key);
    }
    public List<String> getConfigStringList(String key)
    {
        return config.getStringList(internalName + "." + key);
    }
    public int getConfigInt(String key)
    {
        return config.getInt(internalName + "." + key);
    }
    public double getConfigDouble(String key)
    {
        return config.getDouble(internalName + "." + key);
    }
    public long getConfigLong(String key)
    {
        return config.getLong(internalName + "." + key);
    }
    public boolean getConfigBoolean(String key)
    {
        return config.getBoolean(internalName + "." + key);
    }
}
