package com.github.rfsmassacre.rizeraces.commands;

import com.github.rfsmassacre.rizeraces.RizeRaces;
import com.github.rfsmassacre.rizeraces.data.PartyGson;
import com.github.rfsmassacre.rizeraces.parties.Party;
import com.github.rfsmassacre.rizeraces.parties.Party.Member;
import com.github.rfsmassacre.spigot.commands.SpigotCommand;
import com.github.rfsmassacre.spigot.files.TextManager;
import com.github.rfsmassacre.spigot.files.configs.Configuration;
import com.github.rfsmassacre.spigot.files.configs.Locale;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyCommand extends SpigotCommand
{
    private static final String PERM_PREFIX = "rizeraces.party.";

    private final Configuration config;
    private final TextManager text;
    private final PartyGson gson;

    private Sound errorSound;
    private Sound successSound;
    private final float volume;
    private final float pitch;

    public PartyCommand()
    {
        super(RizeRaces.getInstance().getLocale(), "party");

        this.config = RizeRaces.getInstance().getBaseConfig();
        this.text = RizeRaces.getInstance().getTextManager();
        this.gson = RizeRaces.getInstance().getPartyGson();
        this.volume = 1.0F;
        this.pitch = 0.75F;
        this.text.cacheTextFile("party.txt");
        this.text.cacheTextFile("party-help.txt");
        this.text.cacheTextFile("party-help2.txt");

        String errorName = config.getString("command-sounds.error");
        String successName = config.getString("command-sounds.success");
        try
        {
            this.errorSound = Sound.valueOf(errorName);
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            //Do nothing
        }

        try
        {
            this.successSound = Sound.valueOf(successName);
        }
        catch (NullPointerException | IllegalArgumentException exception)
        {
            //Do nothing
        }

        addSubCommand(new InfoCommand());
        addSubCommand(new CreateCommand());
        addSubCommand(new LeaveCommand());
        addSubCommand(new InviteCommand());
        addSubCommand(new KickCommand());
        addSubCommand(new JoinCommand());
        addSubCommand(new PromoteCommand());
        addSubCommand(new FriendlyFireCommand());
        addSubCommand(new HelpCommand());
    }

    @Override
    protected void onFail(CommandSender sender)
    {
        locale.sendLocale(sender, true, "error.no-perm");
        playSound(sender, errorSound);
    }

    @Override
    protected void onInvalidArgs(CommandSender sender)
    {
        locale.sendLocale(sender, true, "error.invalid-args");
        playSound(sender, errorSound);
    }

    private void playSound(CommandSender sender, Sound sound)
    {
        if (!(sender instanceof Player player))
        {
            return;
        }

        if (sound == null)
        {
            return;
        }

        player.playSound(player, sound, volume, pitch);
    }

    @SuppressWarnings("deprecation")
    private class InfoCommand extends SubCommand
    {
        public InfoCommand()
        {
            super("info", PERM_PREFIX + "info");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            Party party = gson.getPlayerParty(player.getUniqueId());
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                return;
            }

            //Format the leader's name
            String leaderName = party.getLeader().getDisplayName();

            //Format the ally names
            String memberList;
            ArrayList<String> memberNames = new ArrayList<>();
            for (Member member : party.getMembers().values())
            {
                memberNames.add(member.getDisplayName());
            }
            memberList = String.join("&f, &a", memberNames);

            //Format friendly fire status
            String friendlyFire = "&aOFF";
            if (party.isFriendlyFire())
            {
                friendlyFire = "&4ON";
            }

            //Load menus and print it
            List<String> lines = text.loadTextFile("party.txt");
            String menu = String.join("\n", lines);
            menu = menu.replace("{leader}", leaderName);
            menu = menu.replace("{members}", memberList);
            menu = menu.replace("{friendlyfire}", friendlyFire);
            locale.sendMessage(player, menu);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }

    private class CreateCommand extends SubCommand
    {
        public CreateCommand()
        {
            super("create", PERM_PREFIX + "create");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            if (gson.getPlayerParty(player.getUniqueId()) != null)
            {
                locale.sendLocale(player, true, "party.already-in-party");
                playSound(player, errorSound);
                return;
            }

            Party party = new Party(player);
            gson.addParty(party);
            gson.writeAsync(party.getPartyId().toString(), party);
            locale.sendLocale(player, true, "party.created");
            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("deprecation")
    private class LeaveCommand extends SubCommand
    {
        public LeaveCommand()
        {
            super("leave", PERM_PREFIX + "leave");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            UUID playerId = player.getUniqueId();
            Party party = gson.getPlayerParty(playerId);
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                playSound(player, errorSound);
                return;
            }

            UUID leaderId = party.getLeaderId();
            if (leaderId.equals(playerId))
            {
                //Disband party.
                locale.sendLocale(player, true, "party.disbanded");
                for (Member member : party.getMembers().values())
                {
                    Player memberPlayer = member.getPlayer();
                    if (memberPlayer != null)
                    {
                        locale.sendLocale(member.getPlayer(), true, "party.disbanded");
                    }
                }

                gson.removeParty(party.getPartyId());
                gson.deleteAsync(party.getPartyId().toString());
                playSound(player, successSound);
                return;
            }

            if (party.contains(playerId))
            {
                locale.sendLocale(player, true, "party.left.self");
                party.removeMember(playerId);

                Member leader = party.getLeader();
                Player leaderPlayer = leader.getPlayer();
                if (leaderPlayer != null)
                {
                    locale.sendLocale(leaderPlayer, true, "party.left.party", "{player}",
                            player.getDisplayName());
                }

                for (Member member : party.getMembers().values())
                {
                    Player memberPlayer = member.getPlayer();
                    if (memberPlayer != null)
                    {
                        locale.sendLocale(memberPlayer, true, "party.left.party", "{player}",
                                player.getDisplayName());
                    }
                }
                playSound(player, successSound);
                return;
            }

            playSound(player, errorSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("deprecation")
    private class InviteCommand extends SubCommand
    {
        public InviteCommand()
        {
            super("invite", PERM_PREFIX + "invite");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //party invite <player>
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            UUID playerId = player.getUniqueId();
            Party party = gson.getPlayerParty(playerId);
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                playSound(player, errorSound);
                return;
            }

            if (!party.getLeaderId().equals(playerId))
            {
                locale.sendLocale(player, true, "party.no-perm");
                playSound(player, errorSound);
                return;
            }

            int maxSize = config.getInt("party.max-size");
            if (party.getSize() >= maxSize)
            {
                locale.sendLocale(player, true, "party.full.self");
                return;
            }

            if (args.length < 2)
            {
                locale.sendLocale(player, true, "party.no-player");
                playSound(player, errorSound);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null)
            {
                locale.sendLocale(player, true, "error.invalid-player");
                playSound(player, errorSound);
                return;
            }

            if (player.equals(target))
            {
                locale.sendLocale(player, true, "party.invited.target-self");
                playSound(player, errorSound);
                return;
            }

            UUID targetId = target.getUniqueId();
            if (party.contains(targetId))
            {
                locale.sendLocale(player, true, "party.already-member");
                playSound(player, errorSound);
                return;
            }

            if (party.isInvited(targetId))
            {
                locale.sendLocale(player, true, "party.already-invited", "{player}",
                        target.getDisplayName());
                playSound(player, errorSound);
                return;
            }

            party.addInvite(targetId);
            locale.sendLocale(player, true, "party.invited.self", "{player}",
                    target.getDisplayName());
            playSound(player, successSound);

            String prefix = locale.getMessage("prefix");
            String message = locale.getMessage("party.invited.target");
            message = message.replace("{leader display}", player.getDisplayName());
            message = message.replace("{leader name}", player.getName());
            String command = "/party join " + player.getName();
            BaseComponent[] hover = TextComponent.fromLegacyText(Locale.format("&eClick here to join the party!"));
            TextComponent text = new TextComponent(TextComponent.fromLegacyText(Locale.format(prefix + message)));
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
            target.spigot().sendMessage(text);

            int timeOut = config.getInt("party.invite-timeout");
            Bukkit.getScheduler().runTaskLater(RizeRaces.getInstance(), () ->
            {
                if(party.isInvited(targetId))
                {
                    party.removeInvite(targetId);
                    locale.sendLocale(target, true, "party.invite-expired", "{leader}",
                            player.getDisplayName());
                }
            }, timeOut);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player leader))
            {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                UUID leaderId = leader.getUniqueId();
                Party party = gson.getPlayerParty(leaderId);
                if (party == null)
                {
                    return Collections.emptyList();
                }

                if (!party.getLeaderId().equals(leaderId))
                {
                    return Collections.emptyList();
                }

                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (!player.equals(sender))
                    {
                        suggestions.add(player.getName());
                    }
                }
            }
            return suggestions;
        }
    }

    private class KickCommand extends SubCommand
    {
        public KickCommand()
        {
            super("kick", PERM_PREFIX + "kick");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //party kick <player>
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            UUID playerId = player.getUniqueId();
            Party party = gson.getPlayerParty(playerId);
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                playSound(player, errorSound);
                return;
            }

            if (!party.getLeaderId().equals(playerId))
            {
                locale.sendLocale(player, true, "party.no-perm");
                playSound(player, errorSound);
                return;
            }

            if (args.length < 2)
            {
                locale.sendLocale(player, true, "error.no-player");
                playSound(player, errorSound);
                return;
            }

            Member member = party.getMember(args[1]);
            if (member == null)
            {
                locale.sendLocale(player, true, "party.member-not-found");
                playSound(player, errorSound);
                return;
            }

            if (player.equals(member.getPlayer()))
            {
                locale.sendLocale(player, true, "party.kick.target-self");
                playSound(player, errorSound);
                return;
            }

            party.removeMember(member.getPlayerId());
            locale.sendLocale(player, true, "party.kicked.self", "{player}",
                    member.getDisplayName());

            Player target = member.getPlayer();
            if (target != null)
            {
                locale.sendLocale(target, true, "party.kicked.target");
            }

            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player leader))
            {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                UUID leaderId = leader.getUniqueId();
                Party party = gson.getPlayerParty(leaderId);
                if (party == null)
                {
                    return Collections.emptyList();
                }

                if (!party.getLeaderId().equals(leaderId))
                {
                    return Collections.emptyList();
                }

                for (Member member : party.getMembers().values())
                {
                    suggestions.add(member.getName());
                }
            }
            return suggestions;
        }
    }

    @SuppressWarnings("deprecation")
    private class JoinCommand extends SubCommand
    {
        public JoinCommand()
        {
            super("join", PERM_PREFIX + "join");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //party join <player>
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            if (gson.getPlayerParty(player.getUniqueId()) != null)
            {
                locale.sendLocale(player, true, "party.already-in-party");
                playSound(player, errorSound);
                return;
            }

            Party party = null;
            Member leader = null;
            for (Party found : gson.getParties())
            {
                Member member = found.getLeader();
                if (member.getName().equals(args[1]))
                {
                    party = found;
                    leader = member;
                    break;
                }
            }

            if (party == null)
            {
                locale.sendLocale(player, true, "party.party-not-found");
                playSound(player, errorSound);
                return;
            }

            int maxSize = config.getInt("party.max-size");
            if (party.getSize() >= maxSize)
            {
                locale.sendLocale(player, true, "party.full.target");
                playSound(player, errorSound);
                return;
            }

            UUID playerId = player.getUniqueId();
            if (!party.isInvited(playerId))
            {
                locale.sendLocale(player, true, "party.not-invited");
                playSound(player, errorSound);
                return;
            }

            locale.sendLocale(player, true, "party.joined.target", "{leader}",
                    leader.getDisplayName());
            locale.sendLocale(leader.getPlayer(), true, "party.joined.self", "{player}",
                    player.getDisplayName());

            for (Member member : party.getMembers().values())
            {
                Player memberPlayer = member.getPlayer();
                if (memberPlayer != null)
                {
                    locale.sendLocale(memberPlayer, true, "party.join.self", "{player}",
                            player.getDisplayName());
                }
            }

            party.removeInvite(playerId);
            party.addMember(player);
            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                for (Party party : gson.getParties())
                {
                    if (party.isInvited(player.getUniqueId()))
                    {
                        suggestions.add(party.getLeader().getName());
                    }
                }
            }
            return suggestions;
        }
    }

    private class PromoteCommand extends SubCommand
    {
        public PromoteCommand()
        {
            super("promote", PERM_PREFIX + "promote");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            //party promote <player>
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            UUID playerId = player.getUniqueId();
            Party party = gson.getPlayerParty(playerId);
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                playSound(player, errorSound);
                return;
            }

            if (!party.getLeaderId().equals(playerId))
            {
                locale.sendLocale(player, true, "party.no-perm");
                playSound(player, errorSound);
                return;
            }

            if (args.length < 2)
            {
                locale.sendLocale(player, true, "error.no-player");
                playSound(player, errorSound);
                return;
            }

            Member member = party.getMember(args[1]);
            if (member == null)
            {
                locale.sendLocale(player, true, "party.member-not-found", "{name}", args[1]);
                playSound(player, errorSound);
                return;
            }

            if (player.equals(member.getPlayer()))
            {
                locale.sendLocale(player, true, "party.kick.target-self");
                playSound(player, errorSound);
                return;
            }

            party.setLeader(member);
            party.removeMember(member.getPlayerId());
            party.addMember(player);

            Player newLeader = member.getPlayer();
            if (newLeader != null)
            {
                locale.sendLocale(newLeader, true, "party.promoted.leader");
            }

            for (Member otherMember : party.getMembers().values())
            {
                Player otherPlayer = otherMember.getPlayer();
                if (otherPlayer != null)
                {
                    locale.sendLocale(otherPlayer, true, "party.promoted.member", "{leader}",
                            member.getDisplayName());
                }
            }

            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                return Collections.emptyList();
            }

            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                UUID playerId = player.getUniqueId();
                Party party = gson.getParty(playerId);
                if (party == null)
                {
                    return Collections.emptyList();
                }

                if (!party.getLeaderId().equals(playerId))
                {
                    return Collections.emptyList();
                }

                for (Member member : party.getMembers().values())
                {
                    suggestions.add(member.getName());
                }
            }
            return suggestions;
        }
    }

    private class FriendlyFireCommand extends SubCommand
    {
        public FriendlyFireCommand()
        {
            super("friendlyfire", PERM_PREFIX + "friendlyfire");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                locale.sendLocale(sender, true, "error.console");
                return;
            }

            UUID playerId = player.getUniqueId();
            Party party = gson.getPlayerParty(playerId);
            if (party == null)
            {
                locale.sendLocale(player, true, "party.no-party");
                playSound(player, errorSound);
                return;
            }

            if (!party.getLeaderId().equals(playerId))
            {
                locale.sendLocale(player, true, "party.no-perm");
                playSound(player, errorSound);
                return;
            }

            if (party.isFriendlyFire())
            {
                party.setFriendlyFire(false);
                locale.sendLocale(player, true, "party.friendly-fire.disabled");
            }
            else
            {
                party.setFriendlyFire(true);
                locale.sendLocale(player, true, "party.friendly-fire.enabled");
            }

            playSound(player, successSound);
        }

        @Override
        protected List<String> onTabComplete(CommandSender commandSender, String[] strings)
        {
            return Collections.emptyList();
        }
    }

    private class HelpCommand extends SubCommand
    {
        public HelpCommand()
        {
            super("help", "rizeraces.party.help");
        }

        @Override
        protected void onRun(CommandSender sender, String[] args)
        {
            try
            {
                int page = Integer.parseInt(args[1]);
                String menu;
                if (page <= 1)
                {
                    menu = String.join("\n", text.loadTextFile("party-help.txt"));
                }
                else
                {
                    menu = String.join("\n", text.loadTextFile("party-help2.txt"));
                }

                locale.sendMessage(sender, menu);
                playSound(sender, successSound);
            }
            catch (NumberFormatException | ArrayIndexOutOfBoundsException exception)
            {
                locale.sendMessage(sender, String.join("\n", text.loadTextFile("party-help.txt")));
                playSound(sender, errorSound);
            }
        }

        @Override
        protected List<String> onTabComplete(CommandSender sender, String[] args)
        {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 2)
            {
                suggestions.add("1");
                suggestions.add("2");
            }
            return suggestions;
        }
    }
}
