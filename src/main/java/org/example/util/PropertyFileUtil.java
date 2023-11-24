package org.example.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileUtil {
    private static final Properties properties;

    static {
        properties = new Properties();
        loadProperties("src/main/resources/socket_config.properties");
    }
    private static void loadProperties(String filePath){
        try {
            var inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            System.out.println("Input stream for given file name " +
                    "cannot be created. Please check if file is present.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Properties failed to load.");
            e.printStackTrace();
        }
    }
    private static String getPropertyNamed(String propName){
        if(propName == null || propName.isBlank()){
            throw new IllegalArgumentException("Property name may not be blank.");
        }
        return properties.getProperty(propName);
    }
    public static String getServerAddress(){
        return getPropertyNamed("server.address");
    }
    public static int getServerPort(){
        return Integer.parseInt(getPropertyNamed("server.port"));
    }
}
