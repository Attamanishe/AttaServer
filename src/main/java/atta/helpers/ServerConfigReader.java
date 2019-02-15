package atta.helpers;

import atta.utill.properties.PropertiesReader;

public class ServerConfigReader
{
    private static String propertyPath;

    private ServerConfigReader()
    {
    }

    public static void setConfigPath(String path)
    {
        propertyPath = path;
    }

    public static int getServerPort()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "port"));
    }

    public static String getServerIP()
    {
        return PropertiesReader.getValue(propertyPath, "host");
    }

    public static boolean isMultiConnectionFromIP()
    {
        return Boolean.parseBoolean(PropertiesReader.getValue(propertyPath, "multiConnection"));
    }

    public static boolean isListeningForReconnection()
    {
        return Boolean.parseBoolean(PropertiesReader.getValue(propertyPath, "reconnection"));
    }

    public static int getMaxConnectionsCount()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "maxConnections"));
    }

    public static int getMaxClientsToPerformPerManager()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "clientsPerManager"));
    }

    public static int getUpdateTimeForSending()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "updateTimeSending"));
    }

    public static int getMaxCountToSendPerUpdate()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "maxCountToSendPerUpdate"));
    }

    public static int getUpdateTimeForListening()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "updateTimeForListening"));
    }

    public static int getUpdateTimeForPing()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "updateTimeForPing"));
    }

    public static int getTimeToReconnect()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "timeToReconnect"));
    }

    public static int getUpdateTimeForReconnect()
    {
        return Integer.parseInt(PropertiesReader.getValue(propertyPath, "updateTimeReconnecting"));
    }

    public static String getLogPath()
    {
        return PropertiesReader.getValue(propertyPath, "loggerPath");
    }
}
