package com.example.projeto.data.remote;

import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySqlHelper {
    private static final String TAG = "MySqlHelper";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public interface QueryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    public static void initDatabase() {
        executor.execute(() -> {
            // First step: Ensure database exists
            try (Connection conn = DriverManager.getConnection(MySqlConfig.getServerUrl(), MySqlConfig.USUARIO, MySqlConfig.SENHA)) {
                try (PreparedStatement stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + MySqlConfig.DATABASE)) {
                    stmt.executeUpdate();
                    Log.d(TAG, "Database " + MySqlConfig.DATABASE + " checked/created");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error creating database " + MySqlConfig.DATABASE + ". Check permissions for user " + MySqlConfig.USUARIO, e);
                // We don't return here because the DB might already exist even if we can't 'CREATE' it (e.g. no global privs)
            }

            // Second step: Initialize tables
            try (Connection conn = getConnection()) {
                if (conn == null) {
                    Log.e(TAG, "Could not establish connection to " + MySqlConfig.DATABASE);
                    return;
                }

                String[] queries = {
                    "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL UNIQUE, " +
                        "password VARCHAR(255) NOT NULL" +
                    ")",
                    "CREATE TABLE IF NOT EXISTS goals (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "targetAmount DOUBLE NOT NULL DEFAULT 0.0, " +
                        "currentAmount DOUBLE DEFAULT 0.0" +
                    ")",
                    "CREATE TABLE IF NOT EXISTS categories (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "userId INT NOT NULL, " +
                        "name VARCHAR(100) NOT NULL, " +
                        "color VARCHAR(20), " +
                        "type ENUM('INCOME', 'EXPENSE') NOT NULL, " +
                        "iconName VARCHAR(100), " +
                        "budgetLimit DOUBLE DEFAULT 0.0, " +
                        "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE" +
                    ")",
                    "CREATE TABLE IF NOT EXISTS transactions (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "value DOUBLE NOT NULL, " +
                        "description VARCHAR(255), " +
                        "date BIGINT NOT NULL, " +
                        "category VARCHAR(100), " +
                        "type ENUM('INCOME', 'EXPENSE') NOT NULL, " +
                        "accountName VARCHAR(100), " +
                        "observation TEXT, " +
                        "linkedGoalId INT, " +
                        "FOREIGN KEY (linkedGoalId) REFERENCES goals(id) ON DELETE SET NULL" +
                    ")"
                };

                for (String sql : queries) {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.executeUpdate();
                    }
                }
                Log.d(TAG, "Database initialized successfully");
            } catch (SQLException e) {
                Log.e(TAG, "Error initializing database tables", e);
            }
        });
    }

    public static void checkEmailExists(String email, QueryCallback<Boolean> callback) {
        executor.execute(() -> {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        callback.onSuccess(rs.getInt(1) > 0);
                    } else {
                        callback.onSuccess(false);
                    }
                }
            } catch (SQLException e) {
                Log.e(TAG, "Erro ao verificar e-mail", e);
                callback.onError(e);
            }
        });
    }

    public static void testConnection(QueryCallback<Boolean> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    callback.onSuccess(true);
                } else {
                    callback.onSuccess(false);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao testar conexão", e);
                callback.onError(e);
            }
        });
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(MySqlConfig.getUrl(), MySqlConfig.USUARIO, MySqlConfig.SENHA);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Driver MySQL não encontrado", e);
            throw new SQLException("Driver não encontrado", e);
        }
    }

    public static void executeUpdate(String sql, Object[] params, QueryCallback<Integer> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                
                int rows = stmt.executeUpdate();
                callback.onSuccess(rows);
            } catch (SQLException e) {
                Log.e(TAG, "Erro ao executar update: " + sql, e);
                callback.onError(e);
            }
        });
    }

    public static void executeQuery(String sql, Object[] params, QueryCallback<ResultSet> callback) {
        executor.execute(() -> {
            try {
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                
                ResultSet rs = stmt.executeQuery();
                callback.onSuccess(rs);
                // Nota: O chamador deve fechar o ResultSet e a Connection se necessário, 
                // mas para simplicidade em JDBC direto, costumamos processar e fechar aqui.
                // Melhor passar uma interface que processa o RS.
            } catch (SQLException e) {
                Log.e(TAG, "Erro ao executar query: " + sql, e);
                callback.onError(e);
            }
        });
    }

    public interface ResultSetProcessor<T> {
        T process(ResultSet rs) throws SQLException;
    }

    public static <T> void query(String sql, Object[] params, ResultSetProcessor<T> processor, QueryCallback<T> callback) {
        executor.execute(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    T result = processor.process(rs);
                    callback.onSuccess(result);
                }
            } catch (SQLException e) {
                Log.e(TAG, "Erro na consulta: " + sql, e);
                callback.onError(e);
            }
        });
    }
}