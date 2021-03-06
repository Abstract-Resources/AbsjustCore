package dev.absjustcore.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.absjustcore.AbsjustPlugin;
import dev.absjustcore.TaskUtils;
import dev.absjustcore.provider.utils.LocalResultSet;
import dev.absjustcore.provider.utils.StoreMeta;
import dev.absjustcore.provider.utils.StoreValue;
import lombok.NonNull;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public final class MysqlProvider implements Provider {

    private HikariDataSource dataSource = null;
    private HikariConfig hikariConfig = null;

    private final Map<String, String> statements = new HashMap<>();

    @Override
    public void init(StoreMeta storeMeta) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s/%s?useSSL=false", storeMeta.fetchString("address"), storeMeta.fetchString("dbname")));
        config.setUsername(storeMeta.fetchString("username"));
        config.setPassword(storeMeta.fetchString("password"));

        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(50);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setValidationTimeout(120000);

        this.dataSource = new HikariDataSource(this.hikariConfig = config);

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("mysql.properties");

        if (inputStream == null) return;

        try {
            Properties properties = new Properties();

            properties.load(inputStream);

            for (String k : properties.stringPropertyNames()) {
                this.statements.put(k, properties.getProperty(k).replace("<prefix>", "absjustcore"));

                if (k.endsWith("_CREATE")) this.storeAsync(StoreMeta.builder().statement(k).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        AbsjustPlugin.getLogger().info("Successfully initialized 'MySQL' as database provider");
    }

    @Override
    public void store(StoreMeta storeMeta) {
        if (this.disconnected()) {
            if (this.reconnect()) {
                this.store(storeMeta);
            }

            return;
        }

        String sql = storeMeta.getStatement();

        if (!this.statements.containsKey(sql)) {
            throw new RuntimeException("Statement " + sql + " not found");
        }

        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(this.statements.get(sql))) {
                this.set(preparedStatement, storeMeta.listValues());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            AbsjustPlugin.getLogger().error("An error occurred when tried execute the SQL Query: " + sql, e);

            this.dataSource.close();
        } finally {
            storeMeta.flush();
        }
    }

    public void storeAsync(StoreMeta storeMeta) {
        TaskUtils.runAsync(() -> this.store(storeMeta));
    }

    public int storeAndFetch(StoreMeta storeMeta) {
        if (this.disconnected()) return this.reconnect() ? this.storeAndFetch(storeMeta) : -1;

        String sql = storeMeta.getStatement();

        if (!this.statements.containsKey(sql)) {
            throw new RuntimeException("Statement " + sql + " not found");
        }

        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(this.statements.get(sql), Statement.RETURN_GENERATED_KEYS)) {
                this.set(preparedStatement, storeMeta.listValues());

                preparedStatement.executeUpdate();

                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(0);
                    }
                }
            }
        } catch (SQLException e) {
            AbsjustPlugin.getLogger().error("An error occurred when tried execute the SQL Query: " + sql, e);

            this.dataSource.close();
        } finally {
            storeMeta.flush();
        }

        return -1;
    }

    @Override
    public @Nullable LocalResultSet fetch(@NonNull StoreMeta storeMeta) {
        if (this.disconnected()) return this.reconnect() ? this.fetch(storeMeta) : null;

        String sql = storeMeta.getStatement();

        if (!this.statements.containsKey(sql)) {
            throw new RuntimeException("Statement " + sql + " not found");
        }

        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(this.statements.get(sql))) {
                this.set(preparedStatement, storeMeta.listValues());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return LocalResultSet.fetch(resultSet);
                }
            }
        } catch (SQLException e) {
            AbsjustPlugin.getLogger().error("An error occurred when tried execute the SQL Query: " + sql, e);

            this.dataSource.close();
        } finally {
            storeMeta.flush();
        }

        return null;
    }

    private boolean disconnected() {
        return this.dataSource == null || this.dataSource.isClosed() || !this.dataSource.isRunning();
    }

    private boolean reconnect() {
        this.close();

        if (this.hikariConfig == null) {
            AbsjustPlugin.getLogger().log(Level.FATAL, "Can't reconnect because Hikari config is null...");

            return false;
        }

        try {
            this.dataSource = new HikariDataSource(this.hikariConfig);
        } catch (Exception e) {
            AbsjustPlugin.getLogger().log(Level.FATAL, "Can't reconnect because, reason: {}", e.getMessage());

            return false;
        } finally {
            AbsjustPlugin.getLogger().info("Successfully reconnected");
        }

        return true;
    }

    public void close() {
        if (this.dataSource != null) this.dataSource.close();

        this.statements.clear();
    }

    private void set(PreparedStatement preparedStatement, Set<StoreValue> values) throws SQLException {
        for (StoreValue value : values) {
            int index = value.getId();
            Object result = value.getValue();

            if (result instanceof String || result instanceof Character) preparedStatement.setString(index, result.toString());
            if (result instanceof Integer) preparedStatement.setInt(index, (Integer) result);
            if (result instanceof Boolean) preparedStatement.setBoolean(index, (Boolean) result);
            if (result instanceof Float) preparedStatement.setFloat(index, (Float) result);
            if (result instanceof Long) preparedStatement.setLong(index, (Long) result);
            if (result == null || (result instanceof String && ((String) result).isEmpty())) preparedStatement.setNull(index, preparedStatement.getParameterMetaData().getParameterType(index));

            System.out.println("Index is " + index + " and result " + result);
        }
    }
}