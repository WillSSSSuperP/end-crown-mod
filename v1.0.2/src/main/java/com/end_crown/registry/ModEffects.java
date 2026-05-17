package com.end_crown.registry;

import com.end_crown.EndCrownMod;
import com.end_crown.effect.BlessingEffect;
import com.end_crown.effect.CrownCooldownEffect;
import com.end_crown.effect.DivineIntimidationEffect;
import com.end_crown.effect.UnyieldingWillEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EndCrownMod.MODID);
    public static final RegistryObject<MobEffect> DIVINE_INTIMIDATION = EFFECTS.register("divine_intimidation", DivineIntimidationEffect::new);
    public static final RegistryObject<MobEffect> CROWN_COOLDOWN = EFFECTS.register("crown_cooldown", CrownCooldownEffect::new);
    public static final RegistryObject<MobEffect> UNYIELDING_WILL = EFFECTS.register("unyielding_will", UnyieldingWillEffect::new);
    public static final RegistryObject<MobEffect> BLESSING = EFFECTS.register("blessing", BlessingEffect::new);

    private ModEffects() {}

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}