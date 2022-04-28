package Steganography;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Image {
    String basefilepath, inputfilepath, outputfilepath;
    BufferedImage image;
    int height, width, max_payload;

    Image(String basefilepath, String inputfilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
    }

    Image(String basefilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
    }

    private int save_medium() throws IOException{
        try {
            File f_op = new File(this.outputfilepath);
            ImageIO.write(image, "png", f_op);
            return 1;
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void load_medium() throws IOException {
        File f = new File(this.basefilepath);
        image = ImageIO.read(f);
        height = image.getHeight();
        width = image.getWidth();
    }

    public int lsb(int bits) throws IOException{
        load_medium();
        max_payload = (width * height * 3) / 8 * bits;

        String message = "";
        int c;
        File ip_file = new File(this.inputfilepath);
        FileInputStream file_reader = new FileInputStream(ip_file);

        if(ip_file.length() > max_payload) {
            file_reader.close();
            return 0;
        }

        while((c = file_reader.read()) != -1) {
            message += (char) c;
        }
        file_reader.close();

        int msgsize = message.length();
        byte[] len = new byte[4];
        len[3] = (byte) (msgsize & 255);
        len[2] = (byte) ((msgsize >> 8) & 255);
        len[1] = (byte) ((msgsize >> 16) & 255);
        len[0] = (byte) ((msgsize >> 24) & 255);
        String binary = "";
        int padding = 1;
        while(true) {
            if((padding * 3 * bits) > 32) {
                padding = (padding * 3 * bits) - 32;
                break;
            }
            else {
                padding += 1;
            }
        }
        for(int i = 0; i < padding; i++) {
            binary += '0';
        }
        byte temp;
        for(int i = 0; i < 4; i++) {
            temp = len[i];
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        for(int i = 0; i < msgsize; i++) {
            temp = (byte) message.charAt(i);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        while(binary.length() % (3 * bits) > 0) {
            binary += '1';
        }
        msgsize = binary.length();

        int a, r, g, b, new_r, new_g, new_b, bit1, bit2, bit3, pixel, new_pixel;
        int x = -1, y = 0;

        for(int idx = 0; idx < msgsize; idx += (3 * bits)) {
            
            x += 1;
            if(x >= width) {
                x = 0;
                y += 1;
            }
            if(y >= height) {
                break;
            }
            pixel = image.getRGB(x, y);

            a = (pixel >> 24) & 255;
            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = Integer.parseInt(binary.substring(idx, idx + bits), 2);
            new_r = ((r & (Integer.MAX_VALUE << bits)) | (bit1)) & 255;

            bit2 = Integer.parseInt(binary.substring(idx + bits, idx + 2 * bits), 2);
            new_g = ((g & (Integer.MAX_VALUE << bits)) | (bit2)) & 255;

            bit3 = Integer.parseInt(binary.substring(idx + 2 * bits, idx + 3 * bits), 2);
            new_b = ((b & (Integer.MAX_VALUE << bits)) | (bit3)) & 255;

            new_pixel = (a << 24) | (new_r << 16) | (new_g << 8) | new_b;
            image.setRGB(x, y, new_pixel);
        }

        return save_medium();
    }

    public void read(int bits) throws IOException{
        load_medium();

        String msgsizeraw = "";
        int r, g, b, bit1, bit2, bit3, pixel;
        int x = -1, y = 0;
        int padding = 1;
        while(true) {
            if((padding * 3 * bits) > 32) {
                padding = (padding * 3 * bits) - 32;
                break;
            }
            else {
                padding += 1;
            }
        }
        int mask = 0;

        for(int i = 0; i < bits; i++) {
            mask = (mask << 1) + 1;
        }

        for(int idx = 0; idx < 32 + padding; idx += (3 * bits)) {
            x += 1;
            if(x >= width) {
                x = 0;
                y += 1;
            }
            if(y >= height) {
                break;
            }
            pixel = image.getRGB(x, y);

            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = r & mask;
            bit2 = g & mask;
            bit3 = b & mask;

            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit2 & 0xff, 2))).substring(8 - bits);
            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit3 & 0xff, 2))).substring(8 - bits);
        }
        int msgsize = Integer.parseInt(msgsizeraw.substring(padding), 2) * 8;
        String binary = "";
        for(int idx = 0; idx < msgsize; idx += (3 * bits)) {
            
            x += 1;
            if(x >= width) {
                x = 0;
                y += 1;
            }
            if(y >= height) {
                break;
            }
            pixel = image.getRGB(x, y);

            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = r & mask;
            bit2 = g & mask;
            bit3 = b & mask;

            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit2 & 0xff, 2))).substring(8 - bits);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit3 & 0xff, 2))).substring(8 - bits);
        }
        byte[] msg = new byte[msgsize / 8];
        for(int i = 0; i < msgsize / 8; i++) {
            msg[i] = (byte) Integer.parseInt(binary.substring(i * 8, i * 8 + 8), 2);
        }
        FileOutputStream file_writer = new FileOutputStream(this.outputfilepath);
        file_writer.write(msg);
        file_writer.close();
    }
}
