package com.autosprintonly;

import com.autosprintonly.config.Config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("autosprintonly");
    private static final Path FILE = CONFIG_DIR.resolve("config.json");

    private static final long SAVE_DEBOUNCE_MS = 400;
    private static boolean savePending = false;
    private static long lastSaveRequest = 0;

    public static void save() {
        savePending = true;
        lastSaveRequest = System.currentTimeMillis();
    }

    public static void saveNow() {
        savePending = false;
        write();
    }

    public static void tick() {
        if (!savePending) return;
        if (System.currentTimeMillis() - lastSaveRequest < SAVE_DEBOUNCE_MS) return;
        savePending = false;
        write();
    }

    private static void write() {
        try {
            if (!Files.exists(CONFIG_DIR)) Files.createDirectories(CONFIG_DIR);

            JsonObject json = new JsonObject();
            json.addProperty("enabled", Config.enabled);

            json.addProperty("showHud", Config.showHud);
            json.addProperty("hudScale", Config.hudScale);
            json.addProperty("hudX", Config.hudX);
            json.addProperty("hudY", Config.hudY);
            json.addProperty("hudColor", Config.hudColor);
            json.addProperty("hudAlpha", Config.hudAlpha);
            json.addProperty("showFoodIcon", Config.showFoodIcon);
            json.addProperty("showBlockReason", Config.showBlockReason);
            json.addProperty("hudBackgroundEnabled", Config.hudBackgroundEnabled);
            json.addProperty("hudBackgroundAlpha", Config.hudBackgroundAlpha);
            json.addProperty("hudBackgroundColor", Config.hudBackgroundColor);
            json.addProperty("hudBackgroundHeight", Config.hudBackgroundHeight);

            json.addProperty("modLogoRussian", Config.modLogoRussian);

            Files.writeString(FILE, GSON.toJson(json));
        } catch (IOException e) {
            AutoSprintOnly.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }

    public static void load() {
        if (!Files.exists(FILE)) return;
        try {
            String content = Files.readString(FILE);
            JsonObject json = GSON.fromJson(content, JsonObject.class);
            if (json == null) return;

            Config.enabled = getBool(json, "enabled", Config.enabled);

            Config.showHud = getBool(json, "showHud", Config.showHud);
            Config.hudScale = getFloat(json, "hudScale", Config.hudScale);
            Config.hudX = getInt(json, "hudX", Config.hudX);
            Config.hudY = getInt(json, "hudY", Config.hudY);
            Config.hudColor = getInt(json, "hudColor", Config.hudColor);
            Config.hudAlpha = getInt(json, "hudAlpha", Config.hudAlpha);
            Config.showFoodIcon = getBool(json, "showFoodIcon", Config.showFoodIcon);
            Config.showBlockReason = getBool(json, "showBlockReason", Config.showBlockReason);
            Config.hudBackgroundEnabled = getBool(json, "hudBackgroundEnabled", Config.hudBackgroundEnabled);
            Config.hudBackgroundAlpha = getInt(json, "hudBackgroundAlpha", Config.hudBackgroundAlpha);
            Config.hudBackgroundColor = getInt(json, "hudBackgroundColor", Config.hudBackgroundColor);
            Config.hudBackgroundHeight = getInt(json, "hudBackgroundHeight", Config.hudBackgroundHeight);

            Config.modLogoRussian = getBool(json, "modLogoRussian", Config.modLogoRussian);
        } catch (Exception e) {
            AutoSprintOnly.LOGGER.error("Failed to load config: {}", e.getMessage());
        }
    }

    private static boolean getBool(JsonObject j, String k, boolean d) { return j.has(k) ? j.get(k).getAsBoolean() : d; }
    private static int getInt(JsonObject j, String k, int d) { return j.has(k) ? j.get(k).getAsInt() : d; }
    private static float getFloat(JsonObject j, String k, float d) { return j.has(k) ? j.get(k).getAsFloat() : d; }
}