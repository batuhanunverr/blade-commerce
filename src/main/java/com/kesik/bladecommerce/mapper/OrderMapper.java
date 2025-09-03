package com.kesik.bladecommerce.mapper;

import com.kesik.bladecommerce.dto.iyzico.BasketItemDto;
import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.KnifeOrderDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {
    private static KnifeService knifeService = null;

    @Autowired
    public OrderMapper(KnifeService knifeService) {
        OrderMapper.knifeService = knifeService;
    }

    @Transactional
    public static OrderDto mapOrderRequestToOrder(OrderRequestDto orderRequest) {
        try {
            OrderDto orderDto = new OrderDto();
            if (orderRequest.getBuyer() == null) {
                throw new IllegalArgumentException("Buyer information is required.");
            }
            orderDto.setOrderDate(LocalDate.now().toString());
            orderDto.setShippingAddress(
                    orderRequest.getShippingAddress() != null ? orderRequest.getShippingAddress().getAddress() : null
            );
            orderDto.setBillingAddress(
                    orderRequest.getBillingAddress() != null ? orderRequest.getBillingAddress().getAddress() : null
            );

            try {
                orderDto.setTotalAmount(Double.parseDouble(orderRequest.getPrice()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid price format: " + orderRequest.getPrice(), e);
            }
            orderDto.setUserName(orderRequest.getBuyer().getName());
            orderDto.setUserSurname(orderRequest.getBuyer().getSurname());
            orderDto.setEmail(orderRequest.getBuyer().getEmail());
            orderDto.setPhoneNumber(orderRequest.getBuyer().getGsmNumber());
            orderDto.setConversationId(orderRequest.getConversationId());
            orderDto.setPaymentId(orderRequest.getPaymentId());
            orderDto.setHistory("Sipariş oluşturuldu.");
            List<KnifeOrderDto> orderKnifes = new ArrayList<>();
            for (BasketItemDto knife : orderRequest.getBasketItems()) {
                KnifeDto knifeDto = knifeService.getKnifeById(knife.getId());
                if (knifeDto == null) {
                    throw new IllegalArgumentException("Knife not found with id: " + knife.getId());
                }
                if (knife.getQuantity() > knifeDto.getStockQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for knife: " + knifeDto.getName());
                }
                if(knifeDto.getKnifeSizes() != null){
                    if (knife.getSelectedSize() != null && !knifeDto.getKnifeSizes().contains(knife.getSelectedSize())) {
                        throw new IllegalArgumentException("Invalid knife size selected for knife: " + knifeDto.getName());
                    }
                }

                KnifeOrderDto knifeOrderDto = generateKnifeOrder(knifeDto, knife);
                int quantity = knifeDto.getStockQuantity() - knife.getQuantity();
                knifeService.updateKnifeStockQuantity(knifeDto.getId(), quantity);
                knifeOrderDto.setStockQuantity(quantity);
                orderKnifes.add(knifeOrderDto);
            }
            orderDto.setKnife(orderKnifes);
            return orderDto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping OrderRequest to Order: " + e.getMessage(), e);
        }
    }

    private static KnifeOrderDto generateKnifeOrder(KnifeDto knifeDto, BasketItemDto knife) {
        KnifeOrderDto knifeOrderDto = new KnifeOrderDto();
        knifeOrderDto.setId(knifeDto.getId());
        knifeOrderDto.setName(knifeDto.getName());
        knifeOrderDto.setCategoryId(knifeDto.getCategoryId());
        knifeOrderDto.setDescription(knifeDto.getDescription());
        knifeOrderDto.setImageUrl(knifeDto.getImageUrl());
        knifeOrderDto.setSelectedSize(knife.getSelectedSize());
        knifeOrderDto.setCustomerNote(knife.getNote());
        return knifeOrderDto;
    }
}
