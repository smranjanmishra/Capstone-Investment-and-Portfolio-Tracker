<template>
  <div class="investment-list-container">
    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-title">
        <i class="bi bi-bar-chart-line-fill me-2"></i>
        Investment Products
      </h1>
      <p class="page-subtitle">Explore our range of investment opportunities</p>
    </div>
 
    <!-- Summary Statistics -->
    <div v-if="filteredInvestments.length > 0" class="row mt-4 mb-5">
      <StatisticsCard
        title="Total Products"
        :value="filteredInvestments.length"
        value-class="text-primary"
      />
      <StatisticsCard
        title="Avg. Expected Return"
        :value="formatPercentage(averageReturn)"
        value-class="text-success"
      />
      <StatisticsCard
        title="Min. Investment Range"
        :value="`${formatCurrency(minInvestmentRange)} - ${formatCurrency(maxInvestmentRange)}`"
        value-class="text-info"
      />
    </div>
 
    <div class="filters-section mb-4">
      <div
        class="filter-container"
        style="flex-direction: row; flex-wrap: wrap; align-items: center; gap: 1rem;"
      >
        <!-- Search -->
        <div class="search-wrapper" style="flex: 1;">
          <div class="input-group search-input">
            <span class="input-group-text bg-white border-end-0">
              <i class="bi bi-search text-muted"></i>
            </span>
            <input
              v-model="searchQuery"
              type="text"
              class="form-control border-start-0 shadow-none"
              placeholder="Search investments..."
              @input="applyFilters"
            />
          </div>
        </div>
 
        <div class="filter-pills" style="margin-left: auto;">
          <div class="filter-pill-group">
            <label class="filter-label">Type:</label>
            <select
              v-model="selectedType"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
            >
              <option value="">All Types</option>
              <option value="STOCK">Stocks</option>
              <option value="MUTUAL_FUND">Mutual Funds</option>
              <option value="BOND">Bonds</option>
              <option value="ETF">ETFs</option>
              <option value="REAL_ESTATE">Real Estate</option>
              <option value="COMMODITY">Commodities</option>
              <option value="CRYPTOCURRENCY">Cryptocurrency</option>
            </select>
          </div>
 
          <div class="filter-pill-group">
            <label class="filter-label">Risk:</label>
            <select
              v-model="selectedRisk"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
            >
              <option value="">All Risks</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
 
          <div class="filter-pill-group">
            <label class="filter-label">Sort:</label>
            <select
              v-model="sortBy"
              class="form-select form-select-sm filter-select"
              @change="applySorting"
            >
              <option value="">Default</option>
              <option value="return-high">Return (High to Low)</option>
              <option value="return-low">Return (Low to High)</option>
              <option value="name">Name (A-Z)</option>
            </select>
          </div>
 
          <button
            class="btn btn-sm btn-outline-secondary clear-btn"
            @click="clearFilters"
          >
            <i class="bi bi-x-circle me-1"></i>Clear
          </button>
        </div>
      </div>
 
      <!-- Results Count -->
      <div v-if="filteredInvestments.length > 0" class="results-count">
        <span class="text-muted">
          Showing <strong>{{ filteredInvestments.length }}</strong>
          {{ filteredInvestments.length === 1 ? 'product' : 'products' }}
        </span>
      </div>
    </div>
 
    <!-- Loading State -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">Loading investment products...</p>
    </div>
 
    <!-- Error State -->
    <div v-else-if="error" class="alert alert-danger" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-2"></i>
      <strong>Error:</strong> {{ error }}
      <button class="btn btn-sm btn-outline-danger ms-3" @click="loadInvestments">
        <i class="bi bi-arrow-clockwise"></i> Retry
      </button>
    </div>
 
    <!-- Empty State -->
    <div
      v-else-if="filteredInvestments.length === 0"
      class="empty-state text-center py-5"
    >
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Investment Products Found</h3>
      <p class="text-muted">
        {{ searchQuery || selectedType || selectedRisk
          ? 'Try adjusting your filters'
          : 'There are no active investment products available at the moment.'
        }}
      </p>
      <button
        v-if="hasActiveFilters"
        class="btn btn-primary mt-3"
        @click="clearFilters"
      >
        Clear Filters
      </button>
    </div>
 
    <div v-else class="investment-grid">
      <InvestmentCard
        v-for="investment in filteredInvestments"
        :key="investment.id"
        :investment="investment"
      />
    </div>
  </div>
</template>
 
<script>
import { ref, computed, onMounted } from 'vue'
import { useInvestmentStore } from '@/stores/investmentStore'
import { useRoute } from 'vue-router'
import StatisticsCard from '@/components/StatisticsCard.vue'
import InvestmentCard from '@/components/InvestmentCard.vue'
 
export default {
  name: 'InvestmentList',
 
  components: {
    StatisticsCard,
    InvestmentCard
  },
 
  setup() {
    const investmentStore = useInvestmentStore()
    const route = useRoute()
 
    const searchQuery = ref('')
    const selectedType = ref(route.query.type || '')
    const selectedRisk = ref(route.query.risk || '')
    const sortBy = ref('')
 
    const investments = computed(() => investmentStore.activeInvestments)
    const loading = computed(() => investmentStore.loading)
    const error = computed(() => investmentStore.error)
 
    const filteredInvestments = computed(() => {
      let result = [...investments.value]
 
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter((inv) =>
          inv.name.toLowerCase().includes(query)
        )
      }
 
      if (selectedType.value) {
        result = result.filter((inv) => inv.type === selectedType.value)
      }
 
      if (selectedRisk.value) {
        result = result.filter((inv) => inv.riskLevel === selectedRisk.value)
      }
 
      if (sortBy.value) {
        switch (sortBy.value) {
          case 'return-high':
            result.sort((a, b) => b.expectedReturnRate - a.expectedReturnRate)
            break
          case 'return-low':
            result.sort((a, b) => a.expectedReturnRate - b.expectedReturnRate)
            break
          case 'name':
            result.sort((a, b) => a.name.localeCompare(b.name))
            break
        }
      }
 
      return result
    })
 
    // Check if any filters are active
    const hasActiveFilters = computed(() => {
      return searchQuery.value || selectedType.value || selectedRisk.value || sortBy.value
    })
 
    const averageReturn = computed(() => {
      if (filteredInvestments.value.length === 0) return 0
      const total = filteredInvestments.value.reduce(
        (sum, inv) => sum + inv.expectedReturnRate,
        0
      )
      return total / filteredInvestments.value.length
    })
 
    const minInvestmentRange = computed(() => {
      if (filteredInvestments.value.length === 0) return 0
      return Math.min(
        ...filteredInvestments.value.map((inv) => inv.minInvestment)
      )
    })
 
    const maxInvestmentRange = computed(() => {
      if (filteredInvestments.value.length === 0) return 0
      return Math.max(
        ...filteredInvestments.value.map((inv) => inv.minInvestment)
      )
    })
 
    async function loadInvestments() {
      try {
        await investmentStore.fetchInvestments()
      } catch (err) {
        console.error('Failed to load investments:', err)
      }
    }
 
    // Apply filters (triggered on input/change)
    function applyFilters() {
      // Filters are automatically applied via computed property
      // This method can be used for additional logic if needed
    }
 
    // Clear all filters
    function clearFilters() {
      searchQuery.value = ''
      selectedType.value = ''
      selectedRisk.value = ''
      sortBy.value = ''
    }
 
    // Apply sorting (triggered on change)
    function applySorting() {
      // Sorting is handled in computed property
    }
 
    // Format currency value
    // Backend sends BigDecimal as number
    // @param {number} value - Currency value
    // @returns {string} - Formatted currency in INR
    function formatCurrency(value) {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(value)
    }
 
    // Format percentage value
    // @param {number} value - Percentage value
    // @returns {string} - Formatted percentage
    function formatPercentage(value) {
      return `${value.toFixed(2)}%`
    }
 
    // Lifecycle Hooks
    onMounted(() => {
      loadInvestments()
    })
 
    return {
      searchQuery,
      selectedType,
      selectedRisk,
      sortBy,
      loading,
      error,
      filteredInvestments,
      hasActiveFilters,
      averageReturn,
      minInvestmentRange,
      maxInvestmentRange,
      loadInvestments,
      applyFilters,
      clearFilters,
      applySorting,
      formatCurrency,
      formatPercentage,
    }
  },
}
</script>
 
<style scoped>
.investment-list-container {
  padding: 2rem 1.5rem;
  max-width: 1400px;
  margin: 0 auto;
  background: #f8f9fa;
}
 
.page-header {
  margin-bottom: 2rem;
}
 
.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}
 
.page-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
  margin-bottom: 0;
}
 
/* Investment Grid */
.investment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}
 
/* Filter Section */
.filters-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
 
.filter-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
 
.search-wrapper {
  flex: 1;
}
 
.search-input {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}
 
.search-input .input-group-text {
  border: none;
  padding: 0.75rem 1rem;
}
 
.search-input .form-control {
  border: none;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
}
 
.search-input .form-control:focus {
  box-shadow: none;
}
 
.filter-pills {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
}
 
.filter-pill-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
 
.filter-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #666;
  margin: 0;
}
 
.filter-select {
  min-width: 150px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  padding: 0.5rem 0.75rem;
  font-size: 0.875rem;
  cursor: pointer;
}
 
.filter-select:focus {
  border-color: #00d09c;
  box-shadow: 0 0 0 0.2rem rgba(0, 208, 156, 0.15);
}
 
.clear-btn {
  border-radius: 8px;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}
 
.results-count {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f0f0f0;
  font-size: 0.9rem;
}
 
/* Responsive Design */
@media (max-width: 768px) {
  .investment-grid {
    grid-template-columns: 1fr;
  }
 
  .filter-pills {
    flex-direction: column;
    align-items: stretch;
  }
 
  .filter-pill-group {
    flex-direction: column;
    align-items: stretch;
  }
 
  .filter-select {
    width: 100%;
  }
}
 
.investment-row {
  transition: all 0.3s ease;
  cursor: pointer;
}
 
.investment-row:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
 
.table-dark {
  background-color: #2c3e50;
}
 
.empty-state i {
  font-size: 4rem;
}
 
/* Responsive Design */
@media (max-width: 768px) {
  .investment-list-container {
    padding: 1.5rem 1rem;
  }
 
  .page-title {
    font-size: 1.75rem;
  }
 
  .page-subtitle {
    font-size: 1rem;
  }
 
  .table-responsive {
    font-size: 0.85rem;
  }
 
  .filters-section .row {
    gap: 0.75rem;
  }
}
</style>
 
 