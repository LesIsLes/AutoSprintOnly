package com.autosprintonly.hud;

import com.autosprintonly.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class HudWidgetRegistry {

    private HudWidgetRegistry() {}

    public static List<HudWidget> buildAll() {
        List<HudWidget> list = new ArrayList<>();
        list.add(new AutoSprintWidget());
        return list;
    }

    public static class AutoSprintWidget implements HudWidget {
        private int lastWidth = 80;
        private int lastHeight = 10;

        @Override public String getId() { return "autosprint"; }
        @Override public String getDisplayName() { return "AutoSprint HUD"; }

        @Override public int getRawX() { return Config.hudX; }
        @Override public int getRawY() { return Config.hudY; }

        @Override public int getEffectiveX(int screenWidth) { return Config.hudX; }
        @Override public int getEffectiveY(int screenHeight) { return Config.hudY; }

        @Override public void setPosition(int x, int y) { Config.hudX = x; Config.hudY = y; }
        @Override public int getDefaultX() { return 10; }
        @Override public int getDefaultY() { return 10; }

        @Override public boolean isEnabled() { return Config.showHud; }
        @Override public void setEnabled(boolean v) { Config.showHud = v; }

        @Override public int getWidth() { return lastWidth; }
        @Override public int getHeight() { return lastHeight; }

        @Override
        public boolean hitTest(double mouseX, double mouseY, int screenWidth, int screenHeight) {
            int x = getEffectiveX(screenWidth);
            int y = getEffectiveY(screenHeight);
            int pad = 4;
            return mouseX >= x - pad && mouseX <= x + lastWidth + pad
                    && mouseY >= y - pad && mouseY <= y + lastHeight + pad;
        }

        @Override
        public int renderPreview(GuiGraphics graphics, int mouseX, int mouseY, float alpha) {
            Minecraft mc = Minecraft.getInstance();
            String text = buildPreviewText();

            int iconW = Config.showFoodIcon ? (int)(20 * Config.hudScale) : 0;
            int textW = (int)(mc.font.width(text) * Config.hudScale);
            lastWidth = textW + iconW;
            lastHeight = (int)(10 * Config.hudScale);

            int a = (int) (alpha * 255);
            int color = (a << 24) | (Config.hudColor & 0x00FFFFFF);

            int x = getEffectiveX(graphics.guiWidth());
            int y = getEffectiveY(graphics.guiHeight());

            graphics.pose().pushMatrix();
            graphics.pose().translate((float) x, (float) y);
            graphics.pose().scale(Config.hudScale, Config.hudScale);

            if (Config.showFoodIcon) {
                graphics.renderItem(new ItemStack(Items.GOLDEN_CARROT), 0, -4);
            }
            graphics.drawString(mc.font, text, iconW, 0, color, true);

            graphics.pose().popMatrix();
            return lastWidth;
        }

        private String buildPreviewText() {
            boolean active = com.autosprintonly.feature.AutoSprintManager.isActive();
            boolean blocked = com.autosprintonly.feature.AutoSprintManager.isBlockedByFood();
            if (active) return "§aAutoSprint: ON";
            if (blocked && Config.showBlockReason) return "§cAutoSprint: OFF (food)";
            return "§7AutoSprint: OFF";
        }
    }
}