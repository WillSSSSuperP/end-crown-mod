package com.end_crown.client;

import com.end_crown.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public final class EndCrownClientEffects {
    private EndCrownClientEffects() {}

    public static void playTotemAnimation() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.gameRenderer == null) {
            return;
        }
        minecraft.gameRenderer.displayItemActivation(new ItemStack(ModItems.END_CROWN_TOTEM.get()));
    }
}