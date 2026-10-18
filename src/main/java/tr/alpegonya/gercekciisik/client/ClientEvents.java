package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tr.alpegonya.gercekciisik.GercekciIsikMod;
import tr.alpegonya.gercekciisik.LampBlock;

@Mod.EventBusSubscriber(modid = GercekciIsikMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 || !(state.getBlock() instanceof LampBlock lamp)) {
                return 0xFFFFFF;
            }
            return LampBlock.rgb(state.getValue(LampBlock.COLOR), lamp.isModern(),
                    state.getValue(LampBlock.TEMPERATURE));
        }, GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(), GercekciIsikMod.MODERN_FLUORESCENT.get());
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(), RenderType.cutout());
            ItemBlockRenderTypes.setRenderLayer(GercekciIsikMod.MODERN_FLUORESCENT.get(), RenderType.cutout());
        });
    }
}
