# Investment & Portfolio Tracker

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.5.22-4FC08D.svg)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)

## Overview

Investment & Portfolio Tracker is a full-stack web application designed to provide users with comprehensive portfolio management capabilities, real-time analytics, and secure transaction processing. The platform supports multiple asset classes including stocks, mutual funds, bonds, ETFs, real estate, commodities, and cryptocurrency investments.

Built with Spring Boot 3.5.6 backend and Vue.js 3 frontend, the applicat## Testing

### Backend Testing Structure

```
src/test/java/com/zeta/backend/
├── controller/     # Controller layer tests
├── service/        # Service layer unit tests
├── repository/     # Repository integration tests
└── util/           # Utility class tests
```

**Running Backend Tests**
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=UserServiceTest

# Run tests with coverage report
./mvnw test jacoco:report

# Skip tests during build
./mvnw clean install -DskipTests
```

**Test Configuration**
- H2 in-memory database used for testing
- Test properties in `src/test/resources/application-test.properties`
- JUnit 5 for test framework
- Mockito for mocking dependencies

### Frontend Testing

**Available Scripts**
```bash
# Run unit tests (if configured)
npm run test:unit

# Run linting
npm run lint

# Format code
npm run format
```

### Manual Testing Checklist

**Authentication**
- [ ] User can register with valid credentials
- [ ] Duplicate email registration is prevented
- [ ] User can login with correct credentials
- [ ] Invalid credentials show appropriate error
- [ ] JWT token is stored and used for subsequent requests
- [ ] Token expiration redirects to login
- [ ] User can view their profile
- [ ] Admin can view all users

**Investment Management**
- [ ] Users can view all active investment products
- [ ] Admin can create new investment products
- [ ] Admin can update existing products
- [ ] Admin can deactivate products
- [ ] Validation prevents invalid product data
- [ ] Investment types and risk levels display correctly

**Portfolio Operations**
- [ ] User can buy investment units
- [ ] Minimum investment amount is enforced
- [ ] Average purchase price calculated correctly
- [ ] User can sell owned units
- [ ] Cannot sell more units than owned
- [ ] Portfolio displays current holdings accurately
- [ ] Transaction history shows all buy/sell operations

**Analytics**
- [ ] Portfolio summary shows correct totals
- [ ] Asset allocation percentages add up to 100%
- [ ] Gain/loss calculations are accurate
- [ ] Charts render correctly
- [ ] Real-time NAV updates reflect in calculations

**Support System**
- [ ] User can create support tickets
- [ ] Invalid investment product ID is rejected
- [ ] Users see only their own tickets
- [ ] Admin sees all tickets
- [ ] Admin can respond to OPEN tickets
- [ ] Admin can close RESPONDED tickets
- [ ] CLOSED tickets cannot be modified
- [ ] Priority levels work correctly

## Project Structure

### Backend Directory Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/zeta/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── config/
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── ModelMapperConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── InvestmentController.java
│   │   │   │   ├── PortfolioAnalyticsController.java
│   │   │   │   ├── PortfolioController.java
│   │   │   │   ├── TicketController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/
│   │   │   │   ├── AssetAllocationDTO.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GainLossDTO.java
│   │   │   │   ├── InvestmentProductRequestDTO.java
│   │   │   │   ├── InvestmentProductResponseDTO.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── PortfolioRequest.java
│   │   │   │   ├── PortfolioResponse.java
│   │   │   │   ├── PortfolioSummaryDTO.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── RespondDto.java
│   │   │   │   ├── TicketRequestDto.java
│   │   │   │   ├── TicketResponseDto.java
│   │   │   │   ├── TransactionResponse.java
│   │   │   │   └── UserResponse.java
│   │   │   ├── enums/
│   │   │   │   ├── InvestmentType.java
│   │   │   │   ├── RiskLevel.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── TicketPriority.java
│   │   │   │   ├── TicketStatus.java
│   │   │   │   └── TxnType.java
│   │   │   ├── exceptions/
│   │   │   │   └── (Custom exception handlers)
│   │   │   ├── models/
│   │   │   │   ├── InvestmentProduct.java
│   │   │   │   ├── Portfolio.java
│   │   │   │   ├── Ticket.java
│   │   │   │   ├── Transaction.java
│   │   │   │   └── User.java
│   │   │   ├── repository/
│   │   │   │   ├── InvestmentProductRepository.java
│   │   │   │   ├── PortfolioRepository.java
│   │   │   │   ├── TicketRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/
│   │   │   │   ├── InvestmentService.java
│   │   │   │   ├── InvestmentServiceImpl.java
│   │   │   │   ├── PortfolioAnalyticsService.java
│   │   │   │   ├── PortfolioAnalyticsServiceImpl.java
│   │   │   │   ├── PortfolioService.java
│   │   │   │   ├── PortfolioServiceImpl.java
│   │   │   │   ├── TicketService.java
│   │   │   │   └── UserService.java
│   │   │   └── util/
│   │   │       └── (Utility classes)
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/
│       ├── java/com/zeta/backend/
│       │   ├── BackendApplicationTests.java
│       │   ├── controller/
│       │   ├── repository/
│       │   ├── service/
│       │   └── util/
│       └── resources/
│           └── application-test.properties
├── logs/
│   └── investment-portfolio-tracker.log
├── target/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── HELP.md
```

### Frontend Directory Structure

```
frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── App.vue
│   ├── main.js
│   ├── assets/
│   │   ├── base.css
│   │   ├── logo.svg
│   │   └── main.css
│   ├── components/
│   │   ├── AssetAllocationChart.vue
│   │   ├── CoreValueCard.vue
│   │   ├── FeatureCard.vue
│   │   ├── InvestmentCard.vue
│   │   ├── PerformanceSummary.vue
│   │   ├── PortfolioCard.vue
│   │   ├── ProductTableRow.vue
│   │   ├── QuickCategory.vue
│   │   ├── StatisticsCard.vue
│   │   ├── TicketListItem.vue
│   │   ├── TransactionCard.vue
│   │   ├── UserDetailsModal.vue
│   │   ├── UserStatCard.vue
│   │   ├── UserTableRow.vue
│   │   └── WhatWeOfferCard.vue
│   ├── composables/
│   │   └── useAuth.js
│   ├── router/
│   │   └── index.js
│   ├── services/
│   │   └── api.js
│   ├── stores/
│   │   ├── investmentStore.js
│   │   ├── portfolioAnalyticsStore.js
│   │   ├── portfolioStore.js
│   │   └── ticketStore.js
│   ├── utils/
│   │   └── auth.js
│   └── views/
│       ├── AboutView.vue
│       ├── AdminTicketList.vue
│       ├── BuyInvestment.vue
│       ├── CreateTicket.vue
│       ├── HelpCenter.vue
│       ├── HomeView.vue
│       ├── InvestmentList.vue
│       ├── Login.vue
│       ├── ManageInvestments.vue
│       ├── MyPortfolio.vue
│       ├── PortfolioAnalytics.vue
│       ├── Profile.vue
│       ├── Register.vue
│       ├── SellInvestment.vue
│       ├── TicketDetail.vue
│       ├── TransactionHistory.vue
│       ├── UserList.vue
│       └── UserTicketList.vue
├── .gitignore
├── eslint.config.js
├── index.html
├── jsconfig.json
├── package.json
├── README.md
└── vite.config.js
```

## Build & Deployment

### Building for Production

**Backend Production Build**
```bash
cd backend
./mvnw clean package -DskipTests
```

This creates a JAR file at `target/backend-0.0.1-SNAPSHOT.jar`

**Frontend Production Build**
```bash
cd frontend
npm run build
```

This creates optimized static files in `dist/` directory

### Running Production Build

**Backend**
```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**Frontend**
Serve the `dist/` folder using any static file server:
```bash
npm install -g serve
serve -s dist -p 5173
```

### Environment Variables

**Backend Production Configuration**
```properties
# Production database
spring.datasource.url=jdbc:postgresql://your-db-host:5432/production_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT security (use strong secret in production)
jwt.secret=${JWT_SECRET}
jwt.expiration=1800000

# Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logging
logging.level.com.zeta.investmentportfoliotracker=INFO
```

**Frontend Production Configuration**
```javascript
// Update baseURL in src/services/api.js
baseURL: 'https://your-production-api.com/api/v1'
```

### Deployment Checklist

Backend:
- [ ] Update database credentials
- [ ] Generate strong JWT secret
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` or `none`
- [ ] Disable SQL logging (`show-sql=false`)
- [ ] Configure CORS to allow only production frontend URL
- [ ] Set up SSL/TLS for HTTPS
- [ ] Configure logging to file
- [ ] Set up database backups
- [ ] Configure connection pooling

Frontend:
- [ ] Update API base URL to production backend
- [ ] Build with `npm run build`
- [ ] Test production build locally
- [ ] Configure web server (Nginx/Apache)
- [ ] Set up SSL/TLS certificate
- [ ] Configure caching headers
- [ ] Enable gzip compression
- [ ] Test all routes and functionality

## Troubleshooting

### Common Backend Issues

**Port 8080 already in use**
```bash
# Find process using port
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

**Database connection failed**
- Verify PostgreSQL is running
- Check credentials in `application.properties`
- Ensure database exists
- Check firewall settings

**JWT token invalid**
- Check if token has expired (30 minute expiration)
- Verify JWT secret matches between application.properties
- Clear browser sessionStorage and login again

**CORS errors**
- Check CORS configuration in `CorsConfig.java`
- Ensure frontend URL is allowed
- Verify preflight OPTIONS requests are handled

### Common Frontend Issues

**API calls failing**
- Verify backend is running on `http://localhost:8080`
- Check browser console for error messages
- Verify API base URL in `src/services/api.js`
- Check network tab in browser dev tools

**Token expired / Automatic logout**
- JWT token expires after 30 minutes
- Login again to get new token
- Implement token refresh if needed

**Build errors**
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear Vite cache
rm -rf node_modules/.vite
```

**Router navigation not working**
- Ensure Vue Router is properly configured
- Check route guards in `router/index.js`
- Verify authentication state

## Logging & Monitoring

### Backend Logging

**Log Configuration** (`logback-spring.xml`)
- Console appender for development
- File appender with daily rotation
- Log files stored in `backend/logs/`
- 30-day retention policy

**Log Levels**
```properties
# Application logs
logging.level.com.zeta.investmentportfoliotracker=DEBUG

# Spring Framework
logging.level.org.springframework=INFO

# Hibernate SQL
logging.level.org.hibernate.SQL=DEBUG
```

**Log Files**
- `investment-portfolio-tracker.log` - Current day logs
- `investment-portfolio-tracker.YYYY-MM-DD.log` - Historical logs

### Frontend Logging

**Console Logging**
- API errors logged to browser console
- Authentication events logged
- Route navigation logged in development

**Error Tracking**
- Axios interceptors catch and log HTTP errors
- Custom event dispatching for auth state changes

## Performance Optimization

### Backend Optimizations

**Database Indexing**
- Indexed columns: email (users), type/risk_level/is_active (investment_products)
- Composite indexes on frequently queried combinations
- Foreign key indexes for join operations

**Query Optimization**
- JPA fetch strategies optimized (LAZY for collections)
- Custom queries for complex analytics
- Transaction boundaries properly defined

**Connection Pooling**
- HikariCP (default in Spring Boot)
- Configured for optimal performance

### Frontend Optimizations

**Code Splitting**
- Route-level code splitting with `import()`
- Lazy loading for admin and user-specific routes
- Reduces initial bundle size

**Asset Optimization**
- Vite handles asset optimization automatically
- Tree-shaking removes unused code
- Minification in production builds

**State Management**
- Pinia stores only load data when needed
- Computed properties for derived state
- Efficient reactivity system

## Contributing

### Development Workflow

1. Fork the repository
2. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes
4. Test thoroughly
5. Commit with descriptive messages
   ```bash
   git commit -m "Add feature: description"
   ```
6. Push to your fork
   ```bash
   git push origin feature/your-feature-name
   ```
7. Create a Pull Request

### Code Style Guidelines

**Backend (Java)**
- Follow Java naming conventions
- Use meaningful variable and method names
- Add Javadoc comments for public methods
- Keep methods focused and short
- Use Lombok to reduce boilerplate

**Frontend (Vue.js)**
- Follow Vue.js style guide
- Use Composition API consistently
- Component names should be PascalCase
- Props and events should be kebab-case
- Use ESLint and Prettier for formatting

### Commit Message Format

```
type(scope): subject

body

footer
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Example:**
```
feat(portfolio): add asset allocation chart

Implemented pie chart using Vue ECharts to display
portfolio asset allocation by investment type.

Closes #42
```

## License

This project is created for educational purposes as part of a capstone project.

## Acknowledgments

**Technologies Used**
- Spring Boot Framework
- Vue.js Framework
- PostgreSQL Database
- Bootstrap CSS Framework
- JWT for authentication
- Maven for build management
- Vite for frontend tooling

**Team**
- ZETA Dreamers Development Team

## Support & Contact

For questions, issues, or contributions:
- Create an issue in the GitHub repository
- Contact: smranjanmishra (GitHub)

---

**Investment & Portfolio Tracker** - A comprehensive portfolio management solution built with Spring Boot and Vue.js

Copyright 2025 ZETA Dreamers. All rights reserved.ole-based access control, detailed portfolio analytics, transaction history tracking, and an integrated support ticket system.

## System Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                         FRONTEND LAYER                             │
│                          Vue.js 3.5.22                             │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌─────────────────────────┐   │
│  │    Views     │  │  Components  │  │     Pinia Stores        │   │
│  │              │  │              │  │                         │   │
│  │ • Login      │  │ • Investment │  │ • investmentStore       │   │
│  │ • Register   │  │   Card       │  │ • portfolioStore        │   │
│  │ • Home       │  │ • Portfolio  │  │ • portfolioAnalytics    │   │
│  │ • Profile    │  │   Card       │  │ • ticketStore           │   │
│  │ • Investment │  │ • Transaction│  │                         │   │
│  │   List       │  │   Card       │  └─────────────────────────┘   │
│  │ • MyPortfolio│  │ • Asset      │                                │
│  │ • Buy/Sell   │  │   Allocation │  ┌─────────────────────────┐   │
│  │ • Analytics  │  │   Chart      │  │    Vue Router 4         │   │
│  │ • Transaction│  │ • Performance│  │                         │   │
│  │   History    │  │   Summary    │  │ • Route Guards          │   │
│  │ • Help Center│  │ • Ticket     │  │ • Auth Middleware       │   │
│  │ • Tickets    │  │   ListItem   │  │ • Admin Guards          │   │
│  │ • Admin      │  │ • User Stats │  └─────────────────────────┘   │
│  │   - Users    │  │ • Feature    │                                │
│  │   - Products │  │   Cards      │  ┌─────────────────────────┐   │
│  │   - Tickets  │  └──────────────┘  │   Axios HTTP Client     │   │
│  └──────────────┘                    │                         │   │
│                                      │ • Request Interceptor   │   │
│                                      │   (JWT Token)           │   │
│                                      │ • Response Interceptor  │   │
│                                      │ • Error Handling        │   │
│                                      └─────────────────────────┘   │
└────────────────────────────────────────────────────────────────────┘
                                 │
                                 │ HTTP REST API
                                 │ (JSON)
                                 ▼
┌────────────────────────────────────────────────────────────────────┐
│                         BACKEND LAYER                              │
│                      Spring Boot 3.5.6                             │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                   Security Filter Chain                      │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │  │
│  │  │ CORS Config  │→ │JWT Auth      │→ │ Security Config  │    │  │
│  │  │              │  │Filter        │  │                  │    │  │
│  │  │ • Origins    │  │• Token Parse │  │ • Role-Based     │    │  │
│  │  │ • Methods    │  │• Validation  │  │   Access         │    │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────┘    │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                  │
│                                 ▼                                  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Controller Layer                          │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │  │
│  │  │   Auth       │  │  Investment  │  │    Portfolio     │    │  │
│  │  │ Controller   │  │  Controller  │  │   Controller     │    │  │
│  │  │              │  │              │  │                  │    │  │
│  │  │• /auth/      │  │• /investments│  │• /portfolio/buy  │    │  │
│  │  │  register    │  │• /admin/     │  │• /portfolio/sell │    │  │
│  │  │• /auth/login │  │  investments │  │• /portfolio      │    │  │
│  │  └──────────────┘  └──────────────┘  │• /portfolio/     │    │  │
│  │                                      │  transactions    │    │  │
│  │  ┌──────────────┐  ┌──────────────┐  └──────────────────┘    │  │
│  │  │   User       │  │Portfolio     │  ┌──────────────────┐    │  │
│  │  │ Controller   │  │Analytics     │  │     Ticket       │    │  │
│  │  │              │  │Controller    │  │   Controller     │    │  │
│  │  │• /user/      │  │              │  │                  │    │  │
│  │  │  profile     │  │• /portfolio/ │  │• /support        │    │  │
│  │  │• /admin/     │  │  summary     │  │• /support/user   │    │  │
│  │  │  users       │  │• /portfolio/ │  │• /admin/support  │    │  │
│  │  └──────────────┘  │  allocation  │  │• /support/{id}/  │    │  │
│  │                    │• /portfolio/ │  │  respond         │    │  │
│  │  ┌──────────────┐  │  gains       │  └──────────────────┘    │  │
│  │  │   Admin      │  └──────────────┘                          │  │
│  │  │ Controller   │                                            │  │
│  │  └──────────────┘                                            │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                  │
│                                 ▼                                  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                      Service Layer                           │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │  │
│  │  │  User        │  │ Investment   │  │   Portfolio      │    │  │
│  │  │  Service     │  │  Service     │  │   Service        │    │  │
│  │  │              │  │              │  │                  │    │  │
│  │  │• Register    │  │• Create      │  │• Buy Investment  │    │  │
│  │  │• Login       │  │• Update      │  │• Sell Investment │    │  │
│  │  │• JWT Generate│  │• Fetch Active│  │• Get Portfolio   │    │  │
│  │  │• Password    │  │• Validation  │  │• Transactions    │    │  │
│  │  │  Hashing     │  │              │  │• Avg Price Calc  │    │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────┘    │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐                          │  │
│  │  │Portfolio     │  │   Ticket     │                          │  │
│  │  │Analytics     │  │   Service    │                          │  │
│  │  │Service       │  │              │                          │  │
│  │  │              │  │• Create      │                          │  │
│  │  │• Summary     │  │• Update      │                          │  │
│  │  │• Allocation  │  │• Get User    │                          │  │
│  │  │• Gains/Loss  │  │  Tickets     │                          │  │
│  │  │• ROI Calc    │  │• Admin Reply │                          │  │
│  │  └──────────────┘  └──────────────┘                          │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                                 │                                  │
│                                 ▼                                  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                   Repository Layer (JPA)                     │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐    │  │
│  │  │   User       │  │ Investment   │  │   Portfolio      │    │  │
│  │  │ Repository   │  │ Repository   │  │   Repository     │    │  │
│  │  └──────────────┘  └──────────────┘  └──────────────────┘    │  │
│  │                                                              │  │
│  │  ┌──────────────┐  ┌──────────────┐                          │  │
│  │  │Transaction   │  │   Ticket     │                          │  │
│  │  │Repository    │  │  Repository  │                          │  │
│  │  └──────────────┘  └──────────────┘                          │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌────────────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                                │
│                     PostgreSQL Database                            │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐      │
│  │    users     │  │investment_   │  │     portfolio        │      │
│  │              │  │  products    │  │                      │      │
│  │ • id (PK)    │  │              │  │ • id (PK)            │      │
│  │ • name       │  │ • id (PK)    │  │ • userId (FK)        │      │
│  │ • email      │  │ • name       │  │ • investmentProduct  │      │
│  │ • password_  │  │ • type       │  │   Id (FK)            │      │
│  │   hash       │  │ • risk_level │  │ • unitsOwned         │      │
│  │ • phone      │  │ • min_       │  │ • avgPurchasePrice   │      │
│  │ • role       │  │   investment │  │                      │      │
│  │ • created_at │  │ • expected_  │  └──────────────────────┘      │
│  └──────────────┘  │   return_rate│                                │
│                    │ • current_nav│  ┌──────────────────────┐      │
│  ┌──────────────┐  │ • is_active  │  │   transactions       │      │
│  │supportTicket │  │ • description│  │                      │      │
│  │              │  │ • created_at │  │ • id (PK)            │      │
│  │ • id (PK)    │  │ • updated_at │  │ • userId (FK)        │      │
│  │ • userId(FK) │  └──────────────┘  │ • investmentProduct  │      │
│  │ • investment │                    │   Id (FK)            │      │
│  │   ProductId  │                    │ • txnType            │      │
│  │ • subject    │                    │   (BUY/SELL)         │      │
│  │ • description│                    │ • units              │      │
│  │ • status     │                    │ • navAtTxn           │      │
│  │ • priority   │                    │ • txnDate            │      │
│  │ • response   │                    └──────────────────────┘      │
│  │ • createdAt  │                                                  │
│  │ • updatedAt  │                                                  │
│  └──────────────┘                                                  │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Backend Technologies

**Core Framework**
- Spring Boot 3.5.6
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation

**Security & Authentication**
- JWT (JSON Web Tokens) - io.jsonwebtoken:jjwt 0.12.5
- BCrypt password hashing
- Role-based access control (USER, ADMIN)

**Database**
- PostgreSQL (Production)
- H2 Database (Testing)
- Hibernate ORM

**Additional Libraries**
- Lombok - Reduces boilerplate code
- ModelMapper 3.2.0 - Object mapping
- Logback - Logging framework with file rotation

**Build & Development**
- Maven 3.x
- Java 17
- Spring Boot DevTools

### Frontend Technologies

**Core Framework**
- Vue.js 3.5.22 (Composition API)
- Vite 7.1.11 (Build tool)

**State Management & Routing**
- Pinia 3.0.3 (State management)
- Vue Router 4.6.3 (Client-side routing)

**UI & Styling**
- Bootstrap 5.3.8
- Bootstrap Icons 1.13.1
- Custom CSS

**HTTP & Data**
- Axios 1.12.2 (HTTP client)
- jwt-decode 4.0.0 (JWT token parsing)
- Vue ECharts 8.0.1 (Charts and visualizations)

**Development Tools**
- ESLint 9.37.0 (Code linting)
- Prettier 3.6.2 (Code formatting)
- Vite Plugin Vue DevTools 8.0.3

**Node Version**
- Node.js ^20.19.0 or >=22.12.0

## Features

### Module 1: User Registration & Authentication

## Features

### Module 1: User Registration & Authentication

**Backend APIs**
- `POST /api/v1/auth/register` - Register new user with role assignment
- `POST /api/v1/auth/login` - Authenticate user and receive JWT token
- `GET /api/v1/user/profile` - Get authenticated user profile details
- `GET /api/v1/admin/users` - List all registered users (Admin only)

**Frontend Views**
- `Register.vue` - User registration form with validation
- `Login.vue` - User authentication with password toggle
- `Profile.vue` - User profile display with role information
- `UserList.vue` - Admin panel for viewing all users

**Business Rules**
- Only administrators can access `/api/v1/admin/**` routes
- Passwords are hashed using BCrypt before storage
- JWT tokens expire after 30 minutes (1800000 ms)
- Email addresses must be unique and valid
- Phone numbers are optional but must be 10 digits if provided
- Default role is USER; ADMIN role is assigned manually

### Module 2: Investment Product Listing & Selection

**Backend APIs**
- `GET /api/v1/investments` - List all active investment products (Public)
- `POST /api/v1/admin/investments` - Create new investment product (Admin only)
- `PUT /api/v1/admin/investments/{id}` - Update investment product (Admin only)

**Frontend Views**
- `InvestmentList.vue` - Browse available investment products with filtering
- `ManageInvestments.vue` - Admin interface for product management

**Investment Types**
- STOCK - Equity shares of a company
- MUTUAL_FUND - Professionally managed investment fund
- BOND - Fixed income debt security
- ETF - Exchange Traded Fund
- REAL_ESTATE - Property investment
- COMMODITY - Physical goods like gold, oil
- CRYPTOCURRENCY - Digital currency investment

**Risk Levels**
- LOW - Low Risk (Score: 1) - Suitable for conservative investors
- MEDIUM - Medium Risk (Score: 2) - Balanced risk-return profile
- HIGH - High Risk (Score: 3) - Suitable for aggressive investors

**Business Rules**
- Each product must have type, risk level, return rate, and current NAV
- `expectedReturnRate` must be between 0.01% and 100%
- `minInvestment` must be greater than 0
- `currentNAV` must be greater than 0
- Products can be activated/deactivated via `isActive` flag
- Only active products are displayed to regular users

### Module 3: Portfolio Management

**Backend APIs**
- `POST /api/v1/portfolio/buy` - Execute buy transaction for investment
- `POST /api/v1/portfolio/sell` - Execute sell transaction for investment
- `GET /api/v1/portfolio` - View current user portfolio holdings
- `GET /api/v1/portfolio/transactions` - View complete transaction history

**Frontend Views**
- `MyPortfolio.vue` - Display current holdings and portfolio summary
- `BuyInvestment.vue` - Form to purchase investment units
- `SellInvestment.vue` - Form to sell owned investment units
- `TransactionHistory.vue` - Detailed transaction history with filters

**Business Rules**
- Portfolio value = Sum of (unitsOwned × currentNAV) per product
- Selling is not allowed if requested units exceed owned units
- Minimum investment amount (minInvestment) must be met on purchase
- Average purchase price is recalculated on each buy transaction using formula:
  ```
  newAvgPrice = (oldAvg × oldUnits + newNAV × newUnits) / (oldUnits + newUnits)
  ```
- Every buy/sell operation is recorded as an immutable transaction
- Transaction types: BUY and SELL
- All transactions store NAV at time of transaction for accurate historical tracking

### Module 4: Portfolio Analytics & Insights

**Backend APIs**
- `GET /api/v1/portfolio/summary` - Total investment value and performance metrics
- `GET /api/v1/portfolio/allocation` - Asset type breakdown by percentage
- `GET /api/v1/portfolio/gains` - Gain/loss analysis for each holding

**Frontend Views**
- `PortfolioAnalytics.vue` - Comprehensive analytics dashboard
- `AssetAllocationChart.vue` - Pie chart showing investment distribution
- `PerformanceSummary.vue` - Gain/loss breakdown with visual indicators

**Analytics Calculations**
- **Current Value**: unitsOwned × currentNAV per holding
- **Total Invested**: Sum of (units × navAtTxn) for all BUY transactions
- **Absolute Return**: Current Value - Total Invested
- **ROI Percentage**: (Absolute Return / Total Invested) × 100
- **Asset Allocation**: Percentage distribution by investment type
- **Gain/Loss per Asset**: Individual holding performance analysis

**Business Rules**
- Analytics use real-time NAV for current valuation
- Historical transaction data ensures accurate cost basis
- Asset allocation calculated based on current portfolio value
- Negative returns indicate losses, positive indicate gains

### Module 5: Support & Helpdesk System

**Backend APIs**
- `POST /api/v1/support` - Create new support ticket
- `GET /api/v1/support/user` - View tickets created by authenticated user
- `GET /api/v1/admin/support` - View all support tickets (Admin only)
- `PUT /api/v1/support/{ticketId}/respond` - Admin responds to ticket

**Frontend Views**
- `HelpCenter.vue` - Support center landing page
- `CreateTicket.vue` - Form to raise new support ticket
- `UserTicketList.vue` - User's ticket history and status
- `AdminTicketList.vue` - Admin panel for managing all tickets
- `TicketDetail.vue` - Detailed view of individual ticket

**Ticket Status Flow**
```
OPEN → RESPONDED → CLOSED
```

**Ticket Priorities**
- LOW - Low priority issue
- MEDIUM - Medium priority (default)
- HIGH - High priority issue requiring immediate attention

**Business Rules**
- Each ticket must be linked to a user
- Investment product ID is optional but validated if provided
- Users can only view their own tickets
- Admins can view and respond to all tickets
- Status transitions: OPEN → RESPONDED → CLOSED
- Once CLOSED, tickets cannot be modified
- Priority can be updated when responding to OPEN tickets
- Response text is required when updating ticket status

## Installation & Setup

### Prerequisites

**Required Software**
- Java Development Kit (JDK) 17 or higher
- Node.js 20.19.0 or 22.12.0 or higher
- PostgreSQL 13 or higher (or any compatible SQL database)
- Maven 3.6 or higher (included with mvnw wrapper)
- Git for version control

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/smranjanmishra/Capstone-Investment-and-Portfolio-Tracker.git
   cd Capstone-Investment-and-Portfolio-Tracker/backend
   ```

2. **Configure PostgreSQL database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE postgres;
   ```

3. **Configure application properties**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   # PostgreSQL Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=admin
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA/Hibernate Configuration
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   
   # JWT Configuration
   jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
   jwt.expiration=1800000
   ```

4. **Build the application**
   ```bash
   ./mvnw clean install
   ```

5. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

6. **Verify backend is running**
   - Application runs on: `http://localhost:8080`
   - API base path: `http://localhost:8080/api/v1`
   - Check logs in `backend/logs/` directory

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure API base URL (Optional)**
   
   Create `.env` file in frontend root:
   ```env
   VITE_API_BASE_URL=http://localhost:8080/api/v1
   ```
   
   Or modify `src/services/api.js` directly:
   ```javascript
   baseURL: 'http://localhost:8080/api/v1'
   ```

4. **Start development server**
   ```bash
   npm run dev
   ```

5. **Access the application**
   - Frontend URL: `http://localhost:5173`
   - Default credentials: Create account via `/register`

### Initial Admin Setup

To create an admin user, you need to manually set the role in the database:

```sql
-- First register a user through the application
-- Then update their role to ADMIN
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

## API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Access Level | Request Body |
|--------|----------|-------------|--------------|--------------|
| POST | `/api/v1/auth/register` | Register new user | Public | `{name, email, password, phone?}` |
| POST | `/api/v1/auth/login` | User login | Public | `{email, password}` |
| GET | `/api/v1/user/profile` | Get user profile | Authenticated | - |
| GET | `/api/v1/admin/users` | List all users | Admin | - |

### Investment Management

| Method | Endpoint | Description | Access Level | Request Body |
|--------|----------|-------------|--------------|--------------|
| GET | `/api/v1/investments` | List active products | Public | - |
| POST | `/api/v1/admin/investments` | Create product | Admin | `{name, type, riskLevel, minInvestment, expectedReturnRate, currentNAV, description?, isActive}` |
| PUT | `/api/v1/admin/investments/{id}` | Update product | Admin | Same as POST |

### Portfolio Operations

| Method | Endpoint | Description | Access Level | Request Body |
|--------|----------|-------------|--------------|--------------|
| GET | `/api/v1/portfolio` | Get user portfolio | User | - |
| POST | `/api/v1/portfolio/buy` | Buy investment | User | `{investmentProductId, units}` |
| POST | `/api/v1/portfolio/sell` | Sell investment | User | `{investmentProductId, units}` |
| GET | `/api/v1/portfolio/transactions` | Transaction history | User | - |

### Portfolio Analytics

| Method | Endpoint | Description | Access Level | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/v1/portfolio/summary` | Portfolio summary | User | Total value, invested amount, ROI |
| GET | `/api/v1/portfolio/allocation` | Asset allocation | User | Breakdown by investment type |
| GET | `/api/v1/portfolio/gains` | Gain/loss analysis | User | Per-holding profit/loss |

### Support System

| Method | Endpoint | Description | Access Level | Request Body |
|--------|----------|-------------|--------------|--------------|
| POST | `/api/v1/support` | Create ticket | User | `{subject, description, investmentProductId?, priority?}` |
| GET | `/api/v1/support/user` | User's tickets | User | - |
| GET | `/api/v1/admin/support` | All tickets | Admin | - |
| PUT | `/api/v1/support/{ticketId}/respond` | Respond to ticket | Admin | `{response, priority?}` |

### API Response Format

**Success Response**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "count": 10
}
```

**Error Response**
```json
{
  "error": "Error message",
  "status": 400,
  "timestamp": "2025-10-31T10:30:00Z"
}
```

## Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐     ┌──────────────────────┐     ┌─────────────────────┐
│     User        │     │     Portfolio        │     │ InvestmentProduct   │
├─────────────────┤     ├──────────────────────┤     ├─────────────────────┤
│ id (PK)         │◄───┐│ id (PK)              │    ┌│ id (PK)             │
│ name            │    ││ userId (FK)          │────┘│ name                │
│ email (UNIQUE)  │    │├ investmentProductId ◄┘     │ type                │
│ password_hash   │    ││   (FK)               │     │ risk_level          │
│ phone           │    ││ unitsOwned           │     │ min_investment      │
│ role            │    ││ avgPurchasePrice     │     │ expected_return_rate│
│ created_at      │    │└──────────────────────┘     │ current_nav         │
└─────────────────┘    │                             │ is_active           │
       │               │                             │ description         │
       │               │                             │ created_at          │
       │               │                             │ updated_at          │
       │               │                             └─────────────────────┘
       │               │
       │               │ ┌──────────────────────┐
       │               │ │    Transaction       │
       │               │ ├──────────────────────┤
       │               └►│ id (PK)              │
       │                 │ userId (FK)          │
       │                 │ investmentProductId  │
       │                 │   (FK)               │
       │                 │ txnType              │
       │                 │ units                │
       │                 │ navAtTxn             │
       │                 │ txnDate              │
       │                 └──────────────────────┘
       │
       │                 ┌──────────────────────┐
       │                 │   SupportTicket      │
       │                 ├──────────────────────┤
       └────────────────►│ id (PK)              │
                         │ userId (FK)          │
                         │ investmentProductId  │
                         │   (FK, optional)     │
                         │ subject              │
                         │ description          │
                         │ status               │
                         │ priority             │
                         │ response             │
                         │ createdAt            │
                         │ updatedAt            │
                         └──────────────────────┘
```

### Table Definitions

**users**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

**investment_products**
```sql
CREATE TABLE investment_products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    min_investment DECIMAL(15,2) NOT NULL,
    expected_return_rate DECIMAL(5,2) NOT NULL,
    current_nav DECIMAL(15,4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_type ON investment_products(type);
CREATE INDEX idx_risk_level ON investment_products(risk_level);
CREATE INDEX idx_is_active ON investment_products(is_active);
```

**portfolio**
```sql
CREATE TABLE portfolio (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    investment_product_id BIGINT NOT NULL REFERENCES investment_products(id),
    units_owned DECIMAL(19,4) NOT NULL,
    avg_purchase_price DECIMAL(19,2) NOT NULL,
    UNIQUE(user_id, investment_product_id)
);

CREATE INDEX idx_portfolio_user_id ON portfolio(user_id);
```

**transactions**
```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    investment_product_id BIGINT NOT NULL REFERENCES investment_products(id),
    txn_type VARCHAR(10) NOT NULL,
    units DECIMAL(19,4) NOT NULL,
    nav_at_txn DECIMAL(19,4) NOT NULL,
    txn_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_date ON transactions(txn_date DESC);
```

**supportTicket**
```sql
CREATE TABLE supportTicket (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    investment_product_id BIGINT REFERENCES investment_products(id),
    subject VARCHAR(255),
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    response TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_support_user_id ON supportTicket(user_id);
CREATE INDEX idx_support_status ON supportTicket(status);
```

## Business Logic & Validation Rules

### User Registration & Authentication

**Password Requirements**
- Must not be blank
- Hashed using BCrypt with strength 10
- Never returned in API responses

**Email Validation**
- Must be valid email format
- Must be unique across system
- Converted to lowercase and trimmed before storage

**Phone Validation**
- Optional field
- Must be exactly 10 digits if provided
- Pattern: `^$|^[0-9]{10}$`

**JWT Token**
- Secret key: Configured in `application.properties`
- Expiration: 30 minutes (1800000 milliseconds)
- Algorithm: HMAC-SHA256
- Stored in `sessionStorage` on frontend

### Investment Product Rules

**Validation**
- Name: 3-200 characters, required
- Type: Must be valid InvestmentType enum
- Risk Level: Must be LOW, MEDIUM, or HIGH
- Min Investment: Must be > 0
- Expected Return Rate: 0.01% - 100%
- Current NAV: Must be > 0
- Is Active: Boolean, defaults to true

**Display Rules**
- Only `isActive = true` products shown to regular users
- Admins can see all products
- Products can be deactivated but not deleted

### Portfolio Management Rules

**Buy Transaction**
- Investment amount (units × currentNAV) must meet `minInvestment`
- Creates new portfolio entry if user doesn't own the product
- Updates existing portfolio if user already owns the product
- Recalculates average purchase price:
  ```
  newAvgPrice = ((currentAvg × currentUnits) + (nav × newUnits)) / (currentUnits + newUnits)
  ```

**Sell Transaction**
- User must own sufficient units
- Cannot sell more than currently owned
- Updates portfolio with new unit count
- If all units sold, portfolio entry remains with 0 units

**Transaction Logging**
- Every buy/sell creates immutable transaction record
- Stores NAV at time of transaction
- Timestamp auto-generated
- Used for accurate historical analysis

### Analytics Calculations

**Portfolio Value**
```
Current Value = Σ(unitsOwned × currentNAV) for each holding
```

**Total Invested**
```
Total Invested = Σ(units × navAtTxn) for all BUY transactions
               - Σ(units × navAtTxn) for all SELL transactions
```

**ROI Calculation**
```
Absolute Return = Current Value - Total Invested
ROI % = (Absolute Return / Total Invested) × 100
```

**Asset Allocation**
```
Type Allocation % = (Value of Type / Total Portfolio Value) × 100
```

### Support Ticket Rules

**Creation**
- Subject and description required
- Investment product ID optional but validated if provided
- Priority defaults to MEDIUM if not specified
- Status automatically set to OPEN

**Status Transitions**
- OPEN → RESPONDED: Admin provides first response
- RESPONDED → CLOSED: Admin closes ticket with final comment
- CLOSED tickets cannot be modified

**Access Control**
- Users can only view their own tickets
- Admins can view and respond to all tickets
- Investment product validation ensures user owns referenced product

## Security Implementation

### Authentication Flow

```
1. User submits login credentials
2. Backend validates credentials
3. If valid, generate JWT token with user ID and role
4. Return token to frontend
5. Frontend stores token in sessionStorage
6. Frontend includes token in Authorization header for subsequent requests
7. Backend validates token on each request
8. Extract user ID from token for authorization
```

### JWT Token Structure

```json
{
  "sub": "12345",
  "iat": 1698765432,
  "exp": 1698767232,
  "roles": ["ROLE_USER"]
}
```

### Security Configuration

**CORS Policy**
- Allows all origins in development (`*`)
- Should be restricted to specific origins in production
- Max age: 3600 seconds

**Protected Endpoints**
- `/api/v1/admin/**` - Requires ADMIN role
- All other `/api/v1/**` - Requires authentication
- `/api/v1/auth/**` - Public (login, register)
- `/api/v1/investments` (GET) - Public

**Password Security**
- BCrypt hashing with default strength (10)
- Passwords never logged or returned in responses
- Stored as `password_hash` in database

**Session Management**
- Stateless JWT authentication (no server-side sessions)
- Token expiration: 30 minutes
- Frontend handles token expiration and redirects to login

### Authorization Rules

**Role-Based Access**
```java
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Admin logic
}
```

**Resource Ownership**
- Users can only access their own portfolio
- Users can only view their own transactions
- Users can only view their own support tickets
- Admins have full access to all resources

## Testing

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