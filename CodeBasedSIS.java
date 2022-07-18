package org.BCHLibrary;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;


public class CodeBasedSIS {
    private final BCHClass bchObj = new BCHClass();

    public CodeBasedSIS(){}
    //Generate AES key which is encoded into byte array for PRNG (Pseudo Random Number Generator)
    public byte[] generateRotationKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        return keyGenerator.generateKey().getEncoded();
    }

    //PRNG function that generate N pseudo random numbers
    public int[] generatePseudoRandomNumbers(byte[] seed, int N) {
        int[] randomNumbers = new int[N];
        SecureRandom pseudoRandomNumberGenerator = new SecureRandom(seed);
        for(int i = 0; i < N; i++) {
            randomNumbers[i] = pseudoRandomNumberGenerator.nextInt(8);
        }

        return randomNumbers;
    }

    public byte[][] generatePixelForVerify(int nPixels, int participants, int[] originalPixels) {
        byte[][] allShadows = new byte[nPixels][participants];
        int[] pixelForSharing;
        byte[] shadowPixels;
        int startIndex = 0;
        int endIndex = 4;
        while(endIndex <= originalPixels.length) {
            pixelForSharing = Arrays.copyOfRange(originalPixels, startIndex, endIndex);
            shadowPixels = bchObj.interleavingEncodeBCH(pixelForSharing);
            System.arraycopy(shadowPixels, 0, allShadows[startIndex / 4], 0, shadowPixels.length);
            startIndex = endIndex;
            endIndex += 4;
        }

        return allShadows;
    }
    public int[] generatePixels(int num) {
        int[] pixels = new int[num];
        Random randomNumberGenerator = new Random(0x7fffffff);
        for(int i = 0; i < num; i++) {
            pixels[i] = randomNumberGenerator.nextInt(256);
        }

        return pixels;
    }

    public byte[] cyclicRightShiftPixels(byte[] pixels, int offset) {
        for(int i = 0; i < pixels.length; i++) {
            pixels[i] = (byte)(((pixels[i] & 0xff) >>> offset) | (pixels[i] << (8 - offset)));
        }
        return pixels;
    }

    //randomly tamper a participant's shadow for experiment
    public void randomTamperParticipantShadows(byte[][] shadows, int participants) {
        int[] tamperedPixels = generatePixels(shadows.length);
        for(int i = 0; i < shadows.length; i++) {
            shadows[i][participants - 1] = (byte) tamperedPixels[i];
        }
    }

    public byte[][] getFinalShadows() throws NoSuchAlgorithmException {
        byte[] secretKey = generateRotationKey();
        int[] rotateTimes = generatePseudoRandomNumbers(secretKey, 300);
        int[] secretPixels = generatePixels(1200);
        byte[][] shadowPixels = generatePixelForVerify(300, 7, secretPixels);

        //Rotate right the shadow pixels
        for(int i = 0; i < shadowPixels.length; i++) {
            System.arraycopy(cyclicRightShiftPixels(shadowPixels[i], rotateTimes[i]), 0, shadowPixels[i], 0, shadowPixels[i].length);
        }

        return shadowPixels;
    }
}
