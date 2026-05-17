package com.end_crown.util;

import com.end_crown.registry.ModTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EndCrownUtil {
    private EndCrownUtil() {}

    public static boolean hasEndCrown(Player player) {

        ItemStack head =
                player.getItemBySlot(EquipmentSlot.HEAD);

        if (head.isEmpty()) {
            return false;
        }

        if (head.is(ModTags.END_CROWN)) {
            return true;
        }

        return head.hasTag()
                && head.getTag()
                .getBoolean("end_crown_fake");
    }
}