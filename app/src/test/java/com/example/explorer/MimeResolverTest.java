package com.example.explorer;

import android.content.Context;
import android.webkit.MimeTypeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MimeResolverTest {

    @Mock
    private Context context;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getResourceID() {
    }

    @Test
    public void resolveMime() {
        String file = "file.pdf";
        String mime = "application/pdf";
        assertEquals("application/pdf", MimeResolver.getResourceID(new File("file.pdf"), context));
    }
}