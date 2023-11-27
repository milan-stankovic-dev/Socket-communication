package org.example.communication;

import lombok.Getter;
import org.example.config.LoggerConfig;
import org.example.util.PacketUtil;
import org.example.util.PropertyFileUtil;
import org.example.util.TextFileUtil;
import org.w3c.dom.Text;

import java.io.*;
import java.util.logging.Logger;

public class PacketWriter extends Thread {
    private final DataOutputStream outputStream;
    private static final Logger logger = Logger.getLogger(PacketWriter.currentThread().getName());
    @Getter
    private final byte[] packetToDeliver;

    public PacketWriter(DataOutputStream outputStream,byte[] packetToDeliver) {
            this.outputStream = outputStream;
            this.packetToDeliver = packetToDeliver;
            setDaemon(true);
    }

    @Override
    public void run() {
        try {
            final int delaySeconds = PacketUtil.getPacketDelayInSec(this.packetToDeliver);
            logger.info("Delay is :" + delaySeconds + " seconds.");
            sleep(delaySeconds * 1000L) ;
            this.outputStream.write(this.packetToDeliver);
            logger.info("Packet with id: " +
                    PacketUtil.getPacketIdAsInt(this.packetToDeliver) + " delivered to " +
                    "the server.");
        } catch (IOException e) {
            logger.warning("Could not deliver " +
                    PacketUtil.getPacketIdAsInt(this.packetToDeliver) + " to server.");
        } catch (InterruptedException e) {
            logger.info("Stopped execution of packet sending. Saving to file...");

            TextFileUtil.saveOnePacketToFile(this.packetToDeliver, "unsent.bin", true);
            Thread.currentThread().interrupt();
        }
    }


}
