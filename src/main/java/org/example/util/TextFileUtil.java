package org.example.util;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class TextFileUtil {
    private static final Logger logger = Logger.getLogger(TextFileUtil.class.getName());
    public static void saveListOfPacketsToFile(List<byte[]> packets,
                                               String filePath) throws Exception {
        validateFilename(filePath);

        try(final FileOutputStream fileOut = new FileOutputStream(filePath);
            final ObjectOutputStream objOut = new ObjectOutputStream(fileOut)){
            packets.forEach((packet)->
                    saveOnePacketToFile(packet,filePath,objOut));
        }catch( IOException ex){
            logger.severe("Could not open file for saving " +
                    "packets.");
        };
    }

    public static List<byte[]> readListOfPacketsFromFile(String filePath)
            throws Exception {
        validateFilename(filePath);
        final List<byte[]> bytesFromFile = new ArrayList<>();
        try(final InputStream fileIn = new FileInputStream(filePath);
            final ObjectInputStream objIn = new ObjectInputStream(fileIn)){

            Object obj;

            while((obj = objIn.readObject()) != null){
                bytesFromFile.add((byte[]) obj);
            }
        }catch (IOException ex){
            logger.severe("Could not open file to read " +
                    "packets.");
        } catch (ClassNotFoundException e) {
            logger.warning("Unknown class of byte[].");
        }
        return bytesFromFile;
    }

    private static void saveOnePacketToFile(byte[] packet,
                                            String filePath,
                                            ObjectOutputStream objOut)  {
        try {
            if(packet == null){
                return;
            }
            objOut.writeObject(packet);
            logger.info("Saved packet " + Arrays.toString(packet) +
                    " to file " + filePath + ".");
        }catch (IOException e){
            logger.warning("Could not save packet" + Arrays.toString(packet) +
                    " to file " + filePath + ".");
        }
    }

    private static void validateFilename(String filePath) throws Exception{

        if(filePath == null || filePath.isBlank()){
            throw new Exception("You must specify a file to write to.");
        }

    }
}
