<template>
  <div class="about-container">
    <!-- Hero Section -->
    <section class="about-hero">
      <div class="container">
        <h1 class="display-4 fw-bold mb-4">About Investment Tracker</h1>
        <p class="lead">
          Your trusted partner in building and managing a successful investment portfolio
        </p>
      </div>
    </section>

    <!-- Mission Section -->
    <section class="mission-section">
      <div class="container">
        <div class="row align-items-center">
          <div class="col-lg-6 mb-4 mb-lg-0">
            <h2 class="section-title mb-4">Our Mission</h2>
            <p class="text-muted mb-4">
              To democratize investment management by providing a powerful, user-friendly platform 
              that helps individuals make informed investment decisions and track their portfolio 
              performance with confidence.
            </p>
            <p class="text-muted">
              We believe that everyone deserves access to professional-grade investment tools 
              without the complexity and high costs traditionally associated with portfolio management.
            </p>
          </div>
          <div class="col-lg-6">
            <div class="mission-card">
              <div class="card border-0 shadow-lg">
                <div class="card-body p-5">
                  <div class="d-flex align-items-center mb-3">
                    <i class="bi bi-bullseye text-primary display-4 me-3"></i>
                    <h4 class="mb-0">Our Goal</h4>
                  </div>
                  <p class="text-muted mb-0">
                    Empower investors with real-time insights, comprehensive analytics, 
                    and intelligent recommendations to achieve their financial goals.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-detail-section bg-light">
      <div class="container">
        <h2 class="section-title text-center mb-5">What We Offer</h2>
        <div class="row g-4">
          <WhatWeOfferCard
            icon="bi-graph-up-arrow"
            iconClass="text-primary"
            title="Portfolio Tracking"
            description="Monitor your investments in real-time with comprehensive performance metrics"
          />
          <WhatWeOfferCard
            icon="bi-shield-check"
            iconClass="text-success"
            title="Risk Analysis"
            description="Understand your portfolio risk with advanced analytics and risk indicators"
          />
          <WhatWeOfferCard
            icon="bi-bar-chart-line"
            iconClass="text-warning"
            title="Market Insights"
            description="Access real-time market data and investment opportunities across asset classes"
          />
          <WhatWeOfferCard
            icon="bi-people"
            iconClass="text-info"
            title="Expert Support"
            description="Get guidance from financial experts and community insights"
          />
        </div>
      </div>
    </section>

    <!-- Statistics Section -->
    <section class="stats-section bg-primary text-white">
      <div class="container">
        <div class="row text-center">
          <div class="col-md-3 mb-4 mb-md-0">
            <div class="stat-item">
              <h2 class="display-4 fw-bold mb-2">10+</h2>
              <p class="mb-0">Investment Types</p>
            </div>
          </div>
          <div class="col-md-3 mb-4 mb-md-0">
            <div class="stat-item">
              <h2 class="display-4 fw-bold mb-2">24/7</h2>
              <p class="mb-0">Real-time Tracking</p>
            </div>
          </div>
          <div class="col-md-3 mb-4 mb-md-0">
            <div class="stat-item">
              <h2 class="display-4 fw-bold mb-2">100%</h2>
              <p class="mb-0">Secure Platform</p>
            </div>
          </div>
          <div class="col-md-3">
            <div class="stat-item">
              <h2 class="display-4 fw-bold mb-2">∞</h2>
              <p class="mb-0">Growth Potential</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Values Section -->
    <section class="values-section">
      <div class="container">
        <h2 class="section-title text-center mb-5">Our Core Values</h2>
        <div class="row g-4">
          <CoreValueCard
            icon="bi-transparency"
            iconClass="text-primary"
            title="Transparency"
            description="We believe in complete transparency in all investment products, fees, and performance metrics. No hidden costs, no surprises."
          />
          <CoreValueCard
            icon="bi-lightbulb"
            iconClass="text-warning"
            title="Innovation"
            description="Constantly evolving our platform with cutting-edge technology to provide the best investment management experience."
          />
          <CoreValueCard
            icon="bi-person-check"
            iconClass="text-success"
            title="User-Centric"
            description="Every feature is designed with our users in mind, ensuring an intuitive and powerful investment management experience."
          />
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container text-center">
        <h2 class="mb-4">Ready to Start Your Investment Journey?</h2>
        <p class="lead text-muted mb-4">
          Join thousands of investors who trust our platform for their portfolio management
        </p>
        <div class="cta-buttons">
          <RouterLink to="/investments" class="btn btn-primary btn-lg me-3 mb-3">
            <i class="bi bi-bar-chart-line-fill me-2"></i>
            Explore Investments
          </RouterLink>
          <RouterLink to="/admin/investments" class="btn btn-outline-primary btn-lg mb-3">
            <i class="bi bi-gear-fill me-2"></i>
            Admin Dashboard
          </RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { RouterLink } from 'vue-router'
import WhatWeOfferCard from '@/components/WhatWeOfferCard.vue'
import CoreValueCard from '@/components/CoreValueCard.vue'

export default {
  name: 'AboutView',
  components: {
    RouterLink,
    WhatWeOfferCard,
    CoreValueCard,
  },
}
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background: #fff;
}

/* Hero Section */
.about-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 5rem 1rem;
  text-align: center;
}

.about-hero h1 {
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.about-hero .lead {
  font-size: 1.25rem;
  opacity: 0.95;
  max-width: 700px;
  margin: 0 auto;
}

/* Section Styles */
section {
  padding: 4rem 1rem;
}

.section-title {
  font-weight: 700;
  color: #2c3e50;
  position: relative;
  display: inline-block;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 2px;
}

.text-center .section-title::after {
  left: 50%;
}

/* Mission Section */
.mission-section {
  background: #fff;
}

.mission-card .card {
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  transition: transform 0.3s ease;
}

.mission-card .card:hover {
  transform: translateY(-5px);
}

/* Features Detail Section */
.features-detail-section {
  background: #f8f9fa;
}

/* Technology Section */
.tech-section {
  background: white;
}

.tech-card {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.tech-card:hover {
  background: white;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.text-purple {
  color: #7952b3;
}

/* Statistics Section */
.stats-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 4rem 1rem;
}

.stat-item {
  padding: 1rem;
}

.stat-item h2 {
  color: white;
}

/* Values Section */
.values-section {
  background: #f8f9fa;
}

/* CTA Section */
.cta-section {
  background: white;
  padding: 5rem 1rem;
}

.cta-buttons .btn {
  min-width: 220px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cta-buttons .btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

/* Responsive Design */
@media (max-width: 768px) {
  .about-hero {
    padding: 3rem 1rem;
  }

  .about-hero h1 {
    font-size: 2rem;
  }

  .about-hero .lead {
    font-size: 1rem;
  }

  section {
    padding: 3rem 1rem;
  }

  .section-title {
    font-size: 1.75rem;
  }

  .feature-box,
  .tech-card {
    margin-bottom: 1rem;
  }

  .cta-buttons {
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .cta-buttons .btn {
    width: 100%;
    max-width: 300px;
    margin: 0.5rem 0 !important;
  }

  .stat-item h2 {
    font-size: 2.5rem;
  }
}

@media (min-width: 1024px) {
  .about-hero {
    padding: 6rem 1rem;
  }
}
</style>
