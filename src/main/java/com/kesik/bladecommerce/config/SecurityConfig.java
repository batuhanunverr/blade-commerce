package com.kesik.bladecommerce.config;

import com.kesik.bladecommerce.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Blade Commerce API.
 *
 * Security model:
 * - Stateless JWT authentication for admin endpoints
 * - Public access for product browsing and order creation
 * - Role-based authorization (ADMIN role required for management operations)
 *
 * CSRF is disabled because:
 * - API is stateless (no sessions)
 * - JWT tokens in Authorization header provide CSRF protection
 * - Frontend is separate SPA making JSON API calls
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF disabled - using stateless JWT authentication
                // JWT in Authorization header inherently protects against CSRF
                .csrf(csrf -> csrf.disable())

                // Stateless session - no server-side session storage
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // ===== PUBLIC ENDPOINTS =====

                        // Authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Product browsing (read-only) - customers can browse
                        .requestMatchers(HttpMethod.GET, "/api/knives/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // Stock checking - customers need to check availability
                        .requestMatchers(HttpMethod.GET, "/api/stock/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/stock/check-batch").permitAll()

                        // Order creation - customers can place orders
                        .requestMatchers(HttpMethod.POST, "/api/orders").permitAll()

                        // Order status list - for dropdowns
                        .requestMatchers(HttpMethod.GET, "/api/orders/status/all").permitAll()

                        // Social proof - recent purchases for trust building
                        // TODO: Consider rate limiting this endpoint to prevent abuse
                        .requestMatchers(HttpMethod.GET, "/api/social-proof/**").permitAll()

                        // ===== ADMIN ONLY ENDPOINTS =====

                        // Product management (create, update, delete)
                        .requestMatchers(HttpMethod.POST, "/api/knives/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/knives/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/knives/**").hasRole("ADMIN")

                        // Category management
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                        // Order management (view all orders, update status, delete)
                        .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/statistics").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/orders/status/{status}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/orders/migrate-order-numbers").hasRole("ADMIN")

                        // Single order by ID - admin only (customers don't have accounts)
                        .requestMatchers(HttpMethod.GET, "/api/orders/{id}").hasRole("ADMIN")

                        // Admin statistics and management endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Disable form login - we use JWT tokens
                .formLogin(form -> form.disable())

                // Add JWT authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
