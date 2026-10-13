package org.accessor;

public class Configuration {

    //Connection Information's
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    //Connection Pooling
    private static int POOL_SIZE = 5;
    private static boolean EXPAND_SIZE_ON_TRAFFIC = true;

    public Configuration(String DbURL, String DbUser, String DbPassword) {
        URL = DbURL;
        USER = DbUser;
        PASSWORD = DbPassword;
    }

    public static void connectionPoolSettings(int poolSize, boolean expandSizeOnTraffic) {
        if (POOL_SIZE <= 0) new Error("Connection pool size cannot be 0 or less!").printStackTrace();
        else POOL_SIZE = poolSize;
        EXPAND_SIZE_ON_TRAFFIC = expandSizeOnTraffic;
    }

    public static String getURL() {
        return URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASSWORD() {
        return PASSWORD;
    }

    public static int getPoolSize() {
        return POOL_SIZE;
    }

    public static boolean isExpandSizeOnTraffic() {
        return EXPAND_SIZE_ON_TRAFFIC;
    }
}
