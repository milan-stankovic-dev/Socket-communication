package org.example.communication;

import lombok.Getter;
import org.example.metadata.PacketMetadata;
import org.example.packet.Packet;
import org.example.user_interaction.UserQuitThread;
import org.example.util.PacketUtil;
import org.example.util.TextFileUtil;

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

public class CommunicationLogicHandler {
    private final List<Packet> unsentPackets;
    private final ExecutorService executors = Executors.newCachedThreadPool();
    @Getter
    private static final CommunicationLogicHandler instance = new CommunicationLogicHandler();
    private CommunicationLogicHandler() {
        this.unsentPackets = readListOfPacketsFromFile("unsent.bin");
    }
    private final Logger logger = Logger.getLogger(CommunicationLogicHandler.class.getName());
    private void handleWriterThreads(DataOutputStream outputStream,
                                     Packet packet) throws IOException{

        final PacketWriter writer = new PacketWriter(outputStream, packet);
        this.executors.execute(writer);
    }

    public void handleCommunicationLogic(DataOutputStream outputStream){
        var userQuitThread = new UserQuitThread();
        userQuitThread.start();

        try {
            final PacketReader reader = PacketReader.getInstance();

            while(!userQuitThread.isToQuit()) {
                if(!this.unsentPackets.isEmpty()){
                    logger.info("UNSENT PACKETS EXIST... SENDING");
                    handleUnsentPackets(outputStream, this.unsentPackets);
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

    private void handleUnsentPackets(DataOutputStream outputStream, List<Packet> unsentPackets) {
        Iterator<Packet> iterator = unsentPackets.iterator();
        while(iterator.hasNext()) {
            final Packet packet = iterator.next();
            try {

                if(isPacketCorrupted(packet)){
                    logger.warning("The packet stored for further sending is corrupted!");
                    iterator.remove();
                    continue;
                }

                handleWriterThreads(outputStream, packet);
                iterator.remove();
            } catch (IOException e) {
                logger.warning(new StringBuilder()
                        .append("Could not send packet with id ")
                        .append(getPacketIdAsInt(packet.bytes()))
                        .append(". This packet had been stored for sending later.").toString());
            }
        }
    }



}
