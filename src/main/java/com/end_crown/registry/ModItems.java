package com.end_crown.registry;

import com.end_crown.EndCrownMod;
import com.end_crown.item.EndCrownItem;
import com.end_crown.item.EndCrownTotemItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EndCrownMod.MODID);
    public static final RegistryObject<Item> END_CROWN = ITEMS.register("end_crown", EndCrownItem::new);
    public static final RegistryObject<Item> END_CROWN_TOTEM = ITEMS.register("end_crown_totem", EndCrownTotemItem::new);
    private ModItems() {}

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}