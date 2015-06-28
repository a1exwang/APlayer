package com.iced.alexwang.libs;

/**
 * Created by alexwang on 15-6-28.
 */
public class MarshalHelper {
    public static byte[] intToBytes(int i) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (i & 0x000000FF);
        ret[1] = (byte) ((i & 0x0000FF00) >> 8);
        ret[2] = (byte) ((i & 0x00FF0000) >> 16);
        ret[3] = (byte) ((i & 0xFF000000) >> 24);
        return ret;
    }

    public static int bytesToInt(byte[] buf, int offset) {
        return buf[offset] | (buf[offset + 1] << 8) | (buf[offset + 2] << 16) | (buf[offset + 3] << 24) ;
    }

}
