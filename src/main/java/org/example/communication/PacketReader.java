package org.example.communication;

import lombok.Getter;
import org.example.packet.Packet;
import org.example.util.ByteUtil;

import java.io.*;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.logging.Logger;

import static org.example.metadata.PacketMetadata.*;
import static org.example.util.ByteUtil.concatenateByteArrays;

public class PacketReader {
    private final DataInputStream inputStream;
    @Getter
    private final DataOutputStream outputStream;
    @Getter
    private static final PacketReader instance = createPacketReader();
    private static final Logger logger = Logger.getLogger(PacketReader.class.getName());
    private PacketReader(DataInputStream inputStream, DataOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    private static PacketReader createPacketReader() {
        try {
            Socket socket = ConnectionFactory.getInstance().establishConnection();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            return new PacketReader(inputStream, outputStream);

        } catch (IOException e) {
            assert logger != null;
            logger.severe("Could not instantiate PacketReader: " + e.getMessage());
            return null;
        }
    }

    public int readPacketId() throws IOException {
        final byte[] idBytes = new byte[DATA_SEGMENT_BYTES];
        int bytesRead = this.inputStream.read(idBytes);

        if (bytesRead == -1) {
            throw new IOException("End of stream reached while reading idBytes");
        } else if (bytesRead != DATA_SEGMENT_BYTES) {
            throw new IOException("Unexpected number of bytes read for idBytes");
        }

        return ByteUtil.byteBlockAsIntLE(idBytes);
    }

    public Packet readPacketFully() throws IOException {
        final int packetId = readPacketId();
        final int dataLength = resolvePacketLengthById(packetId);

        if (dataLength == -1) {
            logger.warning("UNKNOWN PACKET TYPE (id =" + packetId + ")");
            throw new IOException("Could not read packet.");
        }

        final byte[] packetBytes = new byte[dataLength];
        this.inputStream.readFully(packetBytes);

        logPacketInformation(packetId, packetBytes);

        return wrapDataIntoPacket(packetId,packetBytes);

    }
    private int resolvePacketLengthById(int packetId){
        final int packetLength;
        if(packetId == DUMMY_PACKET_ID){
            packetLength = DUMMY_PACKET_SIZE_BYTES
                    - DATA_SEGMENT_BYTES;
        } else if(packetId == CANCEL_PACKET_ID){
            packetLength = CANCEL_PACKET_SIZE_BYTES
                    - DATA_SEGMENT_BYTES;
        } else {
            packetLength = -1;
        }
        return packetLength;
    }

    private Packet wrapDataIntoPacket(int packetId, byte[] packetBytes){

        final Timestamp receivedAt = new Timestamp(
                System.currentTimeMillis()
        );
        final byte[] fixedPacket = concatenateByteArrays(
                ByteUtil.intAsByteBlockLE(packetId),
                packetBytes
        );
        return new Packet(fixedPacket, receivedAt);
    }

    private void logPacketInformation(int packetId, byte[] packetBytes){
        logger.info("PACKET ID: " + packetId);
        logger.info("PACKET DATA: ");
        ByteUtil.printDataBits(packetBytes);

        if (packetId == 1) {
            logger.info("PACKET TYPE: Dummy");
        } else {
            logger.info("PACKET TYPE: Cancel");
        }
    }

}
