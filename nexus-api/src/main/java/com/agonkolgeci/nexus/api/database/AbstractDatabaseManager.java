package com.agonkolgeci.nexus.api.database;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.common.config.ConfigUtils;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.utils.storage.DatabaseUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.List;

@Getter
public abstract class AbstractDatabaseManager extends PluginManager<AbstractPlugin> implements PluginAdapter {

    @NotNull private final HikariConfig hikariConfig;
    @NotNull private final HikariDataSource hikariDataSource;

    public AbstractDatabaseManager(@NotNull AbstractPlugin instance, @NotNull ConfigSection configuration) {
        super(instance);

        @Nullable final DatabaseCredentials databaseCredentials = ConfigUtils.retrieveDatabaseCredentials(configuration);
        if(databaseCredentials == null) throw new IllegalStateException("Database connection credentials are not specified.");

        this.hikariConfig = databaseCredentials.toHikariConfig();
        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() {
        try {
            hikariDataSource.getConnection().close();
            hikariDataSource.close();
        } catch (Exception ignored) {}
    }

    public void executeSchema(@NotNull InputStream schema) throws IOException {
        @NotNull final List<String> statements = DatabaseUtils.retrieveStatements(schema);
        for(@NotNull final String statement : statements) {
            this.executeUpdate(statement);
        }
    }

    @NotNull
    public Connection getConnection() {
        try {
            return hikariDataSource.getConnection();
        } catch (SQLException exception) {
            logger.severe("Unable to recover the connection to the database, launching a new attempt...");
        }

        return getConnection();
    }

    @NotNull
    private PreparedStatement prepareStatement(@NotNull String sql, @NotNull Object... objects) throws SQLException {
        @NotNull final Connection connection = this.getConnection();
        @NotNull final PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        for(int i = 0; i < objects.length; i++) {
            preparedStatement.setObject(i + 1, objects[i]);
        }

        return preparedStatement;
    }

    @NotNull
    public ResultSet executeQuery(@NotNull String sql, @NotNull Object... objects) {
        try {
            @NotNull final PreparedStatement preparedStatement = this.prepareStatement(sql, objects);

            return preparedStatement.executeQuery();
        } catch (SQLException exception) {
            exception.printStackTrace();

            throw new IllegalStateException(String.format("Unable to execute this SQL query: %s", sql));
        }

    }

    public void executeUpdate(@NotNull String sql, @NotNull Object... objects) {
        try {
            @NotNull final PreparedStatement preparedStatement = this.prepareStatement(sql, objects);

            preparedStatement.executeUpdate();

            this.closeStatement(preparedStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();

            throw new IllegalStateException(String.format("Unable to execute this SQL update: %s", sql));
        }
    }

    public void closeConnection(@NotNull Connection... connections) {
        try {
            for(@NotNull final Connection connection : connections) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();

            throw new IllegalStateException("Unable to close a SQL connection.");
        }
    }

    public void closeStatement(@NotNull Statement... statements) {
        try {
            for(@NotNull final Statement statement : statements) {
                statement.close();

                this.closeConnection(statement.getConnection());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();

            throw new IllegalStateException("Unable to close a SQL statement.");
        }
    }

    public void closeResults(@NotNull ResultSet... results) {
        try {
            for(@NotNull final ResultSet result : results) {
                result.close();

                this.closeStatement(result.getStatement());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();

            throw new IllegalStateException("Unable to close a SQL results.");
        }
    }
}
