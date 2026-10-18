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
            int color = state.getValue(LampBlock.COLOR);
            int brightness = state.getValue(LampBlock.BRIGHTNESS);
            int temperature = state.getValue(LampBlock.TEMPERATURE);
            boolean powered = state.getValue(LampBlock.POWERED);
            boolean modern = lamp.isModern();
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> tr.alpegonya.gercekciisik.client.ClientScreenOpener.open(
                            pos, color, brightness, temperature, powered, modern));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
