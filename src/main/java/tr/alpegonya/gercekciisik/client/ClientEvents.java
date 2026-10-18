package tr.alpegonya.gercekciisik.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tr.alpegonya.gercekciisik.AdjustableLightBlockEntity;
import tr.alpegonya.gercekciisik.ConfigurableLightBlock;
import tr.alpegonya.gercekciisik.GercekciIsikMod;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.LampBlockEntity;
import tr.alpegonya.gercekciisik.LedStairBlock;

@Mod.EventBusSubscriber(modid = GercekciIsikMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0 || !(state.getBlock() instanceof LampBlock lamp)) return 0xFFFFFF;
            if (!state.getValue(LampBlock.POWERED)) return 0xBEBEBE;
            int color = state.getValue(LampBlock.COLOR);
            int temperature = 9;
            if (color == LampBlock.MODERN_KELVIN_COLOR
                    && level != null && pos != null
                    && level.getBlockEntity(pos) instanceof LampBlockEntity data) {
                temperature = data.getTemperature();
            }
            return LampBlock.rgb(color, lamp.isModern(), temperature);
        }, GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(), GercekciIsikMod.MODERN_FLUORESCENT.get());

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFF;
            if (state.getBlock() instanceof ConfigurableLightBlock light
                    && !state.getValue(ConfigurableLightBlock.POWERED)) return 0xBEBEBE;
            if (state.getBlock() instanceof LedStairBlock
                    && !state.getValue(LedStairBlock.POWERED)) return 0xBEBEBE;

            int color = 13;
            int temperature = 9;
            if (level != null && pos != null
                    && level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity data) {
                color = data.getColor();
                temperature = data.getTemperature();
            } else if (state.getBlock() instanceof ConfigurableLightBlock light) {
                color = light.getDefaultColor();
            } else if (state.getBlock() instanceof LedStairBlock stairs) {
                color = stairs.getDefaultColor();
            }
            if (state.getBlock() instanceof ConfigurableLightBlock light) return light.colorFor(color, temperature);
            return LampBlock.rgb(color, true, temperature);
        }, GercekciIsikMod.SQUARE_SPOT.get(), GercekciIsikMod.ROUND_SPOT.get(),
                GercekciIsikMod.TRADITIONAL_RING_LIGHT.get(), GercekciIsikMod.MODERN_RING_LIGHT.get(),
                GercekciIsikMod.HORIZONTAL_LED_STRIP.get(), GercekciIsikMod.VERTICAL_LED_STRIP.get(),
                GercekciIsikMod.LED_STAIRS.get());
    }
}
