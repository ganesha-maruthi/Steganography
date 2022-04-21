package Steganography;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;

public class Medium {

    public int lsb(int bits) throws IOException{
        Scanner s = new Scanner(System.in);
        System.out.print("Enter the path: ");
        String path = "C:\\Users\\ultim\\Desktop\\sample.jpg";
        // path = s.nextLine();

        System.out.print("Enter output filename: ");
        String op_path = "C:\\Users\\ultim\\Desktop\\" + "out.png"; // + s.nextLine() + ".png";
        String ext = "";
        for(int i = path.length() - 1; path.charAt(i) != '.'; i--) {
            ext = path.charAt(i) + ext;
        }
        System.out.println("Extension: " + ext);

        File f = new File(path);
        File f_op = new File(op_path);
        int height, width, max_payload;
        BufferedImage image = null;
        image = ImageIO.read(f);
        height = image.getHeight();
        width = image.getWidth();
        max_payload = (width * height * 3) / 8;
        System.out.println("Height: " + height + ", Width: " + width + ", Max Payload: " + max_payload + " bytes, " + (float)(max_payload / 1024) + " KB, " + (float)(max_payload / (1024 * 1024)) + " MB");

        String message = "";
        int c;
        System.out.print("Enter the message: ");
        String ip_path = "C:\\Users\\ultim\\Desktop\\";// + s.nextLine();
        ip_path = "C:\\Users\\ultim\\Desktop\\sample.xlsx";
        File ip_file = new File(ip_path);
        FileInputStream file_reader = new FileInputStream(ip_file);
        System.out.println(ip_file.exists());
        System.out.println(ip_file.getName());
        System.out.println(ip_file.length());

        if(ip_file.length() > max_payload) {
            System.out.println("File size too large!");
            s.close();
            file_reader.close();
            return 0;
        }

        while((c = file_reader.read()) != -1) {
            message += (char) c;
            // System.out.print((char) c);
        }
        // System.out.print("\n================\n");
        file_reader.close();
        // System.out.println(message);
        // System.out.println(message.length());

        int msgsize = message.length();
        System.out.println("Message length: " + msgsize + " (" + msgsize * 8 + ")");
        byte[] len = new byte[4];
        len[3] = (byte) (msgsize & 255);
        len[2] = (byte) ((msgsize >> 8) & 255);
        len[1] = (byte) ((msgsize >> 16) & 255);
        len[0] = (byte) ((msgsize >> 24) & 255);
        String binary = "0";
        byte temp;
        for(int i = 0; i < 4; i++) {
            temp = len[i];
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        System.out.println(binary.substring(1));
        for(int i = 0; i < msgsize; i++) {
            temp = (byte) message.charAt(i);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
            // System.out.println(temp + " " + String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2))));
        }
        while(binary.length() % 3 > 0) {
            binary += '1';
        }
        // System.out.println(binary.substring(33));
        msgsize = binary.length();
        System.out.println("Message size in bits: " + msgsize);
        /* FileWriter file_writer = new FileWriter("C:\\Users\\ultim\\Desktop\\bin");
        file_writer.write(binary.substring(33));
        file_writer.close(); */

        int a, r, g, b, new_r, new_g, new_b, bit1, bit2, bit3, pixel, new_pixel;
        int x = -1, y = 0;

        for(int idx = 0; idx < msgsize; idx += 3) {
            // System.out.print("(" + x + ", " + y + "): ");
            
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
            
            bit1 = (int) binary.charAt(idx);
            new_r = (r & (Integer.MAX_VALUE << 1)) | (bit1 - 48);

            bit2 = (int) binary.charAt(idx + 1);
            new_g = (g & (Integer.MAX_VALUE << 1)) | (bit2 - 48);

            bit3 = (int) binary.charAt(idx + 2);
            new_b = (b & (Integer.MAX_VALUE << 1)) | (bit3 - 48);

            /* System.out.print((char)bit1);
            System.out.print((char)bit2);
            System.out.print((char)bit3); */

            // System.out.println(": (" + r + ", " + g + ", " + b + ") -> (" + new_r + ", " + new_g + ", " + new_b + ")");

            new_pixel = (a << 24) | (new_r << 16) | (new_g << 8) | new_b;
            image.setRGB(x, y, new_pixel);

        }
        ImageIO.write(image, "png", f_op);

        s.close();
        return 1;
    }

    public void read() throws IOException{
        // Scanner s = new Scanner(System.in);
        // System.out.print("\n\nEnter input filename: ");
        // String filename = s.nextLine();
        File f = new File("C:\\Users\\ultim\\Desktop\\out.png");
        BufferedImage image = ImageIO.read(f);
        int height = image.getHeight(), width = image.getWidth();

        String binary = "";
        int r, g, b, bit1, bit2, bit3, pixel;
        int x = -1, y = 0;
        for(int idx = 0; idx < 33; idx += 3) {
            // System.out.print("(" + x + ", " + y + "): ");
            
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

            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = r & 1;
            bit2 = g & 1;
            bit3 = b & 1;
            // System.out.print("(" + r + ", " + g + ", " + b + ")");
            // System.out.println(": (" + bit1 + ", " + bit2 + ", " + bit3 + ")");

            binary = binary + Integer.toString(bit1) + Integer.toString(bit2) + Integer.toString(bit3);
        }
        System.out.print(binary.substring(1) + ": ");
        System.out.println(Integer.parseInt(binary.substring(1), 2));
        int msgsize = Integer.parseInt(binary.substring(1), 2) * 8;
        // System.out.println(msgsize);
        binary = "";
        for(int idx = 0; idx < msgsize; idx += 3) {
            // System.out.print("(" + x + ", " + y + "): ");
            
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

            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = r & 1;
            bit2 = g & 1;
            bit3 = b & 1;
            // System.out.print("(" + r + ", " + g + ", " + b + ")");
            // System.out.println(": (" + bit1 + ", " + bit2 + ", " + bit3 + ")");

            binary = binary + Integer.toString(bit1) + Integer.toString(bit2) + Integer.toString(bit3);
        }
        // System.out.println(binary);
        /* String msg = "";
        for(int i = 0; i < msgsize; i += 8) {
            msg += (char)Integer.parseInt(binary.substring(i, i + 8), 2);
        } */
        byte[] msg = new byte[msgsize / 8];
        for(int i = 0; i < msgsize / 8; i++) {
            msg[i] = (byte) Integer.parseInt(binary.substring(i * 8, i * 8 + 8), 2);
        }
        // s.close();
        System.out.println("Hidden message length: " + msgsize / 8);
        FileOutputStream file_writer = new FileOutputStream("C:\\Users\\ultim\\Desktop\\steg_out");
        file_writer.write(msg);
        file_writer.close();
        /* FileWriter file_writer2 = new FileWriter("C:\\Users\\ultim\\Desktop\\bin2");
        file_writer2.write(binary);
        file_writer2.close(); */
    }
    public static void main(String []args) throws IOException{
        Medium l = new Medium();
        l.lsb(0);
        l.read();
    }
}
