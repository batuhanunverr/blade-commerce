package com.kesik.bladecommerce.dto.order;

import lombok.Data;

@Data
public class OrderStatusDto {
    private String orderStatusText;
    private int orderStatusCode;
    public OrderStatusDto() {
    }
    public OrderStatusDto(int i, String pending) {
        this.orderStatusCode = i;
        this.orderStatusText = pending;
    }
}
