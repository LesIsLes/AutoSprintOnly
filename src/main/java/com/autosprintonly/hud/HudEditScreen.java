package com.autosprintonly.hud;

import com.autosprintonly.ConfigManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class HudEditScreen extends Screen {

    private static final int PANEL_W = 180;
    private static final int ROW_H = 18;
    private static final int PANEL_PAD = 8;
    private static final int PANEL_TOP = 100;
    private static final int HEADER_H = 20;

    private final List<HudWidget> widgets = new ArrayList<>();
    private final List<Checkbox> checkboxes = new ArrayList<>();

    private HudWidget draggingWidget = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public HudEditScreen() {
        super(Component.literal("AutoSprint HUD Editor"));
    }

    private int panelLeft()   { return this.width - PANEL_W - PANEL_PAD; }
    private int panelRight()  { return this.width - PANEL_PAD; }
    private int panelBottom() { return this.height - PANEL_PAD; }
    private int listTop()     { return PANEL_TOP + HEADER_H + 4; }
    private int listBottom()  { return panelBottom() - 4; }

    private boolean inPanel(double x, double y) {
        return x >= panelLeft() && x <= panelRight()
                && y >= PANEL_TOP && y <= panelBottom();
    }

    @Override
    protected void init() {
        widgets.clear();
        widgets.addAll(HudWidgetRegistry.buildAll());
        checkboxes.clear();

        int panelX = panelLeft();
        int rowY = listTop();

        for (HudWidget w : widgets) {
            final HudWidget widgetRef = w;
            Checkbox cb = Checkbox.builder(
                            Component.literal(w.getDisplayName()), this.font)
                    .pos(panelX + 6, rowY)
                    .selected(w.isEnabled())
                    .onValueChange((c, v) -> {
                        widgetRef.setEnabled(v);
                        ConfigManager.save();
                    })
                    .build();
            this.addRenderableWidget(cb);
            checkboxes.add(cb);
            rowY += ROW_H;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // ВАЖНО: не вызываем renderBackground() — в 1.21.11 это крашит игру.
        // Простое затемнение фона вручную:
        graphics.fill(0, 0, this.width, this.height, 0x80000000);

        // Правая панель
        int px = panelLeft();
        int pr = panelRight();
        int pb = panelBottom();

        graphics.fill(px, PANEL_TOP, pr, pb, 0xC0101010);
        graphics.fill(px, PANEL_TOP, pr, PANEL_TOP + 1, 0xFF60A0FF);
        graphics.fill(px, pb - 1, pr, pb, 0xFF60A0FF);
        graphics.fill(px, PANEL_TOP, px + 1, pb, 0xFF60A0FF);
        graphics.fill(pr - 1, PANEL_TOP, pr, pb, 0xFF60A0FF);

        graphics.drawString(this.font, "§lHUD Widgets", px + 6, PANEL_TOP + 6, 0xFFFFFFFF, true);

        // Заголовок сверху
        graphics.drawString(this.font,
                "§lHUD Editor §7· ЛКМ — перетащить · ПКМ — сброс · Esc — закрыть",
                10, 10, 0xFFFFFFFF, true);

        // Превью виджетов
        for (HudWidget w : widgets) {
            boolean enabled = w.isEnabled();
            float alpha = enabled ? 1.0f : 0.35f;

            boolean hovered = (draggingWidget == null)
                    && !inPanel(mouseX, mouseY)
                    && w.hitTest(mouseX, mouseY, graphics.guiWidth(), graphics.guiHeight());

            if (draggingWidget == w) {
                alpha = Math.min(1.0f, alpha + 0.3f);
            }

            // Подсветка hovered
            if (hovered) {
                int x = w.getEffectiveX(graphics.guiWidth());
                int y = w.getEffectiveY(graphics.guiHeight());
                int highlightColor = 0x60FFFFFF;
                graphics.fill(x - 2, y - 2, x + w.getWidth() + 2, y, highlightColor);
                graphics.fill(x - 2, y + w.getHeight(), x + w.getWidth() + 2, y + w.getHeight() + 2, highlightColor);
                graphics.fill(x - 2, y - 2, x, y + w.getHeight() + 2, highlightColor);
                graphics.fill(x + w.getWidth(), y - 2, x + w.getWidth() + 2, y + w.getHeight() + 2, highlightColor);
            }

            w.renderPreview(graphics, mouseX, mouseY, alpha);
        }

        // Виджеты (чекбоксы справа) — super вызывается ПОСЛЕ, чтобы они рисовались поверх
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        // Клик по панели — отдаём чекбоксам
        if (inPanel(mouseX, mouseY)) {
            return super.mouseClicked(event, isDoubleClick);
        }

        // Hit-test по превью — в обратном порядке (top-most)
        for (int i = widgets.size() - 1; i >= 0; i--) {
            HudWidget w = widgets.get(i);
            if (!w.hitTest(mouseX, mouseY, this.width, this.height)) continue;

            if (button == 1) {
                // ПКМ — сброс
                w.setPosition(w.getDefaultX(), w.getDefaultY());
                ConfigManager.save();
                return true;
            }

            // ЛКМ — начать drag
            draggingWidget = w;
            dragOffsetX = (int) mouseX - w.getEffectiveX(this.width);
            dragOffsetY = (int) mouseY - w.getEffectiveY(this.height);
            return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (draggingWidget != null && event.button() == 0) {
            double mouseX = event.x();
            double mouseY = event.y();

            int newX = (int) mouseX - dragOffsetX;
            int newY = (int) mouseY - dragOffsetY;

            newX = Math.max(0, Math.min(this.width - draggingWidget.getWidth(), newX));
            newY = Math.max(0, Math.min(this.height - draggingWidget.getHeight(), newY));

            draggingWidget.setPosition(newX, newY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingWidget != null && event.button() == 0) {
            draggingWidget = null;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}