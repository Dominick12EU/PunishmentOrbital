package it.dominick.orbital.commands;

import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Command("unban")
public class CmdUnBan extends CommandBase {
    private PunishmentDatabase pdb;
    private FileConfiguration config;
    private CsvData data;

    public CmdUnBan(PunishmentDatabase pdb, FileConfiguration config, CsvData data) {
        this.pdb = pdb;
        this.config = config;
        this.data = data;
    }

    @Default
    @Permission("punishmentorbital.unban")
    public void unBanCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        UUID playerUUID = UUID.fromString(data.getPlayerUUID(playerName));

        if(!pdb.isBanned(playerUUID)) {
            ChatUtils.send(sender, config, "messages.playerNotBanned", "{player}", playerName);
        }

        String staffName = sender.getName();
        String staffAction = "UNBAN";
        String reason = "No Reason";
        LocalDateTime now = LocalDateTime.now();
        Timestamp expiration = Timestamp.valueOf(now);

        pdb.unbanPlayer(playerUUID);
        pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, staffAction);

        ChatUtils.send(sender, config, "messages.playerUnbanned", "{player}", playerName);
    }
}
