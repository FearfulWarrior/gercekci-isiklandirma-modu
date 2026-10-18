package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ConfigurableLightBlock extends DirectionalBlock implements EntityBlock {
    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 0, 4);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    private static final int[] LIGHT_LEVELS = {1, 4, 10, 13, 15};

    private static final VoxelShape SLAB_BOTTOM = Shapes.box(0, 0, 0, 1, 2.0 / 16.0, 1);
    private static final VoxelShape SLAB_TOP = Shapes.box(0, 14.0 / 16.0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_NORTH = Shapes.box(0, 0, 14.0 / 16.0, 1, 1, 1);
    private static final VoxelShape SLAB_SOUTH = Shapes.box(0, 0, 0, 1, 1, 2.0 / 16.0);
    private static final VoxelShape SLAB_WEST = Shapes.box(14.0 / 16.0, 0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_EAST = Shapes.box(0, 0, 0, 2.0 / 16.0, 1, 1);

    private final int paletteMode;
    private final int defaultColor;
    // 0 = ordinary fixture, 1 = horizontal strip, 2 = vertical strip.
    private final int stripKind;

    public ConfigurableLightBlock(int paletteMode, int defaultColor, int defaultBrightness, int stripKind,
                                  BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state ->
                state.getValue(POWERED) ? lightLevelForMode(state.getValue(BRIGHTNESS)) : 0));
        this.paletteMode = paletteMode;
        this.defaultColor = defaultColor;
        this.stripKind = stripKind;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.DOWN)
                .setValue(BRIGHTNESS, Mth.clamp(defaultBrightness, 0, 4))
                .setValue(POWERED, true));
    }

    public static int lightLevelForMode(int mode) {
        return LIGHT_LEVELS[Mth.clamp(mode, 0, LIGHT_LEVELS.length - 1)];
    }

    public int getPaletteMode() { return paletteMode; }
    public int getDefaultColor() { return defaultColor; }
    public boolean isStrip() { return stripKind != 0; }
    public boolean isVerticalStrip() { return stripKind == 2; }

    public int colorFor(int color, int temperature) {
        if (paletteMode == 2) {
            return switch (Mth.clamp(color, 0, 2)) {
                case 0 -> 0xFFFFFF;
                case 1 -> 0x9FC9FF;
                default -> 0xE7C982;
            };
        }
        return LampBlock.rgb(color, true, temperature);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AdjustableLightBlockEntity(pos, state);
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
        if (!canSurvive(state, level, pos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (stripKind == 2) {
            // A vertical strip is a narrow full-height bar on walls or a slim pendant below/above surfaces.
            return switch (state.getValue(FACING)) {
                case DOWN, UP -> Shapes.box(7.0 / 16.0, 0, 7.0 / 16.0, 9.0 / 16.0, 1, 9.0 / 16.0);
                case NORTH -> Shapes.box(6.0 / 16.0, 0, 14.0 / 16.0, 10.0 / 16.0, 1, 1);
                case SOUTH -> Shapes.box(6.0 / 16.0, 0, 0, 10.0 / 16.0, 1, 2.0 / 16.0);
                case WEST -> Shapes.box(14.0 / 16.0, 0, 6.0 / 16.0, 1, 1, 10.0 / 16.0);
                case EAST -> Shapes.box(0, 0, 6.0 / 16.0, 2.0 / 16.0, 1, 10.0 / 16.0);
            };
        }
        return switch (state.getValue(FACING)) {
            case DOWN -> SLAB_TOP;
            case UP -> SLAB_BOTTOM;
            case NORTH -> SLAB_NORTH;
            case SOUTH -> SLAB_SOUTH;
            case WEST -> SLAB_WEST;
            case EAST -> SLAB_EAST;
        };
    }

    @Override public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    @Override public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, BRIGHTNESS, POWERED);
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
}
