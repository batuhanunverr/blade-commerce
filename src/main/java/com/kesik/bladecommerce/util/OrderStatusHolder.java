package com.kesik.bladecommerce.util;

import com.kesik.bladecommerce.dto.order.OrderStatusDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderStatusHolder {

    private Map<Integer, String> orderStatusMap;

    @PostConstruct
    public void initOrderStatusMap() {
        orderStatusMap = new HashMap<>();
        orderStatusMap.put(1, "Alındı");
        orderStatusMap.put(2, "İşleniyor");
        orderStatusMap.put(3, "Kargoya verilmeyi bekliyor");
        orderStatusMap.put(4, "Kargoya verildi");
        orderStatusMap.put(5, "Tamamlandı");
        orderStatusMap.put(6, "İptal Edildi");
        orderStatusMap.put(7, "İade Edildi");
        orderStatusMap.put(8, "İade Talebi Bekliyor");
        orderStatusMap.put(9, "İade Talebi Onaylandı");
        orderStatusMap.put(10, "İade Talebi Reddedildi");
        orderStatusMap.put(11, "İade Talebi Tamamlandı");
        orderStatusMap.put(12, "İade Talebi İptal Edildi");
    }

    public String getOrderStatusTextByCode(int code) {
        return orderStatusMap.getOrDefault(code, "Bilinmeyen Durum");
    }

    public Map<Integer, String> getOrderStatusMap() {
        return Collections.unmodifiableMap(orderStatusMap);
    }

    public OrderStatusDto getOrderStatusByCode(int code) {
        if (orderStatusMap.containsKey(code)) {
            return new OrderStatusDto(code, orderStatusMap.get(code));
        }
        return null;
    }
    public List<OrderStatusDto> getAllOrderStatusAsDtoList() {
        List<OrderStatusDto> orderStatusList = new ArrayList<>();
        orderStatusMap.forEach((code, description) ->
                orderStatusList.add(new OrderStatusDto(code, description))
        );
        return orderStatusList;
    }
}