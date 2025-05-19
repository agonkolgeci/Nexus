package com.agonkolgeci.nexus.core.stylizer;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.api.events.ListenerAdapter;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.event.group.GroupDataRecalculateEvent;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class StylizerManager extends PluginManager<NexusAPI> implements PluginAdapter, ListenerAdapter {

    @NotNull private final LuckPerms luckPerms;
    @NotNull private final Scoreboard scoreboard;

    @NotNull private final Map<Group, Team> teams;

    public StylizerManager(@NotNull NexusAPI instance) {
        super(instance);

        this.luckPerms = instance.getLuckPerms();
        this.scoreboard = instance.getServer().getScoreboardManager().getMainScoreboard();

        this.teams = new HashMap<>();
    }

    @Override
    public void load() throws Exception {
        this.updateTeams();

        instance.getEventsManager().registerAdapter(this);

        luckPerms.getGroupManager().loadAllGroups().join();

        luckPerms.getEventBus().subscribe(GroupDataRecalculateEvent.class, event -> this.updateTeams());
        luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> {
            @Nullable final Player player = instance.getServer().getPlayer(event.getUser().getUniqueId());
            if(player == null) return;

            this.updatePlayer(player);
        });
    }

    @Override
    public void unload() {
        this.unregisterTeams();

        instance.getEventsManager().unregisterAdapter(this);
    }

    protected void unregisterTeams() {
        teams.values().forEach(Team::unregister);
        teams.clear();
    }

    protected void updateTeams() {
        @NotNull final Collection<Group> groups = luckPerms.getGroupManager().getLoadedGroups();
        for(@NotNull Group group : groups) {
            final int maxWeight = groups.stream().map(g -> g.getWeight().orElse(0)).max(Comparator.naturalOrder()).orElse(0);
            final int position = maxWeight - group.getWeight().orElse(0);

            @NotNull final String teamName = new StringJoiner("_").add("0" + position).add(group.getName()).toString();
            @NotNull final Team team = ObjectUtils.requireNonNullElseGet(scoreboard.getTeam(teamName), () -> {
                return scoreboard.registerNewTeam(teamName);
            });

            @NotNull final CachedMetaData cachedMetaData = group.getCachedData().getMetaData();

            if(cachedMetaData.getPrefix() != null) team.prefix(LegacyComponentSerializer.legacyAmpersand().deserialize(cachedMetaData.getPrefix()));
            if(cachedMetaData.getSuffix() != null) team.suffix(LegacyComponentSerializer.legacyAmpersand().deserialize(cachedMetaData.getSuffix()));

            team.color(NamedTextColor.nearestTo(Objects.requireNonNullElse(team.prefix().color(), NamedTextColor.GRAY)));

            this.teams.put(group, team);
        }
    }

    @Nullable
    protected Team retrieveTeam(@NotNull Player player) {
        @NotNull final User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        @NotNull final Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());

        return teams.getOrDefault(inheritedGroups.stream().max(Comparator.comparingInt(group -> group.getWeight().orElse(0))).orElseThrow(() -> new IllegalStateException("Unable to retrieve to the LuckPerms' User group.")), null);
    }

    public void updatePlayer(@NotNull Player player) {
        @Nullable final Team team = retrieveTeam(player);
        if(team == null) return;

        team.addPlayer(player);

        player.displayName(Component.empty().append(team.prefix()).append(player.name().color(team.prefix().color())).append(team.suffix()));
    }

    public void unloadPlayer(@NotNull Player player) {
        @Nullable final Team team = scoreboard.getPlayerTeam(player);
        if(team == null) return;

        team.removePlayer(player);
    }

    @EventHandler
    public void onAsyncPlayerChat(@NotNull AsyncChatEvent event) {
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            return sourceDisplayName
                    .appendSpace()
                    .append(Component.text(":"))
                    .appendSpace()
                    .append(event.originalMessage())
                    .colorIfAbsent(NamedTextColor.WHITE);
        });
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        this.updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        this.unloadPlayer(event.getPlayer());
    }

}
