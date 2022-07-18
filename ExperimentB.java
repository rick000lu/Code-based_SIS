package org.BCHLibrary;

import java.security.NoSuchAlgorithmException;

public class ExperimentB {
    public static final CodeBasedSIS codeBasedSISObj = new CodeBasedSIS();
    public static final BCHClass bchObj = new BCHClass();
    public static void main(String[] args) throws NoSuchAlgorithmException {

        //get final shadow pixels
        byte[][] finalShadowPixels = codeBasedSISObj.getFinalShadows();
        codeBasedSISObj.randomTamperParticipantShadows(finalShadowPixels, 5);
        codeBasedSISObj.randomTamperParticipantShadows(finalShadowPixels, 6);
        int[] syndromeCount = new int[8];
        for(int i = 0; i < finalShadowPixels.length; i++) {
            int[][] finalPixelsBitArray = bchObj.convertEightPixelsToBitArray(finalShadowPixels[i]);
            int[][] bchCodeBitArray = bchObj.matrixTranspose(finalPixelsBitArray);
            int[][] syndromes = new int[8][3];
            int[] currentSyndromeCount;

            for(int j = 0; j < bchCodeBitArray.length; j++) {
                System.arraycopy(bchObj.calculateSyndrome(bchCodeBitArray[j]), 0, syndromes[j], 0, syndromes[j].length);
            }

            currentSyndromeCount = bchObj.syndromeCount(syndromes);
            for(int j = 0; j < syndromeCount.length; j++) {
                syndromeCount[j] += currentSyndromeCount[j];
            }
        }

        bchObj.showSyndromeCount(syndromeCount);

    }
}
