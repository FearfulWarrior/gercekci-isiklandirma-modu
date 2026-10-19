package tr.alpegonya.gercekciisik;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class LampBlockEntity extends BlockEntity {
    private int temperature=9, rgb=0xFFFFFF;
    public LampBlockEntity(BlockPos pos, BlockState state) {
        super(GercekciIsikMod.LAMP_BLOCK_ENTITY.get(), pos, state);
        int c=state.getValue(LampBlock.COLOR);
        rgb=LampBlock.rgb(c==LampBlock.CUSTOM_RGB_COLOR?13:c,true,temperature);
    }
    public int getTemperature(){return temperature;}
    public int getRgb(){return rgb;}
    public boolean setConfiguration(int t,int newRgb){
        int st=Mth.clamp(t,0,18), sr=newRgb&0xFFFFFF;
        if(temperature==st&&rgb==sr)return false;
        temperature=st;rgb=sr;setChanged();return true;
    }
    public boolean setTemperature(int t){
        int st=Mth.clamp(t,0,18);
        if(temperature==st)return false;
        temperature=st;setChanged();return true;
    }
    @Override protected void saveAdditional(CompoundTag tag){
        super.saveAdditional(tag);tag.putInt("LampTemperature",temperature);tag.putInt("LampRGB",rgb);
    }
    @Override public void load(CompoundTag tag){
        super.load(tag);temperature=Mth.clamp(tag.getInt("LampTemperature"),0,18);
        if(tag.contains("LampRGB"))rgb=tag.getInt("LampRGB")&0xFFFFFF;
        else {int c=getBlockState().getValue(LampBlock.COLOR);rgb=c==LampBlock.CUSTOM_RGB_COLOR?0xFFFFFF:LampBlock.rgb(c,true,temperature);}
    }
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public void handleUpdateTag(CompoundTag tag){load(tag);}
    @Override public Packet<ClientGamePacketListener> getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
