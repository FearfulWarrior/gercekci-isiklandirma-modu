package tr.alpegonya.gercekciisik.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tr.alpegonya.gercekciisik.GercekciIsikMod;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.LampBlockEntity;

@Mod.EventBusSubscriber(modid = GercekciIsikMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 || !(state.getBlock() instanceof LampBlock lamp)) return 0xFFFFFF;
            // Off lamps show a neutral, muted diffuser rather than the saved selected hue.
            if (!state.getValue(LampBlock.POWERED)) return 0xC8C8C8;

            int color = state.getValue(LampBlock.COLOR);
            int temperature = 9;
            if (color == LampBlock.MODERN_KELVIN_COLOR
                    && level != null && pos != null
                    && level.getBlockEntity(pos) instanceof LampBlockEntity data) {
                temperature = data.getTemperature();
            }
            return LampBlock.rgb(color, lamp.isModern(), temperature);
        }, GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(), GercekciIsikMod.MODERN_FLUORESCENT.get());
    }
}
