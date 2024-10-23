package com.example.petshopapplication.API;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjIyMGZjNDQwNDNhZDgyNGIxOGMxODA3Y2YzY2VkODQ5MjJhOTljZTgwZTYwMDYwODc1MzdhN2JiYzkwMjU0OGUyNDI1ZjA2Yjk4YzY4NjQzIn0.eyJhdWQiOiIxMzAiLCJqdGkiOiIyMjBmYzQ0MDQzYWQ4MjRiMThjMTgwN2NmM2NlZDg0OTIyYTk5Y2U4MGU2MDA2MDg3NTM3YTdiYmM5MDI1NDhlMjQyNWYwNmI5OGM2ODY0MyIsImlhdCI6MTcyOTYxNDQzMCwibmJmIjoxNzI5NjE0NDMwLCJleHAiOjE3NjExNTA0MzAsInN1YiI6IjMzMzQiLCJzY29wZXMiOltdfQ.HsgopEvG_zGyJ7kTPpfY5Pst9uo2vY3YN9bK9s8YaKQQrRfrXzQRO0WnjTB0yzZ_bgK9srjRSuopR82dwj8sPdcSPjXPV-czZeQfwiHLVAKDj6MKqnLF_A4IQIYz8MCgcxYfd-vrdSbdzGHejcctJKEAYJx4ucHoVCU4KRWqPnbXUZysZLT2_ytuztpWlqkOPhttI0u2z1wX0ctoR0jLpaSI_o7wTkFjtnZHu_4zxackwH96FXQq-5FpdmtNsRXmij6dLP1gKqVNVcSp9Q2mkMIqnrwNfLRyYI8QzvSHUJPtwydyTib97mjo1GT1nt69nxzF3pf10K-KE0ZwIBS7qxIrM4QqcZeM_hADx8iKzOuLumMbsYSZwuiLe3oLXGcP-whDcJxEUzlDdcxPRJPav9B79o7BQFuQ_Se1mY9Vw5gV6d7ZaWh3JxaxORhiicObpKD6G9BamD0-ltRJ0EZwjAFc2uMedAcdGnSHRtN11wP2I-sysutACXTsKX7bGz370_sSkacpY1LI_X88U91n1UKxZP-vGLowaHeZdB_n_R_0IDHFqyCzEwbChOp2BmOch06aXqaij-v9O0jHCcBWxmpIX2qEBcEeI6hf6D_AiH4NQoL8HRJRsK1GU12sw2u5j3WqCzDwL7zUstHYw3BDbyIUjK4Yt0PJD6JbnU5K2KA")
                .header("Content-Type", "application/json");

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
