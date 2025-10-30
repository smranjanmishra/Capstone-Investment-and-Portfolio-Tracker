<template>
  <div class="investment-list-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="bi bi-bar-chart-line-fill me-2"></i>
        Investment Products
      </h1>
      <p class="page-subtitle">Explore our range of investment opportunities</p>
    </div>

    <div v-if="filteredInvestments.length > 0" class="row mt-4 mb-5">
      <StatisticsCard title="Total Products" :value="filteredInvestments.length" value-class="text-primary" />
      <StatisticsCard title="Avg. Expected Return" :value="formatPercentage(averageReturn)" value-class="text-success" />
      <StatisticsCard
        title="Min. Investment Range"
        :value="`${formatCurrency(minInvestmentRange)} - ${formatCurrency(maxInvestmentRange)}`"
        value-class="text-info"
      />
    </div>

    <!-- Filters -->
    <div class="filters-section mb-4">
      <div class="filter-container">
        <!-- Search -->
        <div class="search-wrapper">
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

        <!-- Right side filters -->
        <div class="filter-pills">
          <div class="filter-pill-group">
            <label class="filter-label">Type:</label>
            <select v-model="selectedType" class="form-select form-select-sm filter-select" @change="applyFilters">
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
            <select v-model="selectedRisk" class="form-select form-select-sm filter-select" @change="applyFilters">
              <option value="">All Risks</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <div class="filter-pill-group">
            <label class="filter-label">Sort:</label>
            <select v-model="sortBy" class="form-select form-select-sm filter-select" @change="applySorting">
              <option value="">Default</option>
              <option value="return-high">Return (High to Low)</option>
              <option value="return-low">Return (Low to High)</option>
              <option value="name">Name (A–Z)</option>
            </select>
          </div>

          <button class="btn btn-sm btn-outline-secondary clear-btn" @click="clearFilters">
            <i class="bi bi-x-circle me-1"></i>
            Clear
          </button>
        </div>
      </div>

      <div v-if="filteredInvestments.length > 0" class="results-count">
        <span class="text-muted">
          Showing <strong>{{ filteredInvestments.length }}</strong>
          {{ filteredInvestments.length === 1 ? 'product' : 'products' }}
        </span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Loading investment products...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger">
      <strong>Error:</strong> {{ error }}
      <button class="btn btn-sm btn-outline-danger ms-2" @click="loadInvestments">Retry</button>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredInvestments.length === 0" class="empty-state text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Investment Products Found</h3>
      <p class="text-muted">
        {{ hasActiveFilters ? 'Try adjusting your filters' : 'There are no active investment products available at the moment.' }}
      </p>
      <button v-if="hasActiveFilters" class="btn btn-primary mt-3" @click="clearFilters">Clear Filters</button>
    </div>

    <!-- Grid -->
    <div v-else class="investment-grid">
      <InvestmentCard
        v-for="investment in filteredInvestments"
        :key="investment.id"
        :investment="investment"
        @buy="goToBuy"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useInvestmentStore } from '@/stores/investmentStore'
import StatisticsCard from '@/components/StatisticsCard.vue'
import InvestmentCard from '@/components/InvestmentCard.vue'

const router = useRouter()
const route = useRoute()
const investmentStore = useInvestmentStore()

const searchQuery = ref('')
const selectedType = ref(route.query.type || '')
const selectedRisk = ref(route.query.risk || '')
const sortBy = ref('')

const investments = computed(() => investmentStore.activeInvestments)
const loading = computed(() => investmentStore.loading)
const error = computed(() => investmentStore.error)

function applyFilters() {}
function applySorting() {}
function clearFilters() {
  searchQuery.value = ''
  selectedType.value = ''
  selectedRisk.value = ''
  sortBy.value = ''
}

async function loadInvestments() {
  try { 
    await investmentStore.fetchInvestments() 
  } catch {
    // Error already handled by store
  }
}

const filteredInvestments = computed(() => {
  let res = [...investments.value]
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    res = res.filter(inv => inv.name.toLowerCase().includes(q))
  }
  if (selectedType.value) res = res.filter(inv => inv.type === selectedType.value)
  if (selectedRisk.value) res = res.filter(inv => inv.riskLevel === selectedRisk.value)

  switch (sortBy.value) {
    case 'return-high': res.sort((a,b)=>b.expectedReturnRate - a.expectedReturnRate); break
    case 'return-low':  res.sort((a,b)=>a.expectedReturnRate - b.expectedReturnRate); break
    case 'name':        res.sort((a,b)=>a.name.localeCompare(b.name)); break
  }
  return res
})

const hasActiveFilters = computed(() =>
  searchQuery.value || selectedType.value || selectedRisk.value || sortBy.value
)

const averageReturn = computed(() =>
  filteredInvestments.value.length
    ? filteredInvestments.value.reduce((s,i)=>s+Number(i.expectedReturnRate||0),0)/filteredInvestments.value.length
    : 0
)
const minInvestmentRange = computed(() =>
  filteredInvestments.value.length
    ? Math.min(...filteredInvestments.value.map(i => Number(i.minInvestment||0)))
    : 0
)
const maxInvestmentRange = computed(() =>
  filteredInvestments.value.length
    ? Math.max(...filteredInvestments.value.map(i => Number(i.minInvestment||0)))
    : 0
)

function formatCurrency(v) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(Number(v)||0)
}
function formatPercentage(v) {
  return `${Number(v||0).toFixed(2)}%`
}

function goToBuy(product) {
  router.push({ name: 'BuyInvestment', query: { id: product.id } })
}

onMounted(loadInvestments)
</script>

<style scoped>
/* Container & header */
.investment-list-container{padding:2rem 1.5rem;max-width:1400px;margin:0 auto;background:#f8f9fa}
.page-header{margin-bottom:2rem}
.page-title{font-size:2.5rem;font-weight:700;color:#2c3e50;margin-bottom:.5rem}
.page-subtitle{font-size:1.1rem;color:#6c757d;margin-bottom:0}

/* Grid */
.investment-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(350px,1fr));gap:1.5rem;margin-top:1.5rem}

/* Filters */
.filters-section{background:#fff;border-radius:12px;padding:1.5rem;box-shadow:0 2px 8px rgba(0,0,0,.05)}
.filter-container{
  display:flex;
  flex-direction:row;
  flex-wrap:wrap;
  align-items:center;
  gap:1rem;
}
.search-wrapper{flex:1;min-width:260px}
.search-input{border:1px solid #e0e0e0;border-radius:8px;overflow:hidden;background:#fff}
.search-input .input-group-text{border:none;padding:.65rem 1rem}
.search-input .form-control{border:none;padding:.65rem 1rem;font-size:.95rem}
.search-input .form-control:focus{box-shadow:none}

.filter-pills{
  display:flex;
  align-items:center;
  gap:1rem;
  flex-wrap:wrap;
  margin-left:auto; /* pushes filters to the right */
}
.filter-pill-group{display:flex;align-items:center;gap:.5rem}
.filter-label{font-size:.875rem;font-weight:600;color:#666;margin:0;white-space:nowrap}
.filter-select{
  min-width:160px;
  border-radius:8px;
  border:1px solid #e0e0e0;
  padding:.5rem .75rem;
  font-size:.875rem;
  cursor:pointer;
}
.filter-select:focus{border-color:#00d09c;box-shadow:0 0 0 .2rem rgba(0,208,156,.15)}
.clear-btn{border-radius:8px;padding:.5rem 1rem;font-size:.875rem}

/* Info line */
.results-count{margin-top:1rem;padding-top:1rem;border-top:1px solid #f0f0f0;font-size:.9rem}

/* Responsive */
@media (max-width: 992px){
  .filter-select{min-width:140px}
}
@media (max-width: 768px){
  .filter-container{flex-direction:column;align-items:stretch}
  .filter-pills{margin-left:0}
  .filter-pill-group{width:100%}
  .filter-select{width:100%}
}
</style>
