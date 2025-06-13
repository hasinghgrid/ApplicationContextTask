package com.cachesystem.capstone.jdbc;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class JDBCUtil {

    private final Connection testConnection;
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    // Default constructor for production usage
    public JDBCUtil() {
        this.testConnection = null;
    }

    // Overloaded constructor for test usage
    public JDBCUtil(Connection testConnection) {
        this.testConnection = testConnection;
    }

    // Select appropriate connection
    private Connection getConnection() throws SQLException {
        if (testConnection != null && !testConnection.isClosed()) {
            return testConnection;
        } else {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    // Execute update with varargs
    public void execute(String query, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParams(stmt, args);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }

    // Execute update with consumer
    public void execute(String query, Consumer<PreparedStatement> consumer) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            consumer.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Execution failed: " + query, e);
        }
    }

    // Find a single result
    public <T> T findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParams(stmt, args);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                T result = mapper.apply(rs);
                if (rs.next()) {
                    throw new RuntimeException("Expected one result, but found multiple.");
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + query, e);
        }
    }

    // Find multiple results
    public <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            setParams(stmt, args);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.apply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + query, e);
        }
        return results;
    }

    // Helper to set parameters
    private void setParams(PreparedStatement stmt, Object... args) throws SQLException {
        if (args == null) return;
        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
    }
}
