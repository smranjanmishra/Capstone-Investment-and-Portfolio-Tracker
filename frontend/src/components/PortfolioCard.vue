<template>
  <div class="portfolio-card">
    <div class="card-header-section">
      <div class="card-title-area">
        <h5 class="holding-name">{{ holding.investmentProduct.name }}</h5>
        <span class="type-badge" :class="`badge-${holding.investmentProduct.type.toLowerCase()}`">
          {{ getTypeDisplayName(holding.investmentProduct.type) }}
        </span>
      </div>
      <span class="performance-badge" :class="performanceClass">
        <i class="bi" :class="performanceIcon"></i>
        {{ formatPercentage(performancePercentage) }}
      </span>
    </div>

    <div class="card-stats">
      <div class="stat-item primary-stat">
        <div class="stat-label">
          <i class="bi bi-currency-dollar"></i> Current Value
        </div>
        <div class="stat-value current-value">
          {{ formatCurrency(holding.currentValue) }}
        </div>
      </div>

      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-cash-stack"></i> Total Invested
          </div>
          <div class="stat-value">
            {{ formatCurrency(holding.totalInvested) }}
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-graph-up"></i> Gain/Loss
          </div>
          <div class="stat-value" :class="gainLossClass">
            {{ formatCurrency(gainLoss) }}
          </div>
        </div>
      </div>

      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-123"></i> Units
          </div>
          <div class="stat-value">{{ holding.units }}</div>
        </div>

        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-graph-up-arrow"></i> Avg Price
          </div>
          <div class="stat-value">
            {{ formatCurrency(holding.avgPurchasePrice) }}
          </div>
        </div>
      </div>

      <div class="stat-row">
        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-tag"></i> Current NAV
          </div>
          <div class="stat-value">
            {{ effectiveNAV !== null ? formatCurrency(effectiveNAV) : 'N/A' }}
          </div>
        </div>

        <div class="stat-item">
          <div class="stat-label">
            <i class="bi bi-exclamation-triangle"></i> Risk Level
          </div>
          <span class="risk-badge" :class="getRiskBadgeClass(holding.investmentProduct.riskLevel)">
            {{ formatRiskLevel(holding.investmentProduct.riskLevel) }}
          </span>
        </div>
      </div>
    </div>

    <div class="action-buttons">
      <button class="action-btn view-btn" @click="viewDetails">
        <i class="bi bi-eye"></i> View
      </button>
      <button class="action-btn sell-btn" @click="sellHolding" :disabled="holding.units <= 0">
        <i class="bi bi-dash-circle"></i> Sell
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PortfolioCard',
  props: {
    holding: {
      type: Object,
      required: true
    }
  },
  emits: ['sell', 'view-details'],
  computed: {
    gainLoss() {
      return Number(this.holding.currentValue || 0) - Number(this.holding.totalInvested || 0)
    },
    performancePercentage() {
      const invested = Number(this.holding.totalInvested || 0)
      if (invested === 0) return 0
      return (this.gainLoss / invested) * 100
    },
    performanceClass() {
      if (this.gainLoss > 0) return 'performance-positive'
      if (this.gainLoss < 0) return 'performance-negative'
      return 'performance-neutral'
    },
    performanceIcon() {
      if (this.gainLoss > 0) return 'bi-arrow-up-circle-fill'
      if (this.gainLoss < 0) return 'bi-arrow-down-circle-fill'
      return 'bi-dash-circle-fill'
    },
    gainLossClass() {
      if (this.gainLoss > 0) return 'text-success'
      if (this.gainLoss < 0) return 'text-danger'
      return 'text-muted'
    },

    // ✅ NAV FIX — No fallback to avg price
    effectiveNAV() {
      const nav = Number(this.holding.investmentProduct.currentNAV)
      return Number.isFinite(nav) && nav > 0 ? nav : null
    }
  },
  methods: {
    sellHolding() {
      this.$emit('sell', this.holding)
    },
    viewDetails() {
      this.$emit('view-details', this.holding)
    },
    getRiskBadgeClass(level) {
      const classes = {
        LOW: 'bg-success text-white',
        MEDIUM: 'bg-warning text-white',
        HIGH: 'bg-danger text-white'
      }
      return classes[level] || 'bg-secondary text-white'
    },
    formatRiskLevel(level) {
      const labels = { LOW: 'Low', MEDIUM: 'Medium', HIGH: 'High' }
      return labels[level] || level
    },
    getTypeDisplayName(type) {
      const names = {
        STOCK: 'Stock',
        MUTUAL_FUND: 'Mutual Fund',
        BOND: 'Bond',
        ETF: 'ETF',
        REAL_ESTATE: 'Real Estate',
        COMMODITY: 'Commodity',
        CRYPTOCURRENCY: 'Crypto'
      }
      return names[type] || type
    },
    formatCurrency(value) {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR'
      }).format(Number(value || 0))
    },
    formatPercentage(value) {
      const prefix = value > 0 ? '+' : ''
      return `${prefix}${Number(value || 0).toFixed(2)}%`
    }
  }
}
</script>

<style scoped>
.portfolio-card {
  background: #fff;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  border: 1px solid #f0f0f0;
}
.portfolio-card:hover {
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

.holding-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.current-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: #00b894;
}

.action-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}
.sell-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
