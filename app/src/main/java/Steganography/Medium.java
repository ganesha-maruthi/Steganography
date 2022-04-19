package Steganography;

import java.io.File;
// import java.awt.Color;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;

public class Medium {
    public int lsb(int bits) throws IOException{
        Scanner s = new Scanner(System.in);
        // System.out.print("Enter the path: ");
        String path = "C:\\Users\\ultim\\Desktop\\sample.jpg";
        String op_path = "C:\\Users\\ultim\\Desktop\\sample_op.jpg";
        String ext = "";
        for(int i = path.length() - 1; path.charAt(i) != '.'; i--) {
            ext = path.charAt(i) + ext;
        }
        System.out.println("Extension: " + ext);

        File f = new File(path);
        File f_op = new File(op_path);
        int height, width;
        BufferedImage image = null;
        image = ImageIO.read(f);
        height = image.getHeight();
        width = image.getWidth();
        System.out.println("Height: " + height + ", Width: " + width);

        /* for(int i = 0; i < image.getWidth(); i++) {
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
        } */

        String message = "attack at once!";
        for(int i = 0; i < 100000; i++) {
            message += (char)(i & 255);
        }
        int msgsize = message.length();
        System.out.println("Message length: " + msgsize);
        int[] len = new int[4];
        len[3] = msgsize & 255;
        len[2] = (msgsize >> 8) & 255;
        len[1] = (msgsize >> 16) & 255;
        len[0] = (msgsize >> 24) & 255;
        String binary = ""; //String.format("%032d", Integer.parseInt(Integer.toString(msgsize, 2)));
        int temp;
        for(int i = 0; i < 3; i++) {
            temp = len[i];
            binary += String.format("%08d", Integer.parseInt(Integer.toString(temp, 2)));
        }
        for(int i = 0; i < msgsize; i++) {
            temp = (int) message.charAt(i);
            binary += String.format("%08d", Integer.parseInt(Integer.toString(temp, 2)));
        }
        while(binary.length() % 3 > 0) {
            binary += '1';
        }
        System.out.println(binary);
        msgsize = binary.length();
        System.out.println("Message size in bits: " + msgsize);

        int a, r, g, b, new_r, new_g, new_b, bit1, bit2, bit3, pixel, new_pixel;
        int x = -1, y = 0;

        for(int idx = 0; idx < msgsize; idx += 3) {
            System.out.print("(" + x + ", " + y + "): ");
            
            x += 1;
            if(x >= width) {
                x = 0;
                y += 1;
            }
            if(y >= height) {
                System.out.println("Data Overflow");
                break;
            }
            pixel = image.getRGB(x, y);

            a = (pixel >> 24) & 255;
            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = binary.charAt(idx);
            new_r = r & (Integer.MAX_VALUE << 1);

            bit2 = binary.charAt(idx + 1);
            new_g = g & (Integer.MAX_VALUE << 1);

            bit3 = binary.charAt(idx + 2);
            new_b = b & (Integer.MAX_VALUE << 1);

            System.out.print((char)bit1);
            System.out.print((char)bit2);
            System.out.print((char)bit3);

            System.out.println(": (" + r + ", " + g + ", " + b + ") -> (" + new_r + ", " + new_g + ", " + new_b + ")");

            new_pixel = (a << 24) | (new_r << 16) | (new_g << 8) | new_b;
            image.setRGB(x, y, new_pixel);

        }
        ImageIO.write(image, ext, f_op);

        s.close();
        return 0;
    }
    public static void main(String []args) throws IOException{
        Medium l = new Medium();
        l.lsb(0);
    }
}
