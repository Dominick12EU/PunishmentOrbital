package it.dominick.orbital.commands.mute;

import it.dominick.orbital.api.PunishEvent;
import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Command("unmute")
public class CmdUnMute extends CommandBase {
    private PunishmentDatabase pdb;
    private FileConfiguration config;
    private CsvData data;

    public CmdUnMute(PunishmentDatabase pdb, FileConfiguration config, CsvData data) {
        this.pdb = pdb;
        this.config = config;
        this.data = data;
    }

    @Default
    @Permission("punishmentorbital.unmute")
    public void unMuteCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        UUID playerUUID = UUID.fromString(data.getPlayerUUID(playerName));

        if(!pdb.isMuted(playerUUID)) {
            ChatUtils.send(sender, config, "messages.playerNotMuted", "{player}", playerName);
        }

        String staffName = sender.getName();
        String staffAction = "UNMUTE";
        String reason = "No Reason";
        LocalDateTime now = LocalDateTime.now();
        Timestamp expiration = Timestamp.valueOf(now);

        PunishEvent punishEvent = new PunishEvent(staffAction, reason, staffName, playerName, expiration);
        Bukkit.getPluginManager().callEvent(punishEvent);

        if (!punishEvent.isCancelled()) {
            pdb.unmutePlayer(playerUUID);
            pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, staffAction);

            ChatUtils.send(sender, config, "messages.playerUnMuted", "{player}", playerName);
        }
    }
}
