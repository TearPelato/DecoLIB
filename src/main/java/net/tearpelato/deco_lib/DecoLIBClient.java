package net.tearpelato.deco_lib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = DecoLIB.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DecoLIB.MOD_ID, value = Dist.CLIENT)
public class DecoLIBClient {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }


}
