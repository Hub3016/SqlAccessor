package org.accessor.database.connectionPool;

import org.accessor.Configuration;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {

    private static final ArrayList<PoolConnection> connections = new ArrayList<>();

    static {
        for (int i = 0; i < Configuration.getPoolSize(); i++) connections.add(getFreshPoolConnection(false));
    }

    public static PoolConnection claimConnection() {
        PoolConnection connection = findAvailableValidConnection();
        if (connection != null) {
            connection.claim();
            return connection;
        }

        return null;
    }

    private @Nullable static PoolConnection findAvailableValidConnection() {
        for (PoolConnection connection : connections) if (checkAndHandleConnection(connection)) {
            return connection;
        }

        //No available connection was found!
        if (Configuration.isExpandSizeOnTraffic()) {
            return getFreshPoolConnection(true);
        }

        return null;
    }

    private static boolean checkAndHandleConnection(PoolConnection poolConnection) {
        if (!poolConnection.isAvailable()) return false;
        Connection connection = poolConnection.getConnection();
        try {
            if (connection.isClosed() || !connection.isValid(1)) {
                connections.remove(poolConnection);
                connections.add(getFreshPoolConnection(false));
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private static PoolConnection getFreshPoolConnection(boolean oneUseConnection) {
        if (Configuration.getUSER() == null || Configuration.getURL() == null || Configuration.getPASSWORD() == null) {
            new Error("Configuration is unset! Please initialize the Configuration of this project using 'new Configuration(...)'").printStackTrace();
        }

        try {
            return new PoolConnection(DriverManager.getConnection(Configuration.getURL(), Configuration.getUSER(), Configuration.getPASSWORD()), oneUseConnection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countUnclaimedConnections() {
        int i = 0;
        for (PoolConnection connection : connections) if (connection.isAvailable()) i++;
        return i;
    }

}
