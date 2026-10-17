package com.autosprintonly.feature;

import com.autosprintonly.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class AutoSprintManager {

    private static boolean lastActive = false;
    private static boolean lastBlockedByFood = false;

    public static boolean isActive() { return lastActive; }
    public static boolean isBlockedByFood() { return lastBlockedByFood; }

    public static void tick(Minecraft mc) {
        lastActive = false;
        lastBlockedByFood = false;

        if (!Config.enabled) return;
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        // Не мешаем в GUI
        if (mc.screen != null) return;

        // Не спринтим в воде/лаве
        if (player.isInWater() || player.isInLava()) return;

        // Не спринтим в полёте (креатив)
        if (player.getAbilities().flying) return;

        // 1. ПРИСЕДАНИЕ (Shift) - Всегда выключаем спринт
        if (player.isShiftKeyDown()) return;

        // 2. ДВИЖЕНИЕ НАЗАД (S) - Всегда выключаем спринт
        if (mc.options.keyDown.isDown()) return;

        // 3. ГОЛОД (Меньше 6) - Всегда выключаем спринт
        int food = player.getFoodData().getFoodLevel();
        if (food < 6) {
            lastBlockedByFood = true;
            return;
        }

        // ===== УСЛОВИЕ ДВИЖЕНИЯ (W, A, D) =====
        boolean moving = mc.options.keyUp.isDown()
                || mc.options.keyLeft.isDown()
                || mc.options.keyRight.isDown();

        if (!moving) return;

        // Включаем спринт
        if (player.isSprinting()) {
            lastActive = true;
            return;
        }

        player.setSprinting(true);
        lastActive = player.isSprinting();
    }

    public static void reset() {
        lastActive = false;
        lastBlockedByFood = false;
    }
}