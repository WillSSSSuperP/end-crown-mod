package com.end_crown.event;

import com.end_crown.EndCrownMod;
import com.end_crown.util.EndCrownState;
import com.end_crown.util.EndCrownUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndCrownPiglinEvents {
    private EndCrownPiglinEvents() {}

    @SubscribeEvent
    public static void onPiglinTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Piglin)) {
            return;
        }

        LivingEntity target = event.getNewTarget();
        if (!(target instanceof Player player)) {
            return;
        }

        if (EndCrownState.isCrownActive(player)) {
            event.setCanceled(true);
        }
    }
}