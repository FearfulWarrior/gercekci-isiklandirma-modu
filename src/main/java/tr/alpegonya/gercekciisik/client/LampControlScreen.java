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
    private static final int[] KELVIN_SWATCHES = {
            0xFF3000, 0xFF5B12, 0xFF8D32, 0xFFB86B,
            0xFFE0B8, 0xFFF4E7, 0xFFFFFF, 0xE8F3FF,
            0xC4E1FF, 0x93C9FF, 0x4C91FF
    };

    private final BlockPos pos;
    private final boolean modern;
    private int color;
    private int brightness;
    private int temperature;

    public LampControlScreen(BlockPos pos, int color, int brightness, int temperature, boolean modern) {
        super(Component.literal(modern ? "Modern Floresan Kontrolü" : "Geleneksel Floresan Kontrolü"));
        this.pos = pos;
        this.color = color;
        this.brightness = brightness;
        this.temperature = temperature;
        this.modern = modern;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int startY;
        if (modern) {
            int columns = 7;
            int buttonWidth = 55;
            int gap = 4;
            int gridWidth = columns * buttonWidth + (columns - 1) * gap;
            int startX = center - gridWidth / 2;
            startY = 39;
            for (int i = 0; i < MODERN_COLORS.length; i++) {
                final int selected = i;
                int col = i % columns;
                int row = i / columns;
                String label = MODERN_COLORS[i];
                addRenderableWidget(Button.builder(Component.literal(label), button -> selectColor(selected))
                        .bounds(startX + col * (buttonWidth + gap), startY + row * 23, buttonWidth, 20)
                        .build());
            }

            addRenderableWidget(Button.builder(Component.literal("− 500 K"), button -> changeTemperature(-1))
                    .bounds(center - 126, 126, 78, 20).build());
            addRenderableWidget(Button.builder(Component.literal("+ 500 K"), button -> changeTemperature(1))
                    .bounds(center + 48, 126, 78, 20).build());

            addRenderableWidget(Button.builder(Component.literal("−"), button -> changeBrightness(-1))
                    .bounds(center - 82, 177, 38, 20).build());
            addRenderableWidget(Button.builder(Component.literal("+"), button -> changeBrightness(1))
                    .bounds(center + 44, 177, 38, 20).build());
        } else {
            int buttonWidth = 124;
            int gap = 8;
            int gridWidth = buttonWidth * 3 + gap * 2;
            int startX = center - gridWidth / 2;
            startY = 53;
            for (int i = 0; i < CLASSIC_COLORS.length; i++) {
                final int selected = i;
                addRenderableWidget(Button.builder(Component.literal(CLASSIC_COLORS[i]),
                                button -> selectColor(selected))
                        .bounds(startX + i * (buttonWidth + gap), startY, buttonWidth, 22).build());
            }
            addRenderableWidget(Button.builder(Component.literal("−"), button -> changeBrightness(-1))
                    .bounds(center - 82, 142, 38, 20).build());
            addRenderableWidget(Button.builder(Component.literal("+"), button -> changeBrightness(1))
                    .bounds(center + 44, 142, 38, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Kapat"), button -> onClose())
                .bounds(center - 40, height - 27, 80, 20).build());
    }

    private void selectColor(int selected) {
        int max = modern ? LampBlock.MODERN_COLOR_COUNT - 1 : 2;
        color = Math.max(0, Math.min(max, selected));
        if (modern && selected == 11) temperature = 4;  // 3000K warm light
        if (modern && selected == 12) temperature = 9;  // 5500K neutral white
        if (modern && selected == 13) temperature = 13; // 7500K cool white
        send();
    }

    private void changeTemperature(int steps) {
        temperature = Math.max(0, Math.min(18, temperature + steps));
        // Kelvin adjustment switches to the white-light mode and uses the black-body tint.
        color = 12;
        send();
    }

    private void changeBrightness(int amount) {
        brightness = Math.max(0, Math.min(15, brightness + amount));
        send();
    }

    private void send() {
        ModNetwork.sendConfig(pos, color, brightness, temperature);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 10, 0xFFFFFF);
        if (modern) {
            graphics.drawCenteredString(font, Component.literal("Renk seçimi"), width / 2, 26, 0xD5DDE8);

            graphics.drawCenteredString(font,
                    Component.literal("Renk sıcaklığı: " + (1000 + temperature * 500) + " K"),
                    width / 2, 91, 0xFFFFFF);

            int barWidth = Math.min(300, width - 80);
            int barX = (width - barWidth) / 2;
            int barY = 105;
            for (int x = 0; x < barWidth; x++) {
                int color = gradientColor(barWidth <= 1 ? 0 : (float) x / (barWidth - 1));
                graphics.fill(barX + x, barY, barX + x + 1, barY + 9, 0xFF000000 | color);
            }
            graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY, 0xFFFFFFFF);
            graphics.fill(barX - 1, barY + 9, barX + barWidth + 1, barY + 10, 0xFF4A4A4A);
            graphics.drawString(font, Component.literal("1000 K"), barX, 116, 0xD7D7D7);
            graphics.drawString(font, Component.literal("10000 K"),
                    barX + barWidth - font.width("10000 K"), 116, 0xD7D7D7);

            graphics.drawCenteredString(font,
                    Component.literal("Parlaklık: " + brightness + " / 15" + (brightness == 0 ? " (Kapalı)" : "")),
                    width / 2, 158, 0xFFFFFF);
        } else {
            graphics.drawCenteredString(font, Component.literal("Üç sabit renk seçeneği"),
                    width / 2, 31, 0xC8D5E5);
            graphics.drawCenteredString(font,
                    Component.literal("Parlaklık: " + brightness + " / 15" + (brightness == 0 ? " (Kapalı)" : "")),
                    width / 2, 120, 0xFFFFFF);
        }
        graphics.drawCenteredString(font, Component.literal("Işık Kontrol Kumandası"),
                width / 2, height - 42, 0x9EB8D2);
    }

    private static int gradientColor(float t) {
        float scaled = t * (KELVIN_SWATCHES.length - 1);
        int index = Math.min(KELVIN_SWATCHES.length - 2, (int) scaled);
        float frac = scaled - index;
        int a = KELVIN_SWATCHES[index];
        int b = KELVIN_SWATCHES[index + 1];
        int r = Math.round(((a >> 16) & 255) * (1 - frac) + ((b >> 16) & 255) * frac);
        int g = Math.round(((a >> 8) & 255) * (1 - frac) + ((b >> 8) & 255) * frac);
        int bl = Math.round((a & 255) * (1 - frac) + (b & 255) * frac);
        return (r << 16) | (g << 8) | bl;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
