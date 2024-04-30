package com.agonkolgeci.nexus_api.utils.world;

import com.agonkolgeci.nexus_api.utils.objects.ObjectUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ItemBuilder<B extends ItemBuilder<B>> {

    @NotNull private ItemStack itemStack;

    @Getter @Nullable private Component flag;

    @Getter @NotNull private final List<Component> tags;
    @Getter @NotNull private final List<Component> actions;

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;

        this.tags = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public ItemBuilder(@NotNull Material material) {
        this(new ItemStack(material));
    }

    @NotNull
    public ItemStack toItemStack() {
        @NotNull final ItemStack itemStack = this.itemStack.clone();
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return itemStack;

        @NotNull final List<Component> itemLore = new ArrayList<>();

        if(flag != null) {
            itemMeta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(ObjectUtils.requireNonNullElse(LegacyComponentSerializer.legacySection().deserialize(itemMeta.getDisplayName()), Component.empty()).appendSpace().append(flag.colorIfAbsent(NamedTextColor.WHITE)).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));
        }

        if(!tags.isEmpty()) {
            itemLore.addAll(tags.stream().map(Component::appendSpace).collect(Collectors.toList()));
            itemLore.add(Component.empty());
        }

        if(itemMeta.hasLore()) {
            itemLore.addAll(itemMeta.getLore().stream().map(line -> LegacyComponentSerializer.legacySection().deserialize(line)).collect(Collectors.toList()));
        }

        if(!actions.isEmpty()) {
            itemLore.add(Component.empty());
            itemLore.addAll(actions);
        }

        if(!itemLore.isEmpty()) {
            itemMeta.setLore(itemLore.stream().map(line -> LegacyComponentSerializer.legacySection().serialize(line)).collect(Collectors.toList()));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @NotNull
    public B amount(final int amount) {
        itemStack.setAmount(amount);
        return (B) this;
    }

    @NotNull
    public B type(@NotNull Material material) {
        itemStack.setType(material);
        return (B) this;
    }

    @NotNull
    public B itemMeta(@NotNull ItemMeta itemMeta) {
        itemStack.setItemMeta(itemMeta);
        return (B) this;
    }

    @Nullable
    public Component displayName() {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return null;

        return LegacyComponentSerializer.legacySection().deserialize(itemMeta.getDisplayName());
    }

    @NotNull
    public B displayName(@NotNull Component displayName) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(displayName.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)));

        return this.itemMeta(itemMeta);
    }

    @Nullable
    public List<Component> lore() {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return null;

        return itemMeta.getLore().stream().map(line -> LegacyComponentSerializer.legacySection().deserialize(line)).collect(Collectors.toList());
    }

    @NotNull
    public B lore(@NotNull List<Component> lines) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.setLore(lines.stream().map(line -> LegacyComponentSerializer.legacySection().serialize(line.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))).collect(Collectors.toList()));

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B lore(@NotNull Component... lines) {
        return lore(Arrays.asList(lines));
    }

    @NotNull
    public B flag(@NotNull Component flag) {
        this.flag = flag.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);

        return (B) this;
    }

    @NotNull
    public B addTag(@NotNull Component tag) {
        this.tags.add(tag.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

        return (B) this;
    }

    @NotNull
    public B addProperty(@NotNull Component displayName, @NotNull Component value) {
        this.addLore(
                Component.text("•", NamedTextColor.DARK_GRAY)
                        .appendSpace()
                        .append(displayName.append(Component.text(":")).colorIfAbsent(NamedTextColor.GRAY))
                        .appendSpace()
                        .append(value.colorIfAbsent(NamedTextColor.WHITE))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        );

        return (B) this;
    }

    @NotNull
    public B addAction(@NotNull Component icon, @NotNull Component description) {
        this.actions.add(
                icon.colorIfAbsent(NamedTextColor.WHITE)
                        .appendSpace()
                        .append(Component.text("•", NamedTextColor.DARK_GRAY))
                        .appendSpace()
                        .append(description.colorIfAbsent(NamedTextColor.GRAY))
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)
        );

        return (B) this;
    }

    @NotNull
    public B addLore(@NotNull List<Component> lines) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        @Nullable final List<Component> newLore = ObjectUtils.requireNonNullElse(lore(), new ArrayList<>());

        newLore.addAll(lines);

        return this.lore(newLore);
    }

    @NotNull
    public B addLore(@NotNull Component... lines) {
        return addLore(Arrays.asList(lines));
    }

    @NotNull
    public B addEmptyLore() {
        return addLore(Component.empty());
    }

    @NotNull
    public B addEnchantments(@NotNull HashMap<Enchantment, Integer> enchantmentLevelsMap, boolean ignoreLevelRestriction) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        enchantmentLevelsMap.forEach(((enchantment, level) -> itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction)));

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B addEnchantment(@NotNull Enchantment enchantment, final int level) {
        itemStack.addEnchantment(enchantment, level);

        return (B) this;
    }

    @NotNull
    public B addUnsafeEnchantment(@NotNull Enchantment enchantment, final int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);

        return (B) this;
    }

    @NotNull
    public B removeEnchantment(@NotNull Enchantment enchantment) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.removeEnchant(enchantment);

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B addItemFlags(@NotNull ItemFlag... itemFlags) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.addItemFlags(itemFlags);

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B addItemFlags(@NotNull List<ItemFlag> itemFlags) {
        return addItemFlags(itemFlags.toArray(new ItemFlag[0]));
    }

    @NotNull
    public B removeItemFlags(@NotNull ItemFlag... itemFlags) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.removeItemFlags(itemFlags);

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B removeItemFlags(@NotNull List<ItemFlag> itemFlags) {
        return removeItemFlags(itemFlags.toArray(new ItemFlag[0]));
    }

    @NotNull
    public B hideAttributes() {
        return addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @NotNull
    public B unbreakable(final boolean state) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        itemMeta.spigot().setUnbreakable(state);

        return this.itemMeta(itemMeta);
    }

    @NotNull
    public B glowing(boolean glowing) {
        @NotNull final Enchantment enchantment = Enchantment.ARROW_INFINITE;

        if(glowing) {
            this.addUnsafeEnchantment(enchantment, 1);
            this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            this.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.removeEnchantment(enchantment);
        }

        return (B) this;
    }

    @NotNull
    public B skullOwner(@NotNull OfflinePlayer offlinePlayer) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        if(!(itemMeta instanceof SkullMeta)) return (B) this;
        @NotNull final SkullMeta skullMeta = (SkullMeta) itemMeta;

        skullMeta.setOwner(offlinePlayer.getName());

        return this.itemMeta(skullMeta);
    }

    @NotNull
    @Deprecated
    public B skullOwner(@NotNull String username) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        if(!(itemMeta instanceof SkullMeta)) return (B) this;
        @NotNull final SkullMeta skullMeta = (SkullMeta) itemMeta;

        skullMeta.setOwner(username);

        return this.itemMeta(skullMeta);
    }

    @NotNull
    public B addPotionCustomEffect(@NotNull PotionEffect effect) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        if(!(itemMeta instanceof PotionMeta)) return (B) this;
        @NotNull final PotionMeta potionMeta = (PotionMeta) itemMeta;

        potionMeta.addCustomEffect(effect, true);

        return this.itemMeta(potionMeta);
    }

    @NotNull
    public B spawnerType(@NotNull EntityType entityType) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        if(!(itemMeta instanceof BlockStateMeta)) return (B) this;
        @NotNull final BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;

        if(!(blockStateMeta.getBlockState() instanceof CreatureSpawner)) return (B) this;
        @NotNull final CreatureSpawner creatureSpawner = (CreatureSpawner) blockStateMeta.getBlockState();

        creatureSpawner.setSpawnedType(entityType);
        blockStateMeta.setBlockState(creatureSpawner);

        return this.itemMeta(blockStateMeta);
    }


    @NotNull
    public B addFireworkEffect(@NotNull FireworkEffect... fireworkEffect) {
        @Nullable final ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return (B) this;

        if(!(itemMeta instanceof @NotNull FireworkMeta)) return (B) this;
        @NotNull final FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;

        fireworkMeta.addEffects(fireworkEffect);

        return this.itemMeta(fireworkMeta);
    }

}