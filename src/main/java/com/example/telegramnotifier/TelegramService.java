package com.example.telegramnotifier;

import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public class TelegramService {

    private final JavaPlugin plugin;
    private final ConfigManager config;
    private final OkHttpClient httpClient;
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    public TelegramService(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        this.httpClient = new OkHttpClient();
    }

    public void sendMessage(String message) {
        if (!config.isConfigured()) {
            plugin.getLogger().warning("Telegram not configured! Set bot-token and chat-id in config.yml");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String url = "https://api.telegram.org/bot" + config.getBotToken() + "/sendMessage";

                JsonObject json = new JsonObject();
                json.addProperty("chat_id", config.getChatId());
                json.addProperty("text", message);
                json.addProperty("parse_mode", "HTML");

                RequestBody body = RequestBody.create(json.toString(), JSON_TYPE);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = httpClient.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "null";
                        plugin.getLogger().log(Level.WARNING,
                                "Telegram API error {0}: {1}",
                                new Object[]{response.code(), responseBody});
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to send Telegram message", e);
            }
        });
    }

    public boolean sendTestMessage() {
        if (!config.isConfigured()) {
            return false;
        }

        String url = "https://api.telegram.org/bot" + config.getBotToken() + "/sendMessage";

        JsonObject json = new JsonObject();
        json.addProperty("chat_id", config.getChatId());
        json.addProperty("text", "🔧 TelegramNotifier test message - plugin is working!");
        json.addProperty("parse_mode", "HTML");

        RequestBody body = RequestBody.create(json.toString(), JSON_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    public void shutdown() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
