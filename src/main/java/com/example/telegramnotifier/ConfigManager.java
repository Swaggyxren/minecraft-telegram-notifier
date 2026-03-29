package com.example.telegramnotifier;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private String botToken;
    private String chatId;
    private boolean notifyJoin;
    private boolean notifyLeave;
    private boolean notifyStart;
    private boolean notifyStop;
    private String msgJoin;
    private String msgLeave;
    private String msgStart;
    private String msgStop;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        botToken = config.getString("telegram.bot-token", "");
        chatId = config.getString("telegram.chat-id", "");

        notifyJoin = config.getBoolean("notifications.player-join", true);
        notifyLeave = config.getBoolean("notifications.player-leave", true);
        notifyStart = config.getBoolean("notifications.server-start", true);
        notifyStop = config.getBoolean("notifications.server-stop", true);

        msgJoin = config.getString("messages.player-join", "🟢 {player} joined ({online}/{max})");
        msgLeave = config.getString("messages.player-leave", "🔴 {player} left ({online}/{max})");
        msgStart = config.getString("messages.server-start", "✅ Server is starting up...");
        msgStop = config.getString("messages.server-stop", "⛔ Server is shutting down...");
    }

    public boolean isConfigured() {
        return botToken != null && !botToken.isEmpty()
            && chatId != null && !chatId.isEmpty();
    }

    public String getBotToken() { return botToken; }
    public String getChatId() { return chatId; }
    public boolean isNotifyJoin() { return notifyJoin; }
    public boolean isNotifyLeave() { return notifyLeave; }
    public boolean isNotifyStart() { return notifyStart; }
    public boolean isNotifyStop() { return notifyStop; }
    public String getMsgJoin() { return msgJoin; }
    public String getMsgLeave() { return msgLeave; }
    public String getMsgStart() { return msgStart; }
    public String getMsgStop() { return msgStop; }
}
