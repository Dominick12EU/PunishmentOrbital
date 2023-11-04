package it.dominick.orbital.commands;

import it.dominick.orbital.storage.CsvData;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import it.dominick.orbital.utils.ExpirationDate;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Command("blacklist")
public class CmdBlacklist extends CommandBase {
    private PunishmentDatabase pdb;
    private FileConfiguration config;
    private CsvData data;

    public CmdBlacklist(PunishmentDatabase pdb, FileConfiguration config, CsvData data) {
        this.pdb = pdb;
        this.config = config;
        this.data = data;
    }

    @SubCommand("add")
    @Permission("punishmentorbital.blacklist.add")
    public void addSubCommand(CommandSender sender, String[] args) {
        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);
        String staffName = sender.getName();

        if (player == null) {
            ChatUtils.send(sender, config, "messages.playerNotOnline");
            return;
        }

        UUID playerUUID = player.getUniqueId();

        if (pdb.isBlacklisted(playerUUID)) {
            ChatUtils.send(sender, config, "messages.playerAlreadyBlacklisted", "{player}", playerName);
            return;
        }

        String reason = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No Reason";
        Timestamp expiration = ExpirationDate.calculateExpirationDate("permanent");

        pdb.blacklistPlayer(playerUUID, playerName, reason);
        pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, "BLACKLIST");

        ChatUtils.send(sender, config, "messages.confirmedBlacklist");
    }

    @SubCommand("remove")
    @Permission("punishmentorbital.blacklist.remove")
    public void removeSubCommand(CommandSender sender, String[] args) {
        String playerName = args[1];
        UUID playerUUID = UUID.fromString(data.getPlayerUUID(playerName));
        String staffName = sender.getName();

        if (!pdb.isBlacklisted(playerUUID)) {
            ChatUtils.send(sender, config, "messages.playerNotBlacklisted", "{player}", playerName);
            return;
        }

        String reason = "No Reason";
        LocalDateTime now = LocalDateTime.now();
        Timestamp expiration = Timestamp.valueOf(now);

        pdb.removeFromBlacklist(playerUUID);
        pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, "UNBLACKLIST");

        ChatUtils.send(sender, config, "messages.playerUnBlacklist", "{player}", playerName);
    }
}
