package it.dominick.orbital.commands;

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

import java.util.Arrays;

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
        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);
        String staffName = sender.getName();

        if (player == null) {
            ChatUtils.send(player, config, "messages.playerNotOnline");
            return;
        }

        String duration = args.length >= 3 ? args[2] : "permanent";
        String reason = args.length >= 4 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : "No Reason";
        String staffAction = "BAN";

        System.out.println(Arrays.toString(args));
    }
}
