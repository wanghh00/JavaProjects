package utils;

import java.nio.ByteBuffer;

public class ByteUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
    
    public static long bytesToLong(byte[] bytes, int offset) {
    	buffer.put(bytes, offset, Long.BYTES);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
        
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static void main(String[] args) {
    	byte[] arr = longToBytes(15);
    	System.out.println(bytesToHex(arr));
    	
    	
    }
}
