<template>
  <div class="transaction-history-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="page-title">
        <i class="bi bi-clock-history me-2"></i>
        Transaction History
      </h1>
      <p class="page-subtitle">Track all your buy and sell activities</p>
    </div>

    <!-- Alerts -->
    <transition name="fade">
      <div v-if="alert.show" :class="['alert', `alert-${alert.type}`]">
        {{ alert.message }}
      </div>
    </transition>

    <!-- Filters -->
    <div class="filters-section mb-4">
      <div class="filter-container">
        <div class="search-wrapper">
          <div class="input-group search-input">
            <span class="input-group-text bg-white border-end-0">
              <i class="bi bi-search text-muted"></i>
            </span>
            <input
              v-model="searchQuery"
              type="text"
              class="form-control border-start-0 shadow-none"
              placeholder="Search by product or ID..."
              aria-label="Search transactions"
              @input="applyFilters"
            />
          </div>
        </div>

        <div class="filter-pills">
          <div class="filter-pill-group">
            <label class="filter-label" for="type-filter">Type</label>
            <select
              id="type-filter"
              v-model="filterType"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
              aria-label="Filter by transaction type"
            >
              <option value="">All Transactions</option>
              <option value="BUY">Buy</option>
              <option value="SELL">Sell</option>
            </select>
          </div>

          <div class="filter-pill-group">
            <label class="filter-label" for="from-date">From Date</label>
            <input
              id="from-date"
              v-model="fromDate"
              type="date"
              class="form-select form-select-sm filter-select"
              :max="toDate || today"
              aria-label="Filter by start date"
              @change="applyFilters"
            />
          </div>

          <div class="filter-pill-group">
            <label class="filter-label" for="to-date">To Date</label>
            <input
              id="to-date"
              v-model="toDate"
              type="date"
              class="form-select form-select-sm filter-select"
              :min="fromDate"
              :max="today"
              aria-label="Filter by end date"
              @change="applyFilters"
            />
          </div>

          <div class="filter-pill-group">
            <label class="filter-label" for="sort-filter">Sort</label>
            <select
              id="sort-filter"
              v-model="sortBy"
              class="form-select form-select-sm filter-select"
              @change="applyFilters"
              aria-label="Sort transactions"
            >
              <option value="">Default</option>
              <option value="date-desc">Date (Newest)</option>
              <option value="date-asc">Date (Oldest)</option>
              <option value="amount-desc">Total Value (High to Low)</option>
              <option value="amount-asc">Total Value (Low to High)</option>
            </select>
          </div>

          <button class="btn btn-sm btn-outline-secondary clear-btn" @click="resetFilters">
            <i class="bi bi-x-circle me-1"></i>Clear
          </button>
        </div>
      </div>

      <div v-if="filteredTxns.length > 0" class="results-count">
        <span class="text-muted">
          Showing <strong>{{ filteredTxns.length }}</strong>
          {{ filteredTxns.length === 1 ? 'transaction' : 'transactions' }}
        </span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="empty-state text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p class="mt-3 text-muted">Loading transactions...</p>
    </div>

    <!-- Error -->
    <div v-else-if="alert.show && alert.type === 'error'" class="alert alert-danger" role="alert">
      {{ alert.message }}
      <button class="btn btn-sm btn-outline-danger ms-3" @click="retryLoad">Retry</button>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredTxns.length === 0" class="empty-state text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Transactions Found</h3>
      <p class="text-muted">
        {{ hasActiveFilters ? 'Try adjusting your filters' : 'You haven\'t made any transactions yet.' }}
      </p>
      <button v-if="hasActiveFilters" class="btn btn-primary mt-3" @click="resetFilters">
        Clear Filters
      </button>
    </div>

    <!-- Transaction Grid -->
    <div v-else class="transaction-grid">
      <div
        v-for="txn in filteredTxns"
        :key="txn.id"
        class="transaction-card"
        :class="txn.txnType === 'BUY' ? 'buy' : 'sell'"
      >
        <div class="card-header-section">
          <div class="card-title-area">
            <h5 class="transaction-name">{{ txn.investmentName }}</h5>
            <span class="type-badge">ID: {{ txn.investmentProductId }}</span>
          </div>
          <span class="status-badge" :class="txn.txnType === 'BUY' ? 'status-buy' : 'status-sell'">
            <i class="bi" :class="txn.txnType === 'BUY' ? 'bi-arrow-up-circle-fill' : 'bi-arrow-down-circle-fill'"></i>
            {{ txn.txnType }}
          </span>
        </div>

        <div class="card-stats">
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-calendar"></i> Date
            </div>
            <div class="stat-value">{{ formatDate(txn.txnDate) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-123"></i> Units
            </div>
            <div class="stat-value">{{ formatNumber(txn.units) }}</div>
          </div>
          <div class="stat-item">
            <div class="stat-label">
              <i class="bi bi-currency-rupee"></i> NAV @ Txn
            </div>
            <div class="stat-value">{{ formatCurrency(txn.navAtTxn) }}</div>
          </div>
          <div class="stat-item primary-stat">
            <div class="stat-label">
              <i class="bi bi-wallet2"></i> Total Value
            </div>
            <div class="stat-value return-value">{{ formatCurrency(txn.units * txn.navAtTxn) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { getTransactions } from "@/services/api";

const transactions = ref([]);
const loading = ref(true);
const alert = ref({ show: false, message: "", type: "" });
const searchQuery = ref("");
const filterType = ref("");
const fromDate = ref("");
const toDate = ref("");
const sortBy = ref("");
const today = new Date().toISOString().split("T")[0];
const retryCount = ref(0);
const maxRetries = 3;
const useMockData = ref(true); // Enabled by default to show real names

// Mock data for fallback
const mockData = [
  { id: 4, name: "ICICI Prudential Bluechip Fund" },
  { id: 5, name: "SBI Bluechip Fund" },
  { id: 8, name: "HDFC Mid-Cap Opportunities Fund" },
  { id: 1, name: "HDFC Top 100 Fund" },
  { id: 2, name: "SBI Small Cap Fund" },
  { id: 3, name: "Reliance Growth Fund" },
];

// Show alert with timeout
const showAlert = (msg, type = "info") => {
  alert.value = { show: true, message: msg, type };
  setTimeout(() => (alert.value.show = false), 3000);
};

// Load transactions
async function loadTransactions(retry = false) {
  loading.value = !retry; // Only show loading on initial attempt
  try {
    console.log(`🔄 Loading transactions (attempt ${retryCount.value + 1}/${maxRetries})`);
    const res = await getTransactions();
    console.log("Transactions Response:", res);

    const list = Array.isArray(res.data.data) ? res.data.data : Array.isArray(res.data) ? res.data : [];
    transactions.value = list.map(normalizeTx).filter((tx) => tx.id);
  } catch (e) {
    retryCount.value++;
    console.error(`❌ Failed to load transactions (attempt ${retryCount.value}/${maxRetries}):`, {
      message: e.message,
      stack: e.stack,
      response: e.response ? { status: e.response.status, data: e.response.data } : null,
    });

    const errorMsg = `Failed to load transactions: ${e.message}. ${retryCount.value < maxRetries ? 'Retrying...' : 'Using fallbacks.'}`;
    showAlert(errorMsg, "error");
    if (retryCount.value < maxRetries && !retry) {
      const delay = Math.pow(2, retryCount.value) * 2000; // 2s, 4s, 8s
      setTimeout(() => loadTransactions(true), delay);
    }
  } finally {
    if (!retry) loading.value = false;
  }
}

// Normalize transaction data
function normalizeTx(t) {
  const productId = t.investmentProductId || "N/A";
  let investmentName = t.investmentProductName || null;

  if (!investmentName && useMockData.value) {
    const mockEntry = mockData.find((m) => m.id === productId);
    investmentName = mockEntry ? mockEntry.name : `Investment #${productId}`;
  } else if (!investmentName) {
    investmentName = `Investment #${productId}`;
  }

  return {
    id: t.id || `temp-${Math.random().toString(36).slice(2)}`,
    txnDate: t.txnDate && !isNaN(new Date(t.txnDate)) ? new Date(t.txnDate) : null,
    txnType: t.txnType?.toUpperCase() || "UNKNOWN",
    investmentProductId: productId,
    investmentName,
    units: parseFloat(t.units || 0),
    navAtTxn: parseFloat(t.navAtTxn || 0),
  };
}

// Retry loading
async function retryLoad() {
  retryCount.value = 0;
  alert.value = { show: false, message: "", type: "" };
  await loadTransactions();
}

// Apply filters and validate dates
function applyFilters() {
  if (fromDate.value && toDate.value && fromDate.value > toDate.value) {
    [fromDate.value, toDate.value] = [toDate.value, fromDate.value];
  }
}

// Reset filters
function resetFilters() {
  searchQuery.value = "";
  filterType.value = "";
  fromDate.value = "";
  toDate.value = "";
  sortBy.value = "";
}

// Filtered and sorted transactions
const filteredTxns = computed(() => {
  let result = [...transactions.value];

  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase().trim();
    result = result.filter(
      (t) =>
        t.investmentName?.toLowerCase().includes(q) ||
        t.investmentProductId?.toString().includes(q) ||
        t.id.toString().includes(q)
    );
  }

  if (filterType.value) {
    result = result.filter((t) => t.txnType === filterType.value);
  }

  if (fromDate.value || toDate.value) {
    const from = fromDate.value ? new Date(fromDate.value) : new Date(0);
    const to = toDate.value ? new Date(toDate.value) : new Date();
    to.setHours(23, 59, 59, 999);
    result = result.filter((t) => t.txnDate && t.txnDate >= from && t.txnDate <= to);
  }

  if (sortBy.value) {
    result.sort((a, b) => {
      switch (sortBy.value) {
        case "date-desc":
          return b.txnDate && a.txnDate ? b.txnDate - a.txnDate : 0;
        case "date-asc":
          return a.txnDate && b.txnDate ? a.txnDate - b.txnDate : 0;
        case "amount-desc":
          return b.units * b.navAtTxn - a.units * a.navAtTxn;
        case "amount-asc":
          return a.units * a.navAtTxn - b.units * b.navAtTxn;
        default:
          return 0;
      }
    });
  }

  return result;
});

const hasActiveFilters = computed(() => searchQuery.value || filterType.value || fromDate.value || toDate.value || sortBy.value);

// Format helpers
const formatCurrency = (v) =>
  new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR" }).format(Number(v) || 0);

const formatNumber = (v) =>
  Number(v || 0).toLocaleString("en-IN", { maximumFractionDigits: 2 });

const formatDate = (d) =>
  d instanceof Date && !isNaN(d)
    ? d.toLocaleString("en-IN", { dateStyle: "medium", timeStyle: "short" })
    : "Invalid Date";

// Initialize
onMounted(async () => {
  await loadTransactions();
});
</script>

<style scoped>
.transaction-history-container {
  padding: 2rem 1.5rem;
  max-width: 1400px;
  margin: 0 auto;
  background: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
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
  margin-left: auto;
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

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 3rem 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.transaction-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.transaction-card {
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

.transaction-card:hover {
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

.transaction-name {
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

.status-buy {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-sell {
  background: #fce7e7;
  color: #b91c1c;
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

.alert {
  border-radius: 8px;
  padding: 0.9rem 1.5rem;
  font-weight: 600;
  color: #fff;
  position: fixed;
  top: 5.5rem;
  right: 1.5rem;
  z-index: 1000;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.alert-success {
  background: linear-gradient(90deg, #22c55e, #15803d);
}

.alert-error {
  background: linear-gradient(90deg, #ef4444, #b91c1c);
}

.alert-info {
  background: linear-gradient(90deg, #3b82f6, #1e40af);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.btn-primary {
  background: linear-gradient(135deg, #00d09c 0%, #00b894 100%);
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  font-weight: 600;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #00b894 0%, #009c74 100%);
}

@media (max-width: 768px) {
  .transaction-grid {
    grid-template-columns: 1fr;
  }
  .filter-pills {
    flex-direction: column;
    align-items: stretch;
    margin-left: 0;
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
