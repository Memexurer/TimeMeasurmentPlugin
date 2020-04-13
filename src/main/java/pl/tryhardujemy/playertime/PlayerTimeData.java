package pl.tryhardujemy.playertime;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.tryhardujemy.playertime.data.PluginDatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTimeData {
    private final ConcurrentHashMap<String, PlayerTime> playerTime;
    private final PluginDatabaseConnection connection;

    PlayerTimeData(PluginDatabaseConnection connection) {
        this.connection = connection;
        this.playerTime = new ConcurrentHashMap<>();
    }

    void loadData() throws Exception {
        connection.update("CREATE TABLE IF NOT EXISTS `playertime` (" +
                "PlayerName varchar(16)," +
                "PlayerTime bigint(8)" +
                ");");

        ResultSet set = connection.query("SELECT * FROM `playertime`");
        while (set.next())
            playerTime.put(set.getString("PlayerName"), new PlayerTime(set.getLong("PlayerTime")));
    }

    void saveData() throws SQLException {
        Statement batchStatement = connection.getConnection().createStatement();

        for (Map.Entry<String, PlayerTime> entry: playerTime.entrySet()) {
            System.out.println(entry.getValue().playerTime);
            if (entry.getValue().needInsert) {
                batchStatement.addBatch("INSERT INTO playertime (PlayerName, PlayerTime) VALUES ('" + entry.getKey() + "', '" + entry.getValue().getPlayerTime() + "');");
            } else if(entry.getValue().needUpdate){
                batchStatement.addBatch("UPDATE playertime " +
                        "SET PlayerTime='" + entry.getValue().getPlayerTime() + "' " +
                        "WHERE PlayerName='" + entry.getKey() + "'");
            }
        }

        batchStatement.executeBatch();
    }

    void joinPlayer(Player player) {
        playerTime.computeIfAbsent(player.getName(), uuid -> new PlayerTime()).playerJoin();
    }

    /***
     * Pobiera czas grania gracza na serwerze.
     * @param player gracz
     * @return czas grania w sekundach
     */
    public Optional<Long> getPlayerTime(Player player) {
        if(!playerTime.containsKey(player.getName())) return Optional.empty();
        else return Optional.of(playerTime.get(player.getName()).getPlayerTime());
    }

    /***
     * Pobiera czas grania gracza na serwerze.
     * @param playerName nazwa gracza
     * @return czas grania w sekundach
     */
    public Optional<Long> getPlayerTime(String playerName) {
        if(!playerTime.containsKey(playerName)) return Optional.empty();
        else return Optional.of(playerTime.get(playerName).getPlayerTime());
    }

    public static class PlayerTime {
        private long joinTime;
        private long playerTime;
        private boolean needUpdate;
        private boolean needInsert;

        PlayerTime(long playerTime) {
            this.playerTime = playerTime;
        }

        PlayerTime() {
            this.playerTime = 0;
            this.needInsert = true;
        }

        void playerJoin() {
            this.joinTime = System.currentTimeMillis();
            this.needUpdate = true;
        }

        long calculateSeconds() {
            if(joinTime == 0) return 0;
            return (System.currentTimeMillis() - joinTime) / 1000;
        }

        long getPlayerTime() {
            this.playerTime = playerTime + calculateSeconds();
            this.joinTime = System.currentTimeMillis();
            this.needUpdate = true;

            return this.playerTime;
        }
    }
}
