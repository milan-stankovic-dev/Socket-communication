package org.example.communication;

import org.example.config.LoggerConfig;
import org.example.user_interaction.UserQuitThread;
import org.example.util.PacketUtil;
import org.example.util.TextFileUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class CommunicationLogicHandler {
    private final List<byte[]> unsentPackets;
    private final ExecutorService executors = Executors.newCachedThreadPool();
    private static CommunicationLogicHandler instance;
    private CommunicationLogicHandler() {
        this.unsentPackets = TextFileUtil.readListOfPacketsFromFile("unsent.bin");
    }
    private final Logger logger = Logger.getLogger(CommunicationLogicHandler.class.getName());
    private void handleWriterThreads(DataOutputStream outputStream, byte[] packet) throws IOException{
        final PacketWriter writer = new PacketWriter(outputStream, packet);
        this.executors.execute(writer);
    }

    public static CommunicationLogicHandler getInstance() {
        return instance != null ? instance : new CommunicationLogicHandler();
    }

    public void handleCommunicationLogic(DataOutputStream outputStream){
        var userQuitThread = new UserQuitThread();

        try {
            final PacketReader reader = PacketReader.getInstance();

            while(!userQuitThread.isShouldQuit()) {
                if(!this.unsentPackets.isEmpty()){
                    logger.info("UNSENT PACKETS EXIST... SENDING");
                    handleUnsentPackets(outputStream, this.unsentPackets);
                }
                final var packet = reader
                        .readPacketFully();
                handleWriterThreads(reader.getOutputStream(),packet);
            }

            logger.info("Saving unsent to file and shutting down...");
            executors.shutdownNow();
            TextFileUtil.saveListOfPacketsToFile(this.unsentPackets, "unsent.bin", false);
            logger.info("Saved all from previous backlog.");
        } catch (IOException e) {
            logger.severe("IOException when handling communication logic: " + e.getMessage());
        }

    }

    private void handleUnsentPackets(DataOutputStream outputStream, List<byte[]> unsentPackets) {
        Iterator<byte[]> iterator = unsentPackets.iterator();
        while(iterator.hasNext()) {
            final byte[] packet = iterator.next();
            try {
                handleWriterThreads(outputStream, packet);
                iterator.remove();
            } catch (IOException e) {
                logger.warning("Could not send packet with id "
                        + PacketUtil.getPacketIdAsInt(packet) +
                        ". This packet had been stored for sending later.");
            }
        }
    }

}
