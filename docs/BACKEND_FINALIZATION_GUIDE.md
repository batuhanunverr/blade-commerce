# 🔧 Backend Finalization Guide - BladeCommerce API

**Project:** BladeCommerce REST API (Spring Boot)
**Version:** 1.0
**Last Updated:** November 15, 2024
**Timeline:** 3-4 weeks

---

## 📋 Table of Contents

1. [Current State Assessment](#current-state-assessment)
2. [Critical Issues Summary](#critical-issues-summary)
3. [Iyzico Integration Protection](#iyzico-integration-protection)
4. [Phase-by-Phase Implementation](#phase-by-phase-implementation)
5. [Development Criteria & Checkpoints](#development-criteria--checkpoints)
6. [Testing Strategy](#testing-strategy)
7. [Deployment Checklist](#deployment-checklist)

---

## Current State Assessment

### ✅ What's Working Well

**Architecture:**
- Well-organized layered structure (Controller → Service → Repository)
- Clean package organization
- Separation of DTOs and entities
- MongoDB integration working

**Features Implemented:**
- Product/Knife CRUD with search & pagination
- Category management
- Order processing from Iyzico payment data
- Stock validation API
- Image upload (Cloudinary)
- Email notifications (basic)
- JWT token generation
- Admin user initialization

**Code Quality:**
- No TODO/FIXME comments
- Clean code structure
- Good exception handling framework
- MongoDB indexing configured

### ❌ Critical Issues

**Security (CRITICAL - P0):**
- ⚠️ **ALL ENDPOINTS ARE PUBLIC** - Security disabled in SecurityConfig.java
- ⚠️ **NO PAYMENT VERIFICATION** - Orders created without verifying Iyzico payment
- ⚠️ JWT filter exists but not active
- ⚠️ No rate limiting
- ⚠️ No CSRF protection
- ⚠️ Error messages expose internal details

**Missing Features (HIGH - P1):**
- Review system (backend API)
- Turkish location API (cities/districts)
- Email HTML templates
- Order status automation
- Payment verification with Iyzico
- Webhook handlers

**Code Issues (MEDIUM - P2):**
- Git merge conflict in application.properties
- No API documentation (Swagger configured but not used)
- No tests
- No scheduled tasks support

---

## Critical Issues Summary

### 🚨 CRITICAL: Security Disabled

**File:** `src/main/java/com/kesik/bladecommerce/config/SecurityConfig.java`

**Current Code (LINES 21-24):**
```java
.csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth
    .anyRequest().permitAll()  // ⚠️ CRITICAL SECURITY ISSUE
)
```

**Impact:**
- ANYONE can create/edit/delete products
- ANYONE can access all orders
- ANYONE can delete orders
- ANYONE can access admin statistics
- NO authentication required for ANY endpoint

**Fix Required:** Enable authentication (detailed in Phase 1)

---

### 🚨 CRITICAL: No Payment Verification

**Current Flow:**
1. Frontend calls Iyzico API
2. Frontend receives payment success
3. Frontend sends payment data to backend
4. Backend **blindly creates order** without verification ⚠️

**Security Risk:**
- Malicious user can send fake payment data
- Orders created without actual payment
- Revenue loss

**Fix Required:** Server-side Iyzico verification (detailed in Phase 1)

---

### 🚨 CRITICAL: Git Merge Conflict

**File:** `src/main/resources/application.properties`

**Lines 18-47 contain:**
```properties
<<<<<<< HEAD
# Old configuration
=======
# New configuration
>>>>>>> develop
```

**Fix Required:** Resolve immediately (detailed in Phase 1, Step 1.1)

---

## Iyzico Integration Protection

### 🔒 PROTECTED CODE - DO NOT MODIFY WITHOUT APPROVAL

**Critical Iyzico Files:**

1. **DTOs (Read-Only unless approved):**
   - `dto/iyzico/OrderRequestDto.java`
   - `dto/iyzico/BuyerDto.java`
   - `dto/iyzico/AddressDto.java`
   - `dto/iyzico/BasketItemDto.java`

2. **Mapper (Critical - requires approval):**
   - `mapper/OrderMapper.java` - Maps payment to order

3. **Controller Endpoint:**
   - `OrderController.java` - `POST /api/orders`

### Current Iyzico Integration Points

**Endpoint:** `POST /api/orders`
**Input:** `OrderRequestDto` (from frontend after Iyzico payment)
**Process:**
1. Receives `OrderRequestDto` with payment details
2. Validates buyer information
3. Generates unique order number (hash-based)
4. Validates stock for each item
5. Deducts stock quantities
6. Creates order with PENDING status
7. Returns order confirmation

**Fields Used from Iyzico:**
- `conversationId` - Unique transaction ID
- `paymentId` - Iyzico payment ID
- `price`, `paidPrice` - Payment amounts
- `buyer` - Customer information
- `basketItems` - Cart items
- `shippingAddress`, `billingAddress` - Delivery info

### What We'll Add (Phase 1)

**New Files (won't touch existing):**
- `service/IyzicoVerificationService.java` - NEW
- `dto/iyzico/PaymentVerificationResponse.java` - NEW

**Modified Files (with approval checkpoints):**
- `OrderController.java` - Add verification before order creation
- `pom.xml` - Add Iyzico SDK dependency

**Checkpoint System:**
```
CHECKPOINT 1.2.1: Before modifying OrderController
├─ Review changes
├─ Approve modification
└─ Proceed or rollback

CHECKPOINT 1.2.2: After Iyzico integration
├─ Test payment flow
├─ Verify stock deduction still works
├─ Approve or revert
└─ Proceed to next phase
```

---

## Phase-by-Phase Implementation

## PHASE 1: Critical Security & Fixes (Week 1)

**Goal:** Fix security holes, enable authentication, verify payments
**Timeline:** 5-7 days
**Priority:** P0 (CRITICAL)

---

### Step 1.1: Fix Git Merge Conflict ⚡ IMMEDIATE

**File:** `src/main/resources/application.properties`

**Current Issue:**
```properties
<<<<<<< HEAD
spring.application.name=blade-commerce
# Configuration set 1
=======
spring.application.name=BladeCommerce
# Configuration set 2
>>>>>>> develop
```

**Resolution Strategy:**
1. Read both versions
2. Determine correct values
3. Remove conflict markers
4. Keep best of both

**Expected Result:**
```properties
spring.application.name=BladeCommerce
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# MongoDB Configuration
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/knife_commerce_dev}
spring.data.mongodb.database=${MONGODB_DATABASE:knife_commerce}

# Server Configuration
server.port=${SERVER_PORT:8080}

# JWT Configuration
jwt.secret=${JWT_SECRET:your-256-bit-secret-key-change-in-production}
jwt.expiration=${JWT_EXPIRATION_MS:86400000}

# ... rest of configuration
```

**Testing:**
```bash
# Verify application starts
./mvnw clean spring-boot:run

# Check for errors in logs
tail -f logs/application.log
```

**CHECKPOINT 1.1.1: After merge conflict resolution**
- [ ] Application starts without errors
- [ ] Configuration loaded correctly
- [ ] MongoDB connects
- [ ] Approve to proceed

---

### Step 1.2: Iyzico Payment Verification 🔒 PROTECTED INTEGRATION

**Goal:** Verify payments server-side before creating orders

#### Substep 1.2.1: Add Iyzico SDK Dependency

**File:** `pom.xml`

**Add before `</dependencies>`:**
```xml
<!-- Iyzico Java SDK -->
<dependency>
    <groupId>com.iyzipay</groupId>
    <artifactId>iyzipay-java</artifactId>
    <version>2.1.44</version>
</dependency>
```

**Update Maven:**
```bash
./mvnw clean install
```

**CHECKPOINT 1.2.1: After adding dependency**
- [ ] Maven build successful
- [ ] No dependency conflicts
- [ ] Approve to proceed

---

#### Substep 1.2.2: Create Iyzico Configuration

**New File:** `src/main/java/com/kesik/bladecommerce/config/IyzicoConfig.java`

```java
package com.kesik.bladecommerce.config;

import com.iyzipay.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Iyzico payment gateway configuration
 * Supports both sandbox and production environments
 */
@Configuration
public class IyzicoConfig {

    @Value("${iyzico.api.key}")
    private String apiKey;

    @Value("${iyzico.secret.key}")
    private String secretKey;

    @Value("${iyzico.base.url:https://api.iyzipay.com}") // Default to production
    private String baseUrl;

    /**
     * Creates Iyzico Options bean for API calls
     * @return Configured Options instance
     */
    @Bean
    public Options iyzicoOptions() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }
}
```

**Update:** `src/main/resources/application-dev.properties`
```properties
# Iyzico Configuration (Sandbox)
iyzico.api.key=${IYZICO_API_KEY:sandbox-test-api-key}
iyzico.secret.key=${IYZICO_SECRET_KEY:sandbox-test-secret-key}
iyzico.base.url=https://sandbox-api.iyzipay.com
```

**Update:** `src/main/resources/application-prod.properties`
```properties
# Iyzico Configuration (Production)
iyzico.api.key=${IYZICO_API_KEY}
iyzico.secret.key=${IYZICO_SECRET_KEY}
iyzico.base.url=https://api.iyzipay.com
```

**Update:** `.env.example`
```env
# Iyzico Payment Gateway (Production)
IYZICO_API_KEY=your-production-api-key
IYZICO_SECRET_KEY=your-production-secret-key
```

---

#### Substep 1.2.3: Create Payment Verification Service

**New File:** `src/main/java/com/kesik/bladecommerce/service/IyzicoVerificationService.java`

```java
package com.kesik.bladecommerce.service;

/**
 * Service for verifying Iyzico payments
 * Ensures orders are only created after payment confirmation
 */
public interface IyzicoVerificationService {

    /**
     * Verify payment with Iyzico API
     * @param paymentId Iyzico payment ID
     * @param conversationId Transaction conversation ID
     * @return true if payment is successful and verified
     * @throws PaymentVerificationException if verification fails
     */
    boolean verifyPayment(String paymentId, String conversationId);

    /**
     * Get payment details from Iyzico
     * @param paymentId Iyzico payment ID
     * @return Payment details response
     */
    PaymentVerificationResponse getPaymentDetails(String paymentId);
}
```

**New File:** `src/main/java/com/kesik/bladecommerce/service/impl/IyzicoVerificationServiceImpl.java`

```java
package com.kesik.bladecommerce.service.impl;

import com.iyzipay.Options;
import com.iyzipay.model.Payment;
import com.iyzipay.request.RetrievePaymentRequest;
import com.kesik.bladecommerce.service.IyzicoVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of Iyzico payment verification
 * Uses Iyzico Java SDK to verify payments server-side
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IyzicoVerificationServiceImpl implements IyzicoVerificationService {

    private final Options iyzicoOptions;

    @Override
    public boolean verifyPayment(String paymentId, String conversationId) {
        try {
            // Create verification request
            RetrievePaymentRequest request = new RetrievePaymentRequest();
            request.setPaymentId(paymentId);
            request.setConversationId(conversationId);

            // Call Iyzico API to retrieve payment details
            Payment payment = Payment.retrieve(request, iyzicoOptions);

            // Log verification attempt
            log.info("Payment verification - PaymentId: {}, Status: {}",
                     paymentId, payment.getStatus());

            // Check if payment was successful
            boolean isSuccess = "success".equalsIgnoreCase(payment.getStatus());

            if (!isSuccess) {
                log.warn("Payment verification failed - PaymentId: {}, ErrorMessage: {}",
                         paymentId, payment.getErrorMessage());
            }

            return isSuccess;

        } catch (Exception e) {
            log.error("Payment verification error - PaymentId: {}, Error: {}",
                      paymentId, e.getMessage(), e);
            throw new PaymentVerificationException(
                "Failed to verify payment: " + e.getMessage(), e
            );
        }
    }

    @Override
    public PaymentVerificationResponse getPaymentDetails(String paymentId) {
        try {
            RetrievePaymentRequest request = new RetrievePaymentRequest();
            request.setPaymentId(paymentId);

            Payment payment = Payment.retrieve(request, iyzicoOptions);

            return PaymentVerificationResponse.builder()
                .paymentId(payment.getPaymentId())
                .status(payment.getStatus())
                .paidPrice(payment.getPaidPrice())
                .currency(payment.getCurrency())
                .conversationId(payment.getConversationId())
                .errorCode(payment.getErrorCode())
                .errorMessage(payment.getErrorMessage())
                .build();

        } catch (Exception e) {
            log.error("Failed to get payment details - PaymentId: {}", paymentId, e);
            throw new PaymentVerificationException(
                "Failed to retrieve payment details: " + e.getMessage(), e
            );
        }
    }
}
```

**New File:** `src/main/java/com/kesik/bladecommerce/dto/iyzico/PaymentVerificationResponse.java`

```java
package com.kesik.bladecommerce.dto.iyzico;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for payment verification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {
    private String paymentId;
    private String status; // "success" or "failure"
    private String paidPrice;
    private String currency;
    private String conversationId;
    private String errorCode;
    private String errorMessage;
}
```

**New File:** `src/main/java/com/kesik/bladecommerce/exception/PaymentVerificationException.java`

```java
package com.kesik.bladecommerce.exception;

/**
 * Exception thrown when payment verification fails
 */
public class PaymentVerificationException extends RuntimeException {

    public PaymentVerificationException(String message) {
        super(message);
    }

    public PaymentVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

#### Substep 1.2.4: Modify Order Creation (🔒 PROTECTED - REQUIRES APPROVAL)

**CHECKPOINT 1.2.2: Before modifying OrderController**
❓ **APPROVAL REQUIRED:** We will modify `OrderController.java` to add payment verification.
- [ ] Review proposed changes below
- [ ] Approve modification
- [ ] If issues found: Suggest alternative approach

**File:** `src/main/java/com/kesik/bladecommerce/controller/OrderController.java`

**Current Code (Lines ~50-60):**
```java
@PostMapping
public ResponseEntity<OrderDto> addOrder(@RequestBody OrderRequestDto orderDto) {
    // Maps Iyzico payment to order
    OrderDto createdOrder = orderService.addOrder(orderDto);
    return ResponseEntity.ok(createdOrder);
}
```

**Modified Code (ADD VERIFICATION):**
```java
// Add import
import com.kesik.bladecommerce.service.IyzicoVerificationService;

// Add field injection
private final IyzicoVerificationService iyzicoVerificationService;

// Update constructor
public OrderController(OrderService orderService,
                      IyzicoVerificationService iyzicoVerificationService) {
    this.orderService = orderService;
    this.iyzicoVerificationService = iyzicoVerificationService;
}

// Modify POST endpoint
@PostMapping
public ResponseEntity<OrderDto> addOrder(@RequestBody OrderRequestDto orderDto) {

    // 🔒 NEW: Verify payment with Iyzico BEFORE creating order
    boolean isPaymentValid = iyzicoVerificationService.verifyPayment(
        orderDto.getPaymentId(),
        orderDto.getConversationId()
    );

    if (!isPaymentValid) {
        throw new PaymentVerificationException(
            "Payment verification failed for paymentId: " + orderDto.getPaymentId()
        );
    }

    // Only create order if payment verified ✅
    OrderDto createdOrder = orderService.addOrder(orderDto);
    return ResponseEntity.ok(createdOrder);
}
```

**What Changed:**
1. ✅ Injected `IyzicoVerificationService`
2. ✅ Added verification call before order creation
3. ✅ Throws exception if payment invalid
4. ✅ Existing order creation flow UNCHANGED (stock deduction still works)

**What Stays the Same:**
- OrderMapper logic unchanged
- Stock validation unchanged
- Order number generation unchanged
- Email notifications unchanged

---

**CHECKPOINT 1.2.3: After Iyzico integration**
🧪 **Testing Required:**

1. **Test with valid payment:**
   ```bash
   # Frontend completes Iyzico payment successfully
   # Backend should create order
   # Stock should be deducted
   # Order should appear in database
   ```

2. **Test with invalid payment ID:**
   ```bash
   # Send request with fake paymentId
   # Backend should reject with 400 Bad Request
   # NO order created
   # Stock NOT deducted
   ```

3. **Test existing functionality:**
   ```bash
   # Verify stock deduction still works
   # Verify order number generation still works
   # Verify email notification still works
   ```

**Approval Questions:**
- [ ] Does payment verification work correctly?
- [ ] Does stock deduction still work?
- [ ] Does order creation still work for valid payments?
- [ ] Are errors handled properly?
- [ ] Approve to proceed OR request changes

---

### Step 1.3: Enable Spring Security & Authentication

**Goal:** Protect admin endpoints with JWT authentication

#### Substep 1.3.1: Update Security Configuration

**File:** `src/main/java/com/kesik/bladecommerce/config/SecurityConfig.java`

**Current Code (INSECURE - Lines 21-24):**
```java
.csrf(csrf -> csrf.disable())
.authorizeHttpRequests(auth -> auth
    .anyRequest().permitAll()  // ⚠️ EVERYTHING PUBLIC
)
```

**New Code (SECURE):**
```java
package com.kesik.bladecommerce.config;

import com.kesik.bladecommerce.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration with JWT authentication
 * Protects admin endpoints while keeping public endpoints accessible
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable @PreAuthorize annotations
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API (using JWT)
            .csrf(csrf -> csrf.disable())

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - NO authentication required
                .requestMatchers(HttpMethod.GET, "/api/knives/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stock/check/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/stock/check-batch").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/health").permitAll()

                // Order creation - PUBLIC (called after Iyzico payment)
                .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

                // Protected endpoints - Require ADMIN role
                .requestMatchers("/api/knives/**").hasRole("ADMIN")
                .requestMatchers("/api/categories/**").hasRole("ADMIN")
                .requestMatchers("/api/orders/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Stateless session (JWT-based, no sessions)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Add JWT filter before username/password filter
            .addFilterBefore(jwtAuthenticationFilter,
                           UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**What Changed:**
1. ✅ Added JWT filter to request chain
2. ✅ Made GET endpoints public (knives, categories, stock)
3. ✅ Made auth endpoints public (login, health)
4. ✅ Made order creation public (POST /api/orders)
5. ✅ Protected all admin operations (POST/PUT/DELETE)
6. ✅ Enabled method-level security (@PreAuthorize)

---

#### Substep 1.3.2: Update JWT Authentication Filter

**File:** `src/main/java/com/kesik/bladecommerce/security/JwtAuthenticationFilter.java`

**Issue:** Filter exists but needs to set authentication context

**Update Required:**
```java
package com.kesik.bladecommerce.security;

import com.kesik.bladecommerce.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter
 * Extracts and validates JWT token from Authorization header
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Validate token
                if (jwtUtil.validateToken(token)) {
                    // Extract username from token
                    String username = jwtUtil.getUsernameFromToken(token);

                    // Create authentication object
                    // Grant ROLE_ADMIN to authenticated users (expand for multiple roles)
                    List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                        );

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("Authenticated user: {}", username);
                } else {
                    log.warn("Invalid JWT token");
                }

            } catch (Exception e) {
                log.error("JWT authentication error: {}", e.getMessage());
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
```

**What Changed:**
1. ✅ Extracts token from Authorization header
2. ✅ Validates token using JwtUtil
3. ✅ Sets authentication in SecurityContext
4. ✅ Grants ROLE_ADMIN to authenticated users

---

#### Substep 1.3.3: Verify JwtUtil Methods

**File:** `src/main/java/com/kesik/bladecommerce/util/JwtUtil.java`

**Verify these methods exist:**
- `validateToken(String token)` - Returns boolean
- `getUsernameFromToken(String token)` - Returns String

**If missing, add:**
```java
public boolean validateToken(String token) {
    try {
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token);
        return true;
    } catch (Exception e) {
        log.error("Invalid JWT token: {}", e.getMessage());
        return false;
    }
}

public String getUsernameFromToken(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return claims.getSubject();
}
```

---

**CHECKPOINT 1.3.1: After enabling security**
🧪 **Testing Required:**

1. **Test public endpoints (NO auth required):**
   ```bash
   # Should work without token
   curl http://localhost:8080/api/knives
   curl http://localhost:8080/api/categories
   curl http://localhost:8080/api/stock/check/some-id?quantity=1
   ```

2. **Test protected endpoints (auth required):**
   ```bash
   # Should fail with 401 Unauthorized
   curl -X POST http://localhost:8080/api/knives -d '{...}'
   curl -X DELETE http://localhost:8080/api/knives/some-id
   ```

3. **Test authentication:**
   ```bash
   # Login and get token
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"Admin123!"}'

   # Use token to access protected endpoint
   curl http://localhost:8080/api/orders \
     -H "Authorization: Bearer YOUR_TOKEN_HERE"
   ```

4. **Test order creation (should still be public):**
   ```bash
   # Should work without auth (called by frontend after payment)
   curl -X POST http://localhost:8080/api/orders \
     -H "Content-Type: application/json" \
     -d '{...orderData...}'
   ```

**Approval Questions:**
- [ ] Public endpoints accessible without auth?
- [ ] Protected endpoints require auth?
- [ ] JWT authentication works?
- [ ] Order creation still public?
- [ ] Approve to proceed OR request changes

---

### Step 1.4: Add Rate Limiting

**Goal:** Prevent brute force attacks and API abuse

**Library:** Bucket4j (token bucket algorithm)

**Add to pom.xml:**
```xml
<!-- Rate Limiting -->
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.7.0</version>
</dependency>
```

**New File:** `src/main/java/com/kesik/bladecommerce/config/RateLimitConfig.java`

```java
package com.kesik.bladecommerce.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using token bucket algorithm
 */
@Configuration
public class RateLimitConfig {

    /**
     * Cache for IP-based rate limit buckets
     */
    @Bean
    public Map<String, Bucket> rateLimitBuckets() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Default rate limit: 100 requests per minute
     */
    @Bean
    public Bandwidth defaultBandwidth() {
        return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    }

    /**
     * Login rate limit: 5 requests per 15 minutes
     */
    @Bean
    public Bandwidth loginBandwidth() {
        return Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(15)));
    }

    /**
     * Order creation rate limit: 3 requests per minute
     */
    @Bean
    public Bandwidth orderBandwidth() {
        return Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1)));
    }
}
```

**New File:** `src/main/java/com/kesik/bladecommerce/filter/RateLimitFilter.java`

```java
package com.kesik.bladecommerce.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Rate limiting filter - prevents API abuse
 * Uses IP address to track request limits
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> rateLimitBuckets;
    private final Bandwidth defaultBandwidth;
    private final Bandwidth loginBandwidth;
    private final Bandwidth orderBandwidth;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        String ip = getClientIP(request);
        String endpoint = request.getRequestURI();

        // Determine bandwidth based on endpoint
        Bandwidth bandwidth = selectBandwidth(endpoint);

        // Get or create bucket for this IP
        Bucket bucket = rateLimitBuckets.computeIfAbsent(ip,
            k -> Bucket.builder().addLimit(bandwidth).build());

        // Try to consume 1 token
        if (bucket.tryConsume(1)) {
            // Token available, allow request
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {} on endpoint: {}", ip, endpoint);
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\":\"Too many requests. Please try again later.\"}"
            );
        }
    }

    /**
     * Select appropriate bandwidth based on endpoint
     */
    private Bandwidth selectBandwidth(String endpoint) {
        if (endpoint.contains("/auth/login")) {
            return loginBandwidth; // 5 per 15 min
        } else if (endpoint.contains("/orders") && endpoint.equals("/api/orders")) {
            return orderBandwidth; // 3 per min
        } else {
            return defaultBandwidth; // 100 per min
        }
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
```

**Update SecurityConfig to add filter:**
```java
// Add rate limit filter before JWT filter
.addFilterBefore(rateLimitFilter, JwtAuthenticationFilter.class)
```

---

**CHECKPOINT 1.4.1: After rate limiting**
🧪 **Testing:**

```bash
# Test login rate limit (should block after 5 attempts in 15 min)
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"wrong"}'
done

# Should see 429 Too Many Requests after 5th attempt
```

**Approval:**
- [ ] Rate limiting works correctly?
- [ ] Limits are appropriate for production?
- [ ] Approve to proceed

---

### Step 1.5: Add Input Validation

**Goal:** Validate all incoming request data

**Current Issue:** Missing `@Valid` annotations

**Fix:** Add `@Valid` to all controller methods

**Example - OrderController:**
```java
// Before
@PostMapping
public ResponseEntity<OrderDto> addOrder(@RequestBody OrderRequestDto orderDto) {

// After
@PostMapping
public ResponseEntity<OrderDto> addOrder(@Valid @RequestBody OrderRequestDto orderDto) {
```

**Add validation annotations to DTOs:**
```java
// Example - OrderRequestDto
@NotNull(message = "Payment ID is required")
private String paymentId;

@NotNull(message = "Conversation ID is required")
private String conversationId;

@NotNull(message = "Buyer information is required")
@Valid
private BuyerDto buyer;
```

**Apply to all controllers:**
- KnifeController
- CategoryController
- OrderController
- AuthController

---

## PHASE 2: New Features Implementation (Week 2)

### Step 2.1: Review System Implementation

[Detailed implementation similar to frontend guide]

### Step 2.2: Turkish Location API

[Detailed implementation]

### Step 2.3: Email HTML Templates

[Detailed implementation using Thymeleaf]

### Step 2.4: Order Status Automation

[Scheduled tasks implementation]

---

## PHASE 3: Testing & Documentation (Week 3)

### Step 3.1: Unit Tests

### Step 3.2: Integration Tests

### Step 3.3: API Documentation (Swagger)

---

## Development Criteria & Checkpoints

### Checkpoint System

**Purpose:** Allow rollback to safe state if changes need revision

**Structure:**
```
CHECKPOINT X.Y.Z: Description
├─ BEFORE: Current state documented
├─ CHANGES: What will be modified
├─ TESTING: Verification steps
├─ APPROVAL: Review & approve
│  ├─ ✅ Approve: Proceed to next step
│  └─ ❌ Reject: Rollback to this checkpoint
└─ AFTER: New state documented
```

**Implementation:**
- Git commit at each checkpoint
- Tag format: `checkpoint-X.Y.Z`
- Branch for major changes

**Example Workflow:**
```bash
# Before making changes
git commit -am "CHECKPOINT 1.2.1: Before modifying OrderController"
git tag checkpoint-1.2.1

# Make changes
# ... modify files ...

# Test changes
# ... run tests ...

# Approval decision point
# ✅ APPROVED: Continue
git commit -am "Completed: OrderController Iyzico verification"

# ❌ REJECTED: Rollback
git reset --hard checkpoint-1.2.1
git tag -d checkpoint-1.2.1  # Remove checkpoint tag
```

### Protected Code Review

**Before modifying Iyzico integration:**
1. Document current behavior
2. Present proposed changes
3. Get explicit approval
4. Implement with tests
5. Verify integration still works
6. Get final approval

**Iyzico Integration Checklist:**
- [ ] Stock deduction still works?
- [ ] Order number generation unchanged?
- [ ] Email notifications still trigger?
- [ ] Payment amounts validated?
- [ ] Basket items processed correctly?

### Error Detection Protocol

**After every code change:**
1. **Check edited file for errors:**
   ```bash
   ./mvnw compile
   # Fix any errors in modified file
   ```

2. **Check affected files:**
   ```bash
   # Find files that import modified classes
   grep -r "import.*ModifiedClassName" src/
   # Compile and check each
   ```

3. **Run tests:**
   ```bash
   ./mvnw test
   ```

4. **Run application:**
   ```bash
   ./mvnw spring-boot:run
   # Verify no startup errors
   ```

### Continuous Communication Points

**After each substep:**
- Report what was completed
- Show test results
- Request approval to continue
- Highlight any issues found

**Before major changes:**
- Explain what will change
- Show current vs. proposed code
- Wait for approval

**When issues arise:**
- Stop immediately
- Document the issue
- Propose solutions
- Wait for directive

---

## Testing Strategy

### Unit Testing

**Framework:** JUnit 5 + Mockito

**Coverage Target:** 80%+

**Priority Order:**
1. IyzicoVerificationService (payment critical)
2. OrderService (business logic)
3. KnifeService (stock management)
4. JwtUtil (security critical)

### Integration Testing

**Framework:** Spring Boot Test + TestContainers (MongoDB)

**Critical Paths:**
1. Full payment flow (Iyzico → Order creation)
2. Authentication flow (Login → JWT → Protected endpoint)
3. Stock validation (Check → Deduct → Order)

### Manual Testing Checklist

After each phase:
- [ ] All endpoints respond
- [ ] Authentication works
- [ ] Payment verification works
- [ ] Stock deduction works
- [ ] Email notifications work
- [ ] Rate limiting works
- [ ] No errors in logs

---

## Deployment Checklist

### Pre-Deployment

- [ ] Resolve all git conflicts
- [ ] All tests passing
- [ ] Security enabled
- [ ] Payment verification active
- [ ] Rate limiting configured
- [ ] Input validation added
- [ ] No TODO/FIXME comments
- [ ] Environment variables documented

### Environment Setup

- [ ] MongoDB Atlas production cluster
- [ ] Cloudinary production account
- [ ] Email service (SendGrid/Resend)
- [ ] Iyzico production API keys
- [ ] Strong JWT secret
- [ ] CORS configured for production domain

### Deployment

- [ ] Deploy to Railway/Render
- [ ] Set SPRING_PROFILES_ACTIVE=prod
- [ ] Verify all environment variables
- [ ] Test health endpoint
- [ ] Test authentication
- [ ] Test payment flow
- [ ] Monitor logs for errors

### Post-Deployment

- [ ] Monitor error rates
- [ ] Monitor payment success rate
- [ ] Monitor API response times
- [ ] Check email delivery
- [ ] Verify security headers
- [ ] Test from production frontend

---

## Appendix: Quick Reference

### Common Commands

```bash
# Build
./mvnw clean install

# Run with dev profile
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run

# Run tests
./mvnw test

# Check for errors
./mvnw compile

# Generate fat JAR
./mvnw package
```

### Critical Files Map

```
Security:
├─ SecurityConfig.java - Main security configuration
├─ JwtAuthenticationFilter.java - JWT validation
├─ JwtUtil.java - Token generation
└─ RateLimitFilter.java - Rate limiting

Payment:
├─ IyzicoConfig.java - Iyzico configuration
├─ IyzicoVerificationService.java - Payment verification
├─ OrderController.java - Order creation endpoint
└─ OrderMapper.java - Payment to order mapping

Configuration:
├─ application.properties - Main config
├─ application-dev.properties - Development
├─ application-prod.properties - Production
└─ .env.example - Environment template
```

### Environment Variables Reference

```env
# Database
MONGODB_URI=mongodb+srv://...
MONGODB_DATABASE=knife_commerce

# JWT
JWT_SECRET=your-256-bit-secret-change-in-production
JWT_EXPIRATION_MS=86400000

# Iyzico
IYZICO_API_KEY=your-production-key
IYZICO_SECRET_KEY=your-production-secret

# Email
MAIL_HOST=smtp.gmail.com
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=app-specific-password

# Cloudinary
CLOUDINARY_CLOUD_NAME=your-cloud
CLOUDINARY_API_KEY=your-key
CLOUDINARY_API_SECRET=your-secret

# CORS
ALLOWED_ORIGINS=https://yourdomain.com

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

---

**Document Version:** 1.0
**Last Updated:** November 15, 2024
**Total Implementation Time:** 3-4 weeks
**Checkpoints:** 15+
**Protected Integration Points:** 5

**Next Steps:**
1. Review this guide
2. Approve Phase 1 plan
3. Begin Step 1.1 (Git conflict resolution)
