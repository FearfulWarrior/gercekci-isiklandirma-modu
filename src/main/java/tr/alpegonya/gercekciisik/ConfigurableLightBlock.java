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
    public static final IntegerProperty BRIGHTNESS=IntegerProperty.create("brightness",0,4);
    public static final BooleanProperty POWERED=BooleanProperty.create("powered");
    private static final int[] LIGHT_LEVELS={1,4,10,13,15};
    private final int paletteMode,defaultColor,stripKind;
    public ConfigurableLightBlock(int paletteMode,int defaultColor,int defaultBrightness,int stripKind,BlockBehaviour.Properties properties){
        super(properties.lightLevel(s->s.getValue(POWERED)?lightLevelForMode(s.getValue(BRIGHTNESS)):0));
        this.paletteMode=paletteMode;this.defaultColor=defaultColor;this.stripKind=stripKind;
        registerDefaultState(stateDefinition.any().setValue(FACING,Direction.DOWN)
                .setValue(BRIGHTNESS,Mth.clamp(defaultBrightness,0,4)).setValue(POWERED,true));
    }
    public static int lightLevelForMode(int mode){return LIGHT_LEVELS[Mth.clamp(mode,0,4)];}
    public int getPaletteMode(){return paletteMode;}
    public int getDefaultColor(){return defaultColor;}
    public boolean isStrip(){return stripKind!=0;}
    public boolean isVerticalStrip(){return stripKind==2;}
    public int colorFor(int color,int temperature,int rgb){
        if(paletteMode==2)return switch(Mth.clamp(color,0,2)){case 0->0xFFFFFF;case 1->0x9FC9FF;default->0xE7C982;};
        if(color==LampBlock.CUSTOM_RGB_COLOR)return rgb&0xFFFFFF;
        return LampBlock.rgb(color,true,temperature);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new AdjustableLightBlockEntity(pos,state);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){
        BlockState state=defaultBlockState().setValue(FACING,context.getClickedFace());
        return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;
    }
    @Override public boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){
        Direction facing=state.getValue(FACING);BlockPos support=pos.relative(facing.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level,support,facing);
    }
    @Override public BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor level,BlockPos pos,BlockPos neighborPos){
        if(!canSurvive(state,level,pos))return Blocks.AIR.defaultBlockState();
        return super.updateShape(state,direction,neighbor,level,pos,neighborPos);
    }
    @Override public VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext ctx){
        Direction f=state.getValue(FACING);
        if(stripKind==1)return switch(f){
            case DOWN->Shapes.box(0,14.0/16,7.0/16,1,15.6/16,9.0/16);
            case UP->Shapes.box(0,0.4/16,7.0/16,1,2.0/16,9.0/16);
            case NORTH->Shapes.box(0,7.0/16,14.0/16,1,9.0/16,1);
            case SOUTH->Shapes.box(0,7.0/16,0,1,9.0/16,2.0/16);
            case WEST->Shapes.box(14.0/16,7.0/16,0,1,9.0/16,1);
            case EAST->Shapes.box(0,7.0/16,0,2.0/16,9.0/16,1);
        };
        if(stripKind==2)return switch(f){
            case DOWN,UP->Shapes.box(6.0/16,0,6.0/16,10.0/16,1,10.0/16);
            case NORTH->Shapes.box(6.0/16,0,14.0/16,10.0/16,1,1);
            case SOUTH->Shapes.box(6.0/16,0,0,10.0/16,1,2.0/16);
            case WEST->Shapes.box(14.0/16,0,6.0/16,1,1,10.0/16);
            case EAST->Shapes.box(0,0,6.0/16,2.0/16,1,10.0/16);
        };
        double lo=2.0/16,hi=14.0/16,th=2.0/16;
        return switch(f){
            case DOWN->Shapes.box(lo,1-th,lo,hi,1,hi);
            case UP->Shapes.box(lo,0,lo,hi,th,hi);
            case NORTH->Shapes.box(lo,lo,1-th,hi,hi,1);
            case SOUTH->Shapes.box(lo,lo,0,hi,hi,th);
            case WEST->Shapes.box(1-th,lo,lo,1,hi,hi);
            case EAST->Shapes.box(0,lo,lo,th,hi,hi);
        };
    }
    @Override public BlockState rotate(BlockState s,Rotation r){return s.setValue(FACING,r.rotate(s.getValue(FACING)));}
    @Override public BlockState mirror(BlockState s,Mirror m){return s.rotate(m.getRotation(s.getValue(FACING)));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block,BlockState>b){b.add(FACING,BRIGHTNESS,POWERED);}
    @Override public RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}
}
