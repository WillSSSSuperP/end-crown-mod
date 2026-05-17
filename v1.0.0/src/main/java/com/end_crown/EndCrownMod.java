package com.end_crown;

import com.end_crown.network.ModNetwork;
import com.end_crown.registry.ModCreativeTabs;
import com.end_crown.registry.ModEffects;
import com.end_crown.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EndCrownMod.MODID)
public class EndCrownMod {

    public static final String MODID = "end_crown";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EndCrownMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modBus);
        ModEffects.register(modBus);
        ModCreativeTabs.register(modBus);
        ModNetwork.register();
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        LOGGER.info("End Crown mod loaded.");
    }
}