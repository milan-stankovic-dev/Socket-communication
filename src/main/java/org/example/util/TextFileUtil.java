package org.example.util;

import org.example.packet.Packet;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

import static org.example.metadata.PacketMetadata.DUMMY_PACKET_SIZE_BYTES;

/**
 * Util class for working with text files
 */
public class TextFileUtil {
    /**
     * logger instance
     */
    private static final Logger logger = Logger.getLogger(TextFileUtil.class.getName());

    /**
     * Saves a list of packets to specified file path. Can be a fresh save or an append.
     * @param packets list of packets to be saved
     * @param filePath path to file designated for saving
     * @param append if purpose is to append should be true, false otherwise
     */
    public static void saveListOfPacketsToFile(List<Packet> packets, String filePath, boolean append) {
        try (DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(filePath, append))) {
            for (final Packet packet : packets) {
                dataOut.write(packet.bytes());
                dataOut.writeLong(packet.receivedAt().getTime());
            }
        } catch (IOException e) {
            logger.severe("Could not open file for saving packets.");
        }
    }

    /**
     * Retrieves a list of packets from file at specified path.
     * @param filePath path to file from which list is to be retrieved
     * @return list of retrieved packets
     */
    public static List<Packet> readListOfPacketsFromFile(String filePath) {
        final List<Packet> packetsFromFile = new ArrayList<>();
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(filePath))) {
            while (true) {
                try {
                    final byte[] packetBytes = new byte[DUMMY_PACKET_SIZE_BYTES];
                    dataIn.readFully(packetBytes);

                    final long timestampMillis = dataIn.readLong();
                    final Timestamp timestamp = new Timestamp(timestampMillis);

                    final Packet packet = new Packet(packetBytes, timestamp);
                    packetsFromFile.add(packet);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.severe("Could not open file to read packets. " +
                    e.getMessage());
        }
        return packetsFromFile;
    }

    /**
     * Saves one packet to file specified with filePath (method is synchronized).
     * @param packet packet to be saved
     * @param filePath path to file designated for saving
     * @param append if purpose is to append should be true, false otherwise
     */
    public static synchronized void saveOnePacketToFile(Packet packet, String filePath, boolean append) {
        if (packet == null) {
            logger.warning("Packet is null. Won't save.");
            return;
        }

        if (packet.bytes().length != DUMMY_PACKET_SIZE_BYTES){
            logger.warning("Packet is corrupted. Won't save. Packet: " +
                    Arrays.toString(packet.bytes()));
            logger.warning("PACKET LEN IS " + packet.bytes().length);
            return;
        }

        final List<Packet> wrapperList = new ArrayList<>();
        wrapperList.add(packet);

        saveListOfPacketsToFile(wrapperList, filePath, append);
    }

}
