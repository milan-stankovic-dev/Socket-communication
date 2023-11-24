package org.example.data_packet;

import lombok.Data;

@Data
public class DataPacket {
    private long packetId;
    private long len;
    private long id;
    private long delayInSec;
}
