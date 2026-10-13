package org.accessor.database.connectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class PoolConnection {

    private final Connection connection;
    private final boolean isOneUseConnection;
    private boolean isAvailable = true;

    public PoolConnection(Connection connection, boolean isOneUseConnection) {
        this.connection = connection;
        this.isOneUseConnection = isOneUseConnection;
    }

    public void claim() {
        isAvailable = false;
    }

    public void unClaim() {
        if (isOneUseConnection) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isTemporary() {
        return isOneUseConnection;
    }

    public Connection getConnection() {
        return connection;
    }
}
