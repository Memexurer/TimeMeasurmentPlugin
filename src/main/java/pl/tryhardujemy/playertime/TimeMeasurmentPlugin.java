package pl.tryhardujemy.playertime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.database.PluginDatabaseConnection;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

import static org.bukkit.Bukkit.getServicesManager;

public final class TimeMeasurmentPlugin extends JavaPlugin implements Listener {
    private PlayerTimeData timeData;

    @Override
    public void onEnable() {
        if (!(new File(getDataFolder(), "config.yml").exists())) saveResource("config.yml", false);

        PluginDatabaseConnection databaseConnection = findDatabaseService();
        if (databaseConnection == null) {
            databaseConnection = new PluginDatabaseConnection(new DatabaseCredentials(getConfig().getConfigurationSection("database")));
            databaseConnection.createConnection();
            getServicesManager().register(PluginDatabaseConnection.class, databaseConnection, this, ServicePriority.Normal);
        }


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

    private PluginDatabaseConnection findDatabaseService() {
        for (Class<?> clazz : getServicesManager().getKnownServices()) {
            RegisteredServiceProvider<?> serviceProvider = getServicesManager().getRegistration(clazz);
            this.getLogger().info("Znaleziono plugin, z ktorego mozna ukrasc baze danych (" + serviceProvider.getPlugin().getName() + ")");
            return (PluginDatabaseConnection) serviceProvider.getProvider();
        }

        this.getLogger().info("Nie znaleziono zadnego pluginu, który używa memowej bazy danych.");
        return null;
    }

    public PlayerTimeData getTimeData() {
        return timeData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // nie ma sensu tworzyc klasy specjalnie dla nowej komendy
        if (command.getName().equals("time")) {
            if (sender.hasPermission("tryhardujemy.czas") || !(sender instanceof Player)) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick gracza)");
                    return true;
                }
                Optional<Long> time = timeData.getPlayerTime(args[0]);
                if (!time.isPresent()) {
                    sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza.");
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Gracz " + args[0] + " posiada " + ChatColor.AQUA + TimeParsingUtils.formatSecs(time.get()) + ChatColor.GRAY + " przegranego czasu na serwerze.");
                }
            } else if (!sender.hasPermission("trhardujemy.czas")) {
                Optional<Long> time = timeData.getPlayerTime((Player) sender);
                if (!time.isPresent()) {
                    sender.sendMessage(ChatColor.RED + "Wystapil blad! Relognij sie, aby sprawdzic swoj czas na serwerze.");
                } else {
                    sender.sendMessage(ChatColor.GRAY + "Posiadasz " + ChatColor.AQUA + TimeParsingUtils.formatSecs(time.get()) + ChatColor.GRAY + " przegranego czasu na serwerze.");
                }
            }
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
