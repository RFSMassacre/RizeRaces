package com.github.rfsmassacre.rizeraces.players;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.abilities.Ability;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import lombok.Getter;
import lombok.Setter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
        DEMON
    }

    @Getter
    private final UUID playerId;
    private String displayName;
    @Getter
    @Setter
    private Race race;

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

    /*
     * Levels Per Race
     */
    @Getter
    private final Map<Race, Long> experience;

    public Origin(Player player, Race race)
    {
        this.playerId = player.getUniqueId();
        this.displayName = player.getDisplayName();
        this.race = race;

        this.abilities = new HashMap<>();

        this.experience = new HashMap<>();

        reset();
    }
    public Origin(Player player)
    {
        this(player, Race.HUMAN);
    }

    public void reset()
    {
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

    //Experience
    public void setExperience(Race race, long experience)
    {
        this.experience.put(race, experience);
    }
    public long getExperience(Race race)
    {
        return experience.get(race);
    }
    public void clearExperience(Race race)
    {
        this.experience.remove(race);
    }
    public void addExperience(Race race, long experience)
    {
        setExperience(race, getExperience(race) + experience);
    }

    public int getLevel(Race race)
    {
        long experience = getExperience(race);
        Configuration config = RizeRaces.getInstance().getBaseConfig();
        String equation = config.getString("equation.level");
        Expression expression = new ExpressionBuilder(equation)
                .variables("x")
                .build()
                .setVariable("x", experience);
        return (int)expression.evaluate();
    }
}
