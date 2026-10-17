package com.autosprintonly.hud;

import net.minecraft.client.gui.GuiGraphics;

public interface HudWidget {
    String getId();
    String getDisplayName();
    int getRawX();
    int getRawY();
    int getEffectiveX(int screenWidth);
    int getEffectiveY(int screenHeight);
    void setPosition(int x, int y);
    int getDefaultX();
    int getDefaultY();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    boolean hitTest(double mouseX, double mouseY, int screenWidth, int screenHeight);
    int renderPreview(GuiGraphics graphics, int mouseX, int mouseY, float alpha);
    int getWidth();
    int getHeight();
}