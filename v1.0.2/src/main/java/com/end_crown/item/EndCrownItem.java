package com.end_crown.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;

import java.util.List;

public class EndCrownItem extends ArmorItem {
    public EndCrownItem() {
        super(EndCrownMaterial.END_CROWN, Type.HELMET, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(0));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "end_crown:textures/armor/end_crown_layer_1.png";
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("advancement.end_crown.fate_and_end.description").withStyle(ChatFormatting.GRAY));
    }
}