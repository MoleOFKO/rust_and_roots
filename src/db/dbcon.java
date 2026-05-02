/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author larry
 */
public class dbcon {

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

    public Connection getConnection() throws ClassNotFoundException {
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
            try (Connection ignored = new dbcon().getConnection()) {
                // Connection creation already ensures schema once; close immediately.
            } catch (Exception ignored) {
                // Warmup is best-effort. Callers will surface DB errors when actually needed.
            }
        }, WARMUP_EXECUTOR);
    }

    private static void ensureDriverLoaded() throws ClassNotFoundException {
        if (driverLoaded) {
            return;
        }

        synchronized (INIT_LOCK) {
            if (driverLoaded) {
                return;
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
            driverLoaded = true;
        }
    }
}
