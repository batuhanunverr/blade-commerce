# Backend Documentation

This directory contains documentation for the Blade Commerce backend API.

## Quick Links

- [Production Readiness](./PRODUCTION_READINESS.md) - Production deployment checklist
- [Backend Finalization Guide](./BACKEND_FINALIZATION_GUIDE.md) - Final steps before launch
- [Backend Phase Checklist](./BACKEND_PHASE_CHECKLIST.md) - Development phase tracking
- [Troubleshooting](./TROUBLESHOOTING.md) - Common issues and solutions
- [API Endpoints](./API_ENDPOINTS.md) - Complete API reference
- [Social Proof Feature](./SOCIAL_PROOF_FEATURE.md) - Recent purchases social proof

## Project Structure

```
blade-commerce/
├── src/
│   └── main/
│       └── java/
│           └── com/kesik/bladecommerce/
│               ├── config/          # Configuration classes
│               ├── controller/      # REST API endpoints
│               ├── dto/             # Data Transfer Objects
│               ├── mapper/          # Entity-DTO mappers
│               ├── repository/      # MongoDB repositories
│               └── service/         # Business logic
└── docs/                            # Documentation
```

## Technology Stack

- **Framework:** Spring Boot 3.3.4
- **Language:** Java 23
- **Database:** MongoDB Atlas
- **Authentication:** JWT (JSON Web Tokens)
- **Payment:** Iyzico Payment Gateway
- **Build Tool:** Maven

## Key Features

### Security
- JWT-based authentication
- Role-based access control (USER, ADMIN)
- Public/protected endpoint separation
- Password encryption with BCrypt

### Payment Integration
- Iyzico payment gateway integration
- Secure payment form generation
- Transaction verification
- Order-payment linking

### Stock Management
- Atomic stock operations (MongoDB findAndModify)
- Automatic rollback on order failure
- 30-second in-memory caching
- Concurrent order prevention

### Data Management
- Pagination support on all list endpoints
- Soft deletes where applicable
- Audit trails for critical operations
- Consistent ApiResponse wrapper

## API Response Format

All endpoints return a standardized response:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* actual data */ },
  "timestamp": "2025-12-06T15:30:00"
}
```

Error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2025-12-06T15:30:00"
}
```

## Environment Variables

Required environment variables:

```bash
# MongoDB
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/dbname

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Iyzico
IYZICO_API_KEY=your-api-key
IYZICO_SECRET_KEY=your-secret-key
IYZICO_BASE_URL=https://sandbox-api.iyzipay.com

# Application
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
```

## Running the Application

### Development Mode

```bash
# Using the start script
./start-dev.sh

# Or using Maven directly
./mvnw spring-boot:run
```

### Production Mode

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/blade-commerce-0.0.1-SNAPSHOT.jar
```

## Testing

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=OrderServiceTest
```

## Database Setup

The application uses MongoDB Atlas. Collections are created automatically on first run.

### Main Collections

- `users` - User accounts and authentication
- `knives` - Product catalog
- `categories` - Product categories
- `orders` - Order records
- `payments` - Payment transactions

## Common Commands

```bash
# Clean build
./mvnw clean compile

# Package without tests
./mvnw package -DskipTests

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# View application logs
tail -f logs/application.log
```

## Troubleshooting

See [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for common issues and solutions.

## Contributing

When adding new features:

1. Create DTO classes in `dto/` package
2. Add repository interface in `repository/`
3. Implement service in `service/impl/`
4. Create controller endpoint in `controller/`
5. Update SecurityConfig if needed
6. Document in appropriate docs file

## Recent Updates (2025-12-06)

- ✅ Added Social Proof feature (RecentPurchaseDTO, SocialProofController)
- ✅ Public endpoint for recent purchases: `/api/social-proof/recent-purchases`
- ✅ Privacy-safe anonymization of customer data
- ✅ Fuzzy time descriptions in Turkish

See [CHANGELOG.md](../../knife-shop/docs/CHANGELOG.md) in frontend docs for detailed changes.
