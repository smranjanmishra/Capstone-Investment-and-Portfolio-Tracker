<template>
  <div class="investment-card">
    <div class="card-header-section">
      <div class="card-title-area">
        <h5 class="investment-name">{{ investment.name }}</h5>
        <span class="type-badge" :class="`badge-${investment.type.toLowerCase()}`">
          {{ investment.typeDisplayName || investment.type }}
        </span>
      </div>
      <span
        class="status-badge"
        :class="investment.isActive ? 'status-active' : 'status-inactive'"
      >
        <i class="bi" :class="investment.isActive ? 'bi-check-circle-fill' : 'bi-x-circle-fill'"></i>
        {{ investment.isActive ? 'Active' : 'Inactive' }}
      </span>
    </div>

    <div class="card-stats">
      <div class="stat-item primary-stat">
        <div class="stat-label">
          <i class="bi bi-graph-up-arrow"></i> Expected Return
        </div>
        <div class="stat-value return-value">
          {{ formatPercentage(investment.expectedReturnRate) }}
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-label">
          <i class="bi bi-cash-coin"></i> Min. Investment
        </div>
        <div class="stat-value">{{ formatCurrency(investment.minInvestment) }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label">
          <i class="bi bi-currency-dollar"></i> Current NAV
        </div>
        <div class="stat-value">{{ formatCurrency(investment.currentNAV) }}</div>
      </div>
    </div>

    <div class="card-details">
      <div class="detail-item">
        <span class="detail-label">
          <i class="bi bi-exclamation-triangle"></i> Risk Level:
        </span>
        <span
          class="risk-badge"
          :class="getRiskBadgeClass(investment.riskLevel)"
        >
          {{ formatRiskLevel(investment.riskLevel) }}
        </span>
      </div>
    </div>

    <button class="invest-btn" :disabled="!investment.isActive">
      <i class="bi bi-arrow-right-circle"></i>
      {{ investment.isActive ? 'Invest Now' : 'Not Available' }}
    </button>
  </div>
</template>

<script>
export default {
  name: 'InvestmentCard',
  
  props: {
    investment: {
      type: Object,
      required: true
    }
  },

  methods: {
    // Get CSS class for risk level badge
    // @param {string} riskLevel - Risk level enum from backend
    // @returns {string} - CSS class
    getRiskBadgeClass(riskLevel) {
      const classes = {
        LOW: 'bg-success text-white',
        MEDIUM: 'bg-warning text-white',
        HIGH: 'bg-danger text-white',
      }
      return classes[riskLevel] || 'bg-secondary text-white'
    },

    // Format risk level for display
    // @param {string} riskLevel - Risk level enum from backend
    // @returns {string} - Formatted risk level
    formatRiskLevel(riskLevel) {
      const displayNames = {
        LOW: 'Low Risk',
        MEDIUM: 'Medium Risk',
        HIGH: 'High Risk',
      }
      return displayNames[riskLevel] || riskLevel
    },

    // Format currency value
    // @param {number} value - Currency value
    // @returns {string} - Formatted currency in INR
    formatCurrency(value) {
      return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
      }).format(value)
    },

    // Format percentage value
    // @param {number} value - Percentage value
    // @returns {string} - Formatted percentage
    formatPercentage(value) {
      return `${value.toFixed(2)}%`
    }
  }
}
</script>

<style scoped>
.investment-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
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

.badge-stock {
  background: #e3f2fd;
  color: #1976d2;
}

.badge-bond {
  background: #f3e5f5;
  color: #7b1fa2;
}

.badge-mutual_fund {
  background: #fff3e0;
  color: #e65100;
}

.badge-etf {
  background: #e8f5e9;
  color: #2e7d32;
}

.badge-real_estate {
  background: #fce4ec;
  color: #c2185b;
}

.badge-commodity {
  background: #fff8e1;
  color: #f57f17;
}

.badge-cryptocurrency {
  background: #e0f2f1;
  color: #00695c;
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

.risk-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 600;
}

.invest-btn {
  width: 100%;
  padding: 0.875rem;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #00d09c 0%, #00b894 100%);
  color: white;
  font-weight: 600;
  font-size: 0.9375rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: auto;
}

.invest-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #00b894 0%, #00a582 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 208, 156, 0.3);
}

.invest-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .card-title-area {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
