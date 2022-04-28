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
    Information data;

    Audio(String basefilepath, String inputfilepath, String outputfilepath) throws IOException {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
        this.data = new Information();
}

    Audio(String basefilepath, String outputfilepath) throws IOException {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
        this.data = new Information();
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

    public int lsb(int bits, String username, String password) throws IOException, UnsupportedAudioFileException{
        load_audio();
        this.data.load_from_file(this.inputfilepath);

        if(username.length() > 0) {
            Driver.put(username, password);
            this.data.message = Encrypt.EncryptText(username, password, this.data.message);
        }

        this.data.encode_binary(1);
        int msgsize = this.data.binary.length();

        int bit1;

        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = Integer.parseInt(this.data.binary.substring(idx, idx + 1), 2);
            this.audio_bytes[this.header] = (byte) (((this.audio_bytes[this.header] & (Integer.MAX_VALUE << 1)) | (bit1)) & 255);
            this.header++;
        }
        return save_audio();
    }

    public void read(int bits, String username) throws IOException, UnsupportedAudioFileException{
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
        for(int idx = 0; idx < msgsize; idx++) {
            bit1 = this.audio_bytes[this.header] & mask;
            this.data.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)bit1 & 0xff, 2))).substring(8 - bits);
            this.header++;
        }
        byte[] msg = this.data.decode_from_binary();
        if(username != null) {
            String password = Driver.fetch(username);
            msg = Decrypt.DecryptText(msg, password);
        }
        this.data.save_to_file(this.outputfilepath, msg);
    }
}
