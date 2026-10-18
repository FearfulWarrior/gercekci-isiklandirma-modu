package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class LampBlock extends DirectionalBlock {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 13);
    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 0, 15);
    public static final int MODERN_COLOR_COUNT = 14;

    private static final VoxelShape SLAB_BOTTOM = Shapes.box(0, 0, 0, 1, 2.0 / 16.0, 1);
    private static final VoxelShape SLAB_TOP = Shapes.box(0, 14.0 / 16.0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_NORTH = Shapes.box(0, 0, 14.0 / 16.0, 1, 1, 1);
    private static final VoxelShape SLAB_SOUTH = Shapes.box(0, 0, 0, 1, 1, 2.0 / 16.0);
    private static final VoxelShape SLAB_WEST = Shapes.box(14.0 / 16.0, 0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_EAST = Shapes.box(0, 0, 0, 2.0 / 16.0, 1, 1);

    private final boolean modern;

    public LampBlock(boolean modern, BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> state.getValue(BRIGHTNESS)));
        this.modern = modern;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.DOWN)
                .setValue(COLOR, modern ? 12 : 0)
                .setValue(BRIGHTNESS, 15));
    }

    public boolean isModern() {
        return modern;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> SLAB_TOP;
            case UP -> SLAB_BOTTOM;
            case NORTH -> SLAB_NORTH;
            case SOUTH -> SLAB_SOUTH;
            case WEST -> SLAB_WEST;
            case EAST -> SLAB_EAST;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COLOR, BRIGHTNESS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static int rgb(int color, boolean modern) {
        if (!modern) {
            return switch (Mth.clamp(color, 0, 2)) {
                case 0 -> 0xD6E4FF;
                case 1 -> 0xFFFFFF;
                default -> 0xE2D0FF;
            };
        }
        return switch (Mth.clamp(color, 0, 13)) {
            case 0 -> 0xFF3030;
            case 1 -> 0xFF8C24;
            case 2 -> 0xFFFF36;
            case 3 -> 0x53F04D;
            case 4 -> 0x36E8D1;
            case 5 -> 0x87DFFF;
            case 6 -> 0x406DFF;
            case 7 -> 0x8847F4;
            case 8 -> 0xD79AFF;
            case 9 -> 0xFF78C7;
            case 10 -> 0xE9D7FF;
            case 11 -> 0xFFE0A3;
            case 12 -> 0xFFFFFF;
            default -> 0xC8E8FF;
        };
    }
}
