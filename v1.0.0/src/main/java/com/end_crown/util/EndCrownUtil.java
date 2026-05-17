package com.end_crown.util;

import com.end_crown.registry.ModItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EndCrownUtil {
    private EndCrownUtil() {}

    public static boolean hasEndCrown(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return !head.isEmpty() && head.is(ModItems.END_CROWN.get());
    }
}