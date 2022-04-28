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

        File audioFile = new File(this.basefilepath);
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

        int n = (int) audioFile.length();

        byte[] audio_bytes = new byte[n];

        audio_bytes = audioInputStream.readAllBytes();

        String message = "";
        int c;
        File ip_file = new File(this.inputfilepath);
        FileInputStream file_reader = new FileInputStream(ip_file);

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

        int msgsize = message.length();
        byte[] len = new byte[4];
        len[3] = (byte) (msgsize & 255);
        len[2] = (byte) ((msgsize >> 8) & 255);
        len[1] = (byte) ((msgsize >> 16) & 255);
        len[0] = (byte) ((msgsize >> 24) & 255);
        
        String binary = "";        
        byte temp;
        for(int i = 0; i < 4; i++) {
            temp = len[i];
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        for(int i = 0; i < msgsize; i++) {
            temp = (byte) message.charAt(i);
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        msgsize = binary.length();

        int bit1, i = 45;

        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = Integer.parseInt(binary.substring(idx, idx + 1), 2);
            audio_bytes[i] = (byte) (((audio_bytes[i] & (Integer.MAX_VALUE << 1)) | (bit1)) & 255);
            i++;
        }
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
        File f = new File(this.basefilepath);
        
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(f);

        int bytes_per_frame = audioInputStream.getFormat().getFrameSize();
        
        int n = bytes_per_frame;

        byte[] audio_bytes = new byte[n];

        audio_bytes = audioInputStream.readAllBytes();

        String msgsizeraw = "";
        int i = 45, bit1;
        int mask = 1;

        for(int idx = 0; idx < 32; idx++) {
            bit1 = audio_bytes[i] & mask;
            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            i++;
        }
        int msgsize = Integer.parseInt(msgsizeraw, 2) * 8;
        String binary = "";
        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = audio_bytes[i] & mask;
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            i++;
        }
        byte[] msg = new byte[msgsize / 8];
        for(i = 0; i < msgsize / 8; i++) {
            msg[i] = (byte) Integer.parseInt(binary.substring(i * 8, i * 8 + 8), 2);
        }
        FileOutputStream file_writer = new FileOutputStream(this.outputfilepath);
        file_writer.write(msg);
        file_writer.close();
    }
}
