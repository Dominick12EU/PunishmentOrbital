package it.dominick.orbital;

import it.dominick.orbital.commands.CmdBan;
import it.dominick.orbital.storage.PunishmentDatabase;
import me.mattstudios.mf.base.CommandManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PunishmentOrbital extends JavaPlugin {

    private FileConfiguration config;
    private PunishmentDatabase pdb;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        try {
            String host = config.getString("database.host");
            int port = config.getInt("database.port");
            String database = config.getString("database.name");
            String username = config.getString("database.username");
            String password = config.getString("database.password");

            pdb = new PunishmentDatabase(host, port, database, username, password);
            pdb.connect();
            pdb.createBlacklistTable();
            pdb.createBansTable();
            pdb.createMuteTable();
            pdb.createHistoryTable();

            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new CmdBan(pdb, config));
        } catch (Exception e) {
            getLogger().severe("Error activating the plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        pdb.close();
    }
}
