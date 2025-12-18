package com.kesik.bladecommerce.controller;

import com.kesik.bladecommerce.dto.knife.KnifeDto;
import com.kesik.bladecommerce.repository.knife.KnifeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final KnifeRepository knifeRepository;

    public StockController(KnifeRepository knifeRepository) {
        this.knifeRepository = knifeRepository;
    }

    /**
     * Check stock availability for a single product
     * Public endpoint - no authentication required
     */
    @GetMapping("/check/{knifeId}")
    public ResponseEntity<Map<String, Object>> checkStock(
            @PathVariable String knifeId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        log.info("Checking stock for knife: {} with quantity: {}", knifeId, quantity);

        KnifeDto knife = knifeRepository.findById(knifeId).orElse(null);

        Map<String, Object> response = new HashMap<>();

        if (knife == null) {
            response.put("available", false);
            response.put("reason", "Product not found");
            response.put("stock", 0);
            return ResponseEntity.ok(response);
        }

        int currentStock = knife.getStockQuantity();
        boolean available = currentStock >= quantity;

        response.put("available", available);
        response.put("stock", currentStock);
        response.put("requested", quantity);
        response.put("productName", knife.getName());

        if (!available) {
            if (currentStock == 0) {
                response.put("reason", "Out of stock");
            } else {
                response.put("reason", "Insufficient stock. Only " + currentStock + " available");
            }
        }

        log.info("Stock check result for {}: available={}, stock={}, requested={}",
                knife.getName(), available, currentStock, quantity);

        return ResponseEntity.ok(response);
    }

    /**
     * Batch check stock for multiple products
     * Public endpoint - used before checkout
     */
    @PostMapping("/check-batch")
    public ResponseEntity<Map<String, Object>> checkBatchStock(
            @RequestBody List<StockCheckRequest> items
    ) {
        log.info("Batch checking stock for {} items", items.size());

        Map<String, Object> response = new HashMap<>();
        boolean allAvailable = true;
        List<Map<String, Object>> results = new java.util.ArrayList<>();

        for (StockCheckRequest item : items) {
            KnifeDto knife = knifeRepository.findById(item.getKnifeId()).orElse(null);

            Map<String, Object> itemResult = new HashMap<>();
            itemResult.put("knifeId", item.getKnifeId());
            itemResult.put("requested", item.getQuantity());

            if (knife == null) {
                itemResult.put("available", false);
                itemResult.put("reason", "Product not found");
                itemResult.put("stock", 0);
                allAvailable = false;
            } else {
                int currentStock = knife.getStockQuantity();
                boolean available = currentStock >= item.getQuantity();

                itemResult.put("available", available);
                itemResult.put("stock", currentStock);
                itemResult.put("productName", knife.getName());

                if (!available) {
                    allAvailable = false;
                    if (currentStock == 0) {
                        itemResult.put("reason", "Out of stock");
                    } else {
                        itemResult.put("reason", "Insufficient stock. Only " + currentStock + " available");
                    }
                }
            }

            results.add(itemResult);
        }

        response.put("allAvailable", allAvailable);
        response.put("items", results);
        response.put("totalItems", items.size());

        log.info("Batch stock check complete: allAvailable={}", allAvailable);

        return ResponseEntity.ok(response);
    }

    /**
     * Request DTO for batch stock check
     */
    public static class StockCheckRequest {
        private String knifeId;
        private int quantity;

        public String getKnifeId() {
            return knifeId;
        }

        public void setKnifeId(String knifeId) {
            this.knifeId = knifeId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
