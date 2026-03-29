# TelegramNotifier

A Minecraft Paper plugin that sends Telegram notifications when players join/leave and when the server starts/stops.

> **Note:** This project was entirely vibecoded. Use at your own discretion.

## Features

- Player join/leave notifications with online count
- Server start/stop notifications
- Customizable message templates
- Toggle individual notifications on/off
- `/tgnotify` command with reload, test, and status subcommands
- Async HTTP calls — never blocks the server tick

## Setup

1. Install [Java 17+](https://adoptium.net/) and [Maven](https://maven.apache.org/)
2. Build the plugin:

```bash
mvn clean package
```

3. Copy `target/TelegramNotifier-1.0.jar` to your server's `plugins/` folder
4. Create a Telegram bot via [@BotFather](https://t.me/BotFather) and copy the token
5. Get your chat ID by visiting `https://api.telegram.org/bot<YOUR_TOKEN>/getUpdates` after sending a message to your bot
6. Edit `plugins/TelegramNotifier/config.yml`:

```yaml
telegram:
  bot-token: "your-bot-token"
  chat-id: "your-chat-id"
```

7. Start the server — you should receive the startup notification

## Commands

| Command | Description |
|---------|-------------|
| `/tgnotify reload` | Reload configuration |
| `/tgnotify test` | Send a test notification |
| `/tgnotify status` | Show current config |

**Permission:** `telegramnotify.admin` (default: op)

## Configuration

See `src/main/resources/config.yml` for all options including notification toggles and message templates.
