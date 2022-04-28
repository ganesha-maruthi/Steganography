package Steganography;

import java.io.*;

enum InformationType {
	text,
	document,
	watermark
}

public class Information {
	InformationType type;
	String message, binary;
	int size;

	Information() throws IOException {
		this.message = "";
        this.binary = "";
        this.type = InformationType.document;
	}

	public void load_from_file(String filename) throws IOException {
		int c;
        File ip_file = new File(filename);
        FileInputStream file_reader = new FileInputStream(ip_file);

        while((c = file_reader.read()) != -1) {
            this.message += (char) c;
        }
        file_reader.close();
		this.size = message.length();
	}

	public void save_to_file(String filename, byte[] msg) throws IOException {
		try {
			FileOutputStream file_writer = new FileOutputStream(filename);
			file_writer.write(msg);
			file_writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void encode_binary(int bits) {
		byte[] len = new byte[4];
        len[3] = (byte) (this.size & 255);
        len[2] = (byte) ((this.size >> 8) & 255);
        len[1] = (byte) ((this.size >> 16) & 255);
        len[0] = (byte) ((this.size >> 24) & 255);
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
            this.binary += '0';
        }
        byte temp;
        for(int i = 0; i < 4; i++) {
            temp = len[i];
            this.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        for(int i = 0; i < this.size; i++) {
            temp = (byte) this.message.charAt(i);
            this.binary += String.format("%08d", Integer.parseInt(Integer.toString((int)temp & 0xff, 2)));
        }
        while(this.binary.length() % (3 * bits) > 0) {
            this.binary += '1';
        }
	}

	public byte[] decode_from_binary() {
		byte[] msg = new byte[this.size];
        for(int i = 0; i < this.size; i++) {
            msg[i] = (byte) Integer.parseInt(this.binary.substring(i * 8, i * 8 + 8), 2);
        }
		return msg;
	}
}
