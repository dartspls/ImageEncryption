package com.dan.encryptimg;

import java.util.Arrays;

public class BmpImage {
    private final byte[] img;
    private int sizeInBytes;
    private int byteOffset;

    /**
     * Create a new BMP image object
     *
     * @param data byte array of BMP file contents
     */
    public BmpImage(byte[] data) {
        img = data;
        byteOffset = getBmpOffset();
        sizeInBytes = getImgSize() - byteOffset;
    }

    private int getImgSize() {
        int size = 0;
        for(int i = 2; i < 6; i ++) {
            int t = img[i] & 0xff;
            t <<= (i - 2) * 8;
            size |= t;
        }
        System.err.println("size: " + size);
        return size;
    }

    private int getBmpOffset() {
        int offset = 0;
        for(int i = 10; i < 14; i ++) {
            int t = img[i] & 0xff;
            t <<= (i - 10) * 8;
            offset |= t;
        }
        System.err.println("offset: " + offset);
        return offset;
    }

    /**
     * Get the pixel data for the image
     *
     * @return byte array of image data
     */
    public byte[] getImgData() {
        return Arrays.copyOfRange(img, byteOffset, byteOffset + sizeInBytes);
    }
}
