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
    private int temperature = 9;

    public LampBlockEntity(BlockPos pos, BlockState state) {
        super(GercekciIsikMod.LAMP_BLOCK_ENTITY.get(), pos, state);
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean setTemperature(int value) {
        int safe = Mth.clamp(value, 0, 18);
        if (temperature == safe) return false;
        temperature = safe;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("LampTemperature", temperature);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        temperature = Mth.clamp(tag.getInt("LampTemperature"), 0, 18);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
