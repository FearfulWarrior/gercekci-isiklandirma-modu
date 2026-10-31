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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class ConfigurableLightBlock extends DirectionalBlock implements EntityBlock {
    public static final IntegerProperty BRIGHTNESS=IntegerProperty.create("brightness",0,4);
    public static final BooleanProperty POWERED=BooleanProperty.create("powered");
    public static final EnumProperty<Direction.Axis> LINE_AXIS =
            EnumProperty.create("line_axis",Direction.Axis.class,Direction.Axis.X,Direction.Axis.Z);
    private static final int[] LIGHT_LEVELS={2,6,12,14,15};
    private static final int[] ROUND_WIDTHS={4,6,8,8,8,8,6,4};
    private static final VoxelShape ROUND_DOWN=makeRoundSpotShape(Direction.DOWN);
    private static final VoxelShape ROUND_UP=makeRoundSpotShape(Direction.UP);
    private static final VoxelShape ROUND_NORTH=makeRoundSpotShape(Direction.NORTH);
    private static final VoxelShape ROUND_SOUTH=makeRoundSpotShape(Direction.SOUTH);
    private static final VoxelShape ROUND_WEST=makeRoundSpotShape(Direction.WEST);
    private static final VoxelShape ROUND_EAST=makeRoundSpotShape(Direction.EAST);
    private final int paletteMode,defaultColor,stripKind,fixtureKind;

    public ConfigurableLightBlock(int paletteMode,int defaultColor,int defaultBrightness,int stripKind,
                                  int fixtureKind,BlockBehaviour.Properties properties) {
        super(properties.lightLevel(s->s.getValue(POWERED)?lightLevelForMode(s.getValue(BRIGHTNESS)):0));
        this.paletteMode=paletteMode;this.defaultColor=defaultColor;
        this.stripKind=stripKind;this.fixtureKind=fixtureKind;
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.DOWN)
                .setValue(LINE_AXIS,Direction.Axis.X)
                .setValue(BRIGHTNESS,Mth.clamp(defaultBrightness,0,4)).setValue(POWERED,true));
    }
    public static int lightLevelForMode(int mode){return LIGHT_LEVELS[Mth.clamp(mode,0,4)];}
    public int getPaletteMode(){return paletteMode;}
    public int getDefaultColor(){return defaultColor;}
    public boolean isStrip(){return stripKind!=0;}
    public boolean isVerticalStrip(){return stripKind==2;}
    public Direction.Axis getLongitudinalAxis(BlockState state){
        return stripKind==2?Direction.Axis.Y:stripKind==1?state.getValue(LINE_AXIS):state.getValue(FACING).getAxis();
    }
    public int colorFor(int color,int temperature,int rgb){
        if(paletteMode==2)return switch(Mth.clamp(color,0,2)){case 0->0xFFFFFF;case 1->0x9FC9FF;default->0xE7C982;};
        if(color==LampBlock.CUSTOM_RGB_COLOR)return rgb&0xFFFFFF;
        return LampBlock.rgb(color,true,temperature);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new AdjustableLightBlockEntity(pos,state);}

    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        Direction face=context.getClickedFace();
        BlockState state=defaultBlockState().setValue(FACING,face);
        if(stripKind==1){
            Direction.Axis axis;
            if(face.getAxis()==Direction.Axis.Y){
                axis=context.getHorizontalDirection().getAxis()==Direction.Axis.X?Direction.Axis.Z:Direction.Axis.X;
            }else{
                axis=face.getAxis()==Direction.Axis.X?Direction.Axis.Z:Direction.Axis.X;
            }
            state=state.setValue(LINE_AXIS,axis);
        }
        return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;
    }
    @Override public boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        Direction facing=state.getValue(FACING);
        BlockPos support=pos.relative(facing.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level,support,facing);
    }
    @Override public BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,
                                             LevelAccessor level,BlockPos pos,BlockPos neighborPos){
        if(!canSurvive(state,level,pos))return Blocks.AIR.defaultBlockState();
        return super.updateShape(state,direction,neighbor,level,pos,neighborPos);
    }
    @Override public VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext ctx){
        Direction facing=state.getValue(FACING);
        if(stripKind==1){
            Direction.Axis axis=state.getValue(LINE_AXIS);
            return switch(facing){
                case DOWN->axis==Direction.Axis.X?Shapes.box(0,14.0/16,7.0/16,1,1,9.0/16):Shapes.box(7.0/16,14.0/16,0,9.0/16,1,1);
                case UP->axis==Direction.Axis.X?Shapes.box(0,0,7.0/16,1,2.0/16,9.0/16):Shapes.box(7.0/16,0,0,9.0/16,2.0/16,1);
                case NORTH->Shapes.box(0,7.0/16,14.0/16,1,9.0/16,1);
                case SOUTH->Shapes.box(0,7.0/16,0,1,9.0/16,2.0/16);
                case WEST->Shapes.box(14.0/16,7.0/16,0,1,9.0/16,1);
                case EAST->Shapes.box(0,7.0/16,0,2.0/16,9.0/16,1);
            };
        }
        if(stripKind==2)return switch(facing){
            case DOWN,UP->Shapes.box(6.0/16,0,6.0/16,10.0/16,1,10.0/16);
            case NORTH->Shapes.box(6.0/16,0,14.0/16,10.0/16,1,1);
            case SOUTH->Shapes.box(6.0/16,0,0,10.0/16,1,2.0/16);
            case WEST->Shapes.box(14.0/16,0,6.0/16,1,1,10.0/16);
            case EAST->Shapes.box(0,0,6.0/16,2.0/16,1,10.0/16);
        };
        if(fixtureKind==2)return switch(facing){
            case DOWN->ROUND_DOWN;case UP->ROUND_UP;case NORTH->ROUND_NORTH;
            case SOUTH->ROUND_SOUTH;case WEST->ROUND_WEST;case EAST->ROUND_EAST;
        };
        if(fixtureKind==1)return switch(facing){
            case DOWN->Shapes.box(4.0/16,14.0/16,4.0/16,12.0/16,1,12.0/16);
            case UP->Shapes.box(4.0/16,0,4.0/16,12.0/16,2.0/16,12.0/16);
            case NORTH->Shapes.box(4.0/16,4.0/16,14.0/16,12.0/16,12.0/16,1);
            case SOUTH->Shapes.box(4.0/16,4.0/16,0,12.0/16,12.0/16,2.0/16);
            case WEST->Shapes.box(14.0/16,4.0/16,4.0/16,1,12.0/16,12.0/16);
            case EAST->Shapes.box(0,4.0/16,4.0/16,2.0/16,12.0/16,12.0/16);
        };
        double lo=2.0/16,hi=14.0/16,th=2.0/16;
        return switch(facing){
            case DOWN->Shapes.box(lo,1-th,lo,hi,1,hi);
            case UP->Shapes.box(lo,0,lo,hi,th,hi);
            case NORTH->Shapes.box(lo,lo,1-th,hi,hi,1);
            case SOUTH->Shapes.box(lo,lo,0,hi,hi,th);
            case WEST->Shapes.box(1-th,lo,lo,1,hi,hi);
            case EAST->Shapes.box(0,lo,lo,th,hi,hi);
        };
    }
    private static VoxelShape makeRoundSpotShape(Direction face){
        VoxelShape shape=Shapes.empty();
        for(int row=0;row<ROUND_WIDTHS.length;row++){
            int yOrZ=4+row,width=ROUND_WIDTHS[row],start=(16-width)/2;
            for(int c=start;c<start+width;c++){
                VoxelShape part=switch(face){
                    case DOWN->Shapes.box(c/16.0,14.0/16,yOrZ/16.0,(c+1)/16.0,1,(yOrZ+1)/16.0);
                    case UP->Shapes.box(c/16.0,0,yOrZ/16.0,(c+1)/16.0,2.0/16,(yOrZ+1)/16.0);
                    case NORTH->Shapes.box(c/16.0,yOrZ/16.0,14.0/16,(c+1)/16.0,(yOrZ+1)/16.0,1);
                    case SOUTH->Shapes.box(c/16.0,yOrZ/16.0,0,(c+1)/16.0,(yOrZ+1)/16.0,2.0/16);
                    case WEST->Shapes.box(14.0/16,yOrZ/16.0,c/16.0,1,(yOrZ+1)/16.0,(c+1)/16.0);
                    case EAST->Shapes.box(0,yOrZ/16.0,c/16.0,2.0/16,(yOrZ+1)/16.0,(c+1)/16.0);
                };
                shape=Shapes.or(shape,part);
            }
        }
        return shape;
    }
    @Override public VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext ctx){
        return stripKind!=0?Shapes.empty():getShape(state,level,pos,ctx);
    }
    @Override public BlockState rotate(BlockState state,Rotation rotation){
        BlockState rotated=state.setValue(FACING,rotation.rotate(state.getValue(FACING)));
        if(stripKind==1){
            Direction axisDirection=state.getValue(LINE_AXIS)==Direction.Axis.X?Direction.EAST:Direction.SOUTH;
            rotated=rotated.setValue(LINE_AXIS,rotation.rotate(axisDirection).getAxis());
        }
        return rotated;
    }
    @Override public BlockState mirror(BlockState state,Mirror mirror){return state.rotate(mirror.getRotation(state.getValue(FACING)));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block,BlockState>b){
        b.add(FACING,BRIGHTNESS,POWERED,LINE_AXIS);
    }
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
}
