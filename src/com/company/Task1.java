package com.company;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class Task1 {

    public static void main(String[] args) throws IOException {
        InputStream stream;
        int result;
        stream = getStream( new byte[] { 0x33, 0x45, 0x01});

        result = checkSumOfStream(stream);
        System.out.print(result);
    }

    public static InputStream getStream(byte [] data)  {
        return new ByteArrayInputStream(data);
    }

    public static int checkSumOfStream(InputStream inputStream) throws IOException {
        int checkSum = 0;
        int bit;
        while ((bit = inputStream.read()) != -1) {
            checkSum = Integer.rotateLeft(checkSum, 1) ^ bit;
        }
        return checkSum;
    }

}

