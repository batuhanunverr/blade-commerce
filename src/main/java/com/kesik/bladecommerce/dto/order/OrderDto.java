package com.kesik.bladecommerce.dto.order;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Data
@Document(collection = "orders")
public class OrderDto {
    private String id;
    private String orderDate;
    private OrderStatusDto orderStatus;
    private String shippingAddress;
    private String billingAddress;
    private Double totalAmount;
    private List<KnifeDto> knife;
    private String history;
    private String userName;
    private String userSurname;
    private String email;
    private String phoneNumber;
    private String paymentId;
    private String selectedKnifeSize;
}