package com.example.explorer;

import android.media.tv.TvContract;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {

    static boolean deleteFile(File file) {
        boolean success = file.delete();
        return success;
    }

    static boolean[] deleteFiles(ArrayList<File> files) {
        boolean[] success = new boolean[files.size()];
        for(int i = 0; i < files.size(); i++) {
            success[i] = files.get(i).delete();
        }
        return success;
    }


    static void cutFile(File from, File to) {
        copyFile(from, to);
        from.delete();
    }

    static void cutFiles(ArrayList<File> from, File to) {
        copyFiles(from, to);
        for (File f: from) {
            f.delete();
        }
    }
    /*
        It is assumed that the passed second parameter is a directory
     */
    static void copyFile(File from, File to) {
        if(from.isDirectory() && from.listFiles() != null) {
            File[] recursiveFile = from.listFiles();
            for(int i = 0; i < recursiveFile.length; i++) {
                copyFile(recursiveFile[i], to);
            }
        }
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream;
        String destinationFile = to.getAbsolutePath() + "/" + from.getName();
        byte[] byteArray = new byte[(int) from.length()];

        try {
            fileInputStream = new FileInputStream(from);
            fileInputStream.read(byteArray);
            File temp = new File(destinationFile);
            Log.d("File copy", temp.getAbsolutePath());
            Log.d("Creation", Boolean.toString(to.mkdir()));
            Log.d("Creation 2", Boolean.toString(temp.createNewFile()));
            fileOutputStream = new FileOutputStream(temp);
            fileOutputStream.write(byteArray);
        } catch (IOException e) {
            System.out.println("");
            Log.d("FileHelper class", "Copy failed");
            e.printStackTrace();
        }
    }

    static void copyFiles(ArrayList<File> from, File to) {
        for(int i = 0; i < from.size(); i++) {
            copyFile(from.get(i), to);
        }
    }
}
