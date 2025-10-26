<template>
  <tr>
    <td><code>{{ investment.id }}</code></td>
    <td><strong>{{ investment.name }}</strong></td>
    <td>
      <span class="badge bg-info">{{ investment.typeDisplayName || investment.type }}</span>
    </td>
    <td>
      <span class="badge" :class="getRiskBadgeClass(investment.riskLevel)">
        {{ formatRiskLevel(investment.riskLevel) }}
      </span>
    </td>
    <td class="text-end">{{ formatCurrency(investment.minInvestment) }}</td>
    <td class="text-end">
      <span class="text-success fw-bold">
        {{ formatPercentage(investment.expectedReturnRate) }}
      </span>
    </td>
    <td class="text-end">{{ formatCurrency(investment.currentNAV) }}</td>
    <td class="text-center">
      <span
        class="badge"
        :class="investment.isActive ? 'bg-success' : 'bg-secondary'"
      >
        {{ investment.isActive ? 'Active' : 'Inactive' }}
      </span>
    </td>
    <td class="text-center">
      <div class="btn-group" role="group">
        <button
          class="btn btn-sm btn-outline-primary"
          @click="$emit('edit', investment)"
          title="Edit"
        >
          <i class="bi bi-pencil-fill"></i>
        </button>
        <button
          v-if="investment.isActive"
          class="btn btn-sm btn-outline-warning"
          @click="$emit('deactivate', investment)"
          title="Deactivate"
        >
          <i class="bi bi-pause-circle-fill"></i>
        </button>
        <button
          v-else
          class="btn btn-sm btn-outline-success"
          @click="$emit('activate', investment)"
          title="Activate"
        >
          <i class="bi bi-play-circle-fill"></i>
        </button>
      </div>
    </td>
  </tr>
</template>

<script>
export default {
  name: 'ProductTableRow',
  
  props: {
    investment: {
      type: Object,
      required: true
    }
  },

  emits: ['edit', 'deactivate', 'activate'],

  methods: {
    // Get CSS class for risk level badge
    // @param {string} riskLevel - Risk level enum from backend
    // @returns {string} - CSS class
    getRiskBadgeClass(riskLevel) {
      const classes = {
        LOW: 'bg-success',
        MEDIUM: 'bg-warning',
        HIGH: 'bg-danger',
      }
      return classes[riskLevel] || 'bg-secondary'
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
        maximumFractionDigits: 2,
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
.btn-group .btn {
  margin: 0 2px;
}
</style>
