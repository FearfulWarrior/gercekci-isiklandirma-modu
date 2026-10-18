package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                if (state.getBlock() instanceof LampBlock) {
                    configureFluorescent(level, packet.pos, packet);
                } else if (state.getBlock() instanceof ConfigurableLightBlock light) {
                    List<BlockPos> targets = light.isStrip()
                            ? collectStripNetwork(level, packet.pos, light) : List.of(packet.pos);
                    for (BlockPos target : targets) configureAdjustable(level, target, packet);
                } else if (state.getBlock() instanceof LedStairBlock) {
                    configureStair(level, packet.pos, packet);
                }
            });
            context.setPacketHandled(true);
        }
    }

    private static void configureFluorescent(ServerLevel level, BlockPos pos, ConfigureLampPacket packet) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof LampBlock lamp)) return;
        int maxColor = lamp.isModern() ? LampBlock.MODERN_KELVIN_COLOR : 2;
        int safeColor = Math.max(0, Math.min(maxColor, packet.color));
        int safeMode = Math.max(0, Math.min(4, packet.brightnessMode));
        int safeTemperature = Math.max(0, Math.min(18, packet.temperature));
        BlockState configured = state.setValue(LampBlock.COLOR, safeColor)
                .setValue(LampBlock.BRIGHTNESS, LampBlock.lightLevelForMode(safeMode))
                .setValue(LampBlock.POWERED, packet.powered);
        boolean stateChanged = !configured.equals(state);
        if (stateChanged) level.setBlock(pos, configured, 3);
        if (level.getBlockEntity(pos) instanceof LampBlockEntity data) {
            boolean temperatureChanged = data.setTemperature(safeTemperature);
            if (stateChanged || temperatureChanged) {
                BlockState current = level.getBlockState(pos);
                level.sendBlockUpdated(pos, current, current, 3);
            }
        }
    }

    private static void configureAdjustable(ServerLevel level, BlockPos pos, ConfigureLampPacket packet) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ConfigurableLightBlock light)) return;
        int maxColor = light.getPaletteMode() == 2 ? 2 : 15;
        BlockState configured = state.setValue(ConfigurableLightBlock.BRIGHTNESS,
                        Math.max(0, Math.min(4, packet.brightnessMode)))
                .setValue(ConfigurableLightBlock.POWERED, packet.powered);
        boolean stateChanged = !configured.equals(state);
        if (stateChanged) level.setBlock(pos, configured, 3);
        if (level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity data) {
            boolean entityChanged = data.setConfiguration(packet.color, packet.temperature, maxColor);
            if (stateChanged || entityChanged) {
                BlockState current = level.getBlockState(pos);
                level.sendBlockUpdated(pos, current, current, 3);
            }
        }
    }

    private static void configureStair(ServerLevel level, BlockPos pos, ConfigureLampPacket packet) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof LedStairBlock)) return;
        BlockState configured = state.setValue(LedStairBlock.BRIGHTNESS,
                        Math.max(0, Math.min(4, packet.brightnessMode)))
                .setValue(LedStairBlock.POWERED, packet.powered);
        boolean stateChanged = !configured.equals(state);
        if (stateChanged) level.setBlock(pos, configured, 3);
        if (level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity data) {
            boolean entityChanged = data.setConfiguration(packet.color, packet.temperature, 15);
            if (stateChanged || entityChanged) {
                BlockState current = level.getBlockState(pos);
                level.sendBlockUpdated(pos, current, current, 3);
            }
        }
    }

    private static List<BlockPos> collectStripNetwork(ServerLevel level, BlockPos start, ConfigurableLightBlock source) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        List<BlockPos> result = new ArrayList<>();
        queue.add(start);
        seen.add(start);
        BlockState startState = level.getBlockState(start);
        Direction first;
        Direction second;
        if (source.isVerticalStrip()) {
            first = Direction.UP;
            second = Direction.DOWN;
        } else {
            Direction.Axis axis = startState.getValue(ConfigurableLightBlock.FACING).getAxis()
                    == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
            first = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
            second = first.getOpposite();
        }

        while (!queue.isEmpty() && seen.size() <= 2048) {
            BlockPos current = queue.removeFirst();
            result.add(current);
            for (Direction direction : new Direction[]{first, second}) {
                BlockPos neighbor = current.relative(direction);
                if (!seen.add(neighbor)) continue;
                BlockState nextState = level.getBlockState(neighbor);
                if (nextState.getBlock() == source
                        && nextState.getValue(ConfigurableLightBlock.FACING)
                        == startState.getValue(ConfigurableLightBlock.FACING)) {
                    queue.addLast(neighbor);
                }
            }
        }
        return result;
    }
}
