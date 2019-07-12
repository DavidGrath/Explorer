package com.example.explorer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileHelperTest {

    File file;

    @Before
    public void setUp() throws Exception {
        file = new File("file.txt");
        file.createNewFile();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void deleteFile() {
        assertTrue(FileHelper.deleteFile(file));
    }
}