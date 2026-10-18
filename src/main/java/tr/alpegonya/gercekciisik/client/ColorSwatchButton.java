package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public final class ColorSwatchButton extends AbstractWidget {
    private final int index;
    private final int rgb;
    private final IntSupplier selectedIndex;
    private final IntConsumer onSelect;

    public ColorSwatchButton(int x, int y, int width, int height, int index, int rgb,
                             IntSupplier selectedIndex, IntConsumer onSelect, Component tooltip) {
        super(x, y, width, height, Component.empty());
        this.index = index;
        this.rgb = rgb & 0xFFFFFF;
        this.selectedIndex = selectedIndex;
        this.onSelect = onSelect;
        setTooltip(Tooltip.create(tooltip));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int outer = selectedIndex.getAsInt() == index ? 0xFFFFD45A
                : (isHoveredOrFocused() ? 0xFFE9F2FF : 0xFF30363B);
        graphics.fill(x, y, x + width, y + height, outer);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF7F878C);
        graphics.fill(x + 3, y + 3, x + width - 3, y + height - 3, 0xFF000000 | rgb);
        if (isHoveredOrFocused()) {
            graphics.fill(x + 4, y + 4, x + width - 4, y + 5, 0x33FFFFFF);
        }
    }

    @Override
    protected void onClick(double mouseX, double mouseY) {
        onSelect.accept(index);
    }
}
