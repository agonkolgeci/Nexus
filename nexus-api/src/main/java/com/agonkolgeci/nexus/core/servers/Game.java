package com.agonkolgeci.nexus.core.servers;

import com.agonkolgeci.nexus.utils.world.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public enum Game {

    STRANGER_HIDE("strangerhide", Component.text("Stranger Hide", NamedTextColor.RED), null, "Cache-cache & Jeu de rôle", "1.18+", "", new ItemBuilder(Material.LEATHER_CHESTPLATE)),
    RANDOMIZED_RUMBLE("rr", Component.text("Randomized Rumble", NamedTextColor.GREEN), Component.text("[NOUVEAU]", NamedTextColor.YELLOW), "Bataille aléatoire solo", "1.20 +", "", new ItemBuilder(Material.BEDROCK)),
    PITCHOUT("pitchout", Component.text("Pitch Out", NamedTextColor.RED), null, "Bataille d'expulsion solo", "1.8 +", "Le but du jeu est d'expulser les joueurs en dehors de la plateforme de jeu pour leur faire perdre une vie, le but étant de conserver pour remporter la partie. ", new ItemBuilder(Material.SNOWBALL)),
    THIMBLE("deacoudre", Component.text("Dé à coudre", NamedTextColor.LIGHT_PURPLE), null, "Plonge dans la piscine", "1.8 +", "Le but du jeu est qu'au fur et à mesure de la game à remplir une piscine avec un bloc en sautant en hauteur. Vous pouvez gagner des vies bonus grâce à un Dé à coudre en faisant un saut en plein centre entre 4 blocs. Le dernier survivant gagne la partie.", new ItemBuilder(Material.SNOWBALL, 1, (short) 6)),
    THETOWERS("thetowers", Component.text("The Towers", NamedTextColor.GOLD), null, "Bataille en équipe", "1.8 +", "Le but du jeu oppose deux équipes, rouges et les bleus, dans un combat sur des plateformes qui constituent les bases respectives des équipes, ainsi que le middle. Le but principal est d'aller dans la base adverse pour sauter dans une \"piscine\" et ainsi marquer 1 point. La première équipe à 10 points remporte la partie. ", new ItemBuilder(Material.NETHER_BRICK_FENCE)),
    BUILDTIONNARY("buildtionnary", Component.text("Buildtionnary", NamedTextColor.YELLOW), null, "Devine la construction", "1.20 +", "Le but du jeu est de construire le mot qui vous est donné et de le faire deviner aux autres joueurs, les plus rapides à trouver le mot gagneront le + de point, celui qui aura le + de point à la fin de la partie sera le grand gagnant de la partie !", new ItemBuilder(Material.BOOKSHELF)),
    GETDOWN("getdown", Component.text("GetDown", NamedTextColor.AQUA), null, "Sauts et PvP", "1.8 +", "", new ItemBuilder(Material.GOLD_BLOCK)),
    FIGHTCLUB("fightclub", Component.text("FightClub", NamedTextColor.DARK_RED), null, "PvP", "1.8 +", "Le but du jeu oppose deux adversaires, lorsqu'un meurt il fait tomber plusieurs équipements qui permette au gagnant d'améliorer son équipement grâce aux différents blocs dans l'arène. Le but étant d'être le dernier survivant pour remporter la partie.", new ItemBuilder(Material.DIAMOND_SWORD))
    ;

    @NotNull private final String id;
    @NotNull private final Component displayName;
    @Nullable private final Component flag;
    @NotNull private final String type;
    @NotNull private final String version;
    @NotNull private final String description;
    @NotNull private final ItemBuilder itemBuilder;

    Game(@NotNull String id, @NotNull Component displayName, @Nullable Component flag, @NotNull String type, @NotNull String version, @NotNull String description, @NotNull ItemBuilder itemBuilder) {
        this.id = id;
        this.displayName = displayName;
        this.flag = flag;
        this.type = type;
        this.version = version;
        this.description = description;
        this.itemBuilder = itemBuilder;
    }

}
