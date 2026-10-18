package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class LedStairBlock extends StairBlock implements net.minecraft.world.level.block.EntityBlock {
    // Shares properties with other adjustable lights for a consistent remote-control protocol.
    public static final net.minecraft.world.level.block.state.properties.IntegerProperty BRIGHTNESS =
            ConfigurableLightBlock.BRIGHTNESS;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty POWERED =
            ConfigurableLightBlock.POWERED;

    public LedStairBlock(BlockBehaviour.Properties properties) {
        super(Blocks.OAK_PLANKS.defaultBlockState(),
                properties.lightLevel(state -> state.getValue(POWERED)
                        ? ConfigurableLightBlock.lightLevelForMode(state.getValue(BRIGHTNESS)) : 0));
        registerDefaultState(defaultBlockState().setValue(BRIGHTNESS, 2).setValue(POWERED, true));
    }

    public int getDefaultColor() { return 11; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdjustableLightBlockEntity(pos, state);
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BRIGHTNESS, POWERED);
    }
}
