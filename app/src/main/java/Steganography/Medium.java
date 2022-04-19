package Steganography;

import java.io.File;
import java.awt.Color;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;

public class Medium {
    String fileName;
    int size;
    public int lsb(int bits) throws IOException{
        Scanner s = new Scanner(System.in);
        System.out.print("Enter the path: ");
        String path = "C:\\Users\\ultim\\Desktop\\sample.jpg";
        String ext = "";
        for(int i = path.length() - 1; path.charAt(i) != '.'; i--) {
            ext = path.charAt(i) + ext;
        }
        System.out.println(ext);
        File f = new File(path);
        int height, width;
        BufferedImage image = null;
        image = ImageIO.read(f);
        height = image.getHeight();
        width = image.getWidth();
        System.out.println("Height: " + height + ", Width: " + width);

        for(int i = 0; i < image.getWidth(); i++) {
            for(int j = 0; j < image.getHeight(); j++) {
                int pixel = image.getRGB(i, j);
                int r, g, b;
                r = (pixel >> 16) & 255;
                g = (pixel >> 8) & 255;
                b = pixel & 255;
                System.out.print(pixel + " (" + r + ", " + g + ", " + b + ") ");
                Color c = new Color(pixel, true);
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                System.out.println(pixel + " (" + r + ", " + g + ", " + b + ")");
            }
        }
        s.close();
        return 0;
    }
    public static void main(String []args) throws IOException{
        Medium l = new Medium();
        l.lsb(0);
    }
}
