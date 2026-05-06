package com.end_crown.registry;

import com.end_crown.EndCrownMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EndCrownMod.MODID);
    public static final RegistryObject<CreativeModeTab> END_CROWN_TAB = TABS.register("end_crown_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.end_crown"))
            .icon(() -> new ItemStack(ModItems.END_CROWN.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.END_CROWN.get());
            })
            .build());

    private ModCreativeTabs() {}

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}