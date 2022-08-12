package com.github.rfsmassacre.rizeraces.parties;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Party
{
    public static class Member
    {
        @Getter
        private final UUID playerId;
        @Setter
        private String name;
        @Setter
        private String displayName;

        public Member(UUID playerId, String name, String displayName)
        {
            this.playerId = playerId;
            this.name = name;
            this.displayName = displayName;
        }
        public Member(Player player)
        {
            this(player.getUniqueId(), player.getName(), player.getDisplayName());
        }

        public Player getPlayer()
        {
            return Bukkit.getPlayer(playerId);
        }

        public String getName()
        {
            Player player = getPlayer();
            if (player != null)
            {
                this.name = player.getName();
            }

            return name;
        }

        public String getDisplayName()
        {
            Player player = getPlayer();
            if (player != null)
            {
                this.displayName = player.getDisplayName();
            }

            return displayName;
        }
    }

    @Getter
    private final UUID partyId;
    @Getter
    @Setter
    private Member leader;
    @Getter
    @Setter
    private Map<UUID, Member> members;
    @Getter
    @Setter
    private Set<UUID> invitedIds;
    @Getter
    @Setter
    private boolean friendlyFire;

    public Party()
    {
        this.partyId = UUID.randomUUID();
        this.members = new HashMap<>();
        this.invitedIds = new HashSet<>();
        this.friendlyFire = false;
    }
    public Party(Player player)
    {
        this();

        this.leader = new Member(player);
    }

    public UUID getLeaderId()
    {
        return leader.getPlayerId();
    }

    public void addMember(Player player)
    {
        UUID playerId = player.getUniqueId();
        this.members.put(playerId, new Member(player));
    }
    public Member getMember(UUID playerId)
    {
        return members.get(playerId);
    }
    public Member getMember(String name)
    {
        for (Member member : members.values())
        {
            if (member.getName().equals(name))
            {
                return member;
            }
        }

        return null;
    }
    public void removeMember(UUID playerId)
    {
        this.members.remove(playerId);
    }

    public boolean isInvited(UUID playerId)
    {
        return invitedIds.contains(playerId);
    }
    public void addInvite(UUID playerId)
    {
        this.invitedIds.add(playerId);
    }
    public void removeInvite(UUID playerId)
    {
        this.invitedIds.remove(playerId);
    }

    public boolean contains(UUID playerId)
    {
        if (leader.getPlayerId().equals(playerId))
        {
            return true;
        }

        return members.containsKey(playerId);
    }

    public int getSize()
    {
        return members.size() + 1;
    }
}
