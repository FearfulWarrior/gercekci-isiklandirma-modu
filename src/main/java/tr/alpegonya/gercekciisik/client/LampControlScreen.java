package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.ModNetwork;

public final class LampControlScreen extends Screen {
    private static final String[] MODERN_COLORS = {
            "Kırmızı", "Turuncu", "Sarı", "Yeşil",
            "Turkuaz", "Açık mavi", "Mavi", "Mor",
            "Eflatun", "Pembe", "Mor-beyaz", "Sıcak ışık",
            "Beyaz", "Soğuk beyaz"
    };
    private static final String[] CLASSIC_COLORS = {
            "Soğuk mavi-beyaz", "Beyaz", "Mor-beyaz"
    };

    private final BlockPos pos;
    private final boolean modern;
    private int color;
    private int brightness;

    public LampControlScreen(BlockPos pos, int color, int brightness, boolean modern) {
        super(Component.literal(modern ? "Modern Floresan Kontrolü" : "Geleneksel Floresan Kontrolü"));
        this.pos = pos;
        this.color = color;
        this.brightness = brightness;
        this.modern = modern;
    }

    @Override
    protected void init() {
        int columns = modern ? 4 : 3;
        int buttonWidth = modern ? 68 : 88;
        int gap = 4;
        int gridWidth = columns * buttonWidth + (columns - 1) * gap;
        int startX = (width - gridWidth) / 2;
        int startY = 40;
        int total = modern ? MODERN_COLORS.length : CLASSIC_COLORS.length;

        for (int i = 0; i < total; i++) {
            final int selected = i;
            int col = i % columns;
            int row = i / columns;
            String label = modern ? MODERN_COLORS[i] : CLASSIC_COLORS[i];
            addRenderableWidget(Button.builder(Component.literal(label), button -> selectColor(selected))
                    .bounds(startX + col * (buttonWidth + gap), startY + row * 23, buttonWidth, 20)
                    .build());
        }

        int rows = modern ? 4 : 1;
        int controlsY = startY + rows * 23 + 8;
        int center = width / 2;
        addRenderableWidget(Button.builder(Component.literal("−"), button -> changeBrightness(-1))
                .bounds(center - 76, controlsY + 18, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> changeBrightness(1))
                .bounds(center + 38, controlsY + 18, 38, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Kapat"), button -> onClose())
                .bounds(center - 40, height - 27, 80, 20).build());
    }

    private void selectColor(int selected) {
        int max = modern ? LampBlock.MODERN_COLOR_COUNT - 1 : 2;
        color = Math.max(0, Math.min(max, selected));
        ModNetwork.sendConfig(pos, color, brightness);
    }

    private void changeBrightness(int amount) {
        brightness = Math.max(0, Math.min(15, brightness + amount));
        ModNetwork.sendConfig(pos, color, brightness);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal(modern
                ? "Renk ve beyaz tonu seç"
                : "Üç sabit renk seçeneği"), width / 2, 27, 0xC8D5E5);

        int rows = modern ? 4 : 1;
        int controlsY = 40 + rows * 23 + 8;
        graphics.drawCenteredString(font,
                Component.literal("Parlaklık: " + brightness + " / 15" + (brightness == 0 ? " (Kapalı)" : "")),
                width / 2, controlsY + 3, 0xFFFFFF);
        graphics.drawCenteredString(font, Component.literal("Işık Kontrol Kumandası"),
                width / 2, height - 42, 0x9EB8D2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
