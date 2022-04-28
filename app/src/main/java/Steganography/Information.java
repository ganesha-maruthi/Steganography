package Steganography;

enum InformationType {
	text,
	document,
	watermark
}

public class Information {
	InformationType type;
	String data;
	int size;
}
