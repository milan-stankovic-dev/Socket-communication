package org.example.metadata;

import org.example.util.PropertyFileUtil;

public interface PacketMetadata {
     int DATA_SEGMENT_BYTES = PropertyFileUtil.getDataSegmentBytes(); //4 by default
     int DUMMY_PACKET_SIZE_BYTES = PropertyFileUtil.getDummyPacketLength(); //16 by default
     int CANCEL_PACKET_SIZE_BYTES = PropertyFileUtil.getCancelPacketLength(); //12 by default
     int DUMMY_PACKET_ID = PropertyFileUtil.getDummyPacketId(); //1 by default
     int CANCEL_PACKET_ID = PropertyFileUtil.getCancelPacketId(); //2 by default
}