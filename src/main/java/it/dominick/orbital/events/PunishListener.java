package it.dominick.orbital.events;

import it.dominick.orbital.api.PunishEvent;
import it.dominick.orbital.utils.ChatUtils;
import it.dominick.orbital.utils.ExpirationDate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PunishListener implements Listener {
    private FileConfiguration config;

    public PunishListener(FileConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onPunish(PunishEvent event) {
        for (Player staffers : Bukkit.getOnlinePlayers()) {
            if (staffers.hasPermission("punishmentorbital.staff")) {

                if (event.getAction().equals("UNBAN") || event.getAction().equals("UNMUTE") || event.getAction().equals("UNBLACKLIST")) {
                    List<String> staffNotify = config.getStringList("messages.unPunishNotify");
                    ChatUtils.alert(staffers, staffNotify,
                            "{action}", event.getAction(),
                            "{player}", event.getPlayer(),
                            "{staffer}", event.getStaffer(),
                            "{reason}", event.getReason());
                    return;
                }

                List<String> staffNotify = config.getStringList("messages.punishNotify");
                ChatUtils.alert(staffers, staffNotify,
                        "{action}", event.getAction(),
                        "{player}", event.getPlayer(),
                        "{staffer}", event.getStaffer(),
                        "{reason}", event.getReason(),
                        "{duration}", ExpirationDate.getTimeUntilNow(event.getDuration().getTime()));
            }
        }
    }
}
