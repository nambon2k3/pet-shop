package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.Parcel;
import com.example.petshopapplication.API_model.Rate;
import com.example.petshopapplication.API_model.RateRequest;
import com.example.petshopapplication.API_model.RateResponse;
import com.example.petshopapplication.API_model.Shipment;
import com.example.petshopapplication.Adapter.PaymentAdapter;
import com.example.petshopapplication.API_model.Address;
import com.example.petshopapplication.Adapter.RateAdapter;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;
import com.example.petshopapplication.model.Payment;
import com.example.petshopapplication.model.ShippingMethod;
import com.example.petshopapplication.model.UAddress;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Variant;
import com.example.petshopapplication.model.Dimension;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements RateAdapter.OnRateSelectListener {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "PaymentActivity";
    private RecyclerView recyclerView;
    private List<Cart> selectedCartItems;
    private TextView tvTotalPrice;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView addressTextView;
    private Button changeAddressButton;
    private String AUTH_TOKEN;
    private double totalAmount;
    private List<ShippingMethod> shippingMethods = new ArrayList<>();
    private int totalWidth, totalHeight, totalLength, totalWeight;
    private UAddress selectedUAddress;
    private RateAdapter rateAdapter;
    private List<Rate> rateList = new ArrayList<>();
    private TextView priceReal;
    private TextView feeShip;
    private TextView purchasedMoney;
    private RadioButton checkboxPaymentOnDelivery;
    private Button payButton;
    private String selectedRateID;
    private String selectedCartierName;
    private String selectedCartierLogo;
    private List<Product> productList = new ArrayList<>();

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();

        Intent intent1 = getIntent();
        selectedCartItems = (ArrayList<Cart>) intent1.getSerializableExtra("selectedItems");
        totalAmount = intent1.getDoubleExtra("totalAmount", 0.0);
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);

        AUTH_TOKEN = "Bearer " + getResources().getString(R.string.goship_api_token);
        database = FirebaseDatabase.getInstance();
        tvTotalPrice = findViewById(R.id.totalPriceTextView);
        addressTextView = findViewById(R.id.addressTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);
        priceReal = findViewById(R.id.price_in_real);
        priceReal.setText(String.format("%s VND", numberFormat.format(totalAmount)));
        feeShip = findViewById(R.id.fee_ship);
        purchasedMoney = findViewById(R.id.purchasedMoney);
        purchasedMoney.setText(String.format("%s VND", numberFormat.format(totalAmount)));

        recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter cho danh sách phí vận chuyển
        rateAdapter = new RateAdapter(rateList, this, this);
        RecyclerView rateRecyclerView = findViewById(R.id.rateRecyclerView);
        rateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rateRecyclerView.setAdapter(rateAdapter);

        getDefaultAddress(userId);
        loadProductDetails();
        checkboxPaymentOnDelivery = findViewById(R.id.checkboxPaymentOnDelivery);
        payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(v -> {
            if (selectedRateID == null || selectedRateID.isEmpty()) {
                Toast.makeText(PaymentActivity.this, "Vui lòng chọn phương thức vận chuyển", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkboxPaymentOnDelivery.isChecked()) {/* other payment method check */
                createOrderAndPayment();
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            }
        });
        changeAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, AddressSelectionActivity.class);
            intent.putExtra("selectedAddress", selectedUAddress);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedUAddress = (UAddress) data.getSerializableExtra("selectedAddress");
            if (selectedUAddress != null) {
                displayAddress(selectedUAddress);
                // Gọi lại loadRates nếu cần thiết
                if (selectedUAddress.getDistrictId() != null && selectedUAddress.getCityId() != null) {
                    loadRates(selectedUAddress.getDistrictId(), selectedUAddress.getCityId(), 1, (int) totalAmount, totalWidth, totalHeight, totalLength, totalWeight);
                }
            }
        }
    }


    private void createOrderAndPayment() {
        // Tạo đơn hàng với UUID
        Order order = new Order();
        order.setId(UUID.randomUUID().toString()); // Tạo ID tự động bằng UUID
        order.setUserId(user.getUid()); // Gán ID người dùng thực tế
        order.setTotalAmount(totalAmount); // Tổng số tiền
        order.setShipmentId(""); // ID vận chuyển
        order.setRateId(selectedRateID); // Sử dụng selectedRateID đã lưu
        order.setOrderDetails(getOrderDetailsList()); // Danh sách chi tiết đơn hàng
        order.setOrderDate(new Date()); // Thời gian đặt hàng
        order.setStatus("Processing"); // Trạng thái đơn hàng
        order.setCityId(selectedUAddress.getCityId());
        order.setDistrictId(selectedUAddress.getDistrictId());
        order.setWardId(selectedUAddress.getWardId());
        order.setCarrierName(selectedCartierName);
        order.setCarrierLogo(selectedCartierLogo);
        order.setCity(selectedUAddress.getCity());
        order.setDistrict(selectedUAddress.getDistrict());
        order.setWard(selectedUAddress.getWard());

        // Thêm vào Firebase
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        String orderId = order.getId(); // Lấy ID từ đơn hàng đã tạo

        ordersRef.child(orderId).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Tạo thông tin thanh toán sau khi tạo đơn hàng thành công
                        Payment payment = new Payment();
                        payment.setId(UUID.randomUUID().toString()); // Tạo ID tự động cho thanh toán
                        payment.setOrderId(orderId);
                        payment.setPaymentMethod(checkboxPaymentOnDelivery.isChecked() ? "COD" : "Chuyển khoản"); // Gán phương thức thanh toán
                        payment.setAmount(order.getTotalAmount()); // Số tiền thanh toán
                        payment.setTransactionId(""); // Nếu có ID giao dịch, thêm vào đây

                        // Thêm vào Firebase
                        DatabaseReference paymentsRef = FirebaseDatabase.getInstance().getReference("payments");
                        String paymentId = payment.getId(); // Lấy ID từ thanh toán đã tạo

                        paymentsRef.child(paymentId).setValue(payment)
                                .addOnCompleteListener(paymentTask -> {
                                    if (paymentTask.isSuccessful()) {
                                        // Cập nhật paymentId vào đơn hàng
                                        order.setPaymentId(paymentId);
                                        ordersRef.child(orderId).setValue(order) // Cập nhật đơn hàng với paymentId
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Toast.makeText(this, "Đơn hàng đã được tạo thành công!", Toast.LENGTH_SHORT).show();
                                                        // Chuyển đến màn hình xác nhận đơn hàng
                                                        Intent intent = new Intent(PaymentActivity.this, OrderingActivity.class);
                                                        intent.putExtra("orderId", orderId);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(this, "Lỗi khi cập nhật thông tin thanh toán vào đơn hàng!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(this, "Lỗi khi tạo thông tin thanh toán!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Lỗi khi tạo đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public List<OrderDetail> getOrderDetailsList() {
        List<OrderDetail> orderDetailsList = new ArrayList<>();

        // Giả sử selectedCartItems là danh sách các mặt hàng trong giỏ hàng
        for (Cart cartItem : selectedCartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setProductId(cartItem.getProductId());
            orderDetail.setVariantId(cartItem.getSelectedVariantId());
            orderDetail.setColorId(cartItem.getSelectedColorId());
            orderDetail.setQuantity(cartItem.getQuantity());

            double price = getPriceForProduct(cartItem.getProductId(), cartItem.getSelectedVariantId());
            double discountPercentage = getDiscountForProduct(cartItem.getProductId());
            double discountedPrice = price * (1 - discountPercentage / 100.0);

            orderDetail.setPurchased(discountedPrice); // Lưu giá đã giảm

            orderDetailsList.add(orderDetail); // Thêm vào danh sách chi tiết đơn hàng
        }
        return orderDetailsList;
    }

    private double getPriceForProduct(String productId, String variantId) {
        // Retrieve product details from your database or data structure
        // Assuming you have a method to find the product by ID and variant ID
        Product product = findProductById(productId); // Implement this method
        if (product != null) {
            for (Variant variant : product.getListVariant()) {
                if (variant.getId().equals(variantId)) {
                    return variant.getPrice(); // Return the price of the variant
                }
            }
        }
        return 0; // Return 0 if product or variant not found
    }

    private double getDiscountForProduct(String productId) {
        // Retrieve product details to get discount
        Product product = findProductById(productId); // Implement this method
        if (product != null) {
            return product.getDiscount(); // Return the discount percentage of the product
        }
        return 0; // Return 0 if product not found
    }
    public Product findProductById(String productId) {
        for (Product product : productList) { // productList là danh sách sản phẩm
            if (product.getId().equals(productId)) {
                return product; // Trả về sản phẩm nếu tìm thấy
            }
        }
        return null; // Trả về null nếu không tìm thấy sản phẩm
    }



    private void loadProductDetails() {

        reference = database.getReference(getString(R.string.tbl_product_name));
        List<String> productIds = new ArrayList<>();
        for (Cart cart : selectedCartItems) {
            productIds.add(cart.getProductId());
        }

        reference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalWidth = totalHeight = totalLength = totalWeight = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String productId = dataSnapshot.child("id").getValue(String.class);
                        if (productIds.contains(productId)) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                                for (Variant variant : product.getListVariant()) {
                                    Dimension dimension = variant.getDimension();
                                    for (Cart cart : selectedCartItems) {
                                        if (cart.getProductId().equals(productId)) {
                                            int quantity = cart.getQuantity();
                                            totalWidth += dimension.getWidth() * quantity;
                                            totalHeight += dimension.getHeight() * quantity;
                                            totalLength += dimension.getLength() * quantity;
                                            totalWeight += dimension.getWeight() * quantity;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    PaymentAdapter paymentAdapter = new PaymentAdapter(productList, selectedCartItems, PaymentActivity.this);
                    recyclerView.setAdapter(paymentAdapter);
                    Log.d(TAG, selectedUAddress.toString());
                    if (selectedUAddress != null) {
                        loadRates(selectedUAddress.getDistrictId(), selectedUAddress.getCityId(), 1, (int) totalAmount, totalWidth, totalHeight, totalLength, totalWeight);

                    } else {
                        Toast.makeText(PaymentActivity.this, "Địa chỉ chưa được chọn", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Có lỗi xảy ra khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRates(String toDistrict, String toCity, int cod, int amount, int width, int height, int length, int weight) {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        String fromCityId = getString(R.string.petshop_address_city_id);
        String fromDistrictId = getString(R.string.petshop_address_district_id);
        Address addressFrom = new Address(fromDistrictId, fromCityId);
        Address addressTo = new Address(toDistrict, toCity);
        Parcel parcel = new Parcel(cod, amount, width, height, length, weight);
        Shipment shipment = new Shipment(addressFrom, addressTo, parcel);
        RateRequest rateRequest = new RateRequest(shipment);

        Log.d(TAG, "Requesting rates with: " +
                "\nFrom District: " + fromDistrictId +
                "\nFrom City: " + fromCityId +
                "\nTo District: " + toDistrict +
                "\nTo City: " + toCity +
                "\nAmount: " + amount +
                "\nWidth: " + width +
                "\nHeight: " + height +
                "\nLength: " + length +
                "\nWeight: " + weight);

        Call<RateResponse> call = api.getRates("application/json", "application/json", AUTH_TOKEN, rateRequest);
        call.enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Rate> rates = response.body().getData();
                    rateAdapter.setRateList(rates);
                    for (Rate rate : rates) {
                        Log.d(TAG, "Rate ID: " + rate.getId() + ", Rate Value: " + rate.getTotalAmount());
                    }
                } else {
                    Log.e(TAG, "Request failed: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to read error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void getDefaultAddress(String userId) {
        DatabaseReference addressReference = database.getReference("addresses");
        // Thay đổi với ID người dùng thực tế

        addressReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                            Boolean isDefault = addressSnapshot.child("default").getValue(Boolean.class);
                            if (isDefault != null && isDefault) {
                                selectedUAddress = addressSnapshot.getValue(UAddress.class);
                                displayAddress(selectedUAddress);
                                Log.d(TAG, selectedUAddress.toString());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Address", "Lỗi truy cập cơ sở dữ liệu: " + error.getMessage());
                    }
                });
    }

    private void displayAddress(UAddress address) {
        addressTextView.setText(address.getFullName() + " | " + address.getPhone() + "\n" + address.getWard() + ", " + address.getDistrict() + ", " + address.getCity());
    }


    @Override
    public void onRateSelected(double fee, String rateID, String cartierName, String cartierLogo) {
        double finalTotalAmount = totalAmount + fee; // Cập nhật tổng giá

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        numberFormat.setMinimumFractionDigits(0);

        // Cập nhật hiển thị phí giao hàng
        feeShip.setText(String.format("%s VND", numberFormat.format(fee)));

        // Cập nhật hiển thị số tiền đã mua
        purchasedMoney.setText(String.format("%s VND", numberFormat.format(finalTotalAmount)));

        // Cập nhật hiển thị tổng giá
        tvTotalPrice.setText(String.format("%s VND", numberFormat.format(finalTotalAmount)));

        this.selectedRateID = rateID;
        this.selectedCartierName = cartierName;
        this.selectedCartierLogo = cartierLogo;

        // Ghi log tất cả thông tin
        Log.d(TAG, "Selected Fee: " + fee + " VND");
        Log.d(TAG, "Final Total Amount: " + finalTotalAmount + " VND");
        Log.d(TAG, "Selected Rate ID: " + selectedRateID);
        Log.d(TAG, "Selected Cartier Name: " + selectedCartierName);
        Log.d(TAG, "Selected Cartier Logo: " + selectedCartierLogo);

    }


}
