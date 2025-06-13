# ApplicationContextTask - Spring Boot with Profiles

This module demonstrates how to configure and run a Spring Boot application for a **Cache Eviction Policy System** using **Spring Profiles** to separate environments and **ApplicationContext** features.

## Overview

The goal of this module is to:

* Convert a standard Java application to a Spring Boot-based application
* Use two Spring Profiles: `dev` and `prod`
* Set up different data sources based on active profiles
* Maintain flexibility to switch between embedded (H2) and external (JDBC) databases

---

## 🔧 Technologies Used

* Java 17+
* Spring Boot 3+
* Spring Context
* Spring JDBC
* Maven
* H2 (for development)
* PostgreSQL/MySQL (via JDBC in prod)


---

## 🚀 Running the Application

### 1. Development Profile (`dev`) - H2 Embedded

Use this profile for local development with an in-memory H2 database.

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

#### `application-dev.properties`

```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:cachedb
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.platform=h2
```

### 2. Production Profile (`prod`) - External JDBC DB

Use this profile when connecting to a production database (e.g., PostgreSQL or MySQL).

```bash
SPRING_PROFILES_ACTIVE=prod mvn spring-boot:run
```

#### `application-prod.properties`

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/cachedb
spring.datasource.username=postgres
spring.datasource.password=secret
```

---

## ✅ Features

* Switch between `dev` (H2) and `prod` (PostgreSQL/MySQL) using profiles
* Loads the appropriate configuration using `@Profile`
* Leverages `ApplicationContext` to initialize and test services
* JDBC-only based implementation (No JPA)

---

## 🧪 Testing

* Run with both profiles and verify correct DB connection and data
* Observe logs for profile-specific beans loaded

---

## 📌 Notes

* JDBC implementation remains the same and is reused
* All beans like `CacheDAO`, `CacheService` are initialized via Spring context
* `ApplicationContextTask` is now Spring Boot aware

---


