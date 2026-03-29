package com.example.telegramnotifier;

import com.google.gson.JsonObject;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TelegramNotifierPlugin extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private TelegramService telegramService;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        telegramService = new TelegramService(this, configManager);

        PlayerListener playerListener = new PlayerListener(telegramService, configManager);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getPluginManager().registerEvents(this, this);

        CommandHandler commandHandler = new CommandHandler(this, configManager, telegramService);
        getCommand("tgnotify").setExecutor(commandHandler);
        getCommand("tgnotify").setTabCompleter(commandHandler);

        if (!configManager.isConfigured()) {
            getLogger().warning("Telegram bot-token or chat-id is not configured!");
            getLogger().warning("Set your credentials in plugins/TelegramNotifier/config.yml");
            getLogger().warning("Then run /tgnotify reload");
        }

        getLogger().info("TelegramNotifier enabled!");
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (configManager.isNotifyStart() && configManager.isConfigured()) {
            int online = Bukkit.getOnlinePlayers().size();
            int max = Bukkit.getMaxPlayers();
            String message = configManager.getMsgStart()
                    .replace("{online}", String.valueOf(online))
                    .replace("{max}", String.valueOf(max));
            telegramService.sendMessage(message);
        }
    }

    @Override
    public void onDisable() {
        if (configManager != null && configManager.isNotifyStop() && configManager.isConfigured()) {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .writeTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(5, TimeUnit.SECONDS)
                        .build();

                JsonObject json = new JsonObject();
                json.addProperty("chat_id", configManager.getChatId());
                json.addProperty("text", configManager.getMsgStop());
                json.addProperty("parse_mode", "HTML");

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );
                Request request = new Request.Builder()
                        .url("https://api.telegram.org/bot" + configManager.getBotToken() + "/sendMessage")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    getLogger().info("Shutdown notification sent: " + response.isSuccessful());
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Failed to send shutdown notification", e);
            }
        }

        if (telegramService != null) {
            telegramService.shutdown();
        }

        getLogger().info("TelegramNotifier disabled!");
    }
}
