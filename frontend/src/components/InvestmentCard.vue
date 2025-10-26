<template>
  <div class="investment-card">
    <div class="card-header-section">
      <div class="card-title-area">
        <h5 class="investment-name">{{ investment.name }}</h5>
        <span class="type-badge" :class="`badge-${investment.type.toLowerCase()}`">
          {{ investment.typeDisplayName || investment.type }}
        </span>
      </div>
      <span class="status-badge" :class="investment.isActive ? 'status-active' : 'status-inactive'">
        <i class="bi" :class="investment.isActive ? 'bi-check-circle-fill' : 'bi-x-circle-fill'"></i>
        {{ investment.isActive ? 'Active' : 'Inactive' }}
      </span>
    </div>

    <div class="card-stats">
      <div class="stat-item primary-stat">
        <div class="stat-label"><i class="bi bi-graph-up-arrow"></i> Expected Return</div>
        <div class="stat-value return-value">{{ formatPercentage(investment.expectedReturnRate) }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label"><i class="bi bi-cash-coin"></i> Min. Investment</div>
        <div class="stat-value">{{ formatCurrency(investment.minInvestment) }}</div>
      </div>
      <div class="stat-item">
        <div class="stat-label"><i class="bi bi-currency-dollar"></i> Current NAV</div>
        <div class="stat-value">{{ formatCurrency(investment.currentNAV) }}</div>
      </div>
    </div>

    <div class="card-details">
      <div class="detail-item">
        <span class="detail-label"><i class="bi bi-exclamation-triangle"></i> Risk Level:</span>
        <span class="risk-badge" :class="getRiskBadgeClass(investment.riskLevel)">
          {{ formatRiskLevel(investment.riskLevel) }}
        </span>
      </div>
    </div>

    <button class="invest-btn" :disabled="!investment.isActive" @click="goBuy(investment)">
      <i class="bi bi-arrow-right-circle"></i>
      {{ investment.isActive ? 'Invest Now' : 'Not Available' }}
    </button>
  </div>
</template>

<script>
export default {
  name: "InvestmentCard",
  props: { investment: { type: Object, required: true }},
  emits: ["buy"],
  methods: {
    goBuy(investment) {
      this.$emit("buy", investment);
    },
    getRiskBadgeClass(level) {
      return {
        LOW: "bg-success text-white",
        MEDIUM: "bg-warning text-white",
        HIGH: "bg-danger text-white",
      }[level] || "bg-secondary text-white";
    },
    formatRiskLevel(level) {
      return { LOW: "Low", MEDIUM: "Medium", HIGH: "High" }[level] || level;
    },
    formatCurrency(v) {
      return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(v);
    },
    formatPercentage(v) {
      return `${Number(v || 0).toFixed(2)}%`;
    }
  }
};
</script>


<style scoped>
/* (unchanged styles) */
.investment-card{background:#fff;border-radius:12px;padding:1.5rem;box-shadow:0 2px 8px rgba(0,0,0,.08);transition:.3s;display:flex;flex-direction:column;gap:1.25rem;border:1px solid #f0f0f0}
.investment-card:hover{box-shadow:0 6px 20px rgba(0,0,0,.12);transform:translateY(-4px)}
.card-header-section{display:flex;flex-direction:column;gap:.75rem}
.card-title-area{display:flex;justify-content:space-between;align-items:flex-start;gap:1rem}
.investment-name{font-size:1.125rem;font-weight:600;color:#1a1a1a;margin:0;line-height:1.4}
.type-badge{padding:.375rem .75rem;border-radius:20px;font-size:.75rem;font-weight:600;white-space:nowrap}
.badge-stock{background:#e3f2fd;color:#1976d2}.badge-bond{background:#f3e5f5;color:#7b1fa2}
.badge-mutual_fund{background:#fff3e0;color:#e65100}.badge-etf{background:#e8f5e9;color:#2e7d32}
.badge-real_estate{background:#fce4ec;color:#c2185b}.badge-commodity{background:#fff8e1;color:#f57f17}
.badge-cryptocurrency{background:#e0f2f1;color:#00695c}
.status-badge{display:inline-flex;align-items:center;gap:.375rem;font-size:.8125rem;font-weight:600;padding:.25rem .5rem;border-radius:6px;align-self:flex-start}
.status-active{background:#e8f5e9;color:#2e7d32}.status-inactive{background:#f5f5f5;color:#666}
.card-stats{display:flex;flex-direction:column;gap:1rem;padding:1rem;background:#fafafa;border-radius:8px}
.stat-item{display:flex;justify-content:space-between;align-items:center}
.stat-item.primary-stat{padding-bottom:1raem;border-bottom:1px solid #e0e0e0}
.stat-label{font-size:.8125rem;color:#666;display:flex;align-items:center;gap:.375rem}
.stat-value{font-size:1rem;font-weight:600;color:#1a1a1a}.return-value{color:#00d09c;font-size:1.25rem}
.card-details{display:flex;flex-direction:column;gap:.75rem}
.detail-item{display:flex;justify-content:space-between;align-items:center}
.detail-label{font-size:.875rem;color:#666;display:flex;align-items:center;gap:.375rem}
.risk-badge{padding:.25rem .75rem;border-radius:6px;font-size:.75rem;font-weight:600}
.invest-btn{width:100%;padding:.875rem;border:none;border-radius:8px;background:linear-gradient(135deg,#00d09c 0%,#00b894 100%);color:#fff;font-weight:600;font-size:.9375rem;cursor:pointer;transition:.3s;display:flex;align-items:center;justify-content:center;gap:.5rem;margin-top:auto}
.invest-btn:hover:not(:disabled){background:linear-gradient(135deg,#00b894 0%,#00a582 100%);transform:translateY(-2px);box-shadow:0 4px 12px rgba(0,208,156,.3)}
.invest-btn:disabled{background:#e0e0e0;color:#999;cursor:not-allowed}
@media (max-width:768px){.card-title-area{flex-direction:column;align-items:flex-start}}
.investment-card {
  background: white; border-radius: 12px; padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: all 0.3s ease; display:flex; flex-direction:column; gap:1rem;
}
.investment-card:hover { transform: translateY(-4px); box-shadow: 0 6px 20px rgba(0,0,0,0.12); }
.invest-btn { width:100%; background: #00b894; color:#fff; border-radius:8px; padding:0.8rem; font-weight:600; }
.invest-btn:hover { background:#009f80; }
.status-active {background:#e8f5e9;color:#2e7d32;}
.status-inactive {background:#eee;color:#666;}
</style>
