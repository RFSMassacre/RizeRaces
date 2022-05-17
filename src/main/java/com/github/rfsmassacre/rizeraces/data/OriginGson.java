package com.github.rfsmassacre.rizeraces.data;

import com.github.rfsmassacre.rizeraces.players.Origin;
import com.github.rfsmassacre.spigot.files.GsonManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class OriginGson extends GsonManager<Origin>
{
    private final HashMap<UUID, Origin> cache;

    public OriginGson(JavaPlugin plugin)
    {
        super(plugin, "players", Origin.class);

        this.cache = new HashMap<>();
    }

    /**
     * Writes Origin file asynchronously.
     * @param fileName Name of file.
     * @param origin Origin.
     */
    public void writeAsync(String fileName, Origin origin)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(fileName, origin));
    }

    public void writeAllAsync()
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {
           for (Origin origin : getOrigins())
           {
               write(origin.getPlayerId().toString(), origin);
           }
        });
    }

    /**
     * Reads the Origin file asynchronously into a callback.
     * @param fileName Name of file.
     * @param callback Callback.
     */
    public void readAsync(String fileName, Consumer<Origin> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(read(fileName)));
    }

    /**
     * Reads all Origin files asynchronously into a callback.
     * @param callback Callback.
     */
    public void readAllAsync(Consumer<Set<Origin>> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(all()));
    }

    /**
     * Searches for Origin file with the same usernames asynchronously into a callback.
     * @param username Player username.
     * @param callback Callback.
     */
    public void readNameAsync(String username, Consumer<Origin> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {
            Origin origin = null;
            for (Origin offline : all())
            {
               OfflinePlayer player = Bukkit.getOfflinePlayer(offline.getPlayerId());
               if (player.hasPlayedBefore() && username.equals(player.getName()))
               {
                   origin = offline;
                   break;
               }
            }
            callback.accept(origin);
        });
    }

    public Origin getOrigin(UUID player)
    {
        return cache.get(player);
    }
    public Collection<Origin> getOrigins()
    {
        return cache.values();
    }
    public void addOrigin(Origin origin)
    {
        this.cache.put(origin.getPlayerId(), origin);
    }
    public void removeOrigin(UUID playerId)
    {
        this.cache.remove(playerId);
    }
}
