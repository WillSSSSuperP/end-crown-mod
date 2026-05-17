package com.end_crown.client;

import com.end_crown.EndCrownMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class EndClientEvents {
    public static long NO_DEATH_SCREEN_UNTIL = 0L;

    private EndClientEvents() {}

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof DeathScreen)) {
            return;
        }

        if (System.currentTimeMillis() < NO_DEATH_SCREEN_UNTIL) {
            event.setCanceled(true);
        }
    }

    public static void triggerNoDeathScreen() {
        NO_DEATH_SCREEN_UNTIL = System.currentTimeMillis() + 1500L;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.screen instanceof DeathScreen) {
            minecraft.setScreen(null);
        }

        if (minecraft != null && minecraft.player != null) {
            minecraft.player.deathTime = 0;
        }
    }
}