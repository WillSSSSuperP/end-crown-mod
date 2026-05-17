package com.end_crown.network;

import com.end_crown.EndCrownMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EndCrownMod.MODID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private ModNetwork() {}
    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(
                id++,
                TotemTriggerPacket.class,
                TotemTriggerPacket::encode,
                TotemTriggerPacket::decode,
                TotemTriggerPacket::handle
        );
    }
}