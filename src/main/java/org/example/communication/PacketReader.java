package org.example.communication;

import lombok.Getter;
import org.example.config.LoggerConfig;
import org.example.metadata.PacketMetadata;
import org.example.packet.Packet;
import org.example.util.ByteUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.logging.Logger;

public class PacketReader {
    private final DataInputStream inputStream;
    @Getter
    private final DataOutputStream outputStream;
    private static PacketReader instance;
    private static final Logger logger = Logger.getLogger(PacketReader.class.getName());
    private PacketReader() throws IOException{
            final Socket socket = ConnectionFactory.getInstance()
                    .establishConnection();
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public static PacketReader getInstance() throws IOException{
        return instance != null ? instance : new PacketReader();
    }

    public int readPacketId() throws IOException {
        final byte[] idBytes = new byte[PacketMetadata.DATA_SEGMENT_BYTES];
        int bytesRead = this.inputStream.read(idBytes);

        if (bytesRead == -1) {
            throw new IOException("End of stream reached while reading idBytes");
        } else if (bytesRead != PacketMetadata.DATA_SEGMENT_BYTES) {
            throw new IOException("Unexpected number of bytes read for idBytes");
        }

        return ByteUtil.byteBlockAsIntLE(idBytes);
    }

    public Packet readPacketFully() throws IOException {
        final int packetId = readPacketId();
        final int dataLength = (packetId == PacketMetadata.DUMMY_PACKET_ID) ?
                (PacketMetadata.DUMMY_PACKET_SIZE_BYTES - PacketMetadata.DATA_SEGMENT_BYTES):
                ((packetId == PacketMetadata.CANCEL_PACKET_ID) ?
                        PacketMetadata.CANCEL_PACKET_SIZE_BYTES - PacketMetadata.DATA_SEGMENT_BYTES : -1);

        if (dataLength == -1) {
            logger.warning("UNKNOWN PACKET TYPE (id =" + packetId + ")");
            throw new IOException("Could not read packet.");
        }

        System.out.println(packetId);

        final byte[] packetBytes = new byte[dataLength];
        this.inputStream.readFully(packetBytes);


        logger.info("PACKET ID: " + packetId);
        logger.info("PACKET DATA: ");
        ByteUtil.printDataBits(packetBytes);

        if (packetId == 1) {
            logger.info("PACKET TYPE: Dummy");
        } else {
            logger.info("PACKET TYPE: Cancel");
        }

        final Timestamp receivedAt = new Timestamp(
                System.currentTimeMillis()
        );
        final byte[] fixedPacket = ByteUtil.concatenateByteArrays(
                ByteUtil.intAsByteBlockLE(packetId),
                packetBytes
        );
        return new Packet(fixedPacket, receivedAt);

    }

}
