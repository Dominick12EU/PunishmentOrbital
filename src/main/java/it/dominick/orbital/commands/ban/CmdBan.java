package it.dominick.orbital.commands.ban;

import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
import it.dominick.orbital.utils.ExpirationDate;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Command("tempban")
@Alias("ban")
public class CmdBan extends CommandBase {

    private PunishmentDatabase pdb;
    private FileConfiguration config;

    public CmdBan(PunishmentDatabase pdb, FileConfiguration config) {
        this.pdb = pdb;
        this.config = config;
    }

    @Default
    @Permission("punishmentorbital.ban")
    public void banCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        String staffName = sender.getName();

        if (player == null) {
            ChatUtils.send(sender, config, "messages.playerNotOnline");
            return;
        }

        String duration = args.length >= 2 ? args[1] : "permanent";
        String reason = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No Reason";
        String staffAction = "BAN";

        if (!duration.equalsIgnoreCase("permanent") && !duration.matches("\\d+[smhdw]")) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            duration = "permanent";
        } else if (args.length >= 3) {
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            staffAction = "TEMPBAN";
        }

        Timestamp expiration = ExpirationDate.calculateExpirationDate(duration);
        UUID playerUUID = player.getUniqueId();

        pdb.banPlayer(playerUUID, playerName, reason, expiration);
        pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, staffAction);

        List<String> banDisplay = config.getStringList("messages.banDisplay");
        player.kickPlayer(ChatUtils.translateHexColorCodes(String.join("\n", banDisplay)
                .replace("{expiration}", expiration.toString()))
                .replace("{reason}", reason));
        ChatUtils.send(sender, config, "messages.confirmedBan");
    }
}
