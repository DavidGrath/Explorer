package com.example.currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConverterTest {

    Converter converter;

    @Before
    public void setUp() throws Exception {
         converter = new Converter(1.0, "USD");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void convert() {
        assertEquals(51.100294, converter.convert(), 0.1);
    }

    @Test
    public void convert1() {
    }

    @Test
    public void setExchangeCurrency() {
        converter.setExchangeCurrency("PHP");
        assertNotNull(converter.exchangeCurrency);
    }

}