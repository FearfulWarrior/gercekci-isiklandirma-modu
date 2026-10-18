package tr.alpegonya.gercekciisik.client;

import net.minecraft.world.level.block.entity.BlockEntity;
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
            if (tintIndex != 0 || !(state.getBlock() instanceof LampBlock lamp)) {
                return 0xFFFFFF;
            }
            int color = lamp.isModern() ? 13 : 0;
            int temperature = 9;
            if (level != null && pos != null && level.getBlockEntity(pos) instanceof LampBlockEntity data) {
                color = data.getColor();
                temperature = data.getTemperature();
            }
            return LampBlock.rgb(color, lamp.isModern(), temperature);
        }, GercekciIsikMod.TRADITIONAL_FLUORESCENT.get(), GercekciIsikMod.MODERN_FLUORESCENT.get());
    }
}
