package com.kesik.bladecommerce.dto.order;

import com.kesik.bladecommerce.dto.iyzico.BasketItemDto;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderDto {
    private String id;
    private String orderNumber; // Human-readable order identifier
    private String orderDate;
    private String conversationId;
    private OrderStatusDto orderStatus;
    private String shippingAddress;
    private String billingAddress;
    private Double totalAmount;
    private Double shippingCost; // Shipping cost (0 if free shipping applied)
    private Double subtotal; // Product total before shipping/tax
    private List<KnifeOrderDto> knives;
    private String history;
    private String userName;
    private String userSurname;
    private String email;
    private String phoneNumber;
    private String paymentId;
    private String selectedKnifeSize;
    private String adminNote;
}