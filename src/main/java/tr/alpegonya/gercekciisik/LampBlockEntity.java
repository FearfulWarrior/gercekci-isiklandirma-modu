package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;

public final class LampBlockEntity extends BlockEntity {
    private int color;
    private int temperature = 9;

    public LampBlockEntity(BlockPos pos, BlockState state) {
        super(GercekciIsikMod.LAMP_BLOCK_ENTITY.get(), pos, state);
        this.color = state.getBlock() instanceof LampBlock lamp && lamp.isModern() ? 13 : 0;
    }

    public int getColor() {
        return color;
    }

    public int getTemperature() {
        return temperature;
    }

    public boolean setColorAndTemperature(int newColor, int newTemperature) {
        boolean modern = getBlockState().getBlock() instanceof LampBlock lamp && lamp.isModern();
        int safeColor = Mth.clamp(newColor, 0, modern ? LampBlock.MODERN_KELVIN_COLOR : 2);
        int safeTemperature = Mth.clamp(newTemperature, 0, 18);
        if (color == safeColor && temperature == safeTemperature) {
            return false;
        }
        color = safeColor;
        temperature = safeTemperature;
        setChanged();
        return true;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("LampColor", color);
        tag.putInt("LampTemperature", temperature);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        boolean modern = getBlockState().getBlock() instanceof LampBlock lamp && lamp.isModern();
        color = Mth.clamp(tag.getInt("LampColor"), 0, modern ? LampBlock.MODERN_KELVIN_COLOR : 2);
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
