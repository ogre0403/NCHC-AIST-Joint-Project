package util;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HammingDistance {
    private static int[] BYTE_BIT_COUNTS;
    static {
        BYTE_BIT_COUNTS = new int[256];
        for (int i = 0; i < 256; i++) {
            BYTE_BIT_COUNTS[i] = (i & 1) + BYTE_BIT_COUNTS[i / 2];
        }
    }


    public static int getHammingDistance(byte i1, byte i2) {
        return BYTE_BIT_COUNTS[(i1 ^ i2) & 0xFF];
    }

    public static int getHammingDistance(byte[] i1, byte[] i2) {
        if(i1.length != i2.length)
            return -1;

        int distance = 0;
        for(int i = 0;i < i1.length;i++){
            distance = distance + getHammingDistance(i1[i],i2[i]);
        }
        return distance;
    }
}
