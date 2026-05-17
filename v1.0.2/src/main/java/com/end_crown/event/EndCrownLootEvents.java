package com.end_crown.event;

import com.end_crown.EndCrownMod;
import com.end_crown.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EndCrownMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EndCrownLootEvents {
    private static final ResourceLocation BASTION_TREASURE = new ResourceLocation("minecraft", "chests/bastion_treasure");
    private static final ResourceLocation END_CITY_TREASURE = new ResourceLocation("minecraft", "chests/end_city_treasure");

    private EndCrownLootEvents() {}

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableName = event.getName();
        if (!BASTION_TREASURE.equals(tableName) && !END_CITY_TREASURE.equals(tableName)) {
            return;
        }

        float chance = BASTION_TREASURE.equals(tableName) ? 0.0002F : 0.002F;

        LootPool pool = LootPool.lootPool()
                .name("end_crown")
                .add(LootItem.lootTableItem(ModItems.END_CROWN.get())
                        .when(LootItemRandomChanceCondition.randomChance(chance)))
                .build();

        event.getTable().addPool(pool);
    }
}