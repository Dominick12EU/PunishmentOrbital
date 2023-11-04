package it.dominick.orbital.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static void send(Player player, String message) {
        player.sendMessage(translateHexColorCodes(message));
    }

    public static void send(CommandSender player, FileConfiguration config, String str) {
        player.sendMessage(translateHexColorCodes(config.getString(str)));
    }

    public static void send(Player player, FileConfiguration config, String str) {
        player.sendMessage(translateHexColorCodes(config.getString(str)));
    }

    public static void send(Player player, String message, Map<String, String> placeholders) {
        if (message == null || message.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            message = message.replace(placeholder, replacement);
        }

        player.sendMessage(translateHexColorCodes(message));
    }

    public static void alert(CommandSender player, List<String> messages, String... placeholders) {
        if (placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                String placeholder = placeholders[i];
                String replacement = placeholders[i + 1];

                for (int j = 0; j < messages.size(); j++) {
                    messages.set(j, messages.get(j).replace(placeholder, replacement));
                }
            }
        } else {
            return;
        }

        player.sendMessage(translateHexColorCodes(String.join("\n", messages)));
    }

    public static void send(CommandSender player, FileConfiguration config, String str, String... placeholders) {
        String message = config.getString(str);

        if (placeholders.length % 2 == 0) {
            for (int i = 0; i < placeholders.length; i += 2) {
                String placeholder = placeholders[i];
                String replacement = placeholders[i + 1];
                message = message.replace(placeholder, replacement);
            }
        } else {
            return;
        }

        player.sendMessage(translateHexColorCodes(message));
    }

    public static String translateHexColorCodes(final String message) {
        final char colorChar = net.md_5.bungee.api.ChatColor.COLOR_CHAR;

        final Matcher hexMatcher = HEX_PATTERN.matcher(message);
        final StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (hexMatcher.find()) {
            final String group = hexMatcher.group(1);

            hexMatcher.appendReplacement(buffer, "");
            buffer.append(colorChar + "x" + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        hexMatcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}

