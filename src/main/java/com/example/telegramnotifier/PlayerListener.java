package com.example.telegramnotifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final TelegramService telegram;
    private final ConfigManager config;

    public PlayerListener(TelegramService telegram, ConfigManager config) {
        this.telegram = telegram;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!config.isNotifyJoin()) return;

        Player player = event.getPlayer();
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        String message = config.getMsgJoin()
                .replace("{player}", player.getName())
                .replace("{online}", String.valueOf(online))
                .replace("{max}", String.valueOf(max));

        telegram.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!config.isNotifyLeave()) return;

        Player player = event.getPlayer();
        int online = Bukkit.getOnlinePlayers().size() - 1;
        int max = Bukkit.getMaxPlayers();

        String message = config.getMsgLeave()
                .replace("{player}", player.getName())
                .replace("{online}", String.valueOf(online))
                .replace("{max}", String.valueOf(max));

        telegram.sendMessage(message);
    }
}
