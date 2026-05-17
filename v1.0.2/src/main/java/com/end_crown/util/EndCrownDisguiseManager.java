package com.end_crown.util;

import com.end_crown.EndCrownMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndCrownDisguiseManager {
    private static final String ACTIVE_KEY = "end_crown_disguised";
    private static final String ORIGINAL_KEY = "end_crown_original";
    private static final String LAST_CROWN_KEY = "end_crown_last_crown";
    private static final String FAKE_TAG = "end_crown_fake";
    private static final String FAKE_ID_TAG = "end_crown_fake_id";
    private static final String RESTORE_COOLDOWN_KEY = "end_crown_restore_cooldown";
    private EndCrownDisguiseManager() {}

    public static void tick(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        ItemStack head = player.getInventory().armor.get(3);
        boolean disguised = data.getBoolean(ACTIVE_KEY);
        boolean currentFake = isFakeCrown(head);

        if (disguised && !currentFake) {
            restore(player);
            cleanupFakeCrowns(player);
            clearFakeCursor(player);
            disguised = false;
        }
        boolean hasCrown = EndCrownUtil.hasEndCrown(player);
        boolean lastCrown = data.getBoolean(LAST_CROWN_KEY);

        if (disguised) {
            return;
        }

        if (hasCrown != lastCrown) {
            data.putBoolean(LAST_CROWN_KEY, hasCrown);
            long lastRestore = data.getLong(RESTORE_COOLDOWN_KEY);
            boolean restoring =
                    System.currentTimeMillis() - lastRestore < 500L;

            if (hasCrown && !restoring) {
                applyDisguise(player);
            }
        }
    }

    private static void applyDisguise(Player player) {
        CompoundTag data = player.getPersistentData();
        ItemStack head = player.getInventory().armor.get(3);
        ItemStack fake = findMatchingHelmet(player);

        if (fake.isEmpty()) {
            return;
        }
        markAsFakeCrown(fake);
        CompoundTag tag = new CompoundTag();
        head.save(tag);
        data.put(ORIGINAL_KEY, tag);
        player.setItemSlot(EquipmentSlot.HEAD, fake);
        data.putBoolean(ACTIVE_KEY, true);
        player.sendSystemMessage(
                Component.translatable("message.end_crown.adapted")
        );
    }

    public static void restore(Player player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(ACTIVE_KEY)) {
            return;
        }

        CompoundTag saved = data.getCompound(ORIGINAL_KEY);

        if (!saved.isEmpty()) {
            ItemStack original = ItemStack.of(saved);

            if (original.hasTag()) {
                CompoundTag tag = original.getTag();

                if (tag != null) {
                    tag.remove(FAKE_TAG);
                    tag.remove(FAKE_ID_TAG);

                    if (tag.isEmpty()) {
                        original.setTag(null);
                    }
                }
            }
            player.setItemSlot(EquipmentSlot.HEAD, original);
        }
        data.remove(ACTIVE_KEY);
        data.remove(ORIGINAL_KEY);
        data.putLong(
                RESTORE_COOLDOWN_KEY,
                System.currentTimeMillis()
        );
    }

    private static void cleanupFakeCrowns(Player player) {
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {

            ItemStack stack = inv.getItem(i);

            if (isFakeCrown(stack)) {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }

        if (player.level() instanceof ServerLevel level) {
            AABB box =
                    player.getBoundingBox().inflate(3.0D);

            for (ItemEntity itemEntity :
                    level.getEntitiesOfClass(ItemEntity.class, box)) {

                if (isFakeCrown(itemEntity.getItem())) {
                    itemEntity.discard();
                }
            }
        }
        inv.setChanged();
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }

    private static void clearFakeCursor(Player player) {
        ItemStack carried = player.containerMenu.getCarried();

        if (isFakeCrown(carried)) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
        }
    }

    private static void markAsFakeCrown(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(FAKE_TAG, true);
        tag.putString(
                FAKE_ID_TAG,
                UUID.randomUUID().toString()
        );
    }

    public static boolean isFakeCrown(ItemStack stack) {

        return !stack.isEmpty()
                && stack.hasTag()
                && stack.getTag().getBoolean(FAKE_TAG);
    }

    private static ItemStack findMatchingHelmet(Player player) {
        ItemStack chest =
                player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs =
                player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet =
                player.getItemBySlot(EquipmentSlot.FEET);

        if (chest.isEmpty()
                || legs.isEmpty()
                || feet.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ResourceLocation chestId =
                net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(chest.getItem());
        ResourceLocation legsId =
                net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(legs.getItem());
        ResourceLocation feetId =
                net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .getKey(feet.getItem());

        if (chestId == null
                || legsId == null
                || feetId == null) {
            return ItemStack.EMPTY;
        }

        String chestPath = chestId.getPath();
        String legsPath = legsId.getPath();
        String feetPath = feetId.getPath();

        if (!chestPath.endsWith("_chestplate")
                || !legsPath.endsWith("_leggings")
                || !feetPath.endsWith("_boots")) {
            return ItemStack.EMPTY;
        }

        String chestBase =
                chestPath.replace("_chestplate", "");
        String legsBase =
                legsPath.replace("_leggings", "");
        String feetBase =
                feetPath.replace("_boots", "");

        if (!chestBase.equals(legsBase)
                || !chestBase.equals(feetBase)) {
            return ItemStack.EMPTY;
        }

        ResourceLocation helmetId =
                new ResourceLocation(
                        chestId.getNamespace(),
                        chestBase + "_helmet"
                );
        var item =
                net.minecraft.core.registries.BuiltInRegistries.ITEM
                        .get(helmetId);

        if (item == null) return ItemStack.EMPTY;
        ItemStack originalHead =
                player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack fake = new ItemStack(item);

        if (originalHead.hasTag()) {
            fake.setTag(originalHead.getTag().copy());
        }

        Component baseName = fake.getHoverName();
        fake.setHoverName(
                Component.literal("")
                        .append(baseName)
                        .append(Component.translatable("tooltip.end_crown.disguise")));
        markAsFakeCrown(fake);
        return fake;
    }
}