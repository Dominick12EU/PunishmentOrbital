package it.dominick.orbital;

import it.dominick.orbital.commands.*;
import it.dominick.orbital.events.ChatListener;
import it.dominick.orbital.events.JoinPlayerListener;
import it.dominick.orbital.storage.CsvData;
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

            CsvData data = new CsvData(this);
            CommandManager commandManager = new CommandManager(this);
            commandManager.register(new CmdUnBan(pdb, config, data));
            commandManager.register(new CmdUnMute(pdb, config, data));
            commandManager.register(new CmdBan(pdb, config));
            commandManager.register(new CmdBlacklist(pdb, config, data));
            commandManager.register(new CmdMute(pdb, config));

            getServer().getPluginManager().registerEvents(new JoinPlayerListener(pdb, config, data), this);
            getServer().getPluginManager().registerEvents(new ChatListener(pdb, config), this);
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
