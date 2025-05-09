package com.kesik.bladecommerce.dto.order;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import lombok.Data;

import java.util.List;

@Data
public class AddOrderDto {
    private String orderDate;
    private String shippingAddress;
    private String billingAddress;
    private List<OrderKnifeDto> knifes;
    private OrderUserDto user;
}
