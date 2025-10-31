<template>
  <div class="investment-list-container">
    <!-- Header -->
    <div class="page-header">
      <h1 class="page-title">
        <i class="bi bi-clock-history me-2"></i>
        Transaction History
      </h1>
      <p class="page-subtitle">All your buy & sell activities</p>
    </div>

    <!-- Filters -->
    <div class="filters-section">
      <div class="filters-grid">
        <!-- Search -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-search"></i> Search
          </label>
          <input
            v-model="filters.search"
            type="text"
            class="filter-input"
            placeholder="Search by name or ID..."
          />
        </div>

        <!-- Type -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-tag"></i> Type
          </label>
          <select v-model="filters.type" class="filter-select">
            <option value="">All Types</option>
            <option v-for="t in uniqueTypes" :key="t" :value="t">{{ t }}</option>
          </select>
        </div>

        <!-- Risk -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-exclamation-triangle"></i> Risk
          </label>
          <select v-model="filters.risk" class="filter-select">
            <option value="">All Risks</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>
        </div>

        <!-- Transaction Type -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-arrow-left-right"></i> Transaction
          </label>
          <select v-model="filters.txnType" class="filter-select">
            <option value="">All Transactions</option>
            <option value="BUY">Buy</option>
            <option value="SELL">Sell</option>
          </select>
        </div>

        <!-- From Date -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-calendar"></i> From Date
          </label>
          <input
            v-model="filters.fromDate"
            type="date"
            class="filter-input"
          />
        </div>

        <!-- To Date -->
        <div class="filter-group">
          <label class="filter-label">
            <i class="bi bi-calendar-check"></i> To Date
          </label>
          <input
            v-model="filters.toDate"
            type="date"
            class="filter-input"
          />
        </div>
      </div>

      <!-- Clear Button & Results Count -->
      <div class="filters-actions">
        <button
          @click="clearFilters"
          class="btn-clear"
          :disabled="!hasActiveFilters"
        >
          <i class="bi bi-x-circle"></i> Clear Filters
        </button>
        <span class="results-count">
          Showing {{ filteredTxns.length }} of {{ transactions.length }} transactions
        </span>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status"></div>
      <p class="mt-3 text-muted">Loading transactions...</p>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredTxns.length === 0" class="empty-state text-center py-5">
      <i class="bi bi-inbox display-1 text-muted"></i>
      <h3 class="mt-3">No Transactions Found</h3>
      <p class="text-muted" v-if="hasActiveFilters">Try adjusting your filters</p>
    </div>

    <!-- Grid -->
    <div v-else class="investment-grid">
      <div
        v-for="txn in filteredTxns"
        :key="txn.id"
        class="investment-card"
      >
        <!-- Row 1: Title (left) + Type badge (right) -->
        <div class="card-header-section">
          <div class="card-title-area">
            <h5 class="investment-name">{{ txn.investmentName }}</h5>
            <span
              v-if="txn.typeDisplayName"
              class="type-badge"
              :class="typeClass(txn.type, txn.typeDisplayName)"
            >
              {{ txn.typeDisplayName || prettifyEnum(txn.type) }}
            </span>
          </div>
        </div>

          <!-- Row 2: BUY/SELL badge (left) -->
        <div class="txn-row">
          <span
            class="status-badge"
            :class="txn.txnType === 'BUY' ? 'status-active' : 'status-inactive'"
          >
            {{ txn.txnType }}
          </span>

          <span class="txn-product-id">
            ID: {{ txn.investmentProductId }}
          </span>
        </div>

        <!-- Row 3: Stats (2-column grid) -->
        <div class="card-stats-2col">
          <div class="stat-block">
            <span class="label">Units</span>
            <span class="value">{{ formatNumber(txn.units) }}</span>
          </div>
          <div class="stat-block">
            <span class="label">Total</span>
            <span class="value value-primary">{{ formatCurrency(txn.units * txn.navAtTxn) }}</span>
          </div>
          <div class="stat-block">
            <span class="label">NAV</span>
            <span class="value">{{ formatCurrency(txn.navAtTxn) }}</span>
          </div>
          <div class="stat-block">
            <span class="label">Date</span>
            <span class="value">{{ formatDate(txn.txnDate) }}</span>
          </div>
        </div>

        <!-- Row 4: Risk line -->
        <div class="card-details">
          <div class="detail-item">
            <span class="detail-label">
              <i class="bi bi-exclamation-triangle"></i> Risk Level:
            </span>
            <span class="risk-badge" :class="getRiskBadgeClass(formatRisk(txn.riskLevel))">
              {{ formatRisk(txn.riskLevel) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getTransactions, getInvestments } from "@/services/api";

const investments = ref([]);
const transactions = ref([]);
const loading = ref(true);

// Filters
const filters = ref({
  search: "",
  type: "",
  risk: "",
  txnType: "",
  fromDate: "",
  toDate: ""
});

// Load investments first (for name/type/risk mapping)
async function loadInvestments() {
  const res = await getInvestments();
  investments.value = res?.data?.data || [];
}

async function loadTransactions() {
  const res = await getTransactions();
  const list = res?.data?.data || [];
  transactions.value = list.map(normalizeTx);
  loading.value = false;
}

function normalizeTx(t) {
  const match = investments.value.find(i => String(i.id) === String(t.investmentProductId));
  return {
    ...t,
    txnDate: new Date(t.txnDate),
    investmentName: match?.name ?? `Investment #${t.investmentProductId}`,
    type: match?.type ?? "",                     // enum (e.g., MUTUAL_FUND)
    typeDisplayName: match?.typeDisplayName ?? "", // display (e.g., Mutual Fund)
    riskLevel: match?.riskLevel ?? ""            // enum HIGH/MEDIUM/LOW
  };
}

const filteredTxns = computed(() => {
  let result = [...transactions.value];

  // Search filter
  if (filters.value.search) {
    const search = filters.value.search.toLowerCase();
    result = result.filter(t =>
      t.investmentName.toLowerCase().includes(search) ||
      String(t.investmentProductId).includes(search)
    );
  }

  // Type filter
  if (filters.value.type) {
    result = result.filter(t => t.typeDisplayName === filters.value.type);
  }

  // Risk filter
  if (filters.value.risk) {
    result = result.filter(t =>
      String(t.riskLevel).toUpperCase() === filters.value.risk
    );
  }

  // Transaction type filter
  if (filters.value.txnType) {
    result = result.filter(t => t.txnType === filters.value.txnType);
  }

  // From date filter
  if (filters.value.fromDate) {
    const from = new Date(filters.value.fromDate);
    from.setHours(0, 0, 0, 0);
    result = result.filter(t => t.txnDate >= from);
  }

  // To date filter
  if (filters.value.toDate) {
    const to = new Date(filters.value.toDate);
    to.setHours(23, 59, 59, 999);
    result = result.filter(t => t.txnDate <= to);
  }

  return result;
});

// Get unique types for dropdown
const uniqueTypes = computed(() => {
  const types = new Set(
    transactions.value
      .map(t => t.typeDisplayName)
      .filter(Boolean)
  );
  return Array.from(types).sort();
});

// Check if any filters are active
const hasActiveFilters = computed(() => {
  return filters.value.search ||
    filters.value.type ||
    filters.value.risk ||
    filters.value.txnType ||
    filters.value.fromDate ||
    filters.value.toDate;
});

// Clear all filters
function clearFilters() {
  filters.value = {
    search: "",
    type: "",
    risk: "",
    txnType: "",
    fromDate: "",
    toDate: ""
  };
}

// Helpers
const formatCurrency = v => new Intl.NumberFormat("en-IN",{ style:"currency", currency:"INR" }).format(Number(v)||0);
const formatNumber = v => Number(v||0).toLocaleString("en-IN");
const formatDate = d => {
  if (!(d instanceof Date) || isNaN(d)) return "-";
  const day = String(d.getDate()).padStart(2, '0');
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const year = String(d.getFullYear()).slice(-2);
  const hours = d.getHours();
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const ampm = hours >= 12 ? 'pm' : 'am';
  const hour12 = hours % 12 || 12;
  return `${day}/${month}/${year}, ${hour12}:${minutes} ${ampm}`;
};

function formatRisk(level) {
  if (!level) return "";
  const u = String(level).toUpperCase();
  if (u === "HIGH") return "High";
  if (u === "MEDIUM") return "Medium";
  if (u === "LOW") return "Low";
  return level;
}

function prettifyEnum(s) {
  if (!s) return "";
  return String(s).toLowerCase().split(/[_\s-]+/).map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(" ");
}

// Map type (enum or display) to InvestmentCard color classes
function typeClass(typeEnum, typeDisplay) {
  const fromEnum = (typeEnum || "").toLowerCase();
  const fromDisplay = (typeDisplay || "").toLowerCase().replace(/\s+/g, "_");
  const key = fromEnum || fromDisplay;

  if (key.includes("stock")) return "badge-stock";
  if (key.includes("bond")) return "badge-bond";
  if (key.includes("mutual_fund") || key.includes("mutual")) return "badge-mutual_fund";
  if (key.includes("etf")) return "badge-etf";
  if (key.includes("real_estate")) return "badge-real_estate";
  if (key.includes("commodity")) return "badge-commodity";
  if (key.includes("cryptocurrency") || key.includes("crypto")) return "badge-cryptocurrency";
  return "badge-mutual_fund"; // sensible default styling
}

// Risk badge classes (same as InvestmentCard theme)
const getRiskBadgeClass = level => ({
  Low: "bg-success text-white",
  Medium: "bg-warning text-white",
  High: "bg-danger text-white"
}[level] || "bg-secondary text-white");

onMounted(async () => {
  await loadInvestments();
  await loadTransactions();
});
</script>

<style scoped>
.investment-list-container{padding:2rem 1.5rem;max-width:1400px;margin:0 auto;background:#f8f9fa}
.page-header{margin-bottom:2rem}
.page-title{font-size:2.5rem;font-weight:700;color:#2c3e50;margin-bottom:.5rem}
.page-subtitle{font-size:1.1rem;color:#6c757d;margin-bottom:0}

.filters-section{background:#fff;border-radius:12px;padding:1.5rem;margin-bottom:2rem;box-shadow:0 2px 8px rgba(0,0,0,.08);border:1px solid #f0f0f0}
.filters-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:1rem;margin-bottom:1rem}
.filter-group{display:flex;flex-direction:column;gap:.5rem}
.filter-label{font-size:.875rem;font-weight:600;color:#2c3e50;display:flex;align-items:center;gap:.375rem}
.filter-input,.filter-select{padding:.625rem .875rem;border:1px solid #ddd;border-radius:8px;font-size:.9375rem;transition:.2s;background:#fff}
.filter-input:focus,.filter-select:focus{outline:none;border-color:#00b894;box-shadow:0 0 0 3px rgba(0,184,148,.1)}
.filter-select{cursor:pointer}

.filters-actions{display:flex;justify-content:space-between;align-items:center;padding-top:1rem;border-top:1px solid #f0f0f0}
.btn-clear{padding:.625rem 1.25rem;background:#fff;border:1px solid #ddd;border-radius:8px;font-size:.9375rem;font-weight:600;color:#666;cursor:pointer;transition:.2s;display:flex;align-items:center;gap:.5rem}
.btn-clear:hover:not(:disabled){background:#f8f9fa;border-color:#00b894;color:#00b894}
.btn-clear:disabled{opacity:.5;cursor:not-allowed}
.results-count{font-size:.875rem;color:#666;font-weight:500}

.investment-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(350px,1fr));gap:1.5rem;margin-top:1.5rem}

.investment-card{background:#fff;border-radius:12px;padding:1.5rem;box-shadow:0 2px 8px rgba(0,0,0,.08);transition:.3s;display:flex;flex-direction:column;gap:1rem;border:1px solid #f0f0f0}
.investment-card:hover{box-shadow:0 6px 20px rgba(0,0,0,.12);transform:translateY(-4px)}

.card-header-section{display:flex;flex-direction:column;gap:.75rem}
.card-title-area{display:flex;justify-content:space-between;align-items:flex-start;gap:1rem}
.investment-name{font-size:1.125rem;font-weight:600;color:#1a1a1a;margin:0;line-height:1.4}

.type-badge{padding:.35rem .75rem;border-radius:8px;font-size:.75rem;font-weight:600;white-space:nowrap}
.badge-stock{background:#e3f2fd;color:#1976d2}.badge-bond{background:#f3e5f5;color:#7b1fa2}
.badge-mutual_fund{background:#fff3e0;color:#e65100}.badge-etf{background:#e8f5e9;color:#2e7d32}
.badge-real_estate{background:#fce4ec;color:#c2185b}.badge-commodity{background:#fff8e1;color:#f57f17}
.badge-cryptocurrency{background:#e0f2f1;color:#00695c}

.status-badge{display:inline-flex;align-items:center;gap:.375rem;font-size:.8125rem;font-weight:600;padding:.25rem .5rem;border-radius:6px;align-self:flex-start}
.status-active{background:#e8f5e9;color:#2e7d32}
.status-inactive{background:#f5f5f5;color:#666}

/* Stats (2-col) */
.card-stats-2col{
  background:#fafafa;padding:1rem;border-radius:8px;border:1px solid #eaeaea;
  display:grid;grid-template-columns:repeat(2,1fr);gap:.9rem 1rem
}
.stat-block{display:flex;flex-direction:column;gap:.25rem}
.label{font-size:.8125rem;color:#666}
.value{font-size:1rem;font-weight:600;color:#1a1a1a}
.value-primary{color:#00b894;font-size:1.1rem}

.card-details{display:flex;flex-direction:column;gap:.75rem}
.detail-item{display:flex;justify-content:space-between;align-items:center}
.detail-label{font-size:.875rem;color:#666;display:flex;align-items:center;gap:.375rem}
.risk-badge{padding:.25rem .75rem;border-radius:6px;font-size:.75rem;font-weight:600}
.txn-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.txn-product-id {
  background: #eef2f6;
  padding: 0.28rem 0.6rem;
  color: #37474f;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
}


/* Responsive */
@media (max-width:768px){
  .investment-grid{grid-template-columns:1fr}
  .card-title-area{flex-direction:column;align-items:flex-start}
  .filters-grid{grid-template-columns:1fr}
  .filters-actions{flex-direction:column;gap:1rem;align-items:stretch}
  .results-count{text-align:center}
}
</style>
