# Troubleshooting Guide

## Issue: MongoDB Authentication Error

### Error Message:
```
Command failed with error 13 (Unauthorized): 'command createIndexes requires authentication'
```

### Root Cause:
The application is trying to connect to MongoDB localhost without credentials, but your MongoDB instance requires authentication.

### Solution:

You have **.env** file with MongoDB Atlas credentials that needs to be loaded. Use the provided startup script:

```bash
cd /Users/serayayakta/IdeaProjects/blade-commerce
./start-dev.sh
```

This script automatically loads all environment variables from `.env` including:
- MongoDB Atlas connection string
- Email credentials
- Cloudinary API keys

### What the startup script does:

```bash
#!/bin/bash
#  Load environment variables from .env file
set -a
source .env
set +a

# Start Spring Boot application
./mvnw spring-boot:run
```

---

## Alternative: Manual Environment Setup

If the script doesn't work, you can manually export environment variables:

```bash
cd /Users/serayayakta/IdeaProjects/blade-commerce

# Load .env into current shell
set -a
source .env
set +a

# Start application
./mvnw spring-boot:run
```

---

## Verification: Check if Backend is Running

```bash
curl http://localhost:8080/api/knives
```

Should return JSON data (empty array or list of knives).

---

## Common Issues:

### 1. Port 8080 Already in Use

**Error:** `Port 8080 is already in use`

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### 2. MongoDB Connection Timeout

**Error:** `Timed out after 30000 ms while waiting to connect`

**Solution:**
- Check internet connection (MongoDB Atlas is cloud-hosted)
- Verify MongoDB URI in `.env` is correct
- Check if IP is whitelisted in MongoDB Atlas

### 3. Environment Variables Not Loading

**Error:** Still connecting to `localhost:27017`

**Solution:**
- Ensure `.env` file exists in backend root directory
- Use `./start-dev.sh` script (not `./mvnw spring-boot:run` directly)
- Verify `.env` file permissions: `chmod 600 .env`

### 4. JWT Compilation Errors

**Error:** `cannot find symbol: method parserBuilder()`

**Solution:**
This has been fixed. If you see this error:
```bash
cd /Users/serayayakta/IdeaProjects/blade-commerce
./mvnw clean compile
```

---

## Success Indicators:

✅ You should see these log messages:

```
INFO --- [BladeCommerce] : Started BladeCommerceApplication in X.XXX seconds
INFO --- [AdminInitializer] : Default admin user created: admin / Admin123!
INFO --- [TomcatWebServer] : Tomcat started on port(s): 8080 (http)
```

✅ No "Unauthorized" or "Authentication" errors
✅ Backend accessible at `http://localhost:8080`

---

## Still Having Issues?

1. Check logs in `logs/application-dev.log`
2. Verify MongoDB Atlas cluster is running
3. Check `.env` file has correct credentials
4. Try cleaning and rebuilding:
   ```bash
   ./mvnw clean package -DskipTests
   ./start-dev.sh
   ```

---

## Quick Reference:

| Component | Status Check | Expected Result |
|-----------|--------------|-----------------|
| Backend | `curl http://localhost:8080/api/knives` | JSON response |
| MongoDB | Check logs for "MongoClient created" | No errors |
| Tomcat | Check logs for "Tomcat started on port 8080" | Success message |
| Admin User | Check logs for "Default admin user created" | Success message |
