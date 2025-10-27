<template>
  <div class="analytics-container container py-5">
    <!-- Page Header -->
    <div class="text-center mb-5">
      <h1 class="fw-bold display-5 text-dark">
        <i class="bi bi-graph-up-arrow me-2 text-success"></i>
        Portfolio Analytics
      </h1>
      <p class="text-muted">
        Track your portfolio performance, returns, and asset distribution
      </p>
    </div>

    <!-- Summary Metrics -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Loading analytics...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger text-center" role="alert">
      <i class="bi bi-exclamation-triangle me-2"></i>{{ error }}
    </div>

    <div v-else class="row g-4 mb-5">
      <div class="col-md-4 col-lg-2" v-for="metric in summaryMetrics" :key="metric.label">
        <div class="card metric-card shadow-sm border-0 text-center p-3">
          <div class="metric-icon mb-2">
            <i :class="metric.icon"></i>
          </div>
          <h6 class="text-muted mb-1">{{ metric.label }}</h6>
          <h4 :class="metric.class">{{ metric.displayValue }}</h4>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-section row g-4 align-items-stretch">
      <!-- Asset Allocation (Left) -->
      <div class="col-lg-6 d-flex">
        <div class="chart-card flex-fill">
          <AssetAllocationChart :data="allocationData" />
        </div>
      </div>


      <div class="col-lg-6 d-flex">
        <div class="chart-card flex-fill">
          <h5 class="fw-bold mb-3">
            <i class="bi bi-graph-up-arrow me-2 text-success"></i>
            Performance Summary
          </h5>

          <!--Scrollable container -->
          <div class="performance-scroll">
            <PerformanceSummary :data="performanceData" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePortfolioAnalyticsStore } from '@/stores/portfolioAnalyticsStore'
import AssetAllocationChart from '@/components/AssetAllocationChart.vue'
import PerformanceSummary from '@/components/PerformanceSummary.vue'

const analyticsStore = usePortfolioAnalyticsStore()

onMounted(() => analyticsStore.fetchAllAnalytics())

const loading = computed(() => analyticsStore.loading)
const error = computed(() => analyticsStore.error)
const summary = computed(() => analyticsStore.summary || {})
const allocationData = computed(() => analyticsStore.allocation || [])
const performanceData = computed(() => analyticsStore.gains || [])

// Formatters
function formatCurrency(value) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2
  }).format(value || 0)
}

function formatPercentage(value) {
  return `${Number(value || 0).toFixed(2)}%`
}

const summaryMetrics = computed(() => [
  {
    label: 'Total Investment',
    value: summary.value.totalInvested,
    displayValue: formatCurrency(summary.value.totalInvested),
    icon: 'bi bi-cash-coin text-primary fs-2',
    class: 'text-dark'
  },
  {
    label: 'Current Value',
    value: summary.value.currentValue,
    displayValue: formatCurrency(summary.value.currentValue),
    icon: 'bi bi-bar-chart-line-fill text-success fs-2',
    class: 'text-success'
  },
  {
    label: 'Net Gain/Loss',
    value: summary.value.absoluteReturn,
    displayValue: formatCurrency(summary.value.absoluteReturn),
    icon:
      summary.value.absoluteReturn >= 0
        ? 'bi bi-graph-up-arrow text-success fs-2'
        : 'bi bi-graph-down-arrow text-danger fs-2',
    class: summary.value.absoluteReturn >= 0 ? 'text-success' : 'text-danger'

  },
  {
    label: 'ROI',
    value: summary.value.roi,
    displayValue: formatPercentage(summary.value.roi),
    icon: 'bi bi-percent text-info fs-2',
    class: 'text-info'
  },
  {
    label: 'Annualized Return',
    value: summary.value.annualizedReturn,
    displayValue: formatPercentage(summary.value.annualizedReturn),
    icon: 'bi bi-calendar3 text-warning fs-2',
    class: 'text-warning'
  }
])
</script>


<style scoped>
.analytics-container {
  background: #f8f9fa;
  border-radius: 12px;
  min-height: 100vh;

}

/* Metric Cards (Summary) */
.metric-card {
  background: #fff;
  border-radius: 10px;
  transition: all 0.3s ease;

}
.row.g-4.mb-5 {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 1rem;
}
.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.metric-icon i {
  display: inline-block;
  font-size: 2rem;
  opacity: 0.85;
}

/* Charts Section */
.charts-section {
  margin-top: 2rem;
  display: flex;
  flex-wrap: wrap;
}

/* Ensure both columns stretch equally */
.charts-section > .col-lg-6 {
  display: flex;
}

/* Common card wrapper for both charts */
.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 1rem;
}

/* Scrollable area for performance summary */
.performance-scroll {
  overflow-y: auto;
  max-height: 460px;
  scrollbar-width: thin;
  scrollbar-color: #adb5bd #f1f3f5;
}

/* Scrollbar styling (for Chrome, Edge, Safari) */
.performance-scroll::-webkit-scrollbar {
  width: 6px;
}

.performance-scroll::-webkit-scrollbar-thumb {
  background-color: #adb5bd;
  border-radius: 4px;
}

.performance-scroll::-webkit-scrollbar-thumb:hover {
  background-color: #6c757d;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .metric-card h4 {
    font-size: 1rem;
  }

  .charts-section {
    flex-direction: column;
  }

  .chart-card {
    max-height: none;
  }

  .performance-scroll {
    max-height: 400px;
  }
}
</style>

