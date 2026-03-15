package net.tearpelato.deco_lib;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DecoLIB.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DecoLIBClient {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }


}
