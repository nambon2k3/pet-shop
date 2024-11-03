package com.example.petshopapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopapplication.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VNPayPaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";

    private EditText etAmount;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vnpay_payment);

        etAmount = findViewById(R.id.et_amount);
        btnPay = findViewById(R.id.btn_pay);

        btnPay.setOnClickListener(view -> initiatePayment());
    }

    private void initiatePayment() {
        String amount = etAmount.getText().toString();
        String transactionId = String.valueOf(System.currentTimeMillis()); // hoặc tạo ID riêng

        // Tạo URL thanh toán VNPay
        String vnPayUrl = createVNPayUrl(transactionId, amount);

        // Chuyển hướng đến trình duyệt hoặc WebView để thanh toán
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(vnPayUrl));
        startActivity(browserIntent);
    }

    private String createVNPayUrl(String transactionId, String amount) {
        String merchantCode = "YIQYN73H"; // Mã thương mại của bạn
        String secretKey = "SYU7YVTGNX13B3KFWVQKYS6Z42LYX3LM"; // Khóa bí mật của bạn
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"; // Thay đổi URL nếu cần
        String returnUrl = "http://your-return-url.com"; // URL để trả về sau khi thanh toán

        // Thêm các tham số vào URL
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_Merchant", merchantCode);
        params.put("vnp_TxnRef", transactionId);
        params.put("vnp_Amount", String.valueOf(Integer.parseInt(amount) * 100)); // VNPay yêu cầu số tiền tính bằng đồng
        params.put("vnp_OrderInfo", "Thanh toán đơn hàng");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis() + 300 * 1000))); // Thời gian hết hạn 5 phút

        // Tạo chữ ký
        StringBuilder queryBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            queryBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String queryString = queryBuilder.toString();
        String signature = hashSignature(queryString, secretKey);

        // Tạo URL hoàn chỉnh
        return vnpUrl + "?" + queryString + "vnp_SecureHash=" + signature;
    }

    private String hashSignature(String data, String secretKey) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((data + secretKey).getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing signature", e);
            return null;
        }
    }
}
