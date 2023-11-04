package it.dominick.orbital.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PunishmentDatabase {
    private final HikariDataSource dataSource;
    private ExecutorService executorService;

    public PunishmentDatabase(String host, int port, String database, String username, String password) {
        this.executorService = Executors.newSingleThreadExecutor();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        dataSource = new HikariDataSource(config);
    }

    public void createBansTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS bans (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "player_name VARCHAR(255) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "reason VARCHAR(255) NOT NULL," +
                    "expiration TIMESTAMP)";
            statement.executeUpdate(query);

            String alterQuery = "ALTER TABLE bans MODIFY COLUMN expiration DATETIME(6)";
            statement.executeUpdate(alterQuery);
        } catch (SQLException e) {
            System.out.println("Error creating ban table: " + e.getMessage());
        }
    }

    public void createBlacklistTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS blacklist (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "player_name VARCHAR(255) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "reason VARCHAR(255) NOT NULL)";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Error creating blacklist table: " + e.getMessage());
        }
    }


    public void createMuteTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS mute (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "player_name VARCHAR(255) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "reason VARCHAR(255) NOT NULL," +
                    "expiration TIMESTAMP)";
            statement.executeUpdate(query);

            String alterQuery = "ALTER TABLE bans MODIFY COLUMN expiration DATETIME(6)";
            statement.executeUpdate(alterQuery);
        } catch (SQLException e) {
            System.out.println("Error creating mute table: " + e.getMessage());
        }
    }

    public void createHistoryTable() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS history (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "player_name VARCHAR(255) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "reason VARCHAR(255) NOT NULL," +
                    "punish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "expiration DATETIME(6), " +
                    "staff_name VARCHAR(255) NOT NULL, " +
                    "staff_action VARCHAR(255) NOT NULL)";
            statement.executeUpdate(query);

            String alterQuery = "ALTER TABLE history MODIFY COLUMN expiration DATETIME(6)";
            statement.executeUpdate(alterQuery);
        } catch (SQLException e) {
            System.out.println("Error creating history table: " + e.getMessage());
        }
    }

    //BLACKLIST

    public void blacklistPlayer(UUID playerUUID, String playerName, String reason) {
        executorService.execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO blacklist (player_uuid, player_name, reason) VALUES (?, ?, ?)")) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, playerName);
                statement.setString(3, reason);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error while blacklisting player: " + e.getMessage());
            }
        });
    }

    public void removeFromBlacklist(UUID playerUUID) {
        executorService.execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM blacklist WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error removing player from blacklist: " + e.getMessage());
            }
        });
    }

    public boolean isBlacklisted(UUID playerUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM blacklist WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error checking blacklist: " + e.getMessage());
        }
        return false;
    }

    public String getBlacklistReason(UUID playerUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT reason FROM blacklist WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("reason");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving blacklist reason: " + e.getMessage());
        }
        return null;
    }

    //BAN

    public void banPlayer(UUID playerUUID, String playerName, String reason, Timestamp expiration) {
        executorService.execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO bans (player_uuid, player_name, reason, expiration) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, playerName);
                statement.setString(3, reason);
                statement.setTimestamp(4, expiration);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error while banning player: " + e.getMessage());
            }
        });
    }

    public void unbanPlayer(UUID playerUUID) {
        executorService.execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM bans WHERE player_uuid = ?")) {
                statement.setString(1, playerUUID.toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error unblocking player: " + e.getMessage());
            }
        });
    }

    public boolean isBanned(UUID playerUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bans WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error checking ban: " + e.getMessage());
        }
        return false;
    }

    public String getBanReason(UUID playerUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT reason FROM bans WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("reason");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving ban reason: " + e.getMessage());
        }
        return null;
    }


    public Timestamp getBanExpiration(UUID playerUUID) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT expiration FROM bans WHERE player_uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getTimestamp("expiration");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving ban expiration: " + e.getMessage());
        }
        return null;
    }

    //MUTE

    //GENERAL

    public void addToHistory(UUID playerUUID, String playerName, String reason, Timestamp expiration, String staffName, String staffAction) {
        executorService.execute(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO history (player_uuid, player_name, reason, expiration, staff_name, staff_action) VALUES (?, ?, ?, ?, ?, ?)")) {
                statement.setString(1, playerUUID.toString());
                statement.setString(2, playerName);
                statement.setString(3, reason);
                statement.setTimestamp(4, expiration);
                statement.setString(5, staffName);
                statement.setString(6, staffAction);
                statement.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Error while adding ban to history: " + e.getMessage());
            }
        });
    }

    public void connect() {
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }
}
