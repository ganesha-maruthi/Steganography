// commented code for handling Audio; must uncomment after merging code

package Steganography;

import java.io.IOException;

enum MediumType {
	audio,
	image
}

public class Medium {
	String basefilepath, inputfilepath, outputfilepath;
	MediumType medium_type;

    Medium(String basefilepath, String inputfilepath, String outputfilepath, MediumType medium_type) {
        this.basefilepath = basefilepath;
        this.inputfilepath = inputfilepath;
        this.outputfilepath = outputfilepath;
		this.medium_type = medium_type;
    }

    Medium(String basefilepath, String outputfilepath, MediumType medium_type) {
        this.basefilepath = basefilepath;
        this.outputfilepath = outputfilepath;
		this.medium_type = medium_type;
    }

	public int lsb(int bits) throws IOException {
		if(this.medium_type == MediumType.audio) {
			/* Audio au = new Audio(basefilepath, inputfilepath, outputfilepath);
			return au.lsb(1); */
			return 1;
		}
		else {
			Image im = new Image(basefilepath, inputfilepath, outputfilepath);
			return im.lsb(1);
		}
	}

	public void read(int bits) throws IOException {
		if(this.medium_type == MediumType.audio) {
			/* Audio au = new Audio(basefilepath, outputfilepath);
			au.read(); */
		}
		else {
			Image im = new Image(basefilepath, outputfilepath);
			im.read(bits);
		}
	}
}