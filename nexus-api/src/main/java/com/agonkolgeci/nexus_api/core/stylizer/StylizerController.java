package com.agonkolgeci.nexus_api.core.stylizer;

import com.agonkolgeci.nexus_api.NexusAPI;
import com.agonkolgeci.nexus_api.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_api.common.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus_api.core.players.NexusPlayer;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import com.agonkolgeci.nexus_api.utils.objects.ObjectUtils;
import com.agonkolgeci.nexus_api.utils.render.MessageUtils;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class StylizerController extends PluginModule<NexusAPI> implements PluginController, ListenerAdapter {

    @NotNull private final LuckPerms luckPerms;
    @NotNull private final Scoreboard scoreboard;

    @NotNull private Map<Group, Team> teams;

    public StylizerController(@NotNull NexusAPI instance) {
        super(instance);

        this.luckPerms = instance.getLuckPerms();
        this.scoreboard = instance.getScoreboardManager().getMainScoreboard();

        this.teams = new HashMap<>();
    }

    @Override
    public void load() {
        this.updateTeams();

        luckPerms.getGroupManager().loadAllGroups().join();

        luckPerms.getEventBus().subscribe(GroupDataRecalculateEvent.class, event -> this.updateTeams());
        luckPerms.getEventBus().subscribe(UserDataRecalculateEvent.class, event -> {
            @Nullable final Player player = server.getPlayer(event.getUser().getUniqueId());
            if(player == null) return;

            @NotNull final NexusPlayer nexusPlayer = instance.getPlayersController().retrieveUserCache(player);

            this.updatePlayer(nexusPlayer);
        });

        instance.getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        this.unregisterTeams();

        instance.getEventsController().unregisterEventAdapter(this);
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

            if(cachedMetaData.getPrefix() != null) team.setPrefix(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(cachedMetaData.getPrefix())));
            if(cachedMetaData.getSuffix() != null) team.setSuffix(LegacyComponentSerializer.legacySection().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(cachedMetaData.getSuffix())));

            this.teams.put(group, team);
        }

        instance.getServer().getOnlinePlayers().forEach(player -> this.updatePlayer(instance.getPlayersController().retrieveUserCache(player)));
    }

    @Nullable
    protected Team retrieveTeam(@NotNull NexusPlayer nexusPlayer) {
        @NotNull final User user = luckPerms.getPlayerAdapter(Player.class).getUser(nexusPlayer.getBukkitPlayer());
        @NotNull final Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());

        return teams.getOrDefault(inheritedGroups.stream().max(Comparator.comparingInt(group -> group.getWeight().orElse(0))).orElseThrow(() -> new IllegalStateException("Unable to retrieve to the LuckPerms' User group.")), null);

    }

    public void updatePlayer(@NotNull NexusPlayer nexusPlayer) {
        @Nullable final Team team = retrieveTeam(nexusPlayer);
        if(team == null) return;

        team.addEntry(nexusPlayer.getUsername());

        nexusPlayer.getBukkitPlayer().setDisplayName(team.getPrefix() + nexusPlayer.getUsername() + team.getSuffix());
    }

    public void unloadPlayer(@NotNull NexusPlayer nexusPlayer) {
        @Nullable final Team team = scoreboard.getEntryTeam(nexusPlayer.getUsername());
        if(team == null) return;

        team.removeEntry(nexusPlayer.getUsername());
    }

    @EventHandler
    public void onAsyncPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        @NotNull final Player player = event.getPlayer();

        @NotNull final Component format = Component.text()
                .append(Component.text(player.getDisplayName()).colorIfAbsent(NamedTextColor.GRAY))
                .appendSpace()
                .append(Component.text(":"))
                .appendSpace()
                .append(MessageUtils.MM_SERIALIZER.deserialize(event.getMessage()))
                .colorIfAbsent(NamedTextColor.WHITE)
                .build();

        event.setFormat(LegacyComponentSerializer.legacySection().serialize(format));
    }

    @EventHandler
    public void onPlayerReady(@NotNull PlayerReadyEvent event) {
        this.updatePlayer(instance.getPlayersController().retrieveUserCache(event.getPlayer()));
    }

}
