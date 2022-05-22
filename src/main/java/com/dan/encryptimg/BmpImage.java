package com.dan.encryptimg;

import java.util.Arrays;

public class BmpImage {
    private final byte[] img;
    private final int sizeInBytes;
    private final int byteOffset;

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

    /**
     * Reads image size from header
     *
     * @return size of images in bytes
     */
    private int getImgSize() {
        int size = 0;
        for(int i = 2; i < 6; i ++) {
            int t = img[i] & 0xff;
            t <<= (i - 2) * 8;
            size |= t;
        }
        return size;
    }

    /**
     * Reads the byte offset of the start of the image data in the bmp file
     *
     * @return byte index of the start of the image data
     */
    private int getBmpOffset() {
        int offset = 0;
        for(int i = 10; i < 14; i ++) {
            int t = img[i] & 0xff;
            t <<= (i - 10) * 8;
            offset |= t;
        }
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
