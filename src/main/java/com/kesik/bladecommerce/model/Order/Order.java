package com.kesik.bladecommerce.model.Order;

import com.kesik.bladecommerce.model.Knife.Knife;
import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String id;
    private String orderDate;
    private String orderStatus;
    private String shippingAddress;
    private String billingAddress;
    private Double totalAmount;
    private List<Knife> knife;
}
