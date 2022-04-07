/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Steganography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author dev
 */
public class Medium {
    String fileName;
    int size;
    //info obj...
//    public void loadMedium(){
//        
//    }
//    public int saveMedium(String fileName){
//        
//    }
    public int lsb(int bits) throws IOException{
        File f = new File("/home/dev/Data/PESU/6th Sem/OOAD/Project/Steganography/app/src/main/java/Steganography/App.java");
        InputStream s = new FileInputStream("/home/dev/Data/PESU/6th Sem/OOAD/Project/Steganography/app/src/main/java/Steganography/1");
        byte[] fi = new byte[100];
        float len = f.length();
//        System.out.println(len);
        s.read(fi);
        String data = new String(fi);
//        for(long i = 0; i < len; i++){
        System.out.println(data);
//        }
        return 0;
    }
    public static void main(String []args) throws IOException{
        Medium l = new Medium();
        l.lsb(0);
    }
}
