package com.end_crown.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class DivineIntimidationEffect extends MobEffect {
    private static final UUID DAMAGE_UUID = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private static final UUID SPEED_UUID = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");

    public DivineIntimidationEffect() {
        super(MobEffectCategory.HARMFUL, 0x5B5B5B);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_UUID.toString(), -8.0D, AttributeModifier.Operation.ADDITION);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_UUID.toString(), -0.6D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}