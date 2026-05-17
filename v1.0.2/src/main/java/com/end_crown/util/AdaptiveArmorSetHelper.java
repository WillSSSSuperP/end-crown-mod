package com.end_crown.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class AdaptiveArmorSetHelper {

    private static final String[] ARMOR_SUFFIXES = {
            "_helmet",
            "_chestplate",
            "_leggings",
            "_boots",
            "_head",
            "_body",
            "_legs",
            "_feet"
    };

    private AdaptiveArmorSetHelper() {
    }

    public record MatchResult(MatchType type, String key) {
    }

    public enum MatchType {
        TAG,
        PREFIX,
        MATERIAL
    }

    public static boolean hasCrownAndMatchingBodySet(Player player) {
        if (!EndCrownUtil.hasEndCrown(player)) {
            return false;
        }
        return resolveBodySet(player) != null;
    }

    public static MatchResult resolveBodySet(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        return resolveSet(chest, legs, feet);
    }

    public static MatchResult resolveSet(ItemStack first, ItemStack second, ItemStack third) {
        MatchResult tagMatch = matchByTag(first, second, third);
        if (tagMatch != null) {
            return tagMatch;
        }
        MatchResult prefixMatch = matchByPrefix(first, second, third);
        if (prefixMatch != null) {
            return prefixMatch;
        }
        MatchResult materialMatch = matchByMaterial(first, second, third);
        if (materialMatch != null) {
            return materialMatch;
        }
        return null;
    }

    private static MatchResult matchByTag(ItemStack first, ItemStack second, ItemStack third) {
        Set<ResourceLocation> commonTags = sharedTags(first, second, third);
        if (commonTags.isEmpty()) {
            return null;
        }
        ResourceLocation best = pickBestTag(commonTags);
        if (best == null) {
            return null;
        }
        return new MatchResult(MatchType.TAG, best.toString());
    }

    private static MatchResult matchByPrefix(ItemStack first, ItemStack second, ItemStack third) {
        String prefix = normalizedPrefix(first);
        if (prefix == null) {
            return null;
        }
        if (!prefix.equals(normalizedPrefix(second))) {
            return null;
        }
        if (!prefix.equals(normalizedPrefix(third))) {
            return null;
        }
        return new MatchResult(MatchType.PREFIX, prefix);
    }

    private static MatchResult matchByMaterial(ItemStack first, ItemStack second, ItemStack third) {
        String material = materialKey(first);
        if (material == null) {
            return null;
        }
        if (!material.equals(materialKey(second))) {
            return null;
        }
        if (!material.equals(materialKey(third))) {
            return null;
        }
        return new MatchResult(MatchType.MATERIAL, material);
    }

    private static Set<ResourceLocation> sharedTags(ItemStack first, ItemStack second, ItemStack third) {
        Set<ResourceLocation> common = null;
        for (ItemStack stack : new ItemStack[]{first, second, third}) {
            Set<ResourceLocation> tags = tagsOf(stack);
            if (common == null) {
                common = new LinkedHashSet<>(tags);
            } else {
                common.retainAll(tags);
            }
            if (common.isEmpty()) {
                return Set.of();
            }
        }
        return common == null ? Set.of() : common;
    }

    private static Set<ResourceLocation> tagsOf(ItemStack stack) {
        if (stack.isEmpty()) {
            return Set.of();
        }
        Set<ResourceLocation> tags = new LinkedHashSet<>();
        BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem())
                .tags()
                .forEach(tag -> tags.add(tag.location()));
        return tags;
    }

    private static ResourceLocation pickBestTag(Set<ResourceLocation> tags) {
        return tags.stream()
                .filter(tag -> !isGenericTag(tag))
                .sorted(Comparator.comparingInt((ResourceLocation tag) -> tag.getPath().length()).reversed())
                .findFirst()
                .orElse(null);
    }
    private static boolean isGenericTag(ResourceLocation tag) {
        String namespace = tag.getNamespace();
        String path = tag.getPath();
        if ("Minecraft".equals(namespace) || "forge".equals(namespace) || "c".equals(namespace)) {
            return true;
        }
        if ("armor".equals(path) || "armors".equals(path) || "equipment".equals(path)) {
            return true;
        }
        if (path.contains("trim")) {
            return true;
        }
        if (path.contains("trimmable")) {
            return true;
        }
        return false;
    }

    private static String normalizedPrefix(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) {
            return null;
        }

        String path = id.getPath().toLowerCase(Locale.ROOT);
        for (String suffix : ARMOR_SUFFIXES) {
            if (path.endsWith(suffix)) {
                String prefix = path.substring(0, path.length() - suffix.length());
                prefix = trimTrailingSeparators(prefix);
                return prefix.isEmpty() ? null : prefix;
            }
        }
        return null;
    }

    private static String trimTrailingSeparators(String text) {
        int end = text.length();
        while (end > 0) {
            char ch = text.charAt(end - 1);
            if (ch == '_' || ch == '-' || ch == '/' || ch == '.') {
                end--;
            } else {
                break;
            }
        }
        return text.substring(0, end);
    }

    private static String materialKey(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        if (!(stack.getItem() instanceof ArmorItem armorItem)) {
            return null;
        }
        ArmorMaterial material = armorItem.getMaterial();
        if (material == null) {
            return null;
        }
        return material.getName();
    }
}
