# Investment & Portfolio Tracker

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Overview

A comprehensive, enterprise-grade Investment & Portfolio Tracker platform designed to provide investors with sophisticated portfolio management capabilities, real-time analytics, and secure transaction processing. The system supports multiple asset classes and provides detailed insights into portfolio performance with professional-grade security and scalability.

## Architecture

### System Architecture
```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND LAYER                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                Vue.js 3                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │    Views    │  │ Components  │  │   Router    │  │      Pinia Store       │ │
│  │             │  │             │  │             │  │                         │ │
│  │ • Login.vue │  │ • Header    │  │ • Auth      │  │ • User State           │ │
│  │ • Register  │  │ • Sidebar   │  │ • Portfolio │  │ • Portfolio State      │ │
│  │ • Portfolio │  │ • Cards     │  │ • Admin     │  │ • Investment State     │ │
│  │ • Analytics │  │ • Forms     │  │ • Support   │  │ • Auth State           │ │
│  │ • Support   │  │ • Charts    │  │             │  │                         │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                        HTTP Client (Axios)                                 │ │
│  │ • Request Interceptors  • Response Interceptors  • Error Handling          │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
                                       │
                             HTTP REST API Calls
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                               BACKEND LAYER                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                              Spring Boot 3.x                                  │
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                        Security Layer                                      │ │
│  │ ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │
│  │ │   CORS      │  │   JWT Auth  │  │ Role-Based  │  │   Security Config   │ │ │
│  │ │ Configuration│  │  Filter     │  │ Access      │  │                     │ │ │
│  │ └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                       │                                         │
│                                       ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                       Controller Layer                                     │ │
│  │ ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │
│  │ │    Auth     │  │  Investment │  │  Portfolio  │  │      Support        │ │ │
│  │ │ Controller  │  │ Controller  │  │ Controller  │  │    Controller       │ │ │
│  │ │             │  │             │  │             │  │                     │ │ │
│  │ │ • /auth/*   │  │ • /invest*  │  │ • /portfolio│  │ • /support/*        │ │ │
│  │ │ • /user/*   │  │ • /admin/*  │  │ • /analytics│  │ • /admin/support/*  │ │ │
│  │ └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                       │                                         │
│                                       ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                        Service Layer                                       │ │
│  │ ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │
│  │ │    Auth     │  │  Investment │  │  Portfolio  │  │      Support        │ │ │
│  │ │   Service   │  │   Service   │  │   Service   │  │      Service        │ │ │
│  │ │             │  │             │  │             │  │                     │ │ │
│  │ │ • JWT Logic │  │ • CRUD Ops  │  │ • Buy/Sell  │  │ • Ticket Management │ │ │
│  │ │ • Password  │  │ • NAV Calc  │  │ • Analytics │  │ • Status Updates    │ │ │
│  │ │   Hashing   │  │ • Validation│  │ • Portfolio │  │ • Priority Handling │ │ │
│  │ └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
│                                       │                                         │
│                                       ▼                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────┐ │
│  │                      Repository Layer                                      │ │
│  │ ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │ │
│  │ │    User     │  │  Investment │  │  Portfolio  │  │     Support         │ │ │
│  │ │ Repository  │  │ Repository  │  │ Repository  │  │   Repository        │ │ │
│  │ │             │  │             │  │             │  │                     │ │ │
│  │ │ • JPA CRUD  │  │ • JPA CRUD  │  │ • JPA CRUD  │  │ • JPA CRUD          │ │ │
│  │ │ • Custom    │  │ • Custom    │  │ • Custom    │  │ • Custom Queries    │ │ │
│  │ │   Queries   │  │   Queries   │  │   Queries   │  │                     │ │ │
│  │ └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘ │ │
│  └─────────────────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                             DATABASE LAYER                                     │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                H2 Database                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │
│  │    Users    │  │ Investment  │  │  Portfolio  │  │    Support Tickets      │ │
│  │    Table    │  │ Products    │  │   Table     │  │       Table             │ │
│  │             │  │   Table     │  │             │  │                         │ │
│  │ • id (PK)   │  │ • id (PK)   │  │ • id (PK)   │  │ • id (PK)              │ │
│  │ • name      │  │ • name      │  │ • userId    │  │ • userId (FK)          │ │
│  │ • email     │  │ • type      │  │ • product   │  │ • subject              │ │
│  │ • password  │  │ • riskLevel │  │ • units     │  │ • description          │ │
│  │ • role      │  │ • currentNAV│  │ • avgPrice  │  │ • status               │ │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────────┘ │
│                                       │                                         │
│                         ┌─────────────┴─────────────┐                         │
│                         │      Transactions         │                         │
│                         │         Table             │                         │
│                         │                           │                         │
│                         │ • id (PK)                 │                         │
│                         │ • userId (FK)             │                         │
│                         │ • investmentId (FK)       │                         │
│                         │ • txnType (BUY/SELL)      │                         │
│                         │ • units                   │                         │
│                         │ • navAtTxn                │                         │
│                         │ • txnDate                 │                         │
│                         └───────────────────────────┘                         │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Frontend Architecture Details

**Component Structure**
```
src/
├── views/                      # Page-level components
│   ├── Login.vue              # User authentication
│   ├── Register.vue           # User registration
│   ├── Portfolio.vue          # Portfolio overview
│   ├── InvestmentList.vue     # Browse investments
│   ├── Analytics.vue          # Portfolio analytics
│   ├── TransactionHistory.vue # Transaction records
│   ├── SupportTickets.vue     # Support system
│   └── admin/                 # Admin-only views
│       ├── UserManagement.vue
│       ├── InvestmentMgmt.vue
│       └── SupportManagement.vue
│
├── components/                 # Reusable components
│   ├── common/
│   │   ├── Header.vue         # Navigation header
│   │   ├── Sidebar.vue        # Side navigation
│   │   ├── LoadingSpinner.vue
│   │   └── AlertMessage.vue
│   ├── forms/
│   │   ├── LoginForm.vue
│   │   ├── BuyInvestmentForm.vue
│   │   └── SupportTicketForm.vue
│   └── charts/
│       ├── AssetAllocationChart.vue
│       └── PerformanceChart.vue
│
├── stores/                     # Pinia state management
│   ├── auth.js                # Authentication state
│   ├── portfolio.js           # Portfolio state
│   ├── investments.js         # Investment products state
│   └── support.js             # Support tickets state
│
├── services/                   # API service layer
│   ├── api.js                 # Axios configuration
│   ├── authService.js         # Authentication APIs
│   ├── portfolioService.js    # Portfolio APIs
│   └── supportService.js      # Support APIs
│
└── router/                     # Vue Router configuration
    └── index.js               # Route definitions & guards
```

**State Management Flow**
```
Vue Component ──► Pinia Action ──► API Service ──► Backend
     ▲                                               │
     │                                               ▼
 UI Update ◄── Pinia State ◄── Response ◄── HTTP Response
```

### Backend Architecture Details

**Package Structure**
```
src/main/java/com/zeta/backend/
├── controller/                 # REST Controllers
│   ├── AuthController.java    # Authentication endpoints
│   ├── UserController.java    # User management
│   ├── InvestmentController.java # Investment products
│   ├── PortfolioController.java  # Portfolio operations
│   ├── AnalyticsController.java  # Portfolio analytics
│   └── SupportController.java    # Support system
│
├── service/                    # Business logic layer
│   ├── AuthService.java       # Authentication logic
│   ├── UserService.java       # User operations
│   ├── InvestmentService.java # Investment CRUD
│   ├── PortfolioService.java  # Portfolio management
│   ├── AnalyticsService.java  # Calculations & analytics
│   └── SupportService.java    # Ticket management
│
├── repository/                 # Data access layer
│   ├── UserRepository.java    # User data access
│   ├── InvestmentRepository.java # Investment data
│   ├── PortfolioRepository.java  # Portfolio data
│   ├── TransactionRepository.java # Transaction history
│   └── SupportRepository.java    # Support tickets
│
├── models/                     # JPA entities
│   ├── User.java             # User entity
│   ├── InvestmentProduct.java # Investment product entity
│   ├── Portfolio.java        # Portfolio holdings
│   ├── Transaction.java      # Transaction records
│   └── SupportTicket.java    # Support tickets
│
├── dto/                        # Data transfer objects
│   ├── request/               # Request DTOs
│   └── response/              # Response DTOs
│
├── config/                     # Configuration classes
│   ├── SecurityConfig.java    # Spring Security setup
│   ├── JwtConfig.java         # JWT configuration
│   └── CorsConfig.java        # CORS settings
│
└── exceptions/                 # Custom exception handling
    ├── GlobalExceptionHandler.java
    ├── UserNotFoundException.java
    └── InsufficientFundsException.java
```

**Request/Response Flow**
```
HTTP Request ──► Security Filter ──► Controller ──► Service ──► Repository ──► Database
     ▲                                    │             │          │            │
     │                                    ▼             ▼          ▼            ▼
HTTP Response ◄─── Response DTO ◄─── Business Logic ◄─── JPA ◄─── Query ◄─── H2 DB
```

### Technology Stack

**Frontend Application**
- **Framework**: Vue.js 3 with Composition API
- **State Management**: Pinia for centralized state management
- **Routing**: Vue Router 4 with navigation guards
- **UI Library**: Bootstrap 5 with custom styling
- **HTTP Client**: Axios with request/response interceptors
- **Build System**: Vite for fast development and optimized builds
- **Icons**: Bootstrap Icons for consistent iconography

**Backend Services**
- **Framework**: Spring Boot 3.x with Spring WebMVC
- **Security**: Spring Security with JWT authentication
- **Database**: JPA/Hibernate with H2 in-memory database
- **Validation**: Bean Validation (JSR-303) with custom validators
- **Testing**: JUnit 5 with Mockito for unit testing
- **Documentation**: Spring Boot Actuator for monitoring
- **Build**: Maven for dependency management and build automation

## Features

### 🔐 Authentication & Authorization
- Secure JWT-based authentication with refresh tokens
- Role-based access control (USER, ADMIN)
- Password encryption using BCrypt
- Session management and automatic logout

### 💼 Investment Management
- Comprehensive investment product catalog
- Multi-asset class support (Stocks, Mutual Funds, Bonds, ETFs)
- Real-time NAV updates
- Risk assessment and categorization
- Administrative product management interface

### 📊 Portfolio Operations
- Real-time portfolio tracking and valuation
- Buy/Sell transaction processing
- Weighted average cost calculation
- Transaction history and audit trail
- Holdings consolidation and reporting

### 📈 Analytics & Insights
- Advanced portfolio analytics dashboard
- ROI calculations and performance metrics
- Asset allocation visualization
- Gain/Loss analysis with tax implications
- Historical performance tracking

### 🎧 Support System
- Integrated helpdesk ticket management
- Priority-based ticket routing
- Administrative response system
- Ticket lifecycle management

## Installation & Setup

### Prerequisites
- **Java Development Kit**: 17 or higher
- **Node.js**: 16.x or higher with npm/yarn
- **Git**: Latest version

### Backend Setup

1. **Clone and navigate to backend**
   ```bash
   git clone https://github.com/smranjanmishra/Investment-and-Portfolio-Tracker.git
   cd Capstone-Investment-and-Portfolio-Tracker/backend
   ```

2. **Configure application properties** (H2 Database - No additional setup required)
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   spring.h2.console.enabled=true
   
   jwt.secret=mySecretKey
   jwt.expiration=86400000
   ```

3. **Build and run the application**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Verify backend deployment**
   - API Base URL: `http://localhost:8080/api/v1`
   - H2 Console: `http://localhost:8080/h2-console`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment variables**
   ```javascript
   // src/services/api.js
   const API_BASE_URL = 'http://localhost:8080/api/v1'
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```

5. **Access the application**
   - Frontend URL: `http://localhost:5173`

## API Documentation

### Authentication Endpoints
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| POST | `/auth/register` | User registration | Public |
| POST | `/auth/login` | User authentication | Public |
| GET | `/user/profile` | Get user profile | Authenticated |
| GET | `/admin/users` | List all users | Admin Only |

### Investment Product Management
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| GET | `/investments` | List active products | Authenticated |
| POST | `/admin/investments` | Create new product | Admin Only |
| PUT | `/admin/investments/{id}` | Update product | Admin Only |
| DELETE | `/admin/investments/{id}` | Deactivate product | Admin Only |

### Portfolio Operations
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| GET | `/portfolio` | Get user portfolio | User |
| POST | `/portfolio/buy` | Execute buy transaction | User |
| POST | `/portfolio/sell` | Execute sell transaction | User |
| GET | `/portfolio/transactions` | Transaction history | User |

### Analytics & Reporting
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| GET | `/portfolio/summary` | Portfolio summary | User |
| GET | `/portfolio/allocation` | Asset allocation | User |
| GET | `/portfolio/gains` | Gain/Loss analysis | User |

### Support System
| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| POST | `/support` | Create support ticket | User |
| GET | `/support/user` | Get user tickets | User |
| GET | `/admin/support` | Get all tickets | Admin Only |
| PUT | `/support/{id}/respond` | Respond to ticket | Admin Only |

## Database Schema

### Entity Relationship Diagram
```
┌─────────────┐     ┌─────────────────┐     ┌──────────────────┐
│    User     │────▶│   Portfolio     │────▶│ InvestmentProduct│
│             │     │                 │     │                  │
│ id (PK)     │     │ id (PK)         │     │ id (PK)          │
│ name        │     │ userId (FK)     │     │ name             │
│ email       │     │ investmentId(FK)│     │ type             │
│ role        │     │ unitsOwned      │     │ currentNAV       │
└─────────────┘     │ avgPurchasePrice│     │ riskLevel        │
       │            └─────────────────┘     └──────────────────┘
       │                     │
       ▼                     ▼
┌─────────────┐     ┌─────────────────┐
│ Transaction │     │ SupportTicket   │
│             │     │                 │
│ id (PK)     │     │ id (PK)         │
│ userId (FK) │     │ userId (FK)     │
│ txnType     │     │ subject         │
│ units       │     │ status          │
│ navAtTxn    │     │ priority        │
└─────────────┘     └─────────────────┘
```

### Core Entities

**User Entity**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Investment Product Entity**
```sql
CREATE TABLE investment_products (
    id BIGINT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    min_investment DECIMAL(15,2) NOT NULL,
    expected_return_rate DECIMAL(5,2) NOT NULL,
    current_nav DECIMAL(10,4) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Business Logic & Validation Rules

### Investment Validation
- **Minimum Investment**: Must meet product-specific minimum thresholds
- **Risk Assessment**: Categorized as LOW, MEDIUM, HIGH with appropriate warnings
- **Return Rate Validation**: Expected returns between 0.1% and 30% annually
- **NAV Updates**: Real-time validation against market data feeds

### Portfolio Management Rules
- **Transaction Integrity**: All buy/sell operations are atomic and logged
- **Average Price Calculation**: Weighted average recalculated on each purchase
- **Selling Constraints**: Cannot sell more units than currently owned
- **Portfolio Valuation**: Real-time calculation using current NAV values

### Security Policies
- **Authentication**: JWT tokens with 24-hour expiration
- **Authorization**: Role-based access with route-level protection
- **Data Validation**: Comprehensive input sanitization and validation
- **Audit Trail**: Complete transaction and user activity logging

## Testing Strategy

### Backend Testing
```bash
# Run complete test suite
./mvnw test

# Run integration tests
./mvnw test -Dtest="*IntegrationTest"

# Generate coverage report
./mvnw jacoco:report

# Run specific test categories
./mvnw test -Dtest="*ControllerTest"
./mvnw test -Dtest="*ServiceTest"
```

### Frontend Testing
```bash
# Unit tests
npm run test:unit

# Component testing
npm run test:component

# End-to-end testing
npm run test:e2e

# Coverage report
npm run test:coverage
```

### Test Coverage Goals
- **Backend**: Minimum 85% line coverage
- **Frontend**: Minimum 80% component coverage
- **Integration**: All API endpoints tested
- **Security**: Authentication and authorization flows validated

## Performance & Scalability

### Performance Metrics
- **API Response Time**: < 200ms for 95th percentile
- **Database Queries**: Optimized with proper indexing
- **Frontend Load Time**: < 3 seconds initial load
- **Transaction Processing**: < 500ms end-to-end

### Scalability Considerations
- **Database Connection Pooling**: HikariCP for optimal connection management
- **API Rate Limiting**: Implemented to prevent abuse
- **Horizontal Scaling**: Stateless design for easy horizontal scaling

## Security Implementation

### Authentication Flow
```
Client Request → JWT Verification → Role Validation → Resource Access
     ↓              ↓                    ↓               ↓
   Login       Token Decode         Permission      API Response
   Endpoint    & Validate           Check           or Rejection
```

### Security Features
- **Password Security**: BCrypt with configurable strength
- **JWT Security**: HMAC-SHA256 signing with secure secret rotation
- **CORS Configuration**: Restricted origins for production deployment
- **Input Sanitization**: Comprehensive validation on all user inputs
- **SQL Injection Prevention**: Parameterized queries and JPA protection

## Deployment Guide

### Production Deployment

**Backend Deployment**
```bash
# Build production JAR
./mvnw clean package -Dmaven.test.skip=true

# Run production JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**Frontend Deployment**
```bash
# Build for production
npm run build

# Serve built files (example with serve)
npm install -g serve
serve -s dist
```

## Contributing

### Development Workflow
1. **Fork Repository**: Create a personal fork of the main repository
2. **Feature Branch**: Create feature branches from `develop`
3. **Code Standards**: Follow established coding conventions
4. **Testing**: Ensure comprehensive test coverage
5. **Documentation**: Update documentation for any API changes
6. **Pull Request**: Submit PR with detailed description and test results

### Code Standards
- **Java**: Follow Google Java Style Guide
- **Vue.js**: Follow Vue.js Style Guide and ESLint rules
- **Commit Messages**: Use conventional commit format
- **Documentation**: JSDoc for frontend, Javadoc for backend

### Development Environment Setup
```bash
# Install development tools
npm install -g @vue/cli vue-tsc
./mvnw install -DskipTests

# Run development environment
./mvnw spring-boot:run --debug
npm run dev
```

---

**Investment & Portfolio Tracker** - Professional Investment Management Platform  
© 2025 ZETA Dreamers. All rights reserved.