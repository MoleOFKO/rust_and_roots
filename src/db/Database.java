package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Centralized database bootstrap + connection factory.
 *
 * Notes:
 * - Keeps connection creation short-lived (callers should still use try-with-resources).
 * - Loads the JDBC driver once and ensures schema once (via DatabaseSchemaManager).
 */
public final class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/rust_and_root";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Er05@vE1!fE5";

    private static final Object INIT_LOCK = new Object();
    private static volatile boolean driverLoaded = false;

    private static final ExecutorService WARMUP_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-warmup");
        t.setDaemon(true);
        return t;
    });

    private Database() {
    }

    public static Connection openConnection() {
        ensureDriverLoaded();

        try {
            Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            DatabaseSchemaManager.ensureSchema(con);
            return con;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to connect or initialize database schema", e);
        }
    }

    public static CompletableFuture<Void> warmUpAsync() {
        return CompletableFuture.runAsync(() -> {
            try (Connection con = openConnection()) {
                // openConnection already ensures schema once; close immediately.
            }
        }, WARMUP_EXECUTOR);
    }

    private static void ensureDriverLoaded() {
        if (driverLoaded) {
            return;
        }
        synchronized (INIT_LOCK) {
            if (driverLoaded) {
                return;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("MySQL JDBC Driver not found", e);
            }
        }
    }
}

