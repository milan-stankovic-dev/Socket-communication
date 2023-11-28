package org.example.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Util class for file properties
 */
public class PropertyFileUtil {
    /**
     * properties instance
     */
    private static final Properties properties;

    static {
        properties = new Properties();
        loadProperties("src/main/resources/socket_config.properties");
    }

    /**
     * Loads all properties from specified property file path
     * @param filePath path to property file
     */
    private static void loadProperties(String filePath){
        try {
            final var inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            System.out.println("Input stream for given file name " +
                    "cannot be created. Please check if file is present.");
        } catch (IOException e) {
            System.out.println("Properties failed to load.");
        }
    }

    /**
     * Gets property from properties instance (after loading) with given name
     * @param propName name of property to be gotten
     * @return property with said name
     */
    private static String getPropertyNamed(String propName){
        if(propName == null || propName.isBlank()){
            throw new IllegalArgumentException("Property name may not be blank.");
        }
        return properties.getProperty(propName);
    }

    /**
     * Get method for server.address property
     * @return server address property
     */
    public static String getServerAddress(){
        return getPropertyNamed("server.address");
    }

    /**
     * Get method for server.port property
     * @return server port property
     */
    public static int getServerPort(){
        return Integer.parseInt(getPropertyNamed("server.port"));
    }

    /**
     * Get method for data.segment.bytes property
     * @return server data segment property
     */
    public static int getDataSegmentBytes(){
        return Integer.parseInt(getPropertyNamed("data.segment.bytes"));
    }

    /**
     * Get method for dummy.packet.length property
     * @return dummy packet length property
     */
    public static int getDummyPacketLength(){
        return Integer.parseInt(getPropertyNamed("dummy.packet.length"));
    }

    /**
     * Get method for cancel.packet.length property
     * @return cancel packet length property
     */
    public static int getCancelPacketLength(){
        return Integer.parseInt(getPropertyNamed("cancel.packet.length"));
    }

    /**
     * Get method for dummy.packet.id property
     * @return dummy packet id property
     */
    public static int getDummyPacketId(){
        return Integer.parseInt(getPropertyNamed("dummy.packet.id"));
    }

    /**
     * Get method for cancel.packet.id property
     * @return cancel packet id property
     */
    public static int getCancelPacketId(){
        return Integer.parseInt(getPropertyNamed("cancel.packet.id"));
    }
}
