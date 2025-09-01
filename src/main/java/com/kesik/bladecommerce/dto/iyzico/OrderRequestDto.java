package com.kesik.bladecommerce.dto.iyzico;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private String locale;
    private String conversationId;
    private String price;
    private String basketId;
    private String paymentGroup;
    private BuyerDto buyer;
    private AddressDto shippingAddress;
    private AddressDto billingAddress;
    private List<BasketItemDto> basketItems;
    private List<Integer> enabledInstallments;
    private String callbackUrl;
    private String currency;
    private String paidPrice;
    private String paymentId;
}
