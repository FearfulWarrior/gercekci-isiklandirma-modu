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
    private int color = 13;
    private int temperature = 9;

    public AdjustableLightBlockEntity(BlockPos pos, BlockState state) {
        super(GercekciIsikMod.ADJUSTABLE_LIGHT_BLOCK_ENTITY.get(), pos, state);
        if (state.getBlock() instanceof ConfigurableLightBlock light) color = light.getDefaultColor();
        else if (state.getBlock() instanceof LedStairBlock) color = 11;
    }

    public int getColor() { return color; }
    public int getTemperature() { return temperature; }

    public boolean setConfiguration(int newColor, int newTemperature, int maxColor) {
        int safeColor = Mth.clamp(newColor, 0, maxColor);
        int safeTemperature = Mth.clamp(newTemperature, 0, 18);
        if (color == safeColor && temperature == safeTemperature) return false;
        color = safeColor;
        temperature = safeTemperature;
        setChanged();
        return true;
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("LightColor", color);
        tag.putInt("LightTemperature", temperature);
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        int maxColor = 15;
        if (getBlockState().getBlock() instanceof ConfigurableLightBlock light && light.getPaletteMode() == 2) {
            maxColor = 2;
        }
        color = Mth.clamp(tag.getInt("LightColor"), 0, maxColor);
        temperature = Mth.clamp(tag.getInt("LightTemperature"), 0, 18);
    }

    @Override public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    @Override public void handleUpdateTag(CompoundTag tag) { load(tag); }
    @Override public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
}
