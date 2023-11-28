package org.example.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Utility class for working with raw bytes
 */
public class ByteUtil {

    /**
     * Converts block of bytes encoded in LITTLE ENDIAN into an integer.
     * @param bytes bytes to be converted
     * @return integer representation of bytes
     */
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

    /**
     * Converts integer into a block of bytes encoded in LITTLE ENDIAN.
     * @param toConvert integer to be converted
     * @return byte representation of integer
     */
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

    /**
     * Creates string representation of single byte
     * @param b byte to be represented as string
     * @return string representation of single byte
     */
    public static String byteToBinaryString(byte b){
        return String.format("%s", Integer.toBinaryString(b & 0xFF))
                .replace(' ', '0');
    }

    /**
     * Prints string representation of array of bytes to console.
     * Appends said representation with "NEXT PACKET"
     * @param bytes bytes to be printed
     */
    public static void printDataBits(byte[] bytes){
        for(var b : bytes){
            System.out.print(byteToBinaryString(b) + " ");
        }
        System.out.println("\nNEXT PACKET");
    }

    /**
     * Concatenates two byte arrays (second after first)
     * @param array1 first array to be concatenated
     * @param array2 second array to be concatenated
     * @return result of concatenation
     */
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
