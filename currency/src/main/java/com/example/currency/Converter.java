package com.example.currency;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.currency.Constants.API_KEY;

public class Converter {


    Currency fromCurrency, toCurrency;
    String exchangeCurrency = null;
    Double exchangeRate;
    ExchangeRateResponse exchangeRateResponse;

    public Converter (Double amount, String currencyCode) {
        this.fromCurrency = new Currency(amount, currencyCode);
    }

    public Converter(Double amount, String currencyCode, String exchangeCurrency) {
        this.fromCurrency = new Currency(amount, currencyCode);
        this.exchangeCurrency = exchangeCurrency;
    }

    public double convert() {
        return convert(fromCurrency.currencyCode, exchangeCurrency);
    }

    public double convert(String from, String to) {
        RetrofitClientInstance.serializedConversionString = from + "_" + to;
        RetrofitClient service = RetrofitClientInstance.getRetrofitInstance().create(RetrofitClient.class);
        Call<ExchangeRateResponse> call = service.getExchangeRate(from + "_" + to,"ultra", API_KEY);
        try {
            exchangeRateResponse = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        toCurrency = new Currency(fromCurrency.amount * exchangeRateResponse.exchangeRate, exchangeCurrency);
        return toCurrency.amount;
    }

    public void setExchangeCurrency(String exchangeCurrency) {
        this.exchangeCurrency = exchangeCurrency;
    }
}
