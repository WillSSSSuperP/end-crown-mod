package com.end_crown.registry;

import com.end_crown.EndCrownMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {

    public static final TagKey<Item> END_CROWN =
            ItemTags.create(
                    new ResourceLocation(
                            EndCrownMod.MODID,
                            "end_crown"
                    )
            );
    private ModTags() {
    }
}