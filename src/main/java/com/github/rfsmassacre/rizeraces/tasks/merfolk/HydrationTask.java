package com.github.rfsmassacre.rizeraces.tasks.merfolk;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.OriginGson;
import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.rizeraces.players.Origin.Race;
import com.github.rfsmassacre.rizeraces.utils.BossBarUtil;
import com.github.rfsmassacre.rizeraces.utils.ConfigUtil;
import com.github.rfsmassacre.rizeraces.utils.PotionUtil;
import com.github.rfsmassacre.rizeraces.utils.SunUtil;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class HydrationTask implements Runnable
{
    private final Configuration config;
    private final OriginGson gson;

    private final Set<PotionEffect> landEffects;
    private final Set<PotionEffect> rainEffects;
    private final Set<PotionEffect> waterEffects;
    private final double up;
    private final double rain;
    private final double down;

    private final Map<Material, Double> blocks;

    private static final HashMap<UUID, BossBar> BOSS_BARS = new HashMap<>();

    public HydrationTask()
    {
        this.config = RizeRaces.getInstance().getBaseConfig();
        this.gson = RizeRaces.getInstance().getOriginGson();
        this.landEffects = ConfigUtil.getPotionEffects(config, "merfolk.land-effects");
        this.rainEffects = ConfigUtil.getPotionEffects(config, "merfolk.rain-effects");
        this.waterEffects = ConfigUtil.getPotionEffects(config, "merfolk.water-effects");
        this.up = config.getDouble("merfolk.hydration.up");
        this.rain = config.getDouble("merfolk.hydration.rain");
        this.down = config.getDouble("merfolk.hydration.down");
        this.blocks = ConfigUtil.getMaterialDouble(config, "ceiling-blocks");
    }


    @Override
    public void run()
    {
        for (Origin origin : gson.getOrigins())
        {
            Player player = origin.getPlayer();
            if (player == null)
            {
                continue;
            }

            if (!origin.getRace().equals(Race.MERFOLK))
            {
                continue;
            }

            UUID playerId = player.getUniqueId();
            if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            {
                origin.setHydration(1.0);
                removeBossBar(playerId);
                continue;
            }

            if (player.isInWater())
            {
                origin.addHydration(up);
                for (PotionEffect effect : waterEffects)
                {
                    ItemStack item = player.getInventory().getBoots();
                    boolean depthStrider = false;
                    if (item != null)
                    {
                        ItemMeta meta = item.getItemMeta();
                        if (meta != null)
                        {
                            depthStrider = meta.getEnchants().containsKey(Enchantment.DEPTH_STRIDER);
                        }
                    }


                    if (!(effect.getType().equals(PotionEffectType.DOLPHINS_GRACE) && depthStrider))
                    {
                        PotionUtil.applyPotion(player, effect);
                    }
                }
            }
            else if (player.getWorld().hasStorm() && !warmBiomes().contains(player.getLocation().getBlock().getBiome()))
            {
                Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
                double terrain = SunUtil.getTerrainOpacity(block, blocks);
                if (terrain == 1.0)
                {
                    origin.addHydration(-down);
                    if (origin.getHydration() == 0.0)
                    {
                        for (PotionEffect effect : landEffects)
                        {
                            PotionUtil.applyPotion(player, effect);
                        }
                    }
                }
                else
                {
                    origin.addHydration(rain);
                    for (PotionEffect effect : rainEffects)
                    {
                        PotionUtil.applyPotion(player, effect);
                    }
                }
            }
            else
            {
                origin.addHydration(-down);
                if (origin.getHydration() == 0.0)
                {
                    for (PotionEffect effect : landEffects)
                    {
                        PotionUtil.applyPotion(player, effect);
                    }
                }
            }

            if (origin.getHydration() < 1.0)
            {
                BossBar bossBar = BOSS_BARS.get(playerId);
                if (bossBar == null)
                {
                    bossBar = BossBarUtil.createBossBar("merfolk");
                    if (bossBar == null)
                    {
                        continue;
                    }

                    bossBar.setProgress(origin.getHydration());
                    bossBar.addPlayer(player);
                    bossBar.setVisible(true);
                    BOSS_BARS.put(playerId, bossBar);
                }

                bossBar.setProgress(origin.getHydration());
            }
            else
            {
                removeBossBar(playerId);
            }
        }
    }

    public static void removeBossBar(UUID playerId)
    {
        BossBar bossBar = BOSS_BARS.get(playerId);
        if (bossBar == null)
        {
            return;
        }

        bossBar.removeAll();
        bossBar.setVisible(false);
        BOSS_BARS.remove(playerId);
    }

    private static Set<Biome> warmBiomes()
    {
        Set<Biome> biomes = new HashSet<>();
        biomes.add(Biome.DESERT);
        biomes.add(Biome.SAVANNA);
        biomes.add(Biome.SAVANNA_PLATEAU);
        biomes.add(Biome.WINDSWEPT_SAVANNA);
        biomes.add(Biome.BADLANDS);
        biomes.add(Biome.WOODED_BADLANDS);
        biomes.add(Biome.ERODED_BADLANDS);
        return biomes;
    }
}
