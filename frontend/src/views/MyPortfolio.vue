<template>
  <div class="investment-list-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="bi bi-briefcase-fill me-2"></i>
        My Portfolio
      </h1>
      <p class="page-subtitle">Track and manage your investment holdings</p>
    </div>

    <div v-if="portfolio.length > 0" class="row mt-4 mb-5">
      <StatisticsCard title="Total Holdings" :value="filteredPortfolio.length" />
      <StatisticsCard title="Portfolio Value" :value="formatCurrency(totalValue)" />
      <StatisticsCard title="Total Investment" :value="formatCurrency(totalCost)" />
    </div>

    <div class="filters-section mb-4">
      <div class="filter-container" style="flex-direction: row; flex-wrap: wrap; align-items: center; gap: 1rem;">
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
              aria-label="Search portfolio"
              @input="applyFilters"
            />
          </div>
        </div>

        <div class="filter-pills" style="margin-left: auto;">
          <div class="filter-pill-group">
            <label class="filter-label" for="type-filter">Type:</label>
            <select
              id="type-filter"
              v-model="selectedType"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
              aria-label="Filter by type"
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
            <label class="filter-label" for="risk-filter">Risk:</label>
            <select
              id="risk-filter"
              v-model="selectedRisk"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
              aria-label="Filter by risk"
            >
              <option value="">All Risks</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          <div class="filter-pill-group">
            <label class="filter-label" for="sort-filter">Sort:</label>
            <select
              id="sort-filter"
              v-model="sortBy"
              class="form-select form-select-sm filter-select"
              @change="applySorting"
              aria-label="Sort portfolio"
            >
              <option value="">Default</option>
              <option value="value-high">Value (High to Low)</option>
              <option value="value-low">Value (Low to High)</option>
              <option value="gain-high">Gain (High to Low)</option>
              <option value="gain-low">Gain (Low to High)</option>
              <option value="name">Name (A–Z)</option>
            </select>
          </div>

          <button class="btn btn-sm btn-outline-secondary clear-btn" @click="clearFilters">
            <i class="bi bi-x-circle me-1"></i>Clear
          </button>
        </div>
      </div>

      <div v-if="filteredPortfolio.length > 0" class="results-count">
        <span class="text-muted">
          Showing <strong>{{ filteredPortfolio.length }}</strong>
          {{ filteredPortfolio.length === 1 ? 'holding' : 'holdings' }}
        </span>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Loading portfolio...</p>
    </div>

    <div v-else-if="error" class="alert alert-danger" role="alert">
      {{ error }}
      <button class="btn btn-sm btn-outline-danger ms-3" @click="loadPortfolio">
        Retry
      </button>
    </div>

    <div v-else-if="filteredPortfolio.length === 0" class="empty-state text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Portfolio Holdings Found</h3>
      <p class="text-muted">
        {{ hasActiveFilters ? 'Try adjusting your filters' : 'You haven\'t made any investments yet.' }}
      </p>
      <button v-if="hasActiveFilters" class="btn btn-primary mt-3" @click="clearFilters">
        Clear Filters
      </button>
      <button v-else class="btn btn-primary mt-3" @click="goToInvestments">
        <i class="bi bi-plus-circle me-2"></i>Start Investing
      </button>
    </div>

    <div v-else class="investment-grid">
      <div v-for="item in filteredPortfolio" :key="item.id" class="investment-card">
        <div class="card-header-section">
          <div class="card-title-area">
            <h5 class="investment-name">{{ item.investmentProductName }}</h5>
            <span class="type-badge badge-mutual_fund">ID: {{ item.investmentProductId }}</span>
          </div>
          <span class="status-badge" :class="item.unitsOwned > 0 ? 'status-active' : 'status-inactive'">
            <i class="bi" :class="item.unitsOwned > 0 ? 'bi-check-circle-fill' : 'bi-x-circle-fill'"></i>
            {{ item.unitsOwned > 0 ? 'Active' : 'Inactive' }}
          </span>
        </div>

        <div class="card-stats">
          <div class="stat-item primary-stat">
            <div class="stat-label">
              <i class="bi bi-currency-dollar"></i> Current Value
            </div>
            <div class="stat-value return-value">
              {{ formatCurrency(getCurrentValue(item)) }}
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-123"></i> Units Owned
            </div>
            <div class="stat-value">{{ formatUnits(item.unitsOwned) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-tag"></i> Avg Purchase Price
            </div>
            <div class="stat-value">{{ formatCurrency(item.avgPurchasePrice) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-graph-up-arrow"></i> Current NAV
            </div>
            <div class="stat-value">{{ formatCurrency(getEffectiveNAV(item)) }}</div>
          </div>
        </div>

        <div class="card-details">
          <div class="detail-item">
            <span class="detail-label">
              <i class="bi bi-cash-stack"></i> Total Investment:
            </span>
            <span class="detail-value">
              {{ formatCurrency(getTotalInvestment(item)) }}
            </span>
          </div>
          <div class="detail-item">
            <span class="detail-label">
              <i class="bi bi-graph-up"></i> Gain/Loss:
            </span>
            <span class="detail-value" :class="getGainClass(item)">
              {{ formatCurrency(getGain(item)) }}
              ({{ formatPercentage(getGainPercentage(item)) }})
              <span class="ms-1">{{ getArrowIcon(item) }}</span>
            </span>
          </div>
        </div>

        <div class="invest-buttons">
          <button class="invest-btn buy-btn" @click="goToBuy(item)">
            <i class="bi bi-plus-circle"></i>
            Buy More
          </button>
          <button
            class="invest-btn sell-btn"
            :disabled="Number(item.unitsOwned) <= 0"
            @click="goToSell(item)"
          >
            <i class="bi bi-dash-circle"></i>
            Sell
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import StatisticsCard from "@/components/StatisticsCard.vue";
import { getPortfolio } from "@/services/api";

const router = useRouter();

const loading = ref(true);
const error = ref("");
const portfolio = ref([]);
const searchQuery = ref("");
const selectedType = ref("");
const selectedRisk = ref("");
const sortBy = ref("");

async function fetchInvestments() {
  try {
    const res = await fetch("http://localhost:8080/api/v1/investments");
    const json = await res.json();
    return Array.isArray(json.data) ? json.data : [];
  } catch (e) {
    console.error("INV API Error:", e);
    return [];
  }
}

async function loadPortfolio() {
  loading.value = true;
  error.value = "";

  try {
    const [portfolioRes, invList] = await Promise.all([
      getPortfolio(),
      fetchInvestments()
    ]);

    const list = portfolioRes?.data?.data || portfolioRes?.data || [];
    if (!Array.isArray(list)) throw new Error("Invalid portfolio");

    const invMap = new Map(
      invList.map(inv => [
        inv.id,
        {
          type: inv.type || "UNKNOWN",
          risk: inv.riskLevel || "UNKNOWN",
          currentNAV: Number(inv.currentNAV) || 0
        }
      ])
    );

    portfolio.value = list.map(p => {
      const inv = invMap.get(p.investmentProductId) || {};

      const units = Number(p.unitsOwned) || 0;
      const avg = Number(p.avgPurchasePrice) || 0;
      const nav = inv.currentNAV > 0 ? inv.currentNAV : avg;

      return {
        id: p.id,
        investmentProductId: p.investmentProductId,
        investmentProductName: p.investmentProductName || `Asset #${p.investmentProductId}`,
        type: inv.type,
        risk: inv.risk,
        unitsOwned: units,
        avgPurchasePrice: avg,
        currentNAV: nav
      };
    });

  } catch (err) {
    error.value = err?.message || "Failed to load portfolio";
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadPortfolio();
  await nextTick();
});

// ✅ Value helpers
function getEffectiveNAV(item) {
  return Number(item.currentNAV) || Number(item.avgPurchasePrice) || 0;
}
function getCurrentValue(item) {
  return item.unitsOwned * getEffectiveNAV(item);
}
function getTotalInvestment(item) {
  return item.unitsOwned * item.avgPurchasePrice;
}
function getGain(item) {
  return getCurrentValue(item) - getTotalInvestment(item);
}
function getGainPercentage(item) {
  const inv = getTotalInvestment(item);
  return inv > 0 ? (getGain(item) / inv) * 100 : 0;
}

// ✅ UI helpers
function getGainClass(item) {
  const g = getGain(item);
  return g > 0 ? "text-success" : g < 0 ? "text-danger" : "text-muted";
}
function getArrowIcon(item) {
  const g = getGain(item);
  return g > 0 ? "▲" : g < 0 ? "▼" : "•";
}
function formatCurrency(v) {
  return new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(Number(v) || 0);
}
function formatUnits(v) {
  return Number(v).toLocaleString("en-IN", { maximumFractionDigits: 2 });
}
function formatPercentage(v) {
  const n = Number(v) || 0;
  return `${n > 0 ? "+" : ""}${n.toFixed(2)}%`;
}

// ✅ Filters + Sorting
const filteredPortfolio = computed(() => {
  let r = [...portfolio.value];

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    r = r.filter(i =>
      i.investmentProductName.toLowerCase().includes(q) ||
      String(i.investmentProductId).includes(q)
    );
  }

  if (selectedType.value)
    r = r.filter(i => i.type === selectedType.value);

  if (selectedRisk.value)
    r = r.filter(i => i.risk === selectedRisk.value);

  if (sortBy.value) {
    r.sort((a, b) => {
      switch (sortBy.value) {
        case "value-high": return getCurrentValue(b) - getCurrentValue(a);
        case "value-low": return getCurrentValue(a) - getCurrentValue(b);
        case "gain-high": return getGain(b) - getGain(a);
        case "gain-low": return getGain(a) - getGain(b);
        case "name": return a.investmentProductName.localeCompare(b.investmentProductName);
      }
    });
  }

  return r;
});

const hasActiveFilters = computed(
  () => searchQuery.value || selectedType.value || selectedRisk.value || sortBy.value
);

// ✅ Summary cards
const totalValue = computed(() =>
  portfolio.value.reduce((s, i) => s + getCurrentValue(i), 0)
);
const totalCost = computed(() =>
  portfolio.value.reduce((s, i) => s + getTotalInvestment(i), 0)
);

// ✅ Navigation
function goToBuy(item) {
  router.push({ name: "BuyInvestment", query: { id: item.investmentProductId } });
}
function goToSell(item) {
  router.push({ name: "SellInvestment", query: { id: item.investmentProductId } });
}
function goToInvestments() {
  router.push("/investments");
}
function clearFilters() {
  searchQuery.value = "";
  selectedType.value = "";
  selectedRisk.value = "";
  sortBy.value = "";
}
</script>

<style scoped>
/* Existing CSS unchanged */
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
.investment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}
.investment-card {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: 0.3s;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid #f0f0f0;
}
.investment-card:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
}
.card-header-section {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.card-title-area {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}
.investment-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.4;
}
.type-badge {
  padding: 0.375rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  white-space: nowrap;
}
.badge-mutual_fund {
  background: #fff3e0;
  color: #e65100;
}
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  font-size: 0.8125rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
  align-self: flex-start;
}
.status-active {
  background: #e8f5e9;
  color: #2e7d32;
}
.status-inactive {
  background: #f5f5f5;
  color: #666;
}
.card-stats {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
  background: #fafafa;
  border-radius: 8px;
}
.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-item.primary-stat {
  padding-bottom: 1rem;
  border-bottom: 1px solid #e0e0e0;
}
.stat-label {
  font-size: 0.8125rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 0.375rem;
}
.stat-value {
  font-size: 1rem;
  font-weight: 600;
  color: #1a1a1a;
}
.return-value {
  color: #00d09c;
  font-size: 1.25rem;
}
.card-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.detail-label {
  font-size: 0.875rem;
  color: #666;
  display: flex;
  align-items: center;
  gap: 0.375rem;
}
.detail-value {
  font-size: 0.875rem;
  font-weight: 600;
  color: #1a1a1a;
}
.invest-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  margin-top: 0.5rem;
}
.invest-btn {
  padding: 0.875rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9375rem;
  cursor: pointer;
  transition: 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}
.invest-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
.invest-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}
.buy-btn {
  background: linear-gradient(135deg, #00d09c 0%, #00b894 100%);
  color: #fff;
}
.sell-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: #fff;
}
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 3rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
/* Filter styles unchanged */
.filters-section {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.filter-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  gap: 1rem;
}
.search-wrapper {
  flex: 1;
}
.search-input {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
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
</style>
