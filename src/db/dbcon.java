/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author larry
 */
public class dbcon {

    // ---- Connection config ----
    private static final String URL = "jdbc:mysql://localhost:3306/rust_and_root";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123123";

    // ---- Driver + schema bootstrap ----
    private static final Object INIT_LOCK = new Object();
    private static volatile boolean driverLoaded = false;

    // ---- Simple connection pool ----
    // Goal: avoid creating a new physical connection for every query while still letting callers
    // use try-with-resources safely (close() returns the connection to the pool).
    private static final int MAX_POOL_SIZE = 3;
    private static final long BORROW_TIMEOUT_MS = 1500;
    private static final BlockingQueue<Connection> IDLE_PHYSICAL = new LinkedBlockingQueue<>();
    private static final AtomicInteger CREATED_PHYSICAL = new AtomicInteger(0);

    private static final ExecutorService WARMUP_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-warmup");
        t.setDaemon(true);
        return t;
    });

    /**
     * Borrow a pooled connection. The returned {@link Connection} is a lightweight proxy that
     * returns the physical connection to the pool when {@link Connection#close()} is called.
     */
    public Connection getConnection() throws ClassNotFoundException {
        return borrow();
    }

    /**
     * Optional startup warm-up: loads the JDBC driver and validates the schema using a short-lived
     * pooled connection.
     */
    public static CompletableFuture<Void> warmUpAsync() {
        return CompletableFuture.runAsync(() -> {
            try (Connection ignored = borrow()) {
                // borrow() already ensures driver + schema; close returns to pool.
            } catch (Exception ignored) {
            }
        }, WARMUP_EXECUTOR);
    }

    private static Connection borrow() throws ClassNotFoundException {
        ensureDriverLoaded();

        Connection physical = pollUsablePhysical();
        if (physical == null) {
            physical = createOrWaitForPhysical();
        }
        if (physical == null) {
            throw new IllegalStateException("Timed out waiting for an available database connection.");
        }

        ensureSchemaOnce(physical);
        return wrap(physical);
    }

    private static Connection pollUsablePhysical() {
        Connection physical;
        while ((physical = IDLE_PHYSICAL.poll()) != null) {
            if (isUsable(physical)) {
                return physical;
            }
            closeQuietly(physical);
            CREATED_PHYSICAL.updateAndGet(v -> Math.max(0, v - 1));
        }
        return null;
    }

    private static Connection createOrWaitForPhysical() {
        while (true) {
            int current = CREATED_PHYSICAL.get();
            if (current < MAX_POOL_SIZE) {
                if (CREATED_PHYSICAL.compareAndSet(current, current + 1)) {
                    try {
                        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
                    } catch (SQLException e) {
                        CREATED_PHYSICAL.updateAndGet(v -> Math.max(0, v - 1));
                        throw new IllegalStateException("Failed to connect to database", e);
                    }
                }
                continue;
            }

            try {
                Connection physical = IDLE_PHYSICAL.poll(BORROW_TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (physical == null) {
                    return null;
                }
                if (isUsable(physical)) {
                    return physical;
                }
                closeQuietly(physical);
                CREATED_PHYSICAL.updateAndGet(v -> Math.max(0, v - 1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    private static Connection wrap(Connection physical) {
        Objects.requireNonNull(physical, "physical");

        InvocationHandler handler = new InvocationHandler() {
            private Connection delegate = physical;
            private boolean returned = false;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();

                if ("close".equals(name)) {
                    if (!returned) {
                        returned = true;
                        Connection toReturn = delegate;
                        delegate = null;
                        returnToPool(toReturn);
                    }
                    return null;
                }

                if ("isClosed".equals(name)) {
                    if (returned || delegate == null) {
                        return true;
                    }
                    try {
                        return delegate.isClosed();
                    } catch (SQLException e) {
                        return true;
                    }
                }

                Connection current = delegate;
                if (returned || current == null) {
                    throw new SQLException("Connection is closed.");
                }

                try {
                    return method.invoke(current, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        };

        return (Connection) Proxy.newProxyInstance(
            dbcon.class.getClassLoader(),
            new Class<?>[] { Connection.class },
            handler
        );
    }

    private static void returnToPool(Connection physical) {
        if (physical == null) {
            return;
        }

        if (!isUsable(physical)) {
            closeQuietly(physical);
            CREATED_PHYSICAL.updateAndGet(v -> Math.max(0, v - 1));
            return;
        }

        resetForReuse(physical);
        IDLE_PHYSICAL.offer(physical);
    }

    private static boolean isUsable(Connection physical) {
        if (physical == null) {
            return false;
        }
        try {
            if (physical.isClosed()) {
                return false;
            }
            try {
                return physical.isValid(1);
            } catch (SQLFeatureNotSupportedException ignored) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private static void resetForReuse(Connection physical) {
        try {
            if (!physical.getAutoCommit()) {
                try {
                    physical.rollback();
                } catch (SQLException ignored) {
                }
                try {
                    physical.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        } catch (SQLException ignored) {
        }
    }

    private static void ensureSchemaOnce(Connection physical) {
        try {
            DatabaseSchemaManager.ensureSchema(physical);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize database schema", e);
        }
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

    private static void closeQuietly(Connection physical) {
        if (physical == null) {
            return;
        }
        try {
            physical.close();
        } catch (Exception ignored) {
        }
    }
}
