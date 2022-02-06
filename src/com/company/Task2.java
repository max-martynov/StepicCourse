package com.company;

import java.io.IOException;

public class Task2 {

    private static final byte nByte = 10;
    private static final byte rByte = 13;

    public static void main(String[] args) throws IOException {
        int prev = System.in.read();
        int cur;
        while ((cur = System.in.read()) != -1) {
            if (!(prev == rByte && cur == nByte))
                System.out.write((byte) prev);
            prev = cur;
        }
        if (prev != -1)
            System.out.write((byte) prev);
        System.out.flush();
    }
}
