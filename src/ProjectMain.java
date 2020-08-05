import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;

/**-------------------------------------------------------------------------------------------------
 * @author Francisco Reyna
 * email: francis@cs.utexas.edu
 * UT Austin Computer Science
 * -------------------------------------------------------------------------------------------------
 * A Program that converts an image file into ASCII Art.
 * Works best if you use IntelliJ or Eclipse and reduce the font size of the command line output to
 * something very small, such as 4.
 * -------------------------------------------------------------------------------------------------
 */

public class ProjectMain {

    //Class Constants
    final static String BRIGHTNESS = "`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#M"+
    "W&8%B@$";
    final static double MAX_BRIGHTNESS = 255.0;

    // Converting an image to art.
    public static void main(String[] args) throws IOException {
        Scanner keyboard = new Scanner(System.in);
        intro();
        BufferedImage img = getImage(keyboard);
        img = askToScale(img, keyboard);
        Color[][] pixelArray = new Color[img.getWidth()][img.getHeight()];
        getPixels(img, pixelArray);
        double[][] brightnessArray = new double[img.getWidth()][img.getHeight()];
        getBrightness(pixelArray, brightnessArray);
        char[][] charactersASCII = new char[img.getWidth()][img.getHeight()];
        mapBrightness(brightnessArray, charactersASCII);
        printASCII(charactersASCII);
    }


    //Prints certain information necessary to make sure that all things are working.
    public static void introTests() {
        System.out.println("The file directory is currently set to: " + System.getProperty("user.dir"));
    }


    //Simple intro printing
    public static void intro() {
        System.out.println("Welcome to Francisco's Image to Art application.");
        System.out.println("This program turns an Image file into ASCII art using brightness ");
        System.out.print("calculations on every single pixel!");
        System.out.println("\n");
    }


    /**
     * Prompts user until a valid File name is found and created
     * @param keyboard Scanner used to read user input
     * @return A File image object
     * @throws IOException In case a file is not found
     */
    public static BufferedImage getImage(Scanner keyboard) throws IOException {
        boolean fileIsValid = false;
        BufferedImage img = null;
        introTests();

        while(!fileIsValid) {
            System.out.print("Please enter the name of your file: ");
            String fileName = keyboard.nextLine();
            File imageFile = new File(fileName);

            if(imageFile.exists()) {
                img = ImageIO.read(imageFile);
                System.out.println("Successfully loaded " + fileName);
                fileIsValid = true;
            } else {
                System.out.println("No image found.");
            }
            System.out.println();
        }
        return img;
    }


    /**
     * Creates and Extracts Color objects from every pixel in the image
     * @param img The image being analyzed
     * @param pixelArray An array of Colors representing each pixel 
     */
    public static void getPixels(BufferedImage img, Color[][] pixelArray) {
        for(int y = 0; y < img.getHeight(); y++) {
            for(int x = 0; x < img.getWidth(); x++) {
                Color currentColor = new Color(img.getRGB(x, y));
                pixelArray[x][y] = currentColor;
            }
        }
    }


    /**
     * Calculates the brightness of each pixel using an Average and stores it
     * @param pixelArray The array of pixels
     * @param brightnessArray The array that stores each brightness double
     */
    public static void getBrightness(Color[][] pixelArray, double[][] brightnessArray) {
        for(int y = 0; y < pixelArray[0].length; y++) {
            for(int x = 0; x < pixelArray.length; x++) {
                //Current brightness algorithm is a simple average of the 3 RGB values for each pixel
                double brightness = (pixelArray[x][y].getRed() + pixelArray[x][y].getGreen() +
                pixelArray[x][y].getBlue()) / 3.0;
                brightnessArray[x][y] = brightness;
            }
        }
    }


    /**
     * Maps each brightness index into its corresponding ASCII character
     * @param brightnessArray Array of brightness values
     * @param charactersASCII Array of corresponding ASCII characters
     */
    public static void mapBrightness(double[][] brightnessArray, char[][] charactersASCII) {
        for(int y = 0; y < brightnessArray[0].length; y++) {
            for(int x = 0; x < brightnessArray.length; x++) {
                int currentIndex = (int)Math.round(BRIGHTNESS.length() *
                (brightnessArray[x][y] / MAX_BRIGHTNESS));
                char currentCh;
                if(currentIndex == BRIGHTNESS.length()) {
                    currentIndex = BRIGHTNESS.length() - 1;
                }
                currentCh = BRIGHTNESS.charAt(currentIndex);
                charactersASCII[x][y] = currentCh;
            }
        }
    }


    /**
     * Prints out to the console!
     * @param charactersASCII Array of characters
     */
    public static void printASCII(char[][] charactersASCII) {
        for(int y = 0; y < charactersASCII[0].length; y++) {
            System.out.println();
            for(int x = 0; x < charactersASCII.length; x++) {
                //Prints 3 times to offset the vertical stretching
                System.out.print(charactersASCII[x][y]);
                System.out.print(charactersASCII[x][y]);
                System.out.print(charactersASCII[x][y]);
            }
        }
    }


    /**
     * Asks the user if they would like to scale down or up their image
     * @param img The image which is to be resized
     * @param keyboard Scanner used to read user input
     */
    public static BufferedImage askToScale(BufferedImage img, Scanner keyboard) {
        System.out.print("Would you like to scale your image down? Y/N: ");
        String answer = keyboard.next().toUpperCase();
        System.out.println();

        if(answer.equals("Y")) {
            System.out.print("What scale would you like to use? ");
            double scale = keyboard.nextDouble();
            img = scaleDown(img, scale);
        }
        return img;
    }


    /**
     * Scales down the image by a factor of double scale
     * @param img The Image that will be resized
     * @param scale The scale that the image will be resized by
     * @return The newly resized image
     */
    public static BufferedImage scaleDown(BufferedImage img, double scale) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(img, after);
        return after;
    }
}