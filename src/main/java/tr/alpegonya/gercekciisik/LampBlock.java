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

public final class LampBlock extends DirectionalBlock implements EntityBlock {
    // Color is a BlockState property so selecting another hue rebuilds the model immediately.
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 15);
    // Five brightness modes: very low, low, medium, high, very high.
    public static final IntegerProperty BRIGHTNESS = IntegerProperty.create("brightness", 0, 15);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final int MODERN_COLOR_COUNT = 15;
    public static final int MODERN_KELVIN_COLOR = 15;
    private static final int[] LIGHT_LEVELS = {2, 4, 6, 8, 15};

    private static final VoxelShape SLAB_BOTTOM = Shapes.box(0, 0, 0, 1, 2.0 / 16.0, 1);
    private static final VoxelShape SLAB_TOP = Shapes.box(0, 14.0 / 16.0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_NORTH = Shapes.box(0, 0, 14.0 / 16.0, 1, 1, 1);
    private static final VoxelShape SLAB_SOUTH = Shapes.box(0, 0, 0, 1, 1, 2.0 / 16.0);
    private static final VoxelShape SLAB_WEST = Shapes.box(14.0 / 16.0, 0, 0, 1, 1, 1);
    private static final VoxelShape SLAB_EAST = Shapes.box(0, 0, 0, 2.0 / 16.0, 1, 1);

    private final boolean modern;

    public LampBlock(boolean modern, BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state ->
                state.getValue(POWERED) ? state.getValue(BRIGHTNESS) : 0));
        this.modern = modern;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.DOWN)
                .setValue(COLOR, modern ? 13 : 0)
                .setValue(BRIGHTNESS, modern ? 10 : 13)
                .setValue(POWERED, true));
    }

    public static int lightLevelForMode(int mode) {
        return LIGHT_LEVELS[Mth.clamp(mode, 0, LIGHT_LEVELS.length - 1)];
    }

    // Keeps the old 0..15 saved blockstate range compatible with worlds made by earlier builds.
    public static int modeForLightLevel(int level) {
        int safeLevel = Mth.clamp(level, 0, 15);
        int bestMode = 0;
        int bestDistance = Integer.MAX_VALUE;
        for (int mode = 0; mode < LIGHT_LEVELS.length; mode++) {
            int distance = Math.abs(LIGHT_LEVELS[mode] - safeLevel);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestMode = mode;
            }
        }
        return bestMode;
    }

    public boolean isModern() {
        return modern;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LampBlockEntity(pos, state);
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
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, COLOR, BRIGHTNESS, POWERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static int rgb(int color, boolean modern) {
        return rgb(color, modern, 9);
    }

    public static int rgb(int color, boolean modern, int temperatureIndex) {
        if (!modern) {
            return switch (Mth.clamp(color, 0, 2)) {
                case 0 -> 0x9FC9FF;
                case 1 -> 0xFFFFFF;
                default -> 0xE8D8FF;
            };
        }
        return switch (Mth.clamp(color, 0, MODERN_KELVIN_COLOR)) {
            case 0 -> 0xFF3030;
            case 1 -> 0xFF8C24;
            case 2 -> 0xFFFF36;
            case 3 -> 0x53F04D;
            case 4 -> 0x36E8D1;
            case 5 -> 0x87CEEB;
            case 6 -> 0x0000FF;
            case 7 -> 0x8847F4;
            case 8 -> 0xC154C1;
            case 9 -> 0xFFCBDB;
            case 10 -> 0xE8D8FF;
            case 11 -> 0xFFE0A3;
            case 12 -> 0xFFF1DF;
            case 13 -> 0xFFFFFF;
            case 14 -> 0xC8E8FF;
            default -> fromKelvin(1000 + Mth.clamp(temperatureIndex, 0, 18) * 500);
        };
    }

    private static int fromKelvin(int kelvin) {
        double temp = Mth.clamp(kelvin, 1000, 10000) / 100.0;
        double red = temp <= 66 ? 255.0 : 329.698727446 * Math.pow(temp - 60, -0.1332047592);
        double green = temp <= 66
                ? 99.4708025861 * Math.log(temp) - 161.1195681661
                : 288.1221695283 * Math.pow(temp - 60, -0.0755148492);
        double blue = temp >= 66 ? 255.0
                : temp <= 19 ? 0.0
                : 138.5177312231 * Math.log(temp - 10) - 305.0447927307;
        int r = Mth.clamp((int) Math.round(red), 0, 255);
        int g = Mth.clamp((int) Math.round(green), 0, 255);
        int b = Mth.clamp((int) Math.round(blue), 0, 255);
        return (r << 16) | (g << 8) | b;
    }
}
