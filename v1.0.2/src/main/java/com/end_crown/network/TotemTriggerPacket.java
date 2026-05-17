package com.end_crown.network;

import com.end_crown.client.EndClientEvents;
import com.end_crown.client.EndCrownClientEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TotemTriggerPacket {
    public TotemTriggerPacket() {}
    public TotemTriggerPacket(FriendlyByteBuf buf) {}
    public void encode(FriendlyByteBuf buf) {
    }

    public static TotemTriggerPacket decode(FriendlyByteBuf buf) {
        return new TotemTriggerPacket(buf);
    }

    public static void handle(TotemTriggerPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            EndCrownClientEffects.playTotemAnimation();
            EndClientEvents.triggerNoDeathScreen();
        });
        context.setPacketHandled(true);
    }
}