package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public final class BrightnessModeButton extends AbstractWidget {
    private final int mode;
    private final String caption;
    private final IntSupplier selectedMode;
    private final IntConsumer onSelect;

    public BrightnessModeButton(int x, int y, int width, int height, int mode, String caption,
                                IntSupplier selectedMode, IntConsumer onSelect, String tooltip) {
        super(x, y, width, height, Component.literal(caption));
        this.mode = mode;
        this.caption = caption;
        this.selectedMode = selectedMode;
        this.onSelect = onSelect;
        setTooltip(Tooltip.create(Component.literal(tooltip)));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean selected = selectedMode.getAsInt() == mode;
        int x = getX();
        int y = getY();
        int border = selected ? 0xFFFFD45A : (isHoveredOrFocused() ? 0xFFE9F2FF : 0xFF30363B);
        graphics.fill(x, y, x + width, y + height, border);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1,
                selected ? 0xFF4C4530 : 0xFF70777C);
        graphics.drawCenteredString(Minecraft.getInstance().font, caption, x + width / 2,
                y + (height - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onSelect.accept(mode);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
