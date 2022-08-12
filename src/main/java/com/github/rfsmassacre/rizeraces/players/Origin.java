package com.github.rfsmassacre.rizeraces.players;

import com.github.rfsmassacre.rizeraces.abilities.Ability;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class Origin
{
    public enum Race
    {
        HUMAN,
        VAMPIRE,
        WEREWOLF,
        MERFOLK,
        ANGEL,
        DEMON,
        ELF,
        ORC
    }

    public enum Role
    {
        MELEE,
        RANGED,
        TANK,
        SUPPORT
    }

    @Getter
    private final UUID playerId;
    //Setters/Getters defined.
    private String displayName;
    @Getter
    @Setter
    private Race race;
    @Getter
    @Setter
    private Role role;

    /*
     * Vampire
     */
    @Getter
    @Setter
    private boolean batForm;
    @Getter
    @Setter
    private boolean bloodLust;
    @Getter
    @Setter
    private int truceTicks;
    @Getter
    @Setter
    private double temperature;

    /*
     * Werewolf
     */
    @Getter
    @Setter
    private boolean wolfForm;
    @Getter
    @Setter
    private int transformTime;
    @Getter
    @Setter
    private String skin;

    /*
     * Merfolk
     */
    @Getter
    @Setter
    private double hydration;

    /*
     * Angel
     */
    @Getter
    @Setter
    private boolean arrowHealing;

    /*
     * Demon
     */
    @Getter
    @Setter
    private boolean enraged;
    @Getter
    @Setter
    private int rageTime;

    /*
     * Abilities
     */
    @Getter
    @Setter
    private boolean abilityMode;
    @Getter
    private final Map<Integer, String> abilities;

    private final Map<Race, Integer> levels;

    public Origin(Player player, Race race)
    {
        this.playerId = player.getUniqueId();
        this.displayName = player.getDisplayName();
        this.race = race;
        this.abilityMode = true;

        this.abilities = new HashMap<>();

        //this.experiences = new HashMap<>();
        this.levels = new HashMap<>();

        reset();
    }
    public Origin(Player player, Race race, Role role)
    {
        this(player, race);

        this.role = role;
    }
    public Origin(Player player)
    {
        this(player, Race.HUMAN);
    }

    public void reset()
    {
        this.role = null;

        this.batForm = false;
        this.bloodLust = false;
        this.truceTicks = 0;
        this.temperature = 0.0;

        this.wolfForm = false;
        this.transformTime = 0;

        this.hydration = 1.0;

        this.arrowHealing = false;

        this.enraged = false;
        this.rageTime = 0;

        this.abilities.clear();
    }

    //Get player from Bukkit
    public Player getPlayer()
    {
        return Bukkit.getPlayer(playerId);
    }

    //Display Name
    public String getDisplayName()
    {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null)
        {
            this.displayName = player.getDisplayName();
        }

        return displayName;
    }

    //Vampire
    public void addTemperature(double temperature)
    {
        this.temperature += temperature;
        if (this.temperature < 0.0)
        {
            this.temperature = 0.0;
        }
        else if (this.temperature > 1.0)
        {
            this.temperature = 1.0;
        }
    }
    public void addTruceTicks(int truceTicks)
    {
        this.truceTicks += truceTicks;
        if (this.truceTicks < 0)
        {
            this.truceTicks = 0;
        }
    }

    //Werewolf
    public void tickTransformTime(int ticks)
    {
        this.transformTime -= ticks;
        if (this.transformTime < 0)
        {
            this.transformTime = 0;
        }
    }

    //Merfolk
    public void addHydration(double hydration)
    {
        this.hydration += hydration;
        if (this.hydration < 0.0)
        {
            this.hydration = 0.0;
        }
        else if (this.hydration > 1.0)
        {
            this.hydration = 1.0;
        }
    }

    //Demon
    public void tickRageTime(int ticks)
    {
        this.rageTime -= ticks;
        if (this.rageTime < 0)
        {
            this.rageTime = 0;
        }
    }

    //Abilities
    public void bindAbility(int slot, Ability ability)
    {
        this.abilities.put(slot, ability.getInternalName());
    }
    public Ability getAbility(int slot)
    {
        return Ability.getAbility(abilities.get(slot));
    }
    public void unbindAbility(int slot)
    {
        this.abilities.remove(slot);
    }

    //Levels
    public void setLevel(Race race, int level)
    {
        this.levels.put(race, level);
    }
    public void setLevel(int level)
    {
        setLevel(race, level);
    }
    public int getLevel(Race race)
    {
        if (levels.get(race) == null)
        {
            return 0;
        }

        return levels.get(race);
    }
    public int getLevel()
    {
        return getLevel(getRace());
    }

    public void addLevel(Race race, int level)
    {
        setLevel(race, getLevel(race) + level);
    }
    public void addLevel(int level)
    {
        addLevel(race, level);
    }
}
