package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class LightingRemoteItem extends Item {
    public LightingRemoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof LampBlock lamp)) return InteractionResult.PASS;

        if (level.isClientSide) {
            int temperature = 9;
            if (level.getBlockEntity(pos) instanceof LampBlockEntity data) temperature = data.getTemperature();
            final int color = state.getValue(LampBlock.COLOR);
            final int brightnessMode = LampBlock.modeForLightLevel(state.getValue(LampBlock.BRIGHTNESS));
            final int safeTemperature = temperature;
            final boolean powered = state.getValue(LampBlock.POWERED);
            final boolean modern = lamp.isModern();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> tr.alpegonya.gercekciisik.client.ClientScreenOpener.open(
                            pos, color, brightnessMode, safeTemperature, powered, modern));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
