package it.dominick.orbital.events;

import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import it.dominick.orbital.utils.ExpirationDate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChatListener implements Listener {

    private PunishmentDatabase pdb;
    private FileConfiguration config;

    public ChatListener(PunishmentDatabase pdb, FileConfiguration config) {
        this.pdb = pdb;
        this.config = config;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp expiration = pdb.getMuteExpiration(playerUUID);

        if (expiration != null && currentDateTime.isAfter(expiration.toLocalDateTime())) {
            pdb.unmutePlayer(playerUUID);
        } else if (pdb.isMuted(playerUUID)) {
            ChatUtils.send(player, config, "messages.muteResponse",
                    "{reason}", pdb.getMuteReason(playerUUID), "{duration}", ExpirationDate.getTimeUntilNow(expiration.getTime()));
            event.setCancelled(true);
        }
    }
}
