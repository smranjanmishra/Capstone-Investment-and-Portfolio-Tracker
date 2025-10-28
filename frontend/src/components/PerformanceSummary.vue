<template>
  <div class="performance-summary card shadow-sm border-0">
    <div class="card-body">


      <!-- No Data -->
      <div v-if="!data || data.length === 0" class="text-center text-muted py-4">
        <i class="bi bi-inbox display-6 d-block mb-2"></i>
        <p>No performance data available</p>
      </div>

      <!-- Data Table -->
      <div v-else class="performance-list">
        <div
          v-for="(item, index) in formattedData"
          :key="index"
          class="performance-item p-3 mb-3 rounded"
          :class="item.gainLoss >= 0 ? 'bg-success-subtle' : 'bg-danger-subtle'"
        >
          <div class="d-flex justify-content-between align-items-center mb-2">
            <div class="d-flex align-items-center">
              <i
                :class="[
                  'bi me-2 fs-5',
                  item.gainLoss >= 0 ? 'bi-arrow-up-right text-success' : 'bi-arrow-down-right text-danger'
                ]"
              ></i>
              <h6 class="mb-0 fw-semibold">{{ item.assetTypeFormatted }}</h6>
            </div>
            <span
              class="fw-semibold"
              :class="item.gainLoss >= 0 ? 'text-success' : 'text-danger'"
            >
              {{ formatCurrency(item.gainLoss) }}
              ({{ formatPercentage(item.gainLossPercent) }})
            </span>
          </div>

          <div class="progress" style="height: 8px;">
            <div
              class="progress-bar"
              role="progressbar"
              :class="item.gainLoss >= 0 ? 'bg-success' : 'bg-danger'"
              :style="{ width: progressWidth(item.gainLossPercent) }"
              :aria-valuenow="item.gainLossPercent"
              aria-valuemin="0"
              aria-valuemax="100"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  data: {
    type: Array,
    default: () => []
  }
})
// Format data safely
const formattedData = computed(() =>
  props.data.map(item => ({
    assetTypeFormatted: formatType(item.productName || item.assetType || item.type || 'Unknown'),
    gainLoss: Number(item.absoluteGainLoss || item.gainLoss || 0),
    gainLossPercent: Number(item.gainLossPercent || 0)
  }))
)
function formatType(type) {
  return type
    .replace(/_/g, ' ')
    .toLowerCase()
    .replace(/\b\w/g, c => c.toUpperCase())
}
function formatCurrency(value) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2
  }).format(value || 0)
}
function formatPercentage(value) {
  return `${Number(value || 0).toFixed(2)}%`
}
function progressWidth(value) {
  const safeValue = Math.min(Math.abs(value), 100)
  return `${safeValue}%`
}
</script>

<style scoped>
.performance-summary {
  border-radius: 12px;
  background: #fff;
}
.performance-item {
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.performance-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.bg-success-subtle {
  background-color: rgba(25, 135, 84, 0.05);
}
.bg-danger-subtle {
  background-color: rgba(220, 53, 69, 0.05);
}
.progress {
  border-radius: 5px;
  background-color: #e9ecef;
}
.progress-bar {
  transition: width 0.5s ease;
}
.card-title {
  font-weight: 600;
  color: #2c3e50;
}
</style>
