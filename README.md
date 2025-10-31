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
┌─────────────────┐    HTTP/REST API    ┌─────────────────┐
│   Frontend      │◄──────────────────►│   Backend       │
│   (Vue.js 3)    │                    │ (Spring Boot)   │
└─────────────────┘                    └─────────────────┘
│                                       │
│ ┌─────────────┐                      │ ┌─────────────┐
│ │ Vue Router  │                      │ │ Spring      │
│ │ Pinia Store │                      │ │ Security    │
│ │ Axios       │                      │ │ JWT Auth    │
│ └─────────────┘                      │ └─────────────┘
│                                       │
│                                       ▼
│                                    ┌─────────────────┐
│                                    │    Database     │
│                                    │ (H2/MySQL/PSQL) │
│                                    └─────────────────┘
```

### Technology Stack

**Backend Services**
- **Framework**: Spring Boot 3.x with Spring WebMVC
- **Security**: Spring Security with JWT authentication
- **Database**: JPA/Hibernate with H2 (dev) / PostgreSQL/MySQL (prod)
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build**: Maven 3.8+

**Frontend Application**
- **Framework**: Vue.js 3 with Composition API
- **State Management**: Pinia
- **Routing**: Vue Router 4
- **UI Library**: Bootstrap 5 with custom components
- **HTTP Client**: Axios with interceptors
- **Build System**: Vite

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
- **Database**: PostgreSQL 13+ (recommended) or MySQL 8+
- **Git**: Latest version

### Backend Setup

1. **Clone and navigate to backend**
   ```bash
   git clone <repository-url>
   cd Capstone-Investment-and-Portfolio-Tracker/backend
   ```

2. **Configure application properties**
   ```properties
   # src/main/resources/application-prod.properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/investment_tracker
   spring.datasource.username=${DB_USERNAME:postgres}
   spring.datasource.password=${DB_PASSWORD:password}
   
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   
   jwt.secret=${JWT_SECRET:your-secure-secret-key}
   jwt.expiration=${JWT_EXPIRATION:86400000}
   ```

3. **Build and run the application**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
   ```

4. **Verify backend deployment**
   - API Base URL: `http://localhost:8080/api/v1`
   - Health Check: `http://localhost:8080/actuator/health`
   - API Documentation: `http://localhost:8080/swagger-ui.html`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Configure environment variables**
   ```bash
   # .env.production
   VITE_API_BASE_URL=http://localhost:8080/api/v1
   VITE_APP_TITLE=Investment & Portfolio Tracker
   VITE_APP_VERSION=1.0.0
   ```

4. **Start development server**
   ```bash
   npm run dev
   # or
   yarn dev
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
| GET | `/analytics/performance` | Performance metrics | User |

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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
- **Caching Strategy**: Redis integration for session and data caching
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

# Docker deployment
docker build -t investment-tracker-backend .
docker run -p 8080:8080 investment-tracker-backend

# Environment variables for production
export DB_HOST=your-db-host
export DB_USERNAME=your-db-user
export JWT_SECRET=your-production-secret
```

**Frontend Deployment**
```bash
# Build for production
npm run build

# Deploy to static hosting
npm run deploy

# Docker deployment
docker build -t investment-tracker-frontend .
docker run -p 80:80 investment-tracker-frontend
```

### Environment Configuration

**Production Environment Variables**
```env
# Database Configuration
DB_HOST=production-db-host
DB_PORT=5432
DB_NAME=investment_tracker_prod
DB_USERNAME=prod_user
DB_PASSWORD=secure_password

# JWT Configuration
JWT_SECRET=your-production-jwt-secret-key
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
CORS_ALLOWED_ORIGINS=https://your-domain.com

# Monitoring
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
```

## Monitoring & Maintenance

### Application Monitoring
- **Health Checks**: Spring Boot Actuator endpoints
- **Performance Metrics**: Custom metrics for business operations
- **Error Tracking**: Comprehensive logging with structured format
- **Database Monitoring**: Connection pool and query performance metrics

### Maintenance Procedures
- **Database Backups**: Automated daily backups with retention policy
- **Log Rotation**: Automated log management with archival
- **Security Updates**: Regular dependency updates and security patches
- **Performance Tuning**: Regular performance analysis and optimization

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

# Setup pre-commit hooks
npm install husky --save-dev
npx husky install

# Run development environment
docker-compose up -d  # For database and external services
./mvnw spring-boot:run --debug
npm run dev
```

## Support & Contact

### Technical Support
- **Issue Tracking**: [GitHub Issues](https://github.com/your-repo/issues)
- **Documentation**: [Wiki](https://github.com/your-repo/wiki)
- **Security Issues**: security@yourcompany.com

### Development Team
- **Project Lead**: [Your Name](mailto:lead@yourcompany.com)
- **Backend Team**: [Backend Lead](mailto:backend@yourcompany.com)
- **Frontend Team**: [Frontend Lead](mailto:frontend@yourcompany.com)

---

**Investment & Portfolio Tracker** - Professional Investment Management Platform  
© 2025 ZETA Dreamers. All rights reserved.