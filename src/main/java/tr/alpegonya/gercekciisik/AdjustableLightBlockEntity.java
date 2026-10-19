package tr.alpegonya.gercekciisik;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class AdjustableLightBlockEntity extends BlockEntity {
    private int color=13, temperature=9, rgb=0xFFFFFF;
    public AdjustableLightBlockEntity(BlockPos pos,BlockState state){
        super(GercekciIsikMod.ADJUSTABLE_LIGHT_BLOCK_ENTITY.get(),pos,state);
        if(state.getBlock() instanceof ConfigurableLightBlock light){
            color=light.getDefaultColor();rgb=light.colorFor(color,temperature,0xFFFFFF);
        }
    }
    public int getColor(){return color;}
    public int getTemperature(){return temperature;}
    public int getRgb(){return rgb;}
    public boolean setConfiguration(int c,int t,int newRgb,int maxColor){
        int sc=Mth.clamp(c,0,maxColor),st=Mth.clamp(t,0,18),sr=newRgb&0xFFFFFF;
        if(color==sc&&temperature==st&&rgb==sr)return false;
        color=sc;temperature=st;rgb=sr;setChanged();return true;
    }
    @Override protected void saveAdditional(CompoundTag tag){
        super.saveAdditional(tag);tag.putInt("LightColor",color);tag.putInt("LightTemperature",temperature);tag.putInt("LightRGB",rgb);
    }
    @Override public void load(CompoundTag tag){
        super.load(tag);
        int max=16;
        if(getBlockState().getBlock() instanceof ConfigurableLightBlock light&&light.getPaletteMode()==2)max=2;
        color=Mth.clamp(tag.getInt("LightColor"),0,max);
        temperature=Mth.clamp(tag.getInt("LightTemperature"),0,18);
        if(tag.contains("LightRGB"))rgb=tag.getInt("LightRGB")&0xFFFFFF;
        else if(getBlockState().getBlock() instanceof ConfigurableLightBlock light)
            rgb=light.colorFor(color,temperature,LampBlock.rgb(color,true,temperature));
    }
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public void handleUpdateTag(CompoundTag tag){load(tag);}
    @Override public Packet<ClientGamePacketListener> getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
