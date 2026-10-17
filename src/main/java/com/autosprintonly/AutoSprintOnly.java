package com.autosprintonly;

import com.autosprintonly.config.Config;
import com.autosprintonly.feature.AutoSprintManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class AutoSprintOnly implements ClientModInitializer {

	public static final String MOD_ID = "autosprintonly";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		KeyBindings.register();

		// Тик конфига (debounced save)
		ClientTickEvents.END_CLIENT_TICK.register(client -> ConfigManager.tick());

		// Тик автоспринта
		ClientTickEvents.END_CLIENT_TICK.register(AutoSprintManager::tick);

		// Сброс при выходе из мира
		ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> AutoSprintManager.reset());

		// HUD
		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(MOD_ID, "autosprint_hud"),
				(graphics, tickCounter) -> com.autosprintonly.hud.HudRenderer.render(graphics)
		);

		LOGGER.info("[AutoSprintOnly] Initialized");
	}
}