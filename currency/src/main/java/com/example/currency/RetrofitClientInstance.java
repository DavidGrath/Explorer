package com.example.currency;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.currency.Constants.BASE_URL;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
    public static String serializedConversionString;


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ExchangeRateResponse.class, new SerializerAdapter())
                    .create();
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}

class SerializerAdapter  implements JsonDeserializer<ExchangeRateResponse>{
    @Override
    public ExchangeRateResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ExchangeRateResponse exchangeRateResponse  =
                new ExchangeRateResponse(jsonObject.get(RetrofitClientInstance.serializedConversionString).getAsDouble());
        return exchangeRateResponse;
    }
}
