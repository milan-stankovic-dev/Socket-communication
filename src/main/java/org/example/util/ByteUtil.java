package org.example.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ByteUtil {

    public static byte[] intToBytes(int value) {
        final byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[2] = (byte) ((value >> 16) & 0xFF);
        bytes[3] = (byte) ((value >> 24) & 0xFF);
        return bytes;
    }

    public static int byteBlockAsIntLE(byte[] bytes){
//        int converted = 0;
//        final int oneByteBits = 8;
//        final int sizeOfBlock = 4;
//
//        for(int i = sizeOfBlock - 1; i>=0;i--){
//            converted |= ((bytes[i] & 0xFF) << i * oneByteBits);
//        }
//        return converted;
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] intAsByteBlockLE(int toConvert){
//        byte[] bytes = new byte[sizeOfBlock];
//        final int oneByteBits = 8;
//        final int sizeOfBlock = 4;
//
//        for(int i = 0; i<sizeOfBlock; i++){
//            bytes[i] = (byte) ((toConvert >> (i * oneByteBits) & 0xFF));
//        }
//
//        return bytes;
        final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(toConvert);
        return buffer.array();
    }

    public static String byteToBinaryString(byte b){
        return String.format("%s", Integer.toBinaryString(b & 0xFF))
                .replace(' ', '0');
    }

    public static void printDataBits(byte[] bytes){
        for(var b : bytes){
            System.out.print(byteToBinaryString(b) + " ");
        }
        System.out.println("NEXT PACKET");
    }
    public static byte[] concatenateByteArrays(byte[] array1,
                                             byte[] array2){
        final int length1 = array1.length;
        final int length2 = array2.length;

        final byte[] result = Arrays.copyOf(array1,
                length1 + length2);
        System.arraycopy(array2, 0, result, length1,length2);
        return result;
    }
}
