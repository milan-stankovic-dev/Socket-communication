package org.example.communication;

import lombok.Getter;
import org.example.packet.Packet;
import org.example.util.PacketUtil;
import org.example.util.TextFileUtil;

import java.io.*;
import java.util.logging.Logger;

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
            final int delaySeconds = PacketUtil.getPacketDelayInSec(this.packetToDeliver.getBytes());
            logger.info("Delay is :" + delaySeconds + " seconds.");
            sleep(delaySeconds * 1000L) ;
            this.outputStream.write(this.packetToDeliver.getBytes());
            logger.info("Packet with id: " +
                    PacketUtil.getPacketIdAsInt(this.packetToDeliver.getBytes()) + " delivered to " +
                    "the server.");
        } catch (IOException e) {
            logger.warning("Could not deliver " +
                    PacketUtil.getPacketIdAsInt(this.packetToDeliver.getBytes()) + " to server.");
        } catch (InterruptedException e) {
            logger.info("Stopped execution of packet sending. Saving to file...");

            TextFileUtil.saveOnePacketToFile(this.packetToDeliver, "unsent.bin", true);
            Thread.currentThread().interrupt();
        }
    }


}
