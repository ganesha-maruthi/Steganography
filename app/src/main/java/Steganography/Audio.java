package Steganography;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Audio {
    String basefilepath, inputfilepath, outputfilepath;

    Audio(String basefilepath, String inputfilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
    }

    Audio(String basefilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
    }

    public int lsb(int bits) throws IOException, UnsupportedAudioFileException{
        Scanner s = new Scanner(System.in);
        // System.out.print("Enter the path: ");
        // String path = "C:\\Users\\ultim\\Desktop\\base.jpg";
        // path = s.nextLine();

        // System.out.print("Enter output filename: ");
        // String this.outputfilepath = "C:\\Users\\ultim\\Desktop\\" + "out.png"; // + s.nextLine() + ".png";
        /* String ext = "";
        for(int i = path.length() - 1; path.charAt(i) != '.'; i--) {
            ext = path.charAt(i) + ext;
        } */
        // System.out.println("Extension: " + ext);

//        File f = new File(this.basefilepath);
//        File f_op = new File(this.outputfilepath);
//        int height, width, max_payload;
//        BufferedImage image = null;
//        image = ImageIO.read(f);
//        height = image.getHeight();
//        width = image.getWidth();
//        max_payload = (width * height * 3) / 8 * bits;
        // System.out.println("Height: " + height + ", Width: " + width + ", Max Payload: " + max_payload + " bytes, " + (float)(max_payload / 1024) + " KB, " + (float)(max_payload / (1024 * 1024)) + " MB");

        File audioFile = new File(this.basefilepath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

        int bytes_per_frame = audioInputStream.getFormat().getFrameSize();
        
        float frame_rate = audioInputStream.getFormat().getFrameRate();
        
//        float audio_len = audioInputStream.length();
        
//        System.out.println(frame_rate);
    //    System.out.println(audioFile.length());


        int n = (int) audioFile.length();

        byte[] audio_bytes = new byte[n];

        audio_bytes = audioInputStream.readAllBytes();

//        System.out.println(audio_bytes[0]);
//        System.out.println(audio_bytes[1]);

        // for(int j = 0; j < n; j++)
        // {
            // System.out.println(audio_bytes[j]);
            // System.out.println(j);
        // }
        
        String message = "";
        int c;
        // System.out.print("Enter the message: ");
        // String ip_path = "C:\\Users\\ultim\\Desktop\\";
        // ip_path = "C:\\Users\\ultim\\Desktop\\samples\\sample.pdf";
        File ip_file = new File(this.inputfilepath);
        FileInputStream file_reader = new FileInputStream(ip_file);
        // System.out.println(ip_file.exists());
        // System.out.println(ip_file.getName());
        // System.out.println(ip_file.length());

        if(ip_file.length() > audio_bytes.length) {
            System.out.println("File size too large!");
            s.close();
            file_reader.close();
            return 0;
        }

        while((c = file_reader.read()) != -1) {
            message += (char) c;
        }
        file_reader.close();
        // System.out.println(message);
        // System.out.println(message.length());

        int msgsize = message.length();
        // System.out.println("Message length: " + msgsize + " (" + msgsize * 8 + ")");
        byte[] len = new byte[4];
        len[3] = (byte) (msgsize & 255);
            // System.out.println(audio_bytes[i]);
        len[2] = (byte) ((msgsize >> 8) & 255);
        len[1] = (byte) ((msgsize >> 16) & 255);
        len[0] = (byte) ((msgsize >> 24) & 255);
        
        String binary = "";        
        byte temp;
        for(int i = 0; i < 4; i++) {
            temp = len[i];
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        // System.out.println("Padding: " + padding);
        // System.out.println(binary);
        for(int i = 0; i < msgsize; i++) {
            temp = (byte) message.charAt(i);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        // System.out.println(binary.substring(33));
        msgsize = binary.length();
        // System.out.println("Message size in bits: " + msgsize);

        int f, new_f, bit1, i = 45;

        for(int idx = 0; idx < msgsize; idx++) {
            // System.out.print("(" + x + ", " + y + "): ");
            
            bit1 = Integer.parseInt(binary.substring(idx, idx + 1), 2);
            // System.out.println(audio_bytes[i]);
            audio_bytes[i] = (byte) (((audio_bytes[i] & (Integer.MAX_VALUE << 1)) | (bit1)) & 255);
            // System.out.println(audio_bytes[i]);
            i++;

            // System.out.print(bit1 + ", ");
            // System.out.print(bit2 + ", ");
            // System.out.print(bit3);

            // System.out.println(": (" + r + ", " + g + ", " + b + ") -> (" + new_r + ", " + new_g + ", " + new_b + ")");

//            new_pixel = (a << 24) | (new_r << 16) | (new_g << 8) | new_b;
//            image.setRGB(x, y, new_pixel);

        }
//        ImageIO.write(image, "png", f_op);
        /* FileWriter file_writer2 = new FileWriter("C:\\Users\\ultim\\Desktop\\bin");
        file_writer2.write(binary);
        file_writer2.close(); */
        System.out.println(audioInputStream.getFormat());
        File fileOut = new File(this.outputfilepath);
		ByteArrayInputStream byteIS = new ByteArrayInputStream(audio_bytes);
		AudioInputStream audioIS = new AudioInputStream(byteIS,
				audioInputStream.getFormat(), audioInputStream.getFrameLength());
		if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, audioIS)) {
			try {
				AudioSystem.write(audioIS, AudioFileFormat.Type.WAVE, fileOut);
				System.out.println("Steganographed AU file is written as "
						+ this.outputfilepath + "...");
			} catch (Exception e) {
				System.err.println("Sound File write error");
			}
		}
        s.close();
        return 1;
    }

    public void read(int bits) throws IOException, UnsupportedAudioFileException{
        // Scanner s = new Scanner(System.in);
        // System.out.print("\n\nEnter input filename: ");
        // String filename = s.nextLine();
        File f = new File(this.basefilepath);
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);

        int bytes_per_frame = audioInputStream.getFormat().getFrameSize();
        
//        float audio_len = audioInputStream.length();
        
//        System.out.println(frame_rate);
//        System.out.println(this.inputfilepath.length());


        int n = bytes_per_frame;

        byte[] audio_bytes = new byte[n];

        audio_bytes = audioInputStream.readAllBytes();

        String msgsizeraw = "";
        int r = 1, i = 45, bit1;
        // System.out.println("Padding: " + padding);
        int mask = 1;

        // System.out.println(Integer.toString(mask, 2));

        for(int idx = 0; idx < 32; idx++) {
            // System.out.print("(" + x + ", " + y + "): ");
            // System.out.println("Index: " + idx);
            
            bit1 = audio_bytes[i] & mask;
            // System.out.print("(" + r + ", " + g + ", " + b + ")");
            // System.out.println(": (" + bit1 + ", " + bit2 + ", " + bit3 + ")");

            // msgsizeraw = msgsizeraw + Integer.toString(bit1) + Integer.toString(bit2) + Integer.toString(bit3);
            /* System.out.println(String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits));
            System.out.println(String.format("%08d", Integer.parseInt(Integer.toString((int)bit2 & 0xff, 2))).substring(8 - bits));
            System.out.println(String.format("%08d", Integer.parseInt(Integer.toString((int)bit3 & 0xff, 2))).substring(8 - bits)); */

            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            i++;
        }
        // System.out.println(binary);
        // System.out.println(msgsizeraw);
        // System.out.print(msgsizeraw.substring(padding) + ": ");
        // System.out.println(Integer.parseInt(msgsizeraw.substring(padding), 2));
        int msgsize = Integer.parseInt(msgsizeraw, 2) * 8;
         System.out.println(msgsize);
        String binary = "";
        for(int idx = 0; idx < msgsize; idx++) {
            // System.out.print("(" + x + ", " + y + "): ");
            
            bit1 = audio_bytes[i] & mask;
            // System.out.print("(" + r + ", " + g + ", " + b + ")");
            // System.out.println(": (" + bit1 + ", " + bit2 + ", " + bit3 + ")");

            // binary = binary + Integer.toString(bit1) + Integer.toString(bit2) + Integer.toString(bit3);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            i++;

            // binary = binary + Integer.toString(bit1) + Integer.toString(bit2) + Integer.toString(bit3);
        }
        // System.out.println(binary);
        byte[] msg = new byte[msgsize / 8];
        for(i = 0; i < msgsize / 8; i++) {
            msg[i] = (byte) Integer.parseInt(binary.substring(i * 8, i * 8 + 8), 2);
        }
        // s.close();
        // System.out.println("Hidden message length: " + msgsize / 8);
        FileOutputStream file_writer = new FileOutputStream(this.outputfilepath);
        file_writer.write(msg);
        file_writer.close();
        /* FileWriter file_writer2 = new FileWriter("C:\\Users\\ultim\\Desktop\\bin2");
        file_writer2.write(msgsizeraw + binary);
        file_writer2.close(); */
    }
    /* public static void main(String []args) throws IOException{
        Medium l = new Medium();
        l.lsb(4);
        l.read(4);
    } */
    public static void main(String [] arg) throws IOException, UnsupportedAudioFileException{
        Audio a = new Audio(null, null, null);
        a.lsb(0);
        System.out.println("a");
    }
}
