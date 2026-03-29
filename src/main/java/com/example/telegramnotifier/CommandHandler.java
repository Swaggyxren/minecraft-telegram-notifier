package com.example.telegramnotifier;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final TelegramNotifierPlugin plugin;
    private final ConfigManager config;
    private final TelegramService telegram;

    public CommandHandler(TelegramNotifierPlugin plugin, ConfigManager config, TelegramService telegram) {
        this.plugin = plugin;
        this.config = config;
        this.telegram = telegram;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                config.load();
                sender.sendMessage("§a[TelegramNotifier] Configuration reloaded.");
                if (!config.isConfigured()) {
                    sender.sendMessage("§e[TelegramNotifier] Warning: bot-token or chat-id is not configured!");
                }
                break;

            case "test":
                if (!config.isConfigured()) {
                    sender.sendMessage("§c[TelegramNotifier] Bot is not configured! Set bot-token and chat-id in config.yml");
                    return true;
                }
                sender.sendMessage("§e[TelegramNotifier] Sending test message...");
                boolean success = telegram.sendTestMessage();
                if (success) {
                    sender.sendMessage("§a[TelegramNotifier] Test message sent successfully!");
                } else {
                    sender.sendMessage("§c[TelegramNotifier] Failed to send test message. Check console for errors.");
                }
                break;

            case "status":
                sender.sendMessage("§6[TelegramNotifier] Status:");
                sender.sendMessage("§7  Bot Token: " + (config.getBotToken().isEmpty() ? "§cNot set" : "§aConfigured"));
                sender.sendMessage("§7  Chat ID: " + (config.getChatId().isEmpty() ? "§cNot set" : "§a" + config.getChatId()));
                sender.sendMessage("§7  Notifications:");
                sender.sendMessage("§7    Player Join: " + (config.isNotifyJoin() ? "§aON" : "§cOFF"));
                sender.sendMessage("§7    Player Leave: " + (config.isNotifyLeave() ? "§aON" : "§cOFF"));
                sender.sendMessage("§7    Server Start: " + (config.isNotifyStart() ? "§aON" : "§cOFF"));
                sender.sendMessage("§7    Server Stop: " + (config.isNotifyStop() ? "§aON" : "§cOFF"));
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6[TelegramNotifier] Commands:");
        sender.sendMessage("§e  /tgnotify reload §7- Reload configuration");
        sender.sendMessage("§e  /tgnotify test §7- Send a test notification");
        sender.sendMessage("§e  /tgnotify status §7- Show current configuration");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "test", "status");
        }
        return List.of();
    }
}
