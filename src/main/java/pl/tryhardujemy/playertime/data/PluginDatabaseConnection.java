package pl.tryhardujemy.playertime.data;

import com.zaxxer.hikari.HikariDataSource;
import pl.tryhardujemy.playertime.data.DatabaseCredentials;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PluginDatabaseConnection{
    private HikariDataSource hikariDataSource;
    private Connection connection;

    public PluginDatabaseConnection(DatabaseCredentials credentials) {
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", credentials.getIp());
        hikariDataSource.addDataSourceProperty("port", credentials.getPort());
        hikariDataSource.addDataSourceProperty("databaseName", credentials.getDatabaseName());
        hikariDataSource.addDataSourceProperty("user", credentials.getUser());
        hikariDataSource.addDataSourceProperty("password", credentials.getPassword());
        hikariDataSource.addDataSourceProperty("autoReconnect", true);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createConnection() {
        try {
            connection = hikariDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (!connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(String query) {
        try {
            System.out.println(query);
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
