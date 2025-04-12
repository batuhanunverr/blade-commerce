package com.kesik.bladecommerce.dto.Order;

import com.kesik.bladecommerce.dto.Knife.KnifeDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String id;
    private String orderDate;
    private String orderStatus;
    private String shippingAddress;
    private String billingAddress;
    private Double totalAmount;
    private List<KnifeDto> knife;
}
