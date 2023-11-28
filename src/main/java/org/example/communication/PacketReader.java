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

/**
 * Used to read packets from server
 */
public class PacketReader {
    /**
     * socket communication input stream
     */
    private final DataInputStream inputStream;
    /**
     * socket communication output stream (contains getter)
     */
    @Getter
    private final DataOutputStream outputStream;
    /**
     * singleton pattern instance (contains getter)
     */
    @Getter
    private static final PacketReader instance = createPacketReader();
    /**
     * logger instance
     */
    private static final Logger logger = Logger.getLogger(PacketReader.class.getName());

    /**
     * Private constructor
     * @param inputStream socket communication input stream
     * @param outputStream socket communication output stream
     */
    private PacketReader(DataInputStream inputStream, DataOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * Initializes PacketReader fully and properly if connection with server can be made
     * @return PacketReader instance if connection can be made, null otherwise
     */
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

    /**
     * Reads id of received packet for further buffer space initialization
     * @return id of packet as int
     * @throws IOException if inputStream cannot read bytes properly
     */
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

    /**
     * Reads packet data fully according to packetId; appends id bytes to newly read
     * bytes to ensure full packet reading.
     * @return instance of wrapper type Packet containing packet data and Timestamp
     * of receipt.
     * @throws IOException if inputStream cannot read bytes properly
     */
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

    /**
     * Determines packet length to be allocated according to packet id.
     * @param packetId type of packet, read from packet header
     * @return proper buffer size for rest of packet (excluding id header) to be allocated.
     */
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

    /**
     * Wraps read packet bytes (combined id bytes and rest) into type Packet,
     * along with timestamp of receipt.
     * @param packetId type of packet, read from packet header
     * @param packetBytes bytes of packet (excluding id bytes)
     * @return instance of wrapper type Packet containing byte data and timestamp
     * of receipt
     */
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

    /**
     * Logs data of received packet.
     * @param packetId type of packet, read from packet header
     * @param packetBytes bytes of packet (excluding id bytes)
     */

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
