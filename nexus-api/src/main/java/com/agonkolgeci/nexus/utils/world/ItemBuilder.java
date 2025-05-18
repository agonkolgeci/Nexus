package com.agonkolgeci.nexus.utils.world;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Lists;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemBuilder {

    public static final Component ANY_CLICK = Component.text("Clic", NamedTextColor.GREEN);
    public static final Component LEFT_CLICK = Component.text("Clic-gauche", NamedTextColor.YELLOW);
    public static final Component RIGHT_CLICK = Component.text("Clic-droit", NamedTextColor.YELLOW);

    @Getter @Nullable private Component flag;
    @Getter @NotNull private final List<Component> tags;
    @Getter @NotNull private final Map<Component, Component> properties;
    @Getter @NotNull private final Map<Component, Component> actions;

    @NotNull private ItemStack itemStack;

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;

        this.tags = new ArrayList<>();
        this.properties = new HashMap<>();
        this.actions = new HashMap<>();
    }

    public ItemBuilder(@NotNull Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(@NotNull Material material, int amount, short data) {
        this(new ItemStack(material, amount, data));
    }

    public @NotNull ItemStack build() {
        @NotNull final ItemStack itemStack = new ItemStack(this.itemStack);

        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return itemStack;

        @NotNull final List<Component> lore = new ArrayList<>();

        if(flag != null) itemMeta.displayName(displayName().appendSpace().append(flag));

        if(!tags.isEmpty()) {
            lore.addAll(Lists.reverse(tags));
        }

        if(itemMeta.hasLore()) {
            lore.addAll(Objects.requireNonNull(itemMeta.lore()));
        }

        if(!properties.isEmpty()) {
            lore.add(Component.empty());
            lore.addAll(properties.entrySet().stream().map(e -> Component.empty().append(Component.text("•", NamedTextColor.DARK_GRAY)).appendSpace().append(e.getKey().colorIfAbsent(NamedTextColor.GRAY)).append(Component.text(":", NamedTextColor.GRAY)).appendSpace().append(e.getValue().colorIfAbsent(NamedTextColor.WHITE))).toList());
        }

        if(!actions.isEmpty()) {
            lore.add(Component.empty());
            lore.addAll(actions.entrySet().stream().map(e -> Component.empty().append(e.getKey().colorIfAbsent(NamedTextColor.WHITE)).appendSpace().append(Component.text("•", NamedTextColor.DARK_GRAY)).appendSpace().append(e.getValue().colorIfAbsent(NamedTextColor.GRAY))).toList());
        }

        itemMeta.lore(lore);
        itemStack.setItemMeta(itemMeta);

        return new ItemStack(itemStack);
    }

    public @NotNull ItemBuilder flag(@NotNull Component flag) {
        this.flag = flag;
        return this;
    }

    public @NotNull ItemBuilder addTag(@NotNull Component tag) {
        this.tags.add(tag);
        return this;
    }

    public @NotNull ItemBuilder addProperty(@NotNull Component name, @NotNull Component value) {
        this.properties.put(name, value);

        return this;
    }

    public @NotNull ItemBuilder addAction(@NotNull Component icon, @NotNull Component action) {
        this.actions.put(icon, action);
        return this;
    }

    public @NotNull ItemBuilder addAction(@NotNull String icon, @NotNull Component action) {
        this.actions.put(Component.text(icon, NamedTextColor.WHITE), action);
        return this;
    }

    public @NotNull ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public @NotNull ItemBuilder type(@NotNull Material category) {
        itemStack = itemStack.withType(category);
        return this;
    }

    public @NotNull ItemBuilder material(@NotNull Material material) {
        return type(material);
    }

    public @NotNull ItemBuilder meta(@NotNull ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
        return this;
    }

    public @NotNull ItemBuilder displayName(@NotNull Component displayName) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.displayName(displayName.decoration(TextDecoration.ITALIC, false));

        return this.meta(itemMeta);
    }

    public @NotNull Component displayName() {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) throw new IllegalStateException("Item meta is null !");

        return Objects.requireNonNullElse(itemMeta.displayName(), itemMeta.itemName());
    }

    public @NotNull ItemBuilder lore(@NotNull List<Component> lines) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.lore(lines.stream().map(line -> line.colorIfAbsent(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)).toList());

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder lore(@NotNull Component... lines) {
        return lore(Arrays.asList(lines));
    }

    public @NotNull List<Component> getLore() {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return new ArrayList<>();

        return Objects.requireNonNullElse(itemMeta.lore(), new ArrayList<>());
    }

    public @NotNull ItemBuilder addLore(@NotNull List<Component> lines) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        @NotNull final List<Component> lore = this.getLore();

        lore.addAll(lines);
        itemMeta.lore(lore);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder addLore(@NotNull Component... lines) {
        return addLore(Arrays.asList(lines));
    }

    public @NotNull ItemBuilder damage(final int damage) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        if(!(itemMeta instanceof @NotNull Damageable damageableMeta)) return this;

        damageableMeta.setDamage(damage);

        return this.meta(damageableMeta);
    }

    public @NotNull ItemBuilder enchantments(@NotNull HashMap<Enchantment, Integer> enchantmentLevelsMap, boolean ignoreLevelRestriction) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        enchantmentLevelsMap.forEach(((enchantment, level) -> itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction)));

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder addEnchantment(@NotNull Enchantment enchantment, final int level, boolean ignoreLevelRestriction) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder removeEnchantment(@NotNull Enchantment enchantment) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.removeEnchant(enchantment);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder addItemFlags(@NotNull ItemFlag... itemFlags) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.addItemFlags(itemFlags);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder addItemFlags(@NotNull List<ItemFlag> itemFlags) {
        return addItemFlags(itemFlags.toArray(new ItemFlag[0]));
    }

    public @NotNull ItemBuilder removeItemFlags(@NotNull ItemFlag... itemFlags) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.removeItemFlags(itemFlags);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder removeItemFlags(@NotNull List<ItemFlag> itemFlags) {
        return removeItemFlags(itemFlags.toArray(new ItemFlag[0]));
    }

    public @NotNull ItemBuilder hideAttributes() {
        return addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    public @NotNull ItemBuilder unbreakable(final boolean state) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;

        itemMeta.setUnbreakable(state);

        return this.meta(itemMeta);
    }

    public @NotNull ItemBuilder glowing(boolean glowing) {
        @NotNull final Enchantment enchantment = itemStack.getType() == Material.BOW ? Enchantment.LURE : Enchantment.INFINITY;

        if(glowing) {
            this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.addEnchantment(enchantment, 0, true);
        } else {
            this.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.removeEnchantment(enchantment);
        }

        return this;
    }

    public @NotNull ItemBuilder skullOwner(@NotNull OfflinePlayer offlinePlayer) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;
        if(!(itemMeta instanceof @NotNull SkullMeta skullMeta)) return this;

        skullMeta.setOwningPlayer(offlinePlayer);

        return this.meta(skullMeta);
    }

    public @NotNull ItemBuilder skullOwner(@NotNull String username) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;
        if(!(itemMeta instanceof @NotNull SkullMeta skullMeta)) return this;

        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(username));

        return this.meta(skullMeta);
    }

    public @NotNull ItemBuilder skullTexture(@NotNull String textureValue) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;
        if(!(itemMeta instanceof @NotNull SkullMeta skullMeta)) return this;
        if(textureValue.isEmpty()) return this;

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", textureValue));

        skullMeta.setPlayerProfile(profile);

        return this.meta(skullMeta);
    }

    @ApiStatus.Experimental
    public @NotNull ItemBuilder customModelData(int customModelData) {
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(customModelData).build());

        return this;
    }

    public @NotNull ItemBuilder addPotionEffect(@NotNull PotionEffect effect) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return this;
        if(!(itemMeta instanceof @NotNull PotionMeta potionMeta)) return this;

        potionMeta.setColor(effect.getType().getColor());
        potionMeta.addCustomEffect(effect, true);

        return this.meta(potionMeta);
    }

}