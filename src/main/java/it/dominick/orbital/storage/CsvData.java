package it.dominick.orbital.storage;

import it.dominick.orbital.PunishmentOrbital;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.UUID;

public class CsvData {
    private final PunishmentOrbital plugin;

    public CsvData(PunishmentOrbital plugin) {
        this.plugin = plugin;
    }

    public boolean isPlayerRegistered(String playerName) {
        File csvFile = new File(plugin.getDataFolder(), "usermap.csv");

        if (!csvFile.exists() || csvFile.isDirectory()) {
            return false;
        }

        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(playerName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerPlayer(String playerName, String playerUUID) {
        File csvFile = new File(plugin.getDataFolder(), "usermap.csv");

        if (!csvFile.exists() || csvFile.isDirectory()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, true))) {
            writer.println(playerName + "," + playerUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerUUID(String playerName) {
        File csvFile = new File(plugin.getDataFolder(), "usermap.csv");

        if (!csvFile.exists() || csvFile.isDirectory()) {
            return null;
        }

        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(playerName)) {
                    return parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
