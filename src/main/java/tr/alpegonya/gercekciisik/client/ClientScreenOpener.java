package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientScreenOpener {
    private ClientScreenOpener() {}

    public static void open(BlockPos pos, int color, int brightness, boolean modern) {
        Minecraft.getInstance().setScreen(new LampControlScreen(pos, color, brightness, modern));
    }
}
