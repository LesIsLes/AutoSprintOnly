package com.autosprintonly;

import com.autosprintonly.gui.SettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {

    public static KeyMapping toggleKey;
    public static KeyMapping openGuiKey;
    public static KeyMapping hudEditorKey;

    private static KeyMapping.Category category;
    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;

        category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(AutoSprintOnly.MOD_ID, "main"));

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.autosprintonly.toggle", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V, category));

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.autosprintonly.open_gui", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G, category));

        hudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.autosprintonly.hud_editor", InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F6, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                com.autosprintonly.config.Config.enabled = !com.autosprintonly.config.Config.enabled;
                ConfigManager.save();
                if (client.player != null) {
                    String state = com.autosprintonly.config.Config.enabled ? "§aON" : "§cOFF";
                    client.player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("§6AutoSprint: " + state), true);
                }
            }
            while (openGuiKey.consumeClick()) {
                Minecraft.getInstance().setScreen(new SettingsScreen(null));
            }
            while (hudEditorKey.consumeClick()) {
                if (client.player != null && client.screen == null) {
                    client.setScreen(new com.autosprintonly.hud.HudEditScreen());
                }
            }
        });
    }
}