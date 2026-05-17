package com.end_crown.event;

import com.end_crown.EndCrownMod;
import com.end_crown.registry.ModEffects;
import com.end_crown.util.EndCrownUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndCrownIntimidationEvents {
    private EndCrownIntimidationEvents() {}

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (!EndCrownUtil.hasEndCrown(victim)) {
            return;
        }

        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity == null) {
            sourceEntity = event.getSource().getDirectEntity();
        }

        LivingEntity attacker = resolveAttacker(sourceEntity);
        if (attacker == null || attacker == victim) {
            return;
        }

        if (attacker instanceof Player attackerPlayer && EndCrownUtil.hasEndCrown(attackerPlayer)) {
            return;
        }

        attacker.addEffect(new MobEffectInstance(ModEffects.DIVINE_INTIMIDATION.get(), 600, 0, false, true));
    }

    private static LivingEntity resolveAttacker(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living;
        }

        if (entity instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner instanceof LivingEntity livingOwner) {
                return livingOwner;
            }
        }
        return null;
    }
}