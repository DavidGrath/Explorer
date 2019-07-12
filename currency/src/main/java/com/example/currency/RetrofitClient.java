package com.example.currency;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitClient {

    @GET("/api/v7/convert")
    Call<ExchangeRateResponse> getExchangeRate(@Query("q") String conversionString,
                                               @Query("compact") String compact,
                                               @Query("apiKey") String apiKey);
}

