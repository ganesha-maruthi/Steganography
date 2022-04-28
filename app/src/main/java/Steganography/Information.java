package Steganography;

import java.io.*;

enum InformationType {
	text,
	document,
	watermark
}

public class Information {
	InformationType type;
	String message;
	int size;

	Information() throws IOException {
		this.message = "";
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
}
