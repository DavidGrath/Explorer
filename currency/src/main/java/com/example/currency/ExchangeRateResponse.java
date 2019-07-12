package com.example.currency;

import com.google.gson.annotations.SerializedName;

public class ExchangeRateResponse {

    public double exchangeRate;

    public ExchangeRateResponse(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
