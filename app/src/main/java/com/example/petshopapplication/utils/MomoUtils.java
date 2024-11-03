package com.example.petshopapplication.utils;

import com.example.petshopapplication.model.Order;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MomoUtils {
    private static final String API_URL = "https://test-payment.momo.vn/gw_payment/transactionProcessor";
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String ACCESS_KEY = "F8BBA842ECF85";
    private static final String PARTNER_CODE = "MOMO";
    private static final String REQUEST_TYPE = "captureMoMoWallet";

    public String createPayment(Order order, String returnUrl) {

        String requestId = String.valueOf(System.currentTimeMillis() * 10000L + 621355968000000000L);

        // Calculate the signature
        String rawData = "partnerCode=" + PARTNER_CODE
                + "&accessKey=" + ACCESS_KEY
                + "&requestId=" + requestId
                + "&amount=" + order.getTotalAmount()
                + "&orderId=swp391_" + order.getId()
                + "&orderInfo=" + order.getOrderDate()
                + "&returnUrl=" + returnUrl
                + "&notifyUrl=" + returnUrl
                + "&extraData=";
        String signature = computeHmacSha256(rawData, SECRET_KEY);

        try {
            // Specify the URL to which you want to send the POST request
            URL url = new URL(API_URL);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Set request headers if needed
            connection.setRequestProperty("Content-Type", "application/json");

            // Create a JSONObject for the POST data
            JSONObject postData = new JSONObject();
            postData.put("accessKey", ACCESS_KEY);
            postData.put("partnerCode", PARTNER_CODE);
            postData.put("requestType", REQUEST_TYPE);
            postData.put("notifyUrl", returnUrl);
            postData.put("returnUrl", returnUrl);
            postData.put("orderId", "swp391_"+order.getId());
            postData.put("amount", order.getTotalAmount()+"");
            postData.put("orderInfo", order.getOrderDate());
            postData.put("requestId", requestId);
            postData.put("extraData", "");
            postData.put("signature", signature);

            // Get the output stream of the connection and write the JSON data to it
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response from the server
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                // Parse the response as JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Extract and print the Pay URL
                if (jsonResponse.has("payUrl")) {
                    String payUrl = jsonResponse.getString("payUrl");
                    return payUrl;
                } else {
                    System.out.println(response.toString());
                    System.out.println("Pay URL not found in the response");
                }

            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String computeHmacSha256(String message, String secretKey) {
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(keySpec);

            byte[] hashBytes = hmacSha256.doFinal(messageBytes);

            StringBuilder hashStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashStringBuilder.append(String.format("%02x", b));
            }

            return hashStringBuilder.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();  // Handle the exception appropriately
            return null;
        }
    }

}
