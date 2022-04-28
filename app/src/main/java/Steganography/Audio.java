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
    byte[] audio_bytes;
    int header = 45;
    AudioInputStream audioInputStream;

    Audio(String basefilepath, String inputfilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
}

    Audio(String basefilepath, String outputfilepath) {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
    }

    private int load_audio() throws IOException, UnsupportedAudioFileException {
        File audioFile = new File(this.basefilepath);
        this.audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        int n = (int) audioFile.length();
        this.audio_bytes = new byte[n];
        this.audio_bytes = audioInputStream.readAllBytes();
        return 0;
    }

    private int save_audio() throws IOException, UnsupportedAudioFileException {
        File file_out = new File(this.outputfilepath);
		ByteArrayInputStream byte_is = new ByteArrayInputStream(audio_bytes);
		AudioInputStream audio_is = new AudioInputStream(byte_is, this.audioInputStream.getFormat(), this.audioInputStream.getFrameLength());
		if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, audio_is)) {
			try {
				AudioSystem.write(audio_is, AudioFileFormat.Type.WAVE, file_out);
			} catch (Exception e) {
                e.printStackTrace();
				System.err.println("Sound File write error");
                return 0;
			}
		}
        return 1;
    }

    public int lsb(int bits) throws IOException, UnsupportedAudioFileException{
        Scanner s = new Scanner(System.in);

        load_audio();

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

        int bit1;

        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = Integer.parseInt(binary.substring(idx, idx + 1), 2);
            this.audio_bytes[this.header] = (byte) (((this.audio_bytes[this.header] & (Integer.MAX_VALUE << 1)) | (bit1)) & 255);
            this.header++;
        }
        s.close();
        return save_audio();
    }

    public void read(int bits) throws IOException, UnsupportedAudioFileException{
        load_audio();

        String msgsizeraw = "";
        int bit1;
        int mask = 1;

        for(int idx = 0; idx < 32; idx++) {
            bit1 = this.audio_bytes[this.header] & mask;
            msgsizeraw += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            this.header++;
        }
        int msgsize = Integer.parseInt(msgsizeraw, 2) * 8;
        String binary = "";
        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = this.audio_bytes[this.header] & mask;
            binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            this.header++;
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
