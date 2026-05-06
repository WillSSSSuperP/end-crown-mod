package com.end_crown.event;

import com.end_crown.EndCrownMod;
import com.end_crown.registry.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndCrownAttributeEvents {
    private static final UUID HEALTH_UUID = UUID.fromString("e7cbb5e4-9c29-4f6e-9f2a-ec1c9b2b0001");
    private static final float ABSORPTION_AMOUNT = 20.0F;
    private static final long ABSORPTION_INTERVAL = 60_000L;
    private static final Map<UUID, Long> LAST_REFRESH = new HashMap<>();

    private EndCrownAttributeEvents() {}

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.level().isClientSide) {
            return;
        }

        UUID playerId = player.getUUID();
        long now = System.currentTimeMillis();
        ItemStack crown = player.getItemBySlot(EquipmentSlot.HEAD);
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);

        if (maxHealth == null) {
            return;
        }

        if (crown.is(ModItems.END_CROWN.get())) {
            ensureHealthBonus(player, maxHealth);
            refreshAbsorption(player, playerId, now);
            return;
        }

        removeHealthBonus(player, maxHealth);
        player.setAbsorptionAmount(0.0F);
        LAST_REFRESH.remove(playerId);
    }

    public static void refreshAbsorption(Player player) {
        player.setAbsorptionAmount(ABSORPTION_AMOUNT);
        LAST_REFRESH.put(player.getUUID(), System.currentTimeMillis());
    }

    private static void ensureHealthBonus(Player player, AttributeInstance maxHealth) {
        if (maxHealth.getModifier(HEALTH_UUID) == null) {
            maxHealth.addTransientModifier(new AttributeModifier(
                    HEALTH_UUID,
                    "End Crown health bonus",
                    10.0D,
                    AttributeModifier.Operation.ADDITION
            ));
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }
    }

    private static void removeHealthBonus(Player player, AttributeInstance maxHealth) {
        if (maxHealth.getModifier(HEALTH_UUID) != null) {
            maxHealth.removeModifier(HEALTH_UUID);
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }
    }

    private static void refreshAbsorption(Player player, UUID playerId, long now) {
        Long lastRefresh = LAST_REFRESH.get(playerId);
        if (lastRefresh == null) {
            player.setAbsorptionAmount(ABSORPTION_AMOUNT);
            LAST_REFRESH.put(playerId, now);
            return;
        }

        if (now - lastRefresh >= ABSORPTION_INTERVAL) {
            if (player.getAbsorptionAmount() < ABSORPTION_AMOUNT) {
                player.setAbsorptionAmount(ABSORPTION_AMOUNT);
            }
            LAST_REFRESH.put(playerId, now);
        }
    }
}