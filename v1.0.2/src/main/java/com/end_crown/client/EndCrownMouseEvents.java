package com.end_crown.client;

import com.end_crown.EndCrownMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EndCrownMod.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class EndCrownMouseEvents {
    private static final String FAKE_TAG = "end_crown_fake";
    private EndCrownMouseEvents() {}

    @SubscribeEvent
    public static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        ItemStack carried = screen.getMenu().getCarried();
        if (carried.isEmpty() || !carried.hasTag()) {
            return;
        }
        CompoundTag tag = carried.getTag();
        if (tag == null || !tag.getBoolean(FAKE_TAG)) {
            return;
        }

        event.setCanceled(true);
        screen.getMenu().setCarried(ItemStack.EMPTY);
        mc.player.containerMenu.setCarried(ItemStack.EMPTY);
        mc.player.inventoryMenu.setCarried(ItemStack.EMPTY);
        screen.getMenu().broadcastChanges();
        mc.player.containerMenu.broadcastChanges();
        mc.player.inventoryMenu.broadcastChanges();
    }
}