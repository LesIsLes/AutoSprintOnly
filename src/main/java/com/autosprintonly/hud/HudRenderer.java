package com.autosprintonly.hud;

import com.autosprintonly.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HudRenderer {

    public static void render(GuiGraphics graphics) {
        if (!Config.showHud) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        boolean active = com.autosprintonly.feature.AutoSprintManager.isActive();
        boolean blocked = com.autosprintonly.feature.AutoSprintManager.isBlockedByFood();

        String text;
        int color;

        if (!Config.enabled) {
            text = "§7AutoSprint: OFF";
            color = 0xFFAAAAAA;
        } else if (active) {
            text = "§aAutoSprint: ON";
            color = Config.hudColor;
        } else if (blocked && Config.showBlockReason) {
            text = "§cAutoSprint: OFF (food)";
            color = 0xFFFF4444;
        } else {
            text = "§7AutoSprint: idle";
            color = 0xFFAAAAAA;
        }

        int alpha = Config.hudAlpha;
        int finalColor = (alpha << 24) | (color & 0x00FFFFFF);

        int textW = mc.font.width(text.replaceAll("§.", ""));
        int iconW = Config.showFoodIcon ? 20 : 0;
        int totalW = (int)((textW + iconW) * Config.hudScale);

        int x = Config.hudX;
        int y = Config.hudY;

        if (Config.hudBackgroundEnabled) {
            int bg = (Config.hudBackgroundAlpha << 24) | (Config.hudBackgroundColor & 0x00FFFFFF);
            int h = (int)(Config.hudBackgroundHeight * Config.hudScale);
            graphics.fill(x - 2, y - 2, x + totalW + 2, y - 2 + h, bg);
        }

        graphics.pose().pushMatrix();
        graphics.pose().translate((float) x, (float) y);
        graphics.pose().scale(Config.hudScale, Config.hudScale);

        if (Config.showFoodIcon) {
            graphics.renderItem(new ItemStack(Items.GOLDEN_CARROT), 0, -4);
        }
        graphics.drawString(mc.font, text, iconW, 0, finalColor, true);

        graphics.pose().popMatrix();
    }
}