package com.kesik.bladecommerce.mapper;

import com.kesik.bladecommerce.dto.iyzico.BasketItemDto;
import com.kesik.bladecommerce.dto.iyzico.OrderRequestDto;
import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.dto.order.OrderDto;
import com.kesik.bladecommerce.service.KnifeService;
import com.kesik.bladecommerce.util.OrderStatusHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private static KnifeService knifeService = null;

    @Autowired
    public OrderMapper(KnifeService knifeService) {
        OrderMapper.knifeService = knifeService;
    }

    public static OrderDto mapOrderRequestToOrder(OrderRequestDto orderRequest) {
        try {
            OrderDto orderDto = new OrderDto();
            orderDto.setOrderDate(LocalDate.now().toString());
            orderDto.setShippingAddress(orderRequest.getShippingAddress().getAddress());
            orderDto.setBillingAddress(orderRequest.getBillingAddress().getAddress());
            orderDto.setTotalAmount(Double.parseDouble(orderRequest.getPrice()));
            orderDto.setUserName(orderRequest.getBuyer().getName());
            orderDto.setUserSurname(orderRequest.getBuyer().getSurname());
            orderDto.setEmail(orderRequest.getBuyer().getEmail());
            orderDto.setPhoneNumber(orderRequest.getBuyer().getGsmNumber());
            orderDto.setConversationId(orderRequest.getConversationId());
            orderDto.setPaymentId(orderRequest.getPaymentId());
            orderDto.setHistory("Sipariş oluşturuldu.");
            List<KnifeDto> orderKnifes = new ArrayList<>();
            for (BasketItemDto knife : orderRequest.getBasketItems()) {
                KnifeDto knifeDto = knifeService.getKnifeById(knife.getId());
                knifeDto.setSelectedSize(knife.getSelectedSize());
                knifeDto.setCustomerNote(knife.getNote());
                knifeService.updateKnifeStockQuantity(knifeDto.getId(), knifeDto.getStockQuantity() - knife.getQuantity());
                orderKnifes.add(knifeDto);
            }
            orderDto.setKnife(orderKnifes);
            return orderDto;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping OrderRequest to Order: " + e.getMessage(), e);
        }
    }
}
