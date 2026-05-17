package com.end_crown.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class CrownCooldownEffect extends MobEffect {
    public CrownCooldownEffect() {
        super(MobEffectCategory.HARMFUL, 0x555555);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}