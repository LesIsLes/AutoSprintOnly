package com.autosprintonly.gui;

import com.autosprintonly.ConfigManager;
import com.autosprintonly.config.Config;
import com.autosprintonly.hud.HudEditScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SettingsScreen extends Screen {

    private final Screen parent;

    public SettingsScreen(Screen parent) {
        super(Component.translatable("gui.autosprintonly.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = 50;

        // Включить автоспринт
        addRenderableWidget(Checkbox.builder(
                        Component.translatable("gui.autosprintonly.enabled"), this.font)
                .pos(cx - 100, y).selected(Config.enabled)
                .onValueChange((c, v) -> { Config.enabled = v; ConfigManager.save(); })
                .build());
        y += 25;

        // Показывать HUD
        addRenderableWidget(Checkbox.builder(
                        Component.translatable("gui.autosprintonly.show_hud"), this.font)
                .pos(cx - 100, y).selected(Config.showHud)
                .onValueChange((c, v) -> { Config.showHud = v; ConfigManager.save(); })
                .build());
        y += 25;

        // Слайдер размера HUD
        addRenderableWidget(new AbstractSliderButton(
                cx - 100, y, 200, 20,
                Component.translatable("gui.autosprintonly.hud_scale", String.format("%.1f", Config.hudScale)),
                (Config.hudScale - 0.5f) / 1.5f) {
            @Override protected void updateMessage() {
                this.setMessage(Component.translatable("gui.autosprintonly.hud_scale", String.format("%.1f", Config.hudScale)));
            }
            @Override protected void applyValue() {
                Config.hudScale = 0.5f + (float)(this.value * 1.5f);
                ConfigManager.save();
            }
        });
        y += 25;

        // Кнопка HUD-редактора
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.autosprintonly.hud_editor"),
                        b -> this.minecraft.setScreen(new HudEditScreen()))
                .bounds(cx - 100, y, 200, 20).build());
        y += 25;

        // Кнопка Готово
        addRenderableWidget(Button.builder(
                        Component.translatable("gui.autosprintonly.done"),
                        b -> this.minecraft.setScreen(parent))
                .bounds(cx - 100, y, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.fill(0, 0, this.width, this.height, 0x80000000);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFFFF);
        super.render(graphics, mouseX, mouseY, delta);
    }
}