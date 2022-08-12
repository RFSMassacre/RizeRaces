package com.github.rfsmassacre.rizeraces.data;

import com.github.rfsmassacre.rizeraces.parties.Party;
import com.github.rfsmassacre.spigot.files.GsonManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

public class PartyGson extends GsonManager<Party>
{
    private final HashMap<UUID, Party> cache;

    public PartyGson(JavaPlugin plugin)
    {
        super(plugin, "parties", Party.class);

        this.cache = new HashMap<>();
        for (Party party : all())
        {
            this.cache.put(party.getPartyId(), party);
        }
    }

    /**
     * Writes Party file asynchronously.
     * @param fileName Name of file.
     * @param party Party.
     */
    public void writeAsync(String fileName, Party party)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> write(fileName, party));
    }

    public void writeAllAsync()
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {
            for (Party party : getParties())
            {
                write(party.getPartyId().toString(), party);
            }
        });
    }

    /**
     * Reads the Party file asynchronously into a callback.
     * @param fileName Name of party.
     * @param callback Callback.
     */
    public void readAsync(String fileName, Consumer<Party> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(read(fileName)));
    }

    /**
     * Reads all Party files asynchronously into a callback.
     * @param callback Callback.
     */
    public void readAllAsync(Consumer<Set<Party>> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.accept(all()));
    }

    /**
     * Searches for Party file with the same usernames asynchronously into a callback.
     * @param leaderName Party leader name.
     * @param callback Callback.
     */
    public void readLeaderAsync(String leaderName, Consumer<Party> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {
            Party party = null;
            for (Party offline : all())
            {
                if (leaderName.equals(offline.getLeader().getName()))
                {
                    party = offline;
                    break;
                }
            }
            callback.accept(party);
        });
    }

    public void deleteAsync(String fileName)
    {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> delete(fileName));
    }

    public Party getParty(UUID partyId)
    {
        return cache.get(partyId);
    }
    public Party getPlayerParty(UUID playerId)
    {
        for (Party party : getParties())
        {
            if (party.contains(playerId))
            {
                return party;
            }
        }

        return null;
    }

    public Collection<Party> getParties()
    {
        return cache.values();
    }
    public void addParty(Party party)
    {
        this.cache.put(party.getPartyId(), party);
    }

    public void removeParty(UUID partyId)
    {
        this.cache.remove(partyId);
    }
    public void removePlayerParty(UUID playerId)
    {
        Iterator<Party> iterator = getParties().iterator();
        while (iterator.hasNext())
        {
            Party party = iterator.next();
            if (party.contains(playerId))
            {
                iterator.remove();
                return;
            }
        }
    }
}
