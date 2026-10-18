package tr.alpegonya.gercekciisik.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tr.alpegonya.gercekciisik.LampBlock;
import tr.alpegonya.gercekciisik.ModNetwork;

public final class LampControlScreen extends Screen {
    private static final ResourceLocation KELVIN_GRADIENT =
            new ResourceLocation("gercekciisik", "textures/gui/kelvin_gradient.png");

    private static final String[] MODERN_COLOR_NAMES = {
            "Kırmızı", "Turuncu", "Sarı", "Yeşil", "Turkuaz",
            "Açık mavi", "Mavi", "Mor", "Eflatun", "Pembe",
            "Mor-beyaz", "Sıcak beyaz", "Günışığı", "Beyaz", "Soğuk beyaz"
    };
    private static final int[] MODERN_COLOR_RGB = {
            0xFF3030, 0xFF8C24, 0xFFFF36, 0x53F04D, 0x36E8D1,
            0x87CEEB, 0x0000FF, 0x8847F4, 0xC154C1, 0xFFCBDB,
            0xE8D8FF, 0xFFE0A3, 0xFFF1DF, 0xFFFFFF, 0xC8E8FF
    };
    private static final String[] CLASSIC_COLOR_NAMES = {
            "Soğuk mavi-beyaz", "Beyaz", "Mor-beyaz"
    };
    private static final int[] CLASSIC_COLOR_RGB = {0xD6E8FF, 0xFFFFFF, 0xE8D8FF};

    private final BlockPos pos;
    private final boolean modern;
    private int color;
    private int brightness;
    private int temperature;
    private boolean powered;
    private Button powerButton;

    public LampControlScreen(BlockPos pos, int color, int brightness, int temperature,
                             boolean powered, boolean modern) {
        super(Component.literal(modern ? "Modern Floresan Kontrolü" : "Geleneksel Floresan Kontrolü"));
        this.pos = pos;
        this.color = color;
        this.brightness = brightness;
        this.temperature = temperature;
        this.powered = powered;
        this.modern = modern;
    }

    private int verticalOffset() {
        return Math.max(0, (height - 330) / 2);
    }

    @Override
    protected void init() {
        int center = width / 2;
        int offset = verticalOffset();

        if (modern) {
            int columns = 5;
            int buttonWidth = 30;
            int buttonHeight = 21;
            int gapX = 4;
            int gapY = 3;
            int totalWidth = columns * buttonWidth + (columns - 1) * gapX;
            int startX = center - totalWidth / 2;
            int startY = 31 + offset;
            for (int i = 0; i < MODERN_COLOR_NAMES.length; i++) {
                int index = i;
                int x = startX + (i % columns) * (buttonWidth + gapX);
                int y = startY + (i / columns) * (buttonHeight + gapY);
                addRenderableWidget(new ColorSwatchButton(
                        x, y, buttonWidth, buttonHeight, index, MODERN_COLOR_RGB[i],
                        () -> color, this::selectColor, Component.literal(MODERN_COLOR_NAMES[i])));
            }

            addRenderableWidget(Button.builder(Component.literal("− 500 K"), b -> changeTemperature(-1))
                    .bounds(center - 111, 144 + offset, 82, 18).build());
            addRenderableWidget(Button.builder(Component.literal("+ 500 K"), b -> changeTemperature(1))
                    .bounds(center + 29, 144 + offset, 82, 18).build());
            addRenderableWidget(Button.builder(Component.literal("−"), b -> changeBrightness(-1))
                    .bounds(center - 65, 181 + offset, 40, 18).build());
            addRenderableWidget(Button.builder(Component.literal("+"), b -> changeBrightness(1))
                    .bounds(center + 25, 181 + offset, 40, 18).build());
            powerButton = Button.builder(powerLabel(), b -> togglePower())
                    .bounds(center - 65, 208 + offset, 130, 20).build();
            addRenderableWidget(powerButton);
        } else {
            int buttonWidth = 46;
            int buttonHeight = 28;
            int gap = 10;
            int totalWidth = CLASSIC_COLOR_NAMES.length * buttonWidth + (CLASSIC_COLOR_NAMES.length - 1) * gap;
            int startX = center - totalWidth / 2;
            int startY = 52 + offset;
            for (int i = 0; i < CLASSIC_COLOR_NAMES.length; i++) {
                int index = i;
                addRenderableWidget(new ColorSwatchButton(
                        startX + i * (buttonWidth + gap), startY, buttonWidth, buttonHeight,
                        index, CLASSIC_COLOR_RGB[i], () -> color, this::selectColor,
                        Component.literal(CLASSIC_COLOR_NAMES[i])));
            }
            addRenderableWidget(Button.builder(Component.literal("−"), b -> changeBrightness(-1))
                    .bounds(center - 65, 122 + offset, 40, 18).build());
            addRenderableWidget(Button.builder(Component.literal("+"), b -> changeBrightness(1))
                    .bounds(center + 25, 122 + offset, 40, 18).build());
            powerButton = Button.builder(powerLabel(), b -> togglePower())
                    .bounds(center - 65, 153 + offset, 130, 20).build();
            addRenderableWidget(powerButton);
        }

        addRenderableWidget(Button.builder(Component.literal("Kapat"), b -> onClose())
                .bounds(center - 40, height - 25, 80, 20).build());
    }

    private Component powerLabel() {
        return Component.literal(powered ? "IŞIĞI KAPAT" : "IŞIĞI AÇ");
    }

    private void selectColor(int selected) {
        int max = modern ? LampBlock.MODERN_COLOR_COUNT - 1 : 2;
        color = Math.max(0, Math.min(max, selected));
        if (modern) {
            temperature = switch (selected) {
                case 11 -> 4;
                case 12 -> 8;
                case 13 -> 9;
                case 14 -> 13;
                default -> temperature;
            };
        }
        send();
    }

    private void changeTemperature(int steps) {
        temperature = Math.max(0, Math.min(18, temperature + steps));
        color = LampBlock.MODERN_KELVIN_COLOR;
        send();
    }

    private void changeBrightness(int amount) {
        brightness = Math.max(0, Math.min(15, brightness + amount));
        send();
    }

    private void togglePower() {
        powered = !powered;
        send();
        if (powerButton != null) {
            powerButton.setMessage(powerLabel());
        }
    }

    private void send() {
        ModNetwork.sendConfig(pos, color, brightness, temperature, powered);
    }

    private int kelvinBarWidth() {
        return Math.max(80, Math.min(256, width - 80));
    }

    private int kelvinBarX() {
        return (width - kelvinBarWidth()) / 2;
    }

    private int kelvinBarY() {
        return 116 + verticalOffset();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (modern && button == 0
                && mouseX >= kelvinBarX() && mouseX < kelvinBarX() + kelvinBarWidth()
                && mouseY >= kelvinBarY() && mouseY < kelvinBarY() + 9) {
            double fraction = (mouseX - kelvinBarX()) / Math.max(1.0, kelvinBarWidth() - 1.0);
            temperature = Math.max(0, Math.min(18, (int) Math.round(fraction * 18.0)));
            color = LampBlock.MODERN_KELVIN_COLOR;
            send();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        int center = width / 2;
        int offset = verticalOffset();
        graphics.drawCenteredString(font, title, center, 8 + offset, 0xFFFFFF);

        if (modern) {
            graphics.drawCenteredString(font, Component.literal("Renk seçimi"), center, 22 + offset, 0xD5DDE8);
            graphics.drawCenteredString(font,
                    Component.literal("Renk sıcaklığı: " + (1000 + temperature * 500) + " K"),
                    center, 106 + offset, 0xFFFFFF);
            int barWidth = kelvinBarWidth();
            int barX = kelvinBarX();
            int barY = kelvinBarY();
            graphics.blit(KELVIN_GRADIENT, barX, barY, barWidth, 9,
                    0, 0, 256, 9, 256, 9);
            graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY, 0xFFFFFFFF);
            graphics.fill(barX - 1, barY + 9, barX + barWidth + 1, barY + 10, 0xFF4A4A4A);
            int markerX = barX + Math.round((temperature / 18.0F) * (barWidth - 1));
            graphics.fill(markerX - 1, barY - 2, markerX + 2, barY + 11, 0xFFFFFFFF);
            graphics.drawString(font, Component.literal("1000 K"), barX, barY + 11, 0xFFD7D7D7);
            graphics.drawString(font, Component.literal("10000 K"),
                    barX + barWidth - font.width("10000 K"), barY + 11, 0xFFD7D7D7);
            graphics.drawCenteredString(font, Component.literal("Parlaklık: " + brightness + " / 15"),
                    center, 168 + offset, 0xFFFFFF);
            graphics.drawCenteredString(font, Component.literal(powered ? "Lamba açık" : "Lamba kapalı"),
                    center, 199 + offset, powered ? 0xBFFFC7 : 0xFFB8B8);
        } else {
            graphics.drawCenteredString(font, Component.literal("Üç sabit renk seçeneği"),
                    center, 34 + offset, 0xC8D5E5);
            graphics.drawCenteredString(font, Component.literal("Parlaklık: " + brightness + " / 15"),
                    center, 101 + offset, 0xFFFFFF);
            graphics.drawCenteredString(font, Component.literal(powered ? "Lamba açık" : "Lamba kapalı"),
                    center, 143 + offset, powered ? 0xBFFFC7 : 0xFFB8B8);
        }

        graphics.drawCenteredString(font, Component.literal("Işık Kontrol Kumandası"),
                center, height - 40, 0x9EB8D2);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
