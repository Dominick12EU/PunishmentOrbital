package it.dominick.orbital.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static void send(Player player, String message) {
        player.sendMessage(translateHexColorCodes(message));
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

