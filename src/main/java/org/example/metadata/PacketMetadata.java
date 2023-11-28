package org.example.metadata;

import org.example.util.PropertyFileUtil;

/**
 * Contains metadata for different type packets. Fetched dynamically from property file
 */
public interface PacketMetadata {
     /**
      * Expected data size in bytes for each packet segment. 4 by default
      */
     int DATA_SEGMENT_BYTES = PropertyFileUtil.getDataSegmentBytes(); //4 by default
     /**
      * Expected size in bytes for dummy packets. 16 by default
      */
     int DUMMY_PACKET_SIZE_BYTES = PropertyFileUtil.getDummyPacketLength(); //16 by default
     /**
      * Expected size in bytes for cancel packets. 12 by default
      */
     int CANCEL_PACKET_SIZE_BYTES = PropertyFileUtil.getCancelPacketLength(); //12 by default
     /**
      * Id of dummy packets as int. 1 by default
      */
     int DUMMY_PACKET_ID = PropertyFileUtil.getDummyPacketId(); //1 by default
     /**
      * Id of cancel packets as int. 2 by default
      */
     int CANCEL_PACKET_ID = PropertyFileUtil.getCancelPacketId(); //2 by default
}
