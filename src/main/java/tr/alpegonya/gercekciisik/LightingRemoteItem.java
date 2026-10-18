package tr.alpegonya.gercekciisik;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public final class LightingRemoteItem extends Item {
    public LightingRemoteItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        int color;
        int temperature = 9;
        int brightness;
        boolean powered;
        int paletteMode;
        String title;

        if (state.getBlock() instanceof LampBlock lamp) {
            color = state.getValue(LampBlock.COLOR);
            if (level.getBlockEntity(pos) instanceof LampBlockEntity data) temperature = data.getTemperature();
            brightness = LampBlock.modeForLightLevel(state.getValue(LampBlock.BRIGHTNESS));
            powered = state.getValue(LampBlock.POWERED);
            paletteMode = lamp.isModern() ? 1 : 0;
            title = lamp.isModern() ? "Modern Floresan Kontrolü" : "Geleneksel Floresan Kontrolü";
        } else if (state.getBlock() instanceof ConfigurableLightBlock light) {
            if (level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity data) {
                color = data.getColor();
                temperature = data.getTemperature();
            } else color = light.getDefaultColor();
            brightness = state.getValue(ConfigurableLightBlock.BRIGHTNESS);
            powered = state.getValue(ConfigurableLightBlock.POWERED);
            paletteMode = light.getPaletteMode();
            title = titleFor(light);
        } else if (state.getBlock() instanceof LedStairBlock stairs) {
            if (level.getBlockEntity(pos) instanceof AdjustableLightBlockEntity data) {
                color = data.getColor();
                temperature = data.getTemperature();
            } else color = stairs.getDefaultColor();
            brightness = state.getValue(LedStairBlock.BRIGHTNESS);
            powered = state.getValue(LedStairBlock.POWERED);
            paletteMode = 3;
            title = "LED'li Merdiven Kontrolü";
        } else return InteractionResult.PASS;

        if (level.isClientSide) {
            final int selectedColor = color;
            final int selectedTemperature = temperature;
            final int selectedBrightness = brightness;
            final boolean selectedPowered = powered;
            final int selectedPaletteMode = paletteMode;
            final String selectedTitle = title;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> tr.alpegonya.gercekciisik.client.ClientScreenOpener.open(
                            pos, selectedColor, selectedBrightness, selectedTemperature,
                            selectedPowered, selectedPaletteMode, selectedTitle));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static String titleFor(ConfigurableLightBlock light) {
        if (light.isVerticalStrip()) return "Dikey Şerit LED Kontrolü";
        if (light.isStrip()) return "Yatay Şerit LED Kontrolü";
        return switch (light.getPaletteMode()) {
            case 2 -> "Geleneksel Simit Armatür Kontrolü";
            default -> "Akıllı Işık Kontrolü";
        };
    }
}
