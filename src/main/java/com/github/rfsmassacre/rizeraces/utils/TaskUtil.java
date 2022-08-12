package com.github.rfsmassacre.rizeraces.utils;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.tasks.CooldownTask;
import com.github.rfsmassacre.rizeraces.tasks.GsonWriteTask;
import com.github.rfsmassacre.rizeraces.tasks.InventoryUpdateTask;
import com.github.rfsmassacre.rizeraces.tasks.angel.FlightBoostTask;
import com.github.rfsmassacre.rizeraces.tasks.demon.FireHealTask;
import com.github.rfsmassacre.rizeraces.tasks.demon.NetherRitesTask;
import com.github.rfsmassacre.rizeraces.tasks.demon.RageTickTask;
import com.github.rfsmassacre.rizeraces.tasks.demon.WaterDamageTask;
import com.github.rfsmassacre.rizeraces.tasks.elf.ArrowTrackTask;
import com.github.rfsmassacre.rizeraces.tasks.elf.ThresholdTask;
import com.github.rfsmassacre.rizeraces.tasks.merfolk.HydrationTask;
import com.github.rfsmassacre.rizeraces.tasks.merfolk.WaterHealTask;
import com.github.rfsmassacre.rizeraces.tasks.orc.WarEffectTask;
import com.github.rfsmassacre.rizeraces.tasks.vampire.*;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.MoonTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfEffectsTask;
import com.github.rfsmassacre.rizeraces.tasks.werewolf.WolfFormTickTask;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class TaskUtil
{
    private static final Set<Integer> TASK_IDS = new HashSet<>();

    public static void startTasks()
    {
        //Save task
        TASK_IDS.add(getTaskId(new GsonWriteTask(), 6000L, "player-data"));

        //Ability
        TASK_IDS.add(getTaskId(new CooldownTask(), 0L, "ability.cooldown"));
        TASK_IDS.add(getTaskId(new AbilityDisplayTask(), 0L, "ability.display"));

        //GUI task
        TASK_IDS.add(getTaskId(new InventoryUpdateTask(), 0L, "gui"));

        //Vampire Tasks
        TASK_IDS.add(getTaskId(new HungerHealTask(), 0L, "vampire.hunger-heal"));
        TASK_IDS.add(getTaskId(new TemperatureTask(), 0L, "vampire.radiation"));
        TASK_IDS.add(getTaskId(new UndeadTruceTask(), 0L, "vampire.truce"));
        TASK_IDS.add(getTaskId(new BatTickTask(), 0L, "vampire.bat-form"));
        TASK_IDS.add(getTaskId(new BloodLustTickTask(), 0L, "vampire.blood-lust"));
        TASK_IDS.add(getTaskId(new DarkEffectTask(), 0L, "vampire.passives"));

        //Werewolf Tasks
        TASK_IDS.add(getTaskId(new WolfEffectsTask(), 0L, "werewolf.passives"));
        TASK_IDS.add(getTaskId(new WolfFormTickTask(), 0L, "werewolf.wolf-form"));
        TASK_IDS.add(getTaskId(new MoonTask(), 0L, "werewolf.moon"));

        //Merfolk Tasks
        TASK_IDS.add(getTaskId(new HydrationTask(), 0L, "merfolk.hydration"));
        TASK_IDS.add(getTaskId(new WaterHealTask(), 0L, "merfolk.water-heal"));

        //Angel Tasks
        TASK_IDS.add(getTaskId(new FlightBoostTask(), 0L, "angel.flight"));

        //Demon Tasks
        TASK_IDS.add(getTaskId(new WaterDamageTask(), 0L, "demon.water"));
        TASK_IDS.add(getTaskId(new FireHealTask(), 0L, "demon.fire"));
        TASK_IDS.add(getTaskId(new NetherRitesTask(), 0L, "demon.nether"));
        TASK_IDS.add(getTaskId(new RageTickTask(), 0L, "demon.rage"));

        //Elf Tasks
        TASK_IDS.add(getTaskId(new ArrowTrackTask(), 0L, "elf.arrow-track"));
        TASK_IDS.add(getTaskId(new ThresholdTask(), 0L, "elf.threshold"));

        //Orc Tasks
        TASK_IDS.add(getTaskId(new WarEffectTask(), 0L, "orc.passives"));
    }

    public static void cancelTasks()
    {
        for (int taskId : TASK_IDS)
        {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        TASK_IDS.clear();
    }

    private static int getTaskId(Runnable task, long delay, String key)
    {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(RizeRaces.getInstance(), task, delay, getThread(key));
    }
    private static int getThread(String key)
    {
        return RizeRaces.getInstance().getBaseConfig().getInt("threads." + key);
    }
}
