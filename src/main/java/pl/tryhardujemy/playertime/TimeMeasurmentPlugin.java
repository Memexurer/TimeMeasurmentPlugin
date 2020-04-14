package pl.tryhardujemy.playertime;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import pl.memexurer.database.DatabaseCredentials;
import pl.memexurer.database.PluginDatabaseConnection;
import pl.tryhardujemy.playertime.data.PlayerTimeData;

import java.io.File;

import static org.bukkit.Bukkit.getServicesManager;

public final class TimeMeasurmentPlugin extends JavaPlugin implements Listener {
    private static TimeMeasurmentPlugin PLUGIN_INSTANCE;
    private PlayerTimeData timeData;

    public static TimeMeasurmentPlugin getPluginInstance() {
        return PLUGIN_INSTANCE;
    }

    @Override
    public void onEnable() {
        PLUGIN_INSTANCE = this;

        if (!(new File(getDataFolder(), "config.yml").exists())) saveResource("config.yml", false);

        PluginDatabaseConnection databaseConnection = findDatabaseService();
        if (databaseConnection == null) {
            databaseConnection = new PluginDatabaseConnection(new DatabaseCredentials(getConfig().getConfigurationSection("database")));
            databaseConnection.createConnection();
            getServicesManager().register(PluginDatabaseConnection.class, databaseConnection, this, ServicePriority.Normal);
        }


        this.timeData = new PlayerTimeData(databaseConnection);
        this.timeData.loadData();

        this.getCommand("time").setExecutor(new TimeMeasurementCommand());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, timeData::saveData, 20L * 60L * 5L, 20L * 60L * 5L);
        Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class, this, EventPriority.NORMAL, (listener, event) -> timeData.joinPlayer(((PlayerJoinEvent) event).getPlayer()), this);
    }

    @Override
    public void onDisable() {
        timeData.saveData();
    }

    public PlayerTimeData getTimeData() {
        return timeData;
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
}
