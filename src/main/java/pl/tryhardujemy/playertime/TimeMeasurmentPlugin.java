package pl.tryhardujemy.playertime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tryhardujemy.playertime.data.DatabaseCredentials;
import pl.tryhardujemy.playertime.data.PluginDatabaseConnection;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

public final class TimeMeasurmentPlugin extends JavaPlugin implements Listener {
    private PlayerTimeData timeData;

    @Override
    public void onEnable() {
        if (!(new File(getDataFolder(), "config.yml").exists())) saveResource("config.yml", false);

        PluginDatabaseConnection databaseConnection = new PluginDatabaseConnection(new DatabaseCredentials(getConfig().getConfigurationSection("database")));
        databaseConnection.createConnection();

        this.timeData = new PlayerTimeData(databaseConnection);
        try {
            this.timeData.loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                timeData.saveData();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }, 20L * 60L * 5L, 20L * 60L * 5L);

        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class, this, EventPriority.NORMAL, (listener, event) -> timeData.joinPlayer(((PlayerJoinEvent) event).getPlayer()), this);
    }

    public PlayerTimeData getTimeData() {
        return timeData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // nie ma sensu tworzyc klasy specjalnie dla nowej komendy
        if (command.getName().equals("time")) {
            if (!sender.hasPermission("tryhardujemy.czasgrania")) {
                sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczających permisji do użycia tej komendy.");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick gracza)");
                return true;
            }

            Optional<Long> time = timeData.getPlayerTime(args[0]);
            if (!time.isPresent())
                sender.sendMessage(ChatColor.RED + "Gracz nie istnieje w bazie danych.");
            else
                sender.sendMessage(ChatColor.GRAY + "Gracz grał na serwerze przez " + ChatColor.AQUA + TimeParsingUtils.formatSecs(time.get()));
        }
        return true;
    }

    @Override
    public void onDisable() {
        try {
            timeData.saveData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
