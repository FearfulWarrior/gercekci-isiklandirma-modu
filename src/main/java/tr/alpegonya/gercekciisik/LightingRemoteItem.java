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
        if (!(state.getBlock() instanceof LampBlock lamp)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            int currentColor = lamp.isModern() ? 13 : 0;
            int currentTemperature = 9;
            if (level.getBlockEntity(pos) instanceof LampBlockEntity data) {
                currentColor = data.getColor();
                currentTemperature = data.getTemperature();
            }
            final int color = currentColor;
            final int temperature = currentTemperature;
            final int brightness = state.getValue(LampBlock.BRIGHTNESS);
            final boolean powered = state.getValue(LampBlock.POWERED);
            final boolean modern = lamp.isModern();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> tr.alpegonya.gercekciisik.client.ClientScreenOpener.open(
                            pos, color, brightness, temperature, powered, modern));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
