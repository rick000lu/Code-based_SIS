package org.BCHLibrary;
public class BCHClassTest {
    static void showArray(int[][] arr) {
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    static void tamperPixel(int[] pixel) {
        for(int i = 0; i < pixel.length; i++) {
            if(i == 0 || i == 3 || i == 4) {
                pixel[i] ^= 2;
                pixel[i] ^= 64;
            }
        }
    }

    static byte cyclicRightShiftPixel(byte pixel, int offset) {
        return (byte)(((pixel & 0xff) >>> offset) | (pixel << (8 - offset)));
    }

    public static void main(String[] args) {
        int[] pixels = {123, 60, 59, 222};
        //int[][] bchCodeBitArray = new int[][];
        BCHClass bchObj = new BCHClass();
        byte[] sharedPixels = bchObj.interleavingEncodeBCH(pixels);

        //Check encode bch pixels
        for(byte pixel : sharedPixels) {
            System.out.printf("%x ", Byte.toUnsignedInt(pixel));
        }
        System.out.print("\n");

        for(int i = 0; i < sharedPixels.length; i++) {
            sharedPixels[i] = cyclicRightShiftPixel(sharedPixels[i], 3);
        }

        //Check encode bch pixels
        for(byte pixel : sharedPixels) {
            System.out.printf("%x ", Byte.toUnsignedInt(pixel));
        }
        System.out.print("\n");

        //convert byte-type pixel to unsigned int pixel
        int[] unsignedIntSharedPixels = new int[7];
        for(int i = 0; i < unsignedIntSharedPixels.length; i++) {
            unsignedIntSharedPixels[i] = Byte.toUnsignedInt(sharedPixels[i]);
        }

        tamperPixel(unsignedIntSharedPixels);

        int[][] sharedPixelsBitArray = new int[7][8];
        int[][] bchCodeBitArray; //= new int[8][7];
        int[][] syndromes = new int[8][3];

        //convert shared pixel to bit array
        for(int i = 0; i < sharedPixelsBitArray.length; i++) {
            System.arraycopy(bchObj.convertPixelValueToBitArray(unsignedIntSharedPixels[i], 8), 0, sharedPixelsBitArray[i], 0, sharedPixelsBitArray[i].length);
        }

        //convert 7 shared pixel to 8 bch code
        bchCodeBitArray = bchObj.matrixTranspose(sharedPixelsBitArray);

        //calculate each bch codes syndrome (to test calculateSyndrome method)
        for(int i = 0; i < bchCodeBitArray.length; i++) {
            System.arraycopy(bchObj.calculateSyndrome(bchCodeBitArray[i]), 0, syndromes[i], 0, syndromes[i].length);
        }

        //Show syndrome matrix
        showArray(syndromes);
        int[] syndromeCount = bchObj.syndromeCount(syndromes);
        System.out.print("Syndrome Counting: \n");
        int i = 0;
        for(int N : syndromeCount) {
            System.out.print("\tN_" + i + ": ");
            System.out.printf("%d ", N);
            System.out.print("\n");
            i++;

        }

    }
}
