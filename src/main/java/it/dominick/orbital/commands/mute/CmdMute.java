package it.dominick.orbital.commands.mute;

import it.dominick.orbital.api.PunishEvent;
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

@Command("tempmute")
@Alias("mute")
public class CmdMute extends CommandBase {

    private PunishmentDatabase pdb;
    private FileConfiguration config;

    public CmdMute(PunishmentDatabase pdb, FileConfiguration config) {
        this.pdb = pdb;
        this.config = config;
    }

    @Default
    @Permission("punishmentorbital.mute")
    public void muteCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        String staffName = sender.getName();

        if (player == null) {
            ChatUtils.send(sender, config, "messages.playerNotOnline");
            return;
        }

        String duration = args.length >= 2 ? args[1] : "permanent";
        String reason = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "No Reason";
        String staffAction = "MUTE";

        if (!duration.equalsIgnoreCase("permanent") && !duration.matches("\\d+[smhdw]")) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            duration = "permanent";
        } else if (args.length >= 3) {
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            staffAction = "TEMPMUTE";
        }

        Timestamp expiration = ExpirationDate.calculateExpirationDate(duration);
        UUID playerUUID = player.getUniqueId();

        if (pdb.isMuted(playerUUID)) {
            ChatUtils.send(sender, config, "messages.playerAlreadyMuted");
            return;
        }

        PunishEvent punishEvent = new PunishEvent(staffAction, reason, staffName, playerName, expiration);
        Bukkit.getPluginManager().callEvent(punishEvent);

        if (!punishEvent.isCancelled()) {
            pdb.mutePlayer(playerUUID, playerName, reason, expiration);
            pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, staffAction);

            ChatUtils.send(sender, config, "messages.confirmedMute");
        }
    }
}
