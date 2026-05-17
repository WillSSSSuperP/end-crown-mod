package com.end_crown.event;

import com.end_crown.network.ModNetwork;
import com.end_crown.network.TotemTriggerPacket;
import com.end_crown.registry.ModEffects;
import com.end_crown.registry.ModItems;
import com.end_crown.util.EndCrownDisguiseManager;
import com.end_crown.util.EndCrownUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import com.end_crown.util.AdaptiveArmorSetHelper;
import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EndCrownEvents {
    private static final long REVIVE_COOLDOWN = 30_000L;
    private static final HashMap<UUID, Long> cooldownCache = new HashMap<>();
    private static final HashMap<UUID, Long> reviveCache = new HashMap<>();
    private static final HashMap<UUID, Long> crownActiveCache = new HashMap<>();
    private static final HashMap<UUID, Long> firstDeathLock = new HashMap<>();
    private static final long FIRST_DEATH_LOCK_TIME = 500L;
    private static final HashMap<UUID, Long> reviveStamp = new HashMap<>();
    private static final HashMap<UUID, Long> crownEquipCache = new HashMap<>();
    private static final HashMap<UUID, DamageSource> lastDamageCache = new HashMap<>();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        UUID id = player.getUUID();
        long now = System.currentTimeMillis();

        lastDamageCache.put(id, event.getSource());

        float dmg = event.getAmount();
        float tal = player.getHealth() + player.getAbsorptionAmount();
        if (tal - dmg > 0) return;

        if (!EndCrownUtil.hasEndCrown(player)) return;

        DamageSource src = event.getSource();
        if ("out_of_world".equals(src.type().msgId())) return;

        if (player.getHealth() - dmg <= 0) {
            firstDeathLock.put(id, now + FIRST_DEATH_LOCK_TIME);
        }

        Long cd = cooldownCache.get(id);
        if (cd != null && now - cd < REVIVE_COOLDOWN) return;

        event.setCanceled(true);
        player.setAbsorptionAmount(20.0F);
        reviveStamp.put(id, now);

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4));

        cooldownCache.put(id, now);

        player.addEffect(new MobEffectInstance(
                ModEffects.CROWN_COOLDOWN.get(),
                (int) (REVIVE_COOLDOWN / 50),
                0,
                false,
                true
        ));
        playEffect(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer sp)) return;

        EndCrownDisguiseManager.tick(sp);

        UUID id = sp.getUUID();
        long now = System.currentTimeMillis();

        Long lock = firstDeathLock.get(id);
        if (lock != null) {
            if (now < lock) return;
            firstDeathLock.remove(id);
        }

        if (EndCrownUtil.hasEndCrown(sp)) {
            crownEquipCache.put(id, now);
            crownActiveCache.put(id, now);
        }

        if (EndCrownUtil.hasEndCrown(sp)) {
            sp.addEffect(new MobEffectInstance(
                    ModEffects.BLESSING.get(),
                    2,
                    0,
                    true,
                    true
            ));
        } else {
            sp.removeEffect(ModEffects.BLESSING.get());
        }

        Long cdStart = cooldownCache.get(id);

        if (cdStart != null) {
            long remain = REVIVE_COOLDOWN - (now - cdStart);

            if (remain > 0) {
                int ticks = (int) Math.ceil(remain / 50.0);

                MobEffectInstance eff = sp.getEffect(ModEffects.CROWN_COOLDOWN.get());

                if (eff == null || eff.getDuration() < ticks) {
                    sp.addEffect(new MobEffectInstance(
                            ModEffects.CROWN_COOLDOWN.get(),
                            ticks,
                            0,
                            false,
                            true
                    ));
                }
            } else {
                cooldownCache.remove(id);
                sp.removeEffect(ModEffects.CROWN_COOLDOWN.get());
                sp.level().playSound(
                        null,
                        sp.blockPosition(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.2F
                );
            }
            if (sp.hasEffect(ModEffects.UNYIELDING_WILL.get())) {
                sp.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST,
                        2,
                        255,
                        true,
                        false
                ));
                sp.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        2,
                        4,
                        true,
                        false
                ));
            }
        }

        Long active = crownActiveCache.get(id);
        if (active == null || now - active > 500) return;
        if (sp.getHealth() <= 0.0F) {
            Long last = reviveCache.get(id);

            if (last != null && now - last < 1) return;

            reviveCache.put(id, now);
            sp.setHealth(1.0F);
            sp.deathTime = 0;
            sp.hurtTime = 0;
            sp.hurtDuration = 0;
            sp.invulnerableTime = 20;
            sp.setHealth(sp.getMaxHealth());
            sp.sendSystemMessage(Component.literal("冠冕拒绝了异常死亡！！！"));

            sp.addEffect(new MobEffectInstance(
                    ModEffects.UNYIELDING_WILL.get(),
                    20 * 60,
                    0,
                    false,
                    true,
                    true
            ));
            killAttacker(sp);
            playEffect(sp);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(net.minecraftforge.event.entity.living.LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (player.hasEffect(ModEffects.UNYIELDING_WILL.get())) {
            event.setAmount(event.getAmount() * 99999999999999999999.0F);
        }
    }

    private static void killAttacker(ServerPlayer victim) {
        DamageSource src = lastDamageCache.remove(victim.getUUID());
        if (src == null) return;

        Entity attacker = src.getEntity();
        if (attacker == null) attacker = src.getDirectEntity();

        if (attacker instanceof LivingEntity living) {
            living.setHealth(0.0F);
            if (!living.isDeadOrDying()) {
                living.hurt(living.damageSources().generic(), Float.MAX_VALUE);
            }
        }
    }

    private static void playEffect(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.blockPosition(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

        level.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                60,
                0.5, 0.8, 0.5,
                0.1
        );
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new TotemTriggerPacket()
        );
    }
}