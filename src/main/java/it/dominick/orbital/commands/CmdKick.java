package it.dominick.orbital.commands;

import it.dominick.orbital.api.PunishEvent;
import it.dominick.orbital.storage.PunishmentDatabase;
import it.dominick.orbital.utils.ChatUtils;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Command("kick")
public class CmdKick extends CommandBase {

    private PunishmentDatabase pdb;
    private FileConfiguration config;

    public CmdKick(PunishmentDatabase pdb, FileConfiguration config) {
        this.pdb = pdb;
        this.config = config;
    }

    @Default
    @Permission("punishmentorbital.kick")
    public void kickCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        String staffName = sender.getName();

        if (player == null) {
            ChatUtils.send(sender, config, "messages.playerNotOnline");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        Timestamp expiration = Timestamp.valueOf(now);
        String reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "No Reason";
        String staffAction = "KICK";
        UUID playerUUID = player.getUniqueId();

        PunishEvent punishEvent = new PunishEvent(staffAction, reason, staffName, playerName, expiration);
        Bukkit.getPluginManager().callEvent(punishEvent);

        if (!punishEvent.isCancelled()) {
            List<String> kickDisplay = config.getStringList("messages.kickDisplay");
            pdb.addToHistory(playerUUID, playerName, reason, expiration, staffName, staffAction);
            player.kickPlayer(ChatUtils.translateHexColorCodes(String.join("\n", kickDisplay).replace("{reason}", reason)));
        }
    }
}