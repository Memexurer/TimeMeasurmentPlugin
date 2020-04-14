package pl.tryhardujemy.playertime.data;

import org.bukkit.entity.Player;
import pl.memexurer.database.PluginDatabaseConnection;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTimeData {
    private final ConcurrentHashMap<String, PlayerTime> playerTime;
    private final PluginDatabaseConnection connection;

    public PlayerTimeData(PluginDatabaseConnection connection) {
        this.connection = connection;
        this.playerTime = new ConcurrentHashMap<>();
    }

    public void loadData() {
        try {
            connection.update("CREATE TABLE IF NOT EXISTS `playertime` (" +
                    "PlayerName varchar(16)," +
                    "PlayerTime bigint(8)" +
                    ");");

            ResultSet set = connection.query("SELECT * FROM `playertime`");
            while (set.next())
                playerTime.put(set.getString("PlayerName"), new PlayerTime(set.getLong("PlayerTime")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveData() {
        try {
            Statement batchStatement = connection.getConnection().createStatement();

            for (Map.Entry<String, PlayerTime> entry : playerTime.entrySet()) {
                if (entry.getValue().isNeedInsert()) {
                    batchStatement.addBatch("INSERT INTO playertime (PlayerName, PlayerTime) VALUES ('" + entry.getKey() + "', '" + entry.getValue().getPlayerTime() + "');");
                } else if (entry.getValue().isNeedUpdate()) {
                    batchStatement.addBatch("UPDATE playertime " +
                            "SET PlayerTime='" + entry.getValue().getPlayerTime() + "' " +
                            "WHERE PlayerName='" + entry.getKey() + "'");
                }
            }

            batchStatement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void joinPlayer(Player player) {
        playerTime.computeIfAbsent(player.getName(), uuid -> new PlayerTime()).playerJoin();
    }

    /***
     * Pobiera czas grania gracza na serwerze.
     * @param player gracz
     * @return czas grania w sekundach
     */
    public Optional<Long> getPlayerTime(Player player) {
        if (!playerTime.containsKey(player.getName())) return Optional.empty();
        else return Optional.of(playerTime.get(player.getName()).getPlayerTime());
    }

    /***
     * Pobiera czas grania gracza na serwerze.
     * @param playerName nazwa gracza
     * @return czas grania w sekundach
     */
    public Optional<Long> getPlayerTime(String playerName) {
        if (!playerTime.containsKey(playerName)) return Optional.empty();
        else return Optional.of(playerTime.get(playerName).getPlayerTime());
    }
}
