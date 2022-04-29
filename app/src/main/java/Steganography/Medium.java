package Steganography;

import java.io.*;

import javax.sound.sampled.UnsupportedAudioFileException;

enum MediumType {
	audio,
	image
}

public class Medium {
	String basefilepath, inputfilepath, outputfilepath;
	MediumType medium_type;
	boolean encrypted;

    Medium(String basefilepath, String inputfilepath, String outputfilepath, MediumType medium_type, boolean encrypted) {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
		this.medium_type = medium_type;
		this.encrypted = encrypted;
    }

    Medium(String basefilepath, String outputfilepath, MediumType medium_type, boolean encrypted) {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
		this.medium_type = medium_type;
		this.encrypted = encrypted;
    }

	public int lsb(int bits, String username, String password) throws IOException, UnsupportedAudioFileException {
		if(this.medium_type == MediumType.audio) {
			Audio au = new Audio(basefilepath, inputfilepath, outputfilepath);
			return au.lsb(1, username, password);
		}
		else {
			Image im = new Image(basefilepath, inputfilepath, outputfilepath);
			return im.lsb(bits, username, password);
		}
	}

	public void read(int bits, String username) throws IOException, UnsupportedAudioFileException {
		if(this.medium_type == MediumType.audio) {
			Audio au = new Audio(basefilepath, outputfilepath);
			au.read(1, username);
		}
		else {
			Image im = new Image(basefilepath, outputfilepath);
			im.read(bits, username);
		}
	}
}