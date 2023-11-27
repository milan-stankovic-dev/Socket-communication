package org.example.util;

import org.example.config.LoggerConfig;
import org.example.metadata.PacketMetadata;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class TextFileUtil {
    private static final Logger logger = Logger.getLogger(TextFileUtil.class.getName());
    public static void saveListOfPacketsToFile(List<byte[]> packets, String filePath, boolean append) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath, append)) {
            for (byte[] packet : packets) {
                fileOut.write(packet);
                fileOut.write(0);
            }
        } catch (IOException ex) {
            logger.severe("Could not open file for saving packets.");
        }
    }


    public static List<byte[]> readListOfPacketsFromFile(String filePath) {
        final List<byte[]> bytesFromFile = new ArrayList<>();
        try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] packetData;
            while ((packetData = readSinglePacketFromStream(fileIn)) != null) {
                bytesFromFile.add(packetData);
            }
        } catch (IOException ex) {
            logger.severe("Could not open file to read packets.");
        }
        return bytesFromFile;
    }

    private static byte[] readSinglePacketFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream packetBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[PacketMetadata.DUMMY_PACKET_SIZE_BYTES]; //Since only dummy packets are saved
        // (They have a fixed delay unlike Cancel packets).

        int bytesRead = inputStream.read(buffer);
        if (bytesRead != PacketMetadata.DUMMY_PACKET_SIZE_BYTES) {
            return null; // End of file or incomplete packet
        }
        packetBuffer.write(buffer);

        // Skip one byte
        int skippedByte = inputStream.read();
        if (skippedByte == -1) {
            return null; // End of file
        }

        return packetBuffer.toByteArray();
    }


    public static synchronized void saveOnePacketToFile(byte[] packet,
                                            String filePath, boolean append)  {
            if(packet == null ||
                    packet.length != PacketMetadata.DUMMY_PACKET_SIZE_BYTES){
                logger.warning("Packet is corrupted. Won't save");
                return;
            }

            final List<byte[]> wrapperList = new ArrayList<>();
            wrapperList.add(packet);

            saveListOfPacketsToFile(wrapperList, filePath, append);
    }

}
