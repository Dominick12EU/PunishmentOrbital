package it.dominick.orbital.events;

import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JoinPlayerListener implements Listener {

    private PunishmentDatabase pdb;
    private FileConfiguration config;
    private CsvData data;

    public JoinPlayerListener(PunishmentDatabase pdb, FileConfiguration config, CsvData data) {
        this.pdb = pdb;
        this.config = config;
        this.data = data;
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        UUID playerUUID = event.getUniqueId();

        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp expiration = pdb.getBanExpiration(playerUUID);

        if (pdb.isBlacklisted(playerUUID)) {
            List<String> blacklistDisplay = config.getStringList("messages.blacklistDisplay");
            event.setLoginResult(null);
            event.setKickMessage(ChatUtils.translateHexColorCodes(String.join("\n", blacklistDisplay)
                    .replace("{reason}", pdb.getBlacklistReason(playerUUID))));
        }

        if (expiration != null && currentDateTime.isAfter(expiration.toLocalDateTime())) {
            pdb.unbanPlayer(playerUUID);
        } else if (pdb.isBanned(playerUUID)) {
            List<String> banDisplay = config.getStringList("messages.banDisplay");
            event.setLoginResult(null);
            event.setKickMessage(ChatUtils.translateHexColorCodes(String.join("\n", banDisplay)
                    .replace("{expiration}", expiration.toString())
                    .replace("{reason}", pdb.getBanReason(playerUUID))));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String playerUUID = event.getPlayer().getUniqueId().toString();

        if (!data.isPlayerRegistered(playerName)) {
            data.registerPlayer(playerName, playerUUID);
        }
    }
}
