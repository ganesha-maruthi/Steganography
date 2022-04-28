package Steganography;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Image {
    String basefilepath, inputfilepath, outputfilepath;
    BufferedImage image;
    int height, width, max_payload;
    Information data;

    Image(String basefilepath, String inputfilepath, String outputfilepath) throws IOException {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
        this.data = new Information();
    }

    Image(String basefilepath, String outputfilepath) throws IOException {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
        this.data = new Information();
    }

    private int save_image() throws IOException {
        try {
            File f_op = new File(this.outputfilepath);
            ImageIO.write(this.image, "png", f_op);
            return 1;
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void load_image() throws IOException {
        File f = new File(this.basefilepath);
        this.image = ImageIO.read(f);
        this.height = image.getHeight();
        this.width = image.getWidth();
    }

    public int lsb(int bits) throws IOException {
        this.load_image();
        this.max_payload = (this.width * this.height * 3) / 8 * bits;
        this.data.load_from_file(this.inputfilepath);

        if(this.data.size > this.max_payload) {
            System.out.println("Data overload");
            return 0;
        }

        int msgsize = this.data.size;
        this.data.encode_binary(bits);        
        msgsize = this.data.binary.length();

        int a, r, g, b, new_r, new_g, new_b, bit1, bit2, bit3, pixel, new_pixel;
        int x = -1, y = 0;

        for(int idx = 0; idx < msgsize; idx += (3 * bits)) {
            
            x += 1;
            if(x >= this.width) {
                x = 0;
                y += 1;
            }
            if(y >= this.height) {
                break;
            }
            pixel = this.image.getRGB(x, y);

            a = (pixel >> 24) & 255;
            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = Integer.parseInt(this.data.binary.substring(idx, idx + bits), 2);
            new_r = ((r & (Integer.MAX_VALUE << bits)) | (bit1)) & 255;

            bit2 = Integer.parseInt(this.data.binary.substring(idx + bits, idx + 2 * bits), 2);
            new_g = ((g & (Integer.MAX_VALUE << bits)) | (bit2)) & 255;

            bit3 = Integer.parseInt(this.data.binary.substring(idx + 2 * bits, idx + 3 * bits), 2);
            new_b = ((b & (Integer.MAX_VALUE << bits)) | (bit3)) & 255;

            new_pixel = (a << 24) | (new_r << 16) | (new_g << 8) | new_b;
            this.image.setRGB(x, y, new_pixel);
        }

        return this.save_image();
    }

    public void read(int bits) throws IOException {
        this.load_image();

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
            if(x >= this.width) {
                x = 0;
                y += 1;
            }
            if(y >= this.height) {
                break;
            }
            pixel = this.image.getRGB(x, y);

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
        this.data.size = msgsize / 8;
        for(int idx = 0; idx < msgsize; idx += (3 * bits)) {
            
            x += 1;
            if(x >= this.width) {
                x = 0;
                y += 1;
            }
            if(y >= this.height) {
                break;
            }
            pixel = this.image.getRGB(x, y);

            r = (pixel >> 16) & 255;
            g = (pixel >> 8) & 255;
            b = pixel & 255;
            
            bit1 = r & mask;
            bit2 = g & mask;
            bit3 = b & mask;

            this.data.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            this.data.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit2 & 0xff, 2))).substring(8 - bits);
            this.data.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit3 & 0xff, 2))).substring(8 - bits);
        }
        byte[] msg = this.data.decode_from_binary();
        this.data.save_to_file(this.outputfilepath, msg);
    }
}
