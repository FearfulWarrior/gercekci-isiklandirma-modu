package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GercekciIsikMod.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private ModNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(0, ConfigureLampPacket.class,
                ConfigureLampPacket::encode, ConfigureLampPacket::decode, ConfigureLampPacket::handle);
    }

    public static void sendConfig(BlockPos pos, int color, int brightnessMode, int temperature, boolean powered) {
        CHANNEL.sendToServer(new ConfigureLampPacket(pos, color, brightnessMode, temperature, powered));
    }

    public static final class ConfigureLampPacket {
        private final BlockPos pos;
        private final int color;
        private final int brightnessMode;
        private final int temperature;
        private final boolean powered;

        public ConfigureLampPacket(BlockPos pos, int color, int brightnessMode, int temperature, boolean powered) {
            this.pos = pos;
            this.color = color;
            this.brightnessMode = brightnessMode;
            this.temperature = temperature;
            this.powered = powered;
        }

        private static void encode(ConfigureLampPacket packet, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(packet.pos);
            buffer.writeVarInt(packet.color);
            buffer.writeVarInt(packet.brightnessMode);
            buffer.writeVarInt(packet.temperature);
            buffer.writeBoolean(packet.powered);
        }

        private static ConfigureLampPacket decode(FriendlyByteBuf buffer) {
            return new ConfigureLampPacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readVarInt(),
                    buffer.readVarInt(), buffer.readBoolean());
        }

        private static void handle(ConfigureLampPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null || !(player.level() instanceof ServerLevel level)) return;
                if (player.distanceToSqr(Vec3.atCenterOf(packet.pos)) > 64.0D) return;

                ItemStack main = player.getMainHandItem();
                ItemStack off = player.getOffhandItem();
                if (main.getItem() != GercekciIsikMod.LIGHT_CONTROL_REMOTE.get()
                        && off.getItem() != GercekciIsikMod.LIGHT_CONTROL_REMOTE.get()) return;

                BlockState state = level.getBlockState(packet.pos);
                if (!(state.getBlock() instanceof LampBlock lamp)) return;

                int maxColor = lamp.isModern() ? LampBlock.MODERN_KELVIN_COLOR : 2;
                int safeColor = Math.max(0, Math.min(maxColor, packet.color));
                int safeBrightnessMode = Math.max(0, Math.min(4, packet.brightnessMode));
                int safeTemperature = Math.max(0, Math.min(18, packet.temperature));
                BlockState configured = state.setValue(LampBlock.COLOR, safeColor)
                        .setValue(LampBlock.BRIGHTNESS, LampBlock.lightLevelForMode(safeBrightnessMode))
                        .setValue(LampBlock.POWERED, packet.powered);
                boolean stateChanged = !configured.equals(state);
                if (stateChanged) level.setBlock(packet.pos, configured, 3);

                if (level.getBlockEntity(packet.pos) instanceof LampBlockEntity data) {
                    boolean temperatureChanged = data.setTemperature(safeTemperature);
                    if (stateChanged || temperatureChanged) {
                        BlockState current = level.getBlockState(packet.pos);
                        // Broadcast temperature NBT and force a client-side tint rebuild.
                        level.sendBlockUpdated(packet.pos, current, current, 3);
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
