package pl.tryhardujemy.playertime.data;

import org.bukkit.configuration.ConfigurationSection;

public class DatabaseCredentials {
    private String ip;
    private int port;
    private String user;
    private String password;
    private String databaseName;

    public DatabaseCredentials(String ip, int port, String user, String password, String databaseName) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
        this.databaseName = databaseName;
    }

    public DatabaseCredentials(ConfigurationSection section) {
        this.ip = section.getString("ip");
        this.port = section.getInt("port");
        this.user = section.getString("user");
        this.password = section.getString("password");
        this.databaseName = section.getString("name");
    }

    String getIp() {
        return ip;
    }

    int getPort() {
        return port;
    }

    String getUser() {
        return user;
    }

    String getPassword() {
        return password;
    }

    String getDatabaseName() {
        return databaseName;
    }
}
