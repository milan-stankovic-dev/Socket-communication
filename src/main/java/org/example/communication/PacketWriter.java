package org.example.communication;

import lombok.Getter;
import org.example.metadata.PacketMetadata;
import org.example.packet.Packet;
import org.example.util.PacketUtil;
import org.example.util.TextFileUtil;

import java.io.*;
import java.util.logging.Logger;

import static org.example.metadata.PacketMetadata.CANCEL_PACKET_ID;
import static org.example.metadata.PacketMetadata.CANCEL_PACKET_SIZE_BYTES;
import static org.example.util.PacketUtil.*;
import static org.example.util.TextFileUtil.saveOnePacketToFile;

public class PacketWriter extends Thread {
    private final DataOutputStream outputStream;
    private static final Logger logger = Logger.getLogger(PacketWriter.currentThread().getName());
    @Getter
    private final Packet packetToDeliver;

    public PacketWriter(DataOutputStream outputStream, Packet packetToDeliver) {
            this.outputStream = outputStream;
            this.packetToDeliver = packetToDeliver;
            setDaemon(true);
    }

    @Override
    public void run() {
        try {
            final int delaySeconds = getPacketDelayInSec(this.packetToDeliver.bytes());
            logger.info(new StringBuilder()
                    .append("Delay is :")
                    .append(delaySeconds)
                    .append(" seconds.").toString());

            handlePacketSending(packetToDeliver, delaySeconds);

        } catch (IOException e) {
            logger.warning(new StringBuilder()
                    .append("Could not deliver ")
                    .append(getPacketIdAsInt(this.packetToDeliver.bytes()))
                    .append(" to server.").toString());

        } catch (InterruptedException e) {
            logger.info("Stopped execution of packet sending. Saving to file...");

            final int packetLen = getPacketLenAsInt(packetToDeliver.bytes());

            if(packetLen == CANCEL_PACKET_SIZE_BYTES){
                logger.info("Packet designated for saving to file found to contain" +
                        " cancel packet id. Skipping since cancel packets have no delay...");
            }else {
                saveOnePacketToFile(this.packetToDeliver, "unsent.bin", true);
            }
            Thread.currentThread().interrupt();
        }
    }

    private void handlePacketSending(Packet packet, int delaySeconds) throws IOException,
            InterruptedException {

        if(isPacketStale(packet)) {
            logger.info("Packet is stale!");
//            return;
//            To disable and enable sending of stale packets, toggle the return; statement.
//            (commented out = will send, left in = won't send)
        } else {
            sleep(delaySeconds * 1000L);
        }
        this.outputStream.write(this.packetToDeliver.bytes());

        logger.info(new StringBuilder()
                .append("Packet with id: ")
                .append(getPacketIdAsInt(this.packetToDeliver.bytes()))
                .append(" delivered to ")
                .append("the server.").toString());
    }

}
