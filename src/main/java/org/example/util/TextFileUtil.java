package org.example.util;

import org.example.metadata.PacketMetadata;
import org.example.packet.Packet;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

public class TextFileUtil {
    private static final Logger logger = Logger.getLogger(TextFileUtil.class.getName());

    public static void saveListOfPacketsToFile(List<Packet> packets, String filePath, boolean append) {
        try (DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(filePath, append))) {
            for (final Packet packet : packets) {
                dataOut.write(packet.getBytes());
                dataOut.writeLong(packet.getReceivedAt().getTime());
            }
        } catch (IOException ex) {
            logger.severe("Could not open file for saving packets.");
        }
    }
    public static List<Packet> readListOfPacketsFromFile(String filePath) {
        final List<Packet> packetsFromFile = new ArrayList<>();
        try (DataInputStream dataIn = new DataInputStream(new FileInputStream(filePath))) {
            while (true) {
                try {
                    final byte[] packetBytes = new byte[PacketMetadata.DUMMY_PACKET_SIZE_BYTES];
                    dataIn.readFully(packetBytes);

                    final long timestampMillis = dataIn.readLong();
                    final Timestamp timestamp = new Timestamp(timestampMillis);

                    final Packet packet = new Packet(packetBytes, timestamp);
                    packetsFromFile.add(packet);
                } catch (EOFException eof) {
                    break;
                }
            }
        } catch (IOException ex) {
            logger.severe("Could not open file to read packets.");
            ex.printStackTrace();
        }
        return packetsFromFile;
    }



    public static synchronized void saveOnePacketToFile(Packet packet, String filePath, boolean append) {
        if (packet == null) {
            logger.warning("Packet is null. Won't save.");
            return;
        }

        if (packet.getBytes().length != PacketMetadata.DUMMY_PACKET_SIZE_BYTES){
            logger.warning("Packet is corrupted. Won't save. Packet: " +
                    Arrays.toString(packet.getBytes()));
            logger.warning("PACKET LEN IS " + packet.getBytes().length);
            return;
        }

        final List<Packet> wrapperList = new ArrayList<>();
        wrapperList.add(packet);

        saveListOfPacketsToFile(wrapperList, filePath, append);
    }

}
