package atta.utill.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader
{
    public static String getValue(String path, String key)
    {
        FileInputStream fis;
        Properties property = new Properties();

        try
        {
            fis = new FileInputStream(path);
            property.load(fis);
        } catch (IOException e)
        {
            System.err.println(key+" "+path);
            System.err.println("Error: There is no properties file!");
        }
        return property.getProperty(key);
    }
}
