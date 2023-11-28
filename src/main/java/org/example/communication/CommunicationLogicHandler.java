package org.example.communication;

import lombok.Getter;
import org.example.packet.Packet;
import org.example.user_interaction.UserQuitThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static org.example.util.PacketUtil.*;
import static org.example.util.TextFileUtil.readListOfPacketsFromFile;
import static org.example.util.TextFileUtil.saveListOfPacketsToFile;

/**
 * Singleton class used in handling logic for async socket network communication.
 */
public class CommunicationLogicHandler {
    /**
     * list of packets to be processed
     */
    private final List<Packet> unsentPackets;
    /**
     * thread executors for managing threads that write to the server
     */
    private final ExecutorService executors = Executors.newCachedThreadPool();
    /**
     * singleton pattern instance (contains getter)
     */
    @Getter
    private static final CommunicationLogicHandler instance = new CommunicationLogicHandler();

    /**
     * private constructor. Fetches unsent packets from a file
     */
    private CommunicationLogicHandler() {
        this.unsentPackets = readListOfPacketsFromFile("unsent.bin");
    }

    /**
     * logger instance
     */
    private final Logger logger = Logger.getLogger(CommunicationLogicHandler.class.getName());

    /**
     * Handler for writer threads. Tells executors to make thread for new packet received.
     * @param outputStream socket communication stream
     * @param packet packet to be further processed by a PacketWriter thread
     *
     */
    private void handleWriterThreads(DataOutputStream outputStream,
                                     Packet packet) {

        final PacketWriter writer = new PacketWriter(outputStream, packet);
        this.executors.execute(writer);
    }

    /**
     * Manages communication logic. While the UserQuitThread is active, goes through
     * backlog of unsent packets, sends them to server, when done, reads one packet,
     * and creates a separate PacketWriter thread to send packet to server for each packet
     * received.
     * @param outputStream socket communication stream
     */
    public void handleCommunicationLogic(DataOutputStream outputStream){
        var userQuitThread = new UserQuitThread();
        userQuitThread.start();

        try {
            final PacketReader reader = PacketReader.getInstance();

            while(!userQuitThread.isToQuit()) {
                if(!this.unsentPackets.isEmpty()){
                    logger.info("UNSENT PACKETS EXIST... SENDING");
                    handleUnsentPackets(outputStream);
                }
                final var packet = reader.readPacketFully();
                handleWriterThreads(reader.getOutputStream(),packet);
            }

            logger.info("Saving unsent to file and shutting down...");
            executors.shutdownNow();
            saveListOfPacketsToFile(this.unsentPackets, "unsent.bin", false);
            logger.info("Saved all from previous backlog.");

        } catch (IOException e) {
            logger.severe("IOException when handling communication logic: " + e.getMessage());
        }

    }

    /**
     * Handles unsent packets in backlog. Warns and skips corrupted packets. Removes processed
     * packets from backlog.
     * @param outputStream socket communication stream
     *
     */
    private void handleUnsentPackets(DataOutputStream outputStream) {
        Iterator<Packet> iterator = unsentPackets.iterator();
        while(iterator.hasNext()) {
            final Packet packet = iterator.next();

                if(isPacketCorrupted(packet)){
                    logger.warning("The packet stored for further sending is corrupted!");
                    iterator.remove();
                    continue;
                }

                handleWriterThreads(outputStream, packet);
                iterator.remove();

        }
    }



}
