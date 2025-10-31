<script setup>
import { RouterLink } from 'vue-router'
import QuickCategory from '@/components/QuickCategory.vue'
import FeatureCard from '@/components/FeatureCard.vue'
</script>

<template>
  <div class="home-container">
    <!-- Hero Section -->
    <div class="hero-section text-center">
      <div class="container">
        <h1 class="display-3 fw-bold mb-3">
          Simple, Free Investing.
        </h1>
        <p class="hero-subtitle mb-4">
          Stocks, Mutual Funds, ETFs & More
        </p>
        <p class="lead mb-4 text-white-50">
          Invest in thousands of products with zero commission fees
        </p>

        <div class="cta-buttons">
          <RouterLink to="/investments" class="btn btn-success btn-lg me-3 px-5">
            <i class="bi bi-arrow-right-circle me-2"></i>
            Get Started
          </RouterLink>
          <RouterLink to="/about" class="btn btn-light-custom btn-lg px-5">
            <i class="bi bi-info-circle me-2"></i>
            Learn More
          </RouterLink>
        </div>
      </div>
    </div>

    <!-- Quick Categories -->
    <div class="categories-section">
      <div class="container">
        <div class="row g-3 justify-content-center">
          <QuickCategory
            to="/investments?type=STOCK"
            icon="bi-graph-up-arrow"
            icon-class="bg-primary bg-opacity-10 text-primary"
            title="Stocks"
          />
          <QuickCategory
            to="/investments?type=MUTUAL_FUND"
            icon="bi-pie-chart"
            icon-class="bg-success bg-opacity-10 text-success"
            title="Mutual Funds"
          />
          <QuickCategory
            to="/investments?type=BOND"
            icon="bi-bank"
            icon-class="bg-warning bg-opacity-10 text-warning"
            title="Bonds"
          />
          <QuickCategory
            to="/investments?type=ETF"
            icon="bi-grid-3x3"
            icon-class="bg-info bg-opacity-10 text-info"
            title="ETFs"
          />
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div class="features-section container my-5 py-4">
      <h2 class="text-center mb-5 fw-bold">Key Features</h2>
      <div class="row g-4">
        <FeatureCard
          icon="bi-search"
          icon-class="bg-primary text-white"
          title="Browse Products"
          description="Explore a wide range of investment products including stocks, mutual funds, bonds, ETFs, and more with detailed information."
        />
        <FeatureCard
          icon="bi-funnel"
          icon-class="bg-success text-white"
          title="Smart Filtering"
          description="Filter investments by type, risk level, and search by name to find products that match your investment strategy."
        />
        <FeatureCard
          icon="bi-shield-check"
          icon-class="bg-warning text-white"
          title="Risk Analysis"
          description="View risk levels and expected returns for each product to make informed investment decisions based on your risk appetite."
        />
        <FeatureCard
          icon="bi-graph-up"
          icon-class="bg-info text-white"
          title="Real-time NAV"
          description="Access current Net Asset Values (NAV) for all products to track market performance and make timely decisions."
        />
        <FeatureCard
          icon="bi-gear"
          icon-class="bg-danger text-white"
          title="Admin Management"
          description="Comprehensive admin panel for managing investment products, adding new offerings, and maintaining product information."
        />
        <FeatureCard
          icon="bi-lightning"
          icon-class="bg-secondary text-white"
          title="Fast & Responsive"
          description="Built with modern technologies for a smooth, fast, and responsive experience across all devices."
        />
      </div>
    </div>

    <!-- Statistics Section -->
    <div class="stats-section bg-primary text-white py-5">
      <div class="container">
        <div class="row text-center">
          <div class="col-md-4 mb-4 mb-md-0">
            <i class="bi bi-wallet2 display-4 mb-3"></i>
            <h3 class="fw-bold">Multiple Types</h3>
            <p>Stocks, Mutual Funds, Bonds, ETFs & More</p>
          </div>
          <div class="col-md-4 mb-4 mb-md-0">
            <i class="bi bi-speedometer2 display-4 mb-3"></i>
            <h3 class="fw-bold">Real-time Data</h3>
            <p>Up-to-date NAV and Market Information</p>
          </div>
          <div class="col-md-4">
            <i class="bi bi-shield-lock display-4 mb-3"></i>
            <h3 class="fw-bold">Secure & Reliable</h3>
            <p>Enterprise-grade Security Standards</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Links Section -->
    <div class="quick-links-section container my-5 py-4">
      <h2 class="text-center mb-5 fw-bold">Quick Access</h2>
      <div class="row g-4 justify-content-center">
        <div class="col-md-5">
          <div class="card shadow-sm border-0 quick-link-card">
            <div class="card-body p-4">
              <h4 class="card-title mb-3">
                <i class="bi bi-person me-2 text-primary"></i>
                For Investors
              </h4>
              <p class="card-text text-muted mb-4">
                Browse our comprehensive list of investment products and find opportunities 
                that align with your financial goals.
              </p>
              <RouterLink to="/investments" class="btn btn-primary w-100">
                View Investment Products
                <i class="bi bi-arrow-right ms-2"></i>
              </RouterLink>
            </div>
          </div>
        </div>
        <div class="col-md-5">
          <div class="card shadow-sm border-0 quick-link-card">
            <div class="card-body p-4">
              <h4 class="card-title mb-3">
                <i class="bi bi-shield-check me-2 text-success"></i>
                For Administrators
              </h4>
              <p class="card-text text-muted mb-4">
                Manage investment products, add new offerings, and maintain accurate 
                information for all listed products.
              </p>
              <RouterLink to="/admin/investments" class="btn btn-success w-100">
                Manage Products
                <i class="bi bi-arrow-right ms-2"></i>
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  min-height: 100vh;
  padding: 0;
  margin: 0;
  width: 100%;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #00d09c 0%, #00b386 100%);
  color: white;
  padding: 5rem 1rem 4rem;
  width: 100%;
  margin: 0;
}

.hero-section h1 {
  font-size: 3.5rem;
  font-weight: 700;
  color: white;
  letter-spacing: -1px;
  margin-bottom: 0.5rem;
}

.hero-subtitle {
  font-size: 1.5rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.95);
  margin-bottom: 1rem;
}

.hero-section .lead {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.85);
  max-width: 600px;
  margin: 0 auto 2rem;
}

/* Search Container */
.search-container {
  max-width: 600px;
  margin: 0 auto 2rem;
}

.search-box {
  background: white;
  border-radius: 50px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.search-box .input-group-text {
  padding: 1rem 1.25rem;
}

.search-box .form-control {
  padding: 1rem 1.25rem;
  font-size: 1rem;
}

.search-box .form-control:focus {
  outline: none;
  box-shadow: none;
}

.search-hint {
  text-align: center;
}

/* Categories Section */
.categories-section {
  background: white;
  padding: 2rem 1rem;
  margin: 0;
  border-bottom: 1px solid #f0f0f0;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-buttons .btn {
  min-width: 180px;
  font-weight: 600;
  transition: all 0.3s ease;
  border-radius: 8px;
  padding: 0.75rem 2rem;
  font-size: 1.1rem;
}

.cta-buttons .btn-success {
  background-color: #00b386;
  border-color: #00b386;
}

.cta-buttons .btn-success:hover {
  background-color: #009970;
  border-color: #009970;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 179, 134, 0.4);
}

/* Custom Learn More button styling */
.btn-light-custom {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: 2px solid white;
}

.btn-light-custom:hover {
  background-color: white;
  color: #00b386;
  border-color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 255, 255, 0.3);
}

.stats-section {
  box-shadow: inset 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stats-section i {
  opacity: 0.9;
}

.quick-link-card {
  transition: all 0.3s ease;
}

.quick-link-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-section {
    padding: 3rem 1rem;
  }

  .hero-section h1 {
    font-size: 1.75rem;
  }

  .cta-buttons {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .cta-buttons .btn {
    min-width: 180px;
    width: 100%;
    max-width: 280px;
    margin: 0.5rem 0;
  }

  .features-section,
  .quick-links-section {
    padding-left: 1rem;
    padding-right: 1rem;
  }
}
</style>
