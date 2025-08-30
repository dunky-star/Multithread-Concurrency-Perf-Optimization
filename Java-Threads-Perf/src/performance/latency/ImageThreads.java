package performance.latency;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.*;

/**
 * Optimizing for Latency using FixedThreadPool ExecutorService
 */
public class ImageThreads {
    public static final String SOURCE_FILE = "./resources/input/many-flowers.jpg";
    public static final String DESTINATION_FILE = "./resources/output/many-flowers.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        int numberOfThreads = Runtime.getRuntime().availableProcessors(); // Use all CPU cores
        long startTime = System.currentTimeMillis();

        recolorWithExecutor(originalImage, resultImage, numberOfThreads);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        boolean written = ImageIO.write(resultImage, "jpg", new File(DESTINATION_FILE));
        System.out.println("Image written: " + written);
        System.out.println("Completed in " + duration + " ms");
    }

    private static void recolorWithExecutor(BufferedImage originalImage, BufferedImage resultImage, int numberOfThreads) {
        int height = originalImage.getHeight();
        int slice = height / numberOfThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            int yStart = i * slice;
            int yEnd = (i == numberOfThreads - 1) ? height : yStart + slice;

            executor.submit(() -> recolorImage(originalImage, resultImage, 0, yStart, originalImage.getWidth(), yEnd - yStart));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(10); // Wait until all tasks are done
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < originalImage.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < originalImage.getHeight(); y++) {
                recolorPixel(originalImage, resultImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);
        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed, newGreen, newBlue;

        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;
        rgb |= 0xFF000000;
        return rgb;
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}