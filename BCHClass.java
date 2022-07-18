package org.BCHLibrary;
import java.util.Arrays;

public class BCHClass {

    public BCHClass(){}
    private final int[][] generatorMatrix = {{1, 1, 0, 1, 0, 0, 0},
                                             {0, 1, 1, 0, 1, 0, 0},
                                             {0, 0, 1, 1, 0, 1, 0},
                                             {0, 0, 0, 1, 1, 0, 1}};

    public final int[][] parityMatrixHTranspose = {{1, 0, 0},
                                                    {0, 1, 0},
                                                    {1, 0, 1},
                                                    {1, 1, 0},
                                                    {1, 1, 1},
                                                    {0, 1, 1},
                                                    {0, 0, 1}};

    //Matrix transpose function
    public int[][] matrixTranspose(int[][] matrix) {
        int[][] transposedMatrix = new int[matrix[0].length][matrix.length];
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[0].length; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }

        return transposedMatrix;
    }

    private int[][] matrixMultiplication(int[][] leftMatrix, int[][] rightMatrix) {
        int[][] multiplicationMatrix = new int[leftMatrix.length][rightMatrix[0].length];
        for (int i = 0; i < leftMatrix.length; i++) {
            for (int j = 0; j < rightMatrix[0].length; j++) {
                multiplicationMatrix[i][j] = 0;
                for (int k = 0; k < leftMatrix[0].length; k++) {
                    //System.out.printf("(%d %d): %d %d %d %d\n", i, j, leftMatrix[i][k], rightMatrix[k][j], (leftMatrix[i][k] & rightMatrix[k][j]), multiplicationMatrix[i][j]);
                    multiplicationMatrix[i][j] = multiplicationMatrix[i][j] ^ (leftMatrix[i][k] & rightMatrix[k][j]);
                }
            }
        }
        return multiplicationMatrix;
    }

    //Convert pixel value into 8-bit int array
    public int[] convertPixelValueToBitArray(int pixel, int length) {
        //Convert pixel value into bit string
        String pixelBitString = String.format("%" + length +"s", Integer.toBinaryString(pixel)).replaceAll(" ", "0");

        //Convert bit string to string array
        String[] pixelBitStringArray = pixelBitString.split("");

        //convert
        int[] bitArray = new int[8];
        for(int i = 0; i < pixelBitStringArray.length; i++){
            bitArray[i] = Integer.parseInt(pixelBitStringArray[i]);
        }

        return bitArray;
    }

    private byte bitArrayToPixel(int[] bitArray) {
        byte pixelValue = 0;
        for(int bit : bitArray) {
            pixelValue <<= 1;
            pixelValue += (byte) bit;
        }

        return pixelValue;
    }

    public int[][] convertEightPixelsToBitArray(byte[] bytePixels) {
        int[] integerPixels = new int[bytePixels.length];
        int[][] pixelBitArray = new int[bytePixels.length][8];

        //convert byte-typed pixels to int-typed pixels
        for(int i = 0; i < bytePixels.length; i++) {
            integerPixels[i] = Byte.toUnsignedInt(bytePixels[i]);
        }

        for(int i = 0; i < integerPixels.length; i++) {
            System.arraycopy(convertPixelValueToBitArray(integerPixels[i], 8), 0, pixelBitArray[i], 0, 8);
        }

        return pixelBitArray;
    }

    //This method will encode 4 pixels into 7-shared pixels
    public byte[] interleavingEncodeBCH(int[] pixels) {
        int[][] pixelBitArray = new int[4][8];
        byte[] bchPixels = new byte[7];
        for(int i = 0; i < pixels.length; i++) {
            int[] bitArray = convertPixelValueToBitArray(pixels[i], 8);
            System.arraycopy(bitArray, 0, pixelBitArray[i], 0, bitArray.length);
        }
        int[][] bitArrayTranspose = matrixTranspose(pixelBitArray);
        int[][] bchCodeBitArray = matrixMultiplication(bitArrayTranspose, generatorMatrix);
        int[][] bchPixelsBitArray = matrixTranspose(bchCodeBitArray);

        //Convert bit array to pixel
        for(int i = 0; i < bchPixels.length; i++) {
            bchPixels[i] = bitArrayToPixel(bchPixelsBitArray[i]);
        }

        return bchPixels;
    }

    //Calculate syndrome code by parity matrix
    public int[] calculateSyndrome(int[] bchCodeBitArray) {
        int[][] matrixBCHBitArray = new int[1][7];
        int[] syndromeBitArray = new int[3];
        System.arraycopy(bchCodeBitArray, 0, matrixBCHBitArray[0], 0, bchCodeBitArray.length);
        int[][] matrixSyndrome = matrixMultiplication(matrixBCHBitArray, parityMatrixHTranspose);
        System.arraycopy(matrixSyndrome[0], 0, syndromeBitArray, 0, matrixSyndrome[0].length);
        return syndromeBitArray;
    }

    public int[] syndromeCount(int[][] syndromes) {
        int[] syndromeCount = new int[8];
        boolean zeroSyndrome = true;
        Arrays.fill(syndromeCount, 0);
        for (int[] syndrome : syndromes) {
            zeroSyndrome = true;
            for (int j = 0; j < parityMatrixHTranspose.length; j++) {
                if (Arrays.equals(syndrome, parityMatrixHTranspose[j])) {
                    syndromeCount[j + 1]++;
                    zeroSyndrome = false;
                }
            }
            if(zeroSyndrome) {
                syndromeCount[0]++;
            }
        }

        return syndromeCount;
    }

    public void showSyndromeCount(int[] syndromeCount) {
        System.out.print("Syndrome Count: \n");

        for(int i = 0; i <syndromeCount.length; i++) {
            System.out.print("\tN_" + i +": ");
            System.out.print(syndromeCount[i] + "\n");
        }
    }
}
