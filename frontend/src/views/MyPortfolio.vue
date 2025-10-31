<template>
  <div class="investment-list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="page-title"><i class="bi bi-briefcase-fill me-2"></i> My Portfolio</h1>
      <p class="page-subtitle">Track and manage your investment holdings</p>
    </div>

    <!-- Summary Cards -->
    <div v-if="portfolio.length > 0" class="row mt-4 mb-5">
      <StatisticsCard title="Total Holdings" :value="filteredPortfolio.length" />
      <StatisticsCard title="Portfolio Value" :value="formatCurrency(totalValue)" />
      <StatisticsCard title="Total Investment" :value="formatCurrency(totalCost)" />
    </div>

    <!-- Filters Section -->
    <div class="filters-section mb-4">
      <div class="filter-container">

        <!-- Search -->
        <div class="search-wrapper">
          <div class="input-group search-input">
            <span class="input-group-text bg-white border-end-0">
              <i class="bi bi-search text-muted"></i>
            </span>
            <input v-model="searchQuery" type="text" class="form-control border-start-0 shadow-none"
              placeholder="Search investments..." />
          </div>
        </div>

        <!-- Right Filters -->
        <div class="filter-pills">

          <!-- Type -->
          <div class="filter-pill-group">
            <label class="filter-label">Type:</label>
            <select v-model="selectedType" class="form-select form-select-sm filter-select">
              <option value="">All Types</option>
              <option v-for="type in availableTypes" :key="type.value" :value="type.value">
                {{ type.label }}
              </option>
            </select>
          </div>

          <!-- Risk -->
          <div class="filter-pill-group">
            <label class="filter-label">Risk:</label>
            <select v-model="selectedRisk" class="form-select form-select-sm filter-select">
              <option value="">All Risks</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <!-- Sort -->
          <div class="filter-pill-group">
            <label class="filter-label">Sort:</label>
            <select v-model="sortBy" class="form-select form-select-sm filter-select">
              <option value="">Default</option>
              <option value="value-high">Value (High to Low)</option>
              <option value="value-low">Value (Low to High)</option>
              <option value="gain-high">Gain (High to Low)</option>
              <option value="gain-low">Gain (Low to High)</option>
              <option value="name">Name (A–Z)</option>
            </select>
          </div>

          <!-- Clear -->
          <button class="btn btn-sm btn-outline-secondary clear-btn" @click="clearFilters">
            <i class="bi bi-x-circle me-1"></i>Clear
          </button>

        </div>
      </div>

      <!-- Count -->
      <div v-if="filteredPortfolio.length > 0" class="results-count">
        <span class="text-muted">
          Showing <strong>{{ filteredPortfolio.length }}</strong> holdings
        </span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="mt-3 text-muted">Loading portfolio...</p>
    </div>

    <!-- Error -->
    <div v-else-if="error" class="alert alert-danger text-center">
      {{ error }}
      <button class="btn btn-sm btn-outline-danger ms-2" @click="loadPortfolio">Retry</button>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredPortfolio.length === 0" class="empty-state text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Portfolio Holdings Found</h3>
      <button class="btn btn-primary mt-3" @click="goToInvestments">Start Investing</button>
    </div>

    <!-- Portfolio Grid -->
    <div v-else class="investment-grid">
      <div v-for="item in filteredPortfolio" :key="item.id" class="investment-card">

        <!-- Header -->
        <div class="card-header-section">
          <div class="card-title-area">
            <h5 class="investment-name">{{ item.investmentProductName }}</h5>
            <span class="type-badge" :class="typeClass(item.type)">
              {{ item.typeDisplay }}
            </span>
          </div>

          <span class="status-badge" :class="item.isActive ? 'status-active':'status-inactive'">
            <i class="bi" :class="item.isActive ? 'bi-check-circle-fill':'bi-x-circle-fill'"></i>
            {{ item.isActive ? 'Active':'Inactive' }}
          </span>
        </div>

        <!-- Stats Section -->
        <div class="card-stats">
          <div class="stat-item primary-stat">
            <span class="stat-label"><i class="bi bi-wallet2"></i> Current Value</span>
            <span class="stat-value return-value">{{ formatCurrency(getCurrentValue(item)) }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label"><i class="bi bi-123"></i> Units Owned</span>
            <span class="stat-value">{{ formatUnits(item.unitsOwned) }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label"><i class="bi bi-tag"></i> Avg Purchase Price</span>
            <span class="stat-value">{{ formatCurrency(item.avgPurchasePrice) }}</span>
          </div>

          <div class="stat-item">
            <span class="stat-label"><i class="bi bi-graph-up-arrow"></i> Current NAV</span>
            <span class="stat-value">{{ formatCurrency(item.currentNAV) }}</span>
          </div>
        </div>

        <!-- Risk Section (Matches InvestmentCard) -->
        <div class="card-details">
          <span class="detail-label"><i class="bi bi-exclamation-triangle"></i> Risk Level:</span>
          <span class="risk-badge" :class="getRiskBadgeClass(item.risk)">
            {{ item.risk }}
          </span>
        </div>

        <!-- Buttons -->
        <div class="invest-buttons">
          <button class="invest-btn buy-btn"
            :disabled="!item.isActive"
            @click="goToBuy(item)">
            <i class="bi bi-plus-circle"></i> Buy More
          </button>

          <button class="invest-btn sell-btn"
            :disabled="item.unitsOwned <= 0"
            @click="goToSell(item)">
            <i class="bi bi-dash-circle"></i> Sell
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import StatisticsCard from "@/components/StatisticsCard.vue";
import { getPortfolio, getInvestments } from "@/services/api";

const router = useRouter();
const loading = ref(true);
const error = ref("");
const portfolio = ref([]);

const searchQuery = ref("");
const selectedType = ref("");
const selectedRisk = ref("");
const sortBy = ref("");

async function loadPortfolio() {
  try {
    const [portfolioRes, invRes] = await Promise.all([getPortfolio(), getInvestments()]);
    const list = portfolioRes?.data?.data || [];

    const map = new Map(
      (invRes?.data?.data || []).map(inv => [
        inv.id,
        {
          type: inv.type,
          typeDisplay: inv.typeDisplayName,
          risk: inv.riskLevel,
          isActive: inv.isActive,
          nav: Number(inv.currentNAV) || 0
        }
      ])
    );

    portfolio.value = list.map(p => {
      const i = map.get(p.investmentProductId);
      return {
        ...p,
        type: i?.type || "",
        typeDisplay: i?.typeDisplay || "Unknown",
        risk: i?.risk || "",
        isActive: i?.isActive === true,
        unitsOwned: Number(p.unitsOwned),
        avgPurchasePrice: Number(p.avgPurchasePrice),
        currentNAV: i?.nav || Number(p.avgPurchasePrice)
      };
    });

  } catch {
    error.value = "Failed to load portfolio";
  } finally {
    loading.value = false;
  }
}
onMounted(loadPortfolio);

const getCurrentValue = i => i.unitsOwned * i.currentNAV;

function sortPortfolio(r) {
  switch (sortBy.value) {
    case "value-high": return r.sort((a,b)=>getCurrentValue(b)-getCurrentValue(a));
    case "value-low": return r.sort((a,b)=>getCurrentValue(a)-getCurrentValue(b));
    case "gain-high": return r.sort((a,b)=> (getCurrentValue(b)-b.unitsOwned*b.avgPurchasePrice) - (getCurrentValue(a)-a.unitsOwned*a.avgPurchasePrice));
    case "gain-low": return r.sort((a,b)=> (getCurrentValue(a)-a.unitsOwned*a.avgPurchasePrice) - (getCurrentValue(b)-b.unitsOwned*b.avgPurchasePrice));
    case "name": return r.sort((a,b)=>a.investmentProductName.localeCompare(b.investmentProductName));
  }
  return r;
}

const filteredPortfolio = computed(() => {
  let r = portfolio.value.filter(i =>
    i.unitsOwned > 0 &&
    (!searchQuery.value || i.investmentProductName.toLowerCase().includes(searchQuery.value.toLowerCase())) &&
    (!selectedType.value || i.type === selectedType.value) &&
    (!selectedRisk.value || i.risk === selectedRisk.value)
  );
  return sortPortfolio(r);
});

const totalValue = computed(() =>
  portfolio.value.filter(i => i.unitsOwned > 0).reduce((s,i)=>s+getCurrentValue(i),0)
);
const totalCost = computed(() =>
  portfolio.value.filter(i => i.unitsOwned > 0).reduce((s,i)=>s+i.unitsOwned*i.avgPurchasePrice,0)
);

// Dynamic type options based on actual portfolio
const availableTypes = computed(() => {
  const types = new Set(
    portfolio.value
      .filter(i => i.unitsOwned > 0)
      .map(i => i.type)
      .filter(Boolean)
  );

  const typeMap = {
    STOCK: "Stocks",
    MUTUAL_FUND: "Mutual Funds",
    BOND: "Bonds",
    ETF: "ETFs",
    REAL_ESTATE: "Real Estate",
    COMMODITY: "Commodities",
    CRYPTOCURRENCY: "Cryptocurrency"
  };

  return Array.from(types)
    .map(type => ({ value: type, label: typeMap[type] || type }))
    .sort((a, b) => a.label.localeCompare(b.label));
});

function goToBuy(item) {
  if (!item.isActive) return;
  router.push({ name:"BuyInvestment", query:{ id:item.investmentProductId }});
}
function goToSell(item) {
  if (item.unitsOwned <=0) return;
  router.push({ name:"SellInvestment", query:{ id:item.investmentProductId }});
}
function goToInvestments() {
  router.push("/investments");
}
function clearFilters() {
  searchQuery.value = selectedType.value = selectedRisk.value = sortBy.value = "";
}

const formatCurrency = v => new Intl.NumberFormat("en-IN",{style:"currency",currency:"INR"}).format(v||0);
const formatUnits = v => Number(v).toLocaleString("en-IN");

const getRiskBadgeClass = r => ({
  LOW:"bg-success text-white",
  MEDIUM:"bg-warning text-dark",
  HIGH:"bg-danger text-white"
}[String(r).toUpperCase()] || "bg-secondary text-white");

const typeClass = t => ({
  STOCK:"badge-stock",
  MUTUAL_FUND:"badge-mutual_fund",
  BOND:"badge-bond",
  ETF:"badge-etf",
  REAL_ESTATE:"badge-real_estate",
  COMMODITY:"badge-commodity",
  CRYPTOCURRENCY:"badge-cryptocurrency"
}[t] || "badge-secondary");
</script>


<style scoped>
/* SAME UI THEME AS INVESTMENT PRODUCTS */
.investment-list-container{padding:2rem 1.5rem;max-width:1400px;margin:0 auto;background:#f8f9fa}
.page-header{margin-bottom:2rem}
.page-title{font-size:2.5rem;font-weight:700;color:#2c3e50;margin-bottom:.5rem}
.page-subtitle{font-size:1.1rem;color:#6c757d;margin-bottom:0}

.investment-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(350px,1fr));gap:1.5rem;margin-top:1.5rem}

.investment-card{background:#fff;border-radius:12px;padding:1.5rem;box-shadow:0 2px 8px rgba(0,0,0,.08);
transition:.3s;display:flex;flex-direction:column;gap:1rem;border:1px solid #f0f0f0}
.investment-card:hover{box-shadow:0 6px 20px rgba(0,0,0,.12);transform:translateY(-4px)}

.card-header-section{display:flex;flex-direction:column;gap:.75rem}
.card-title-area{display:flex;justify-content:space-between;align-items:flex-start;gap:1rem}
.investment-name{font-size:1.125rem;font-weight:600;color:#1a1a1a;margin:0;line-height:1.4}

.type-badge{padding:.35rem .75rem;border-radius:20px;font-size:.75rem;font-weight:600;white-space:nowrap}
.badge-stock{background:#e3f2fd;color:#1976d2}
.badge-bond{background:#f3e5f5;color:#7b1fa2}
.badge-mutual_fund{background:#fff3e0;color:#e65100}
.badge-etf{background:#e8f5e9;color:#2e7d32}
.badge-real_estate{background:#fce4ec;color:#c2185b}
.badge-commodity{background:#fff8e1;color:#f57f17}
.badge-cryptocurrency{background:#e0f2f1;color:#00695c}

.status-badge{display:inline-flex;align-items:center;gap:.375rem;font-size:.8125rem;font-weight:600;padding:.25rem .5rem;border-radius:6px;align-self:flex-start}
.status-active{background:#e8f5e9;color:#2e7d32}
.status-inactive{background:#f5f5f5;color:#666}

.card-stats{display:flex;flex-direction:column;gap:1rem;padding:1rem;background:#fafafa;border-radius:8px}
.stat-item{display:flex;justify-content:space-between;align-items:center}
.stat-item.primary-stat{padding-bottom:1rem;border-bottom:1px solid #e0e0e0}

.stat-label{font-size:.8125rem;color:#666;display:flex;align-items:center;gap:.375rem}
.stat-value{font-size:1rem;font-weight:600;color:#1a1a1a}
.return-value{color:#00d09c;font-size:1.25rem}

.card-details{display:flex;justify-content:space-between;align-items:center;background:#fafafa;
padding:.8rem 1rem;border-radius:8px;border:1px solid #e0e0e0}
.detail-label{font-size:.875rem;color:#666;display:flex;align-items:center;gap:.375rem}
.risk-badge{padding:.25rem .75rem;border-radius:6px;font-size:.75rem;font-weight:600}

.invest-buttons{display:grid;grid-template-columns:1fr 1fr;gap:.75rem;margin-top:.5rem}
.invest-btn{padding:.875rem;border:none;border-radius:8px;font-weight:600;font-size:.9375rem;cursor:pointer;
transition:.3s;display:flex;align-items:center;justify-content:center;gap:.5rem}
.invest-btn:hover:not(:disabled){transform:translateY(-2px);box-shadow:0 4px 12px rgba(0,0,0,.2)}
.invest-btn:disabled{background:#e0e0e0;color:#999;cursor:not-allowed}
.buy-btn{background:linear-gradient(135deg,#00d09c 0%,#00b894 100%);color:#fff}
.sell-btn{background:linear-gradient(135deg,#ff6b6b 0%,#ee5a52 100%);color:#fff}

.filters-section{background:#fff;border-radius:12px;padding:1.5rem;box-shadow:0 2px 8px rgba(0,0,0,.05)}
.filter-container{display:flex;flex-wrap:wrap;align-items:center;gap:1rem}
.search-wrapper{flex:1;min-width:260px}
.search-input{border:1px solid #e0e0e0;border-radius:8px;overflow:hidden;background:#fff}

.filter-pills{display:flex;align-items:center;gap:1rem;flex-wrap:wrap;margin-left:auto}
.filter-pill-group{display:flex;align-items:center;gap:.5rem}
.filter-label{font-size:.875rem;font-weight:600;color:#666}
.filter-select{min-width:160px;border-radius:8px;border:1px solid #e0e0e0;padding:.5rem .75rem;font-size:.875rem}
.clear-btn{border-radius:8px;padding:.5rem 1rem;font-size:.875rem}

.results-count{margin-top:1rem;padding-top:1rem;border-top:1px solid #f0f0f0;font-size:.9rem}

@media(max-width:768px){
  .investment-grid{grid-template-columns:1fr}
  .filter-container{flex-direction:column;align-items:stretch}
  .filter-pills{margin-left:0;flex-direction:column;align-items:stretch}
  .filter-select{width:100%}
}
</style>
